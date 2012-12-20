/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package edu.upenn.mkse212.pennbook.server;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.db.KeyValueStoreFactory;
import edu.upenn.mkse212.db.MapUtil;
import edu.upenn.mkse212.db.Settings;
import edu.upenn.mkse212.pennbook.client.FriendshipService;
import edu.upenn.mkse212.pennbook.models.User;
import edu.upenn.mkse212.pennbook.shared.FeedItemType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ListMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FriendshipServiceImpl extends RemoteServiceServlet implements FriendshipService {
	public static final String USER_ID_ATTR="userId";
	public static final String FRIEND_USER_ID_ATTR="friendUserId";
	public static final String FIRST_NAME_ATTR="firstName";
	public static final String LAST_NAME_ATTR="lastName";
	public static final String AFFILIATION_ATTR = "affiliation";
	public static final String DB_DOMAIN = "pennbook_friendships";

	public IKeyValueStorage store;
	private ObjectMapper mapper;

	private static FriendshipServiceImpl instance;
	
	public FriendshipServiceImpl() {
		super();
		store = KeyValueStoreFactory.getKeyValueStore(Settings.storeType, DB_DOMAIN, Settings.path, Settings.userId, Settings.authKey, false);
		mapper = new ObjectMapper();
	}
	
	public static FriendshipServiceImpl getServerInstance(){
		if (instance == null) {
			instance = new FriendshipServiceImpl();
		}
		return instance;
	}
	
	/*gets all the friendships for a given user
	 * getFriendship
	 * (non-Javadoc)
	 * @see edu.upenn.mkse212.pennbook.client.FriendshipService#getFriendships(java.lang.String)
	 */
	@Override
	public List<Map<String,String>> getFriendships(String userId) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'",DB_DOMAIN, USER_ID_ATTR, userId);
		List<Map<String,String>> results = store.select(query);
		return results;
	}
	
	public List<String> getFriendIds(String userId) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'",DB_DOMAIN, USER_ID_ATTR, userId);
		List<String> friendIds = new ArrayList<String>();
		List<Map<String,String>> results = store.select(query);
		for (Map<String,String> frMap : results)
		{
			friendIds.add(frMap.get(FRIEND_USER_ID_ATTR));
		}
		return friendIds;
	}

	//create both directions of user friendship in DB
	@Override
	public void addFriend(String userId, String friendUserId, String userName, String friendName, String requestId) {
		UserServiceImpl userServ = UserServiceImpl.getServerInstance();
		ListMultimap<String,String> u1multimap = userServ.getMultimap(userId);
		ListMultimap<String,String> u2multimap = userServ.getMultimap(friendUserId);
		Map<String, ArrayList<String>> u1map = MapUtil.convertMultimap(u1multimap);
		Map<String, ArrayList<String>> u2map = MapUtil.convertMultimap(u2multimap);
		User u1 = new User(u1map);
		User u2 = new User(u2map);
		createFriendship(u1,u2);
		createFriendship(u2,u1);
		
		// Doing all server work in one call for efficiency
		FriendRequestServiceImpl friendRequestService = FriendRequestServiceImpl.getServerInstance();
		friendRequestService.deleteFriendRequest(requestId);
		
		FeedServiceImpl feedService = FeedServiceImpl.getServerInstance();
		feedService.postFeedItem("", userId, friendUserId, userName, friendName, FeedItemType.NEW_FRIENDSHIP);
	}
	
	private void createFriendship(User u1, User u2) {
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(USER_ID_ATTR, u1.getId());
		map.put(FRIEND_USER_ID_ATTR, u2.getId());
		map.put(FIRST_NAME_ATTR, u2.getFirstName());
		map.put(LAST_NAME_ATTR, u2.getLastName());
		map.put(AFFILIATION_ATTR,u2.getAffiliation());
		String friendshipId = UUID.randomUUID().toString();
		
		store.put(friendshipId, map);
	}

	// Method for retrieiving nodes to show for network visualizer, returns a list of trees to add to graph
	@Override
	public ArrayList<String> getNetwork(String userId) {
		
		ArrayList<String> trees = new ArrayList<String>();
		UserServiceImpl userServ = UserServiceImpl.getServerInstance();
		
		// Add the user and user's friends
		Map<String, ArrayList<String>> user = userServ.get(userId);
		String affiliation = user.get(UserServiceImpl.AFFILIATION_ATTR).get(0);
		List<Map<String,String>> friendships = getFriendships(userId);
		Set<Map<String,String>> userFriends = new HashSet<Map<String,String>>();
		// Add each friend as child node
		for (Map<String,String> friendship : friendships) {
			Map<String,String> friendDetails = new HashMap<String,String>();
			String friendId = friendship.get(FRIEND_USER_ID_ATTR);
			String friendName = friendship.get(FriendshipServiceImpl.FIRST_NAME_ATTR)+" "+friendship.get(FriendshipServiceImpl.LAST_NAME_ATTR);
			friendDetails.put("friendId", friendId);
			friendDetails.put("friendName", friendName);
			userFriends.add(friendDetails);
		}
		String userName = user.get(FIRST_NAME_ATTR).get(0)+" "+user.get(LAST_NAME_ATTR).get(0);
		Map<String,String> userDetails = new HashMap<String,String>();
		userDetails.put("userId", userId);
		userDetails.put("userName", userName);
		// create a tree
		trees.add(treeFromUserAndFriends(userDetails, userFriends));
		
		// Add the friends and friends' friends
		for (Map<String,String> friendship : friendships) {
			String friendId = friendship.get(FriendshipServiceImpl.FRIEND_USER_ID_ATTR);
			Map<String, ArrayList<String>> friend = userServ.get(friendId);
			List<Map<String,String>> friendFriendships = getFriendships(friendId);
			Set<Map<String,String>> friendFriends = new HashSet<Map<String,String>>();
			for (Map<String,String> friendFriendship : friendFriendships) {
				String friendFriendId = friendFriendship.get(FriendshipServiceImpl.FRIEND_USER_ID_ATTR);
				Map<String, ArrayList<String>> friendFriend = userServ.get(friendFriendId);
				if (friendFriend.get(UserServiceImpl.AFFILIATION_ATTR).get(0).equals(affiliation)) {
					Map<String,String> friendFriendDetails = new HashMap<String,String>();
					String friendFriendName = friendFriendship.get(FriendshipServiceImpl.FIRST_NAME_ATTR)+" "+friendFriendship.get(FriendshipServiceImpl.LAST_NAME_ATTR);
					friendFriendDetails.put("friendId", friendFriendId);
					friendFriendDetails.put("friendName", friendFriendName);
					friendFriends.add(friendFriendDetails);
				}
			}
			Map<String,String> friendDetails = new HashMap<String,String>();
			String friendUserName = friend.get(FIRST_NAME_ATTR).get(0)+" "+friend.get(LAST_NAME_ATTR).get(0);
			friendDetails.put("userId", friendId);
			friendDetails.put("userName", friendUserName);
			trees.add(treeFromUserAndFriends(friendDetails, friendFriends));
		}

		return trees;
	}

	// Helper function to generate a JSON string
	public static String treeFromUserAndFriends(Map<String,String> user, Set<Map<String,String>> friends) {
		String tree = "{\"id\": \""+user.get("userId")+"\", \"name\": \""+user.get("userName")+"\", \"children\": [";
		boolean first = true;
		for (Map<String,String> friend : friends) {
			if (!first) { tree += ","; }
			else { first = false; }
			tree += "{\"id\": \""+friend.get("friendId")+"\", \"name\": \""+friend.get("friendName")+"\", \"children\": []}";
		}
		tree += "]}";
		return tree;
	}

	public JsonNode makeJsonNode(String userId, String name, ArrayList<JsonNode> children )
	{
		ObjectNode on = mapper.createObjectNode();
		on.put("userId", userId);
		on.put("name",name);
		on.put("children", mapper.valueToTree(children));
		return on;
	}

	//get all friends that are online
	public List<Map<String,String>> getOnlineFriendships(String userId)
	{
		UserServiceImpl userServ = UserServiceImpl.getServerInstance();
		userServ.setUserPresence(userId);
		HashSet<String> onlineUserIds = userServ.getPresentUsers();
		List<Map<String,String>> friendships = getFriendships(userId);
		List<Map<String,String>> friendsOnline = new ArrayList<Map<String,String>>() ;
		for (Map<String,String> friendship : friendships)
		{
			//System.out.println(friendship);
			String friendId = friendship.get(FRIEND_USER_ID_ATTR);
			if ( onlineUserIds.contains(friendId) )
				friendsOnline.add(friendship);
		}
		return friendsOnline;
	}

	//used for tagging: get a friend of userId who has full name matching friendName
	@Override
	public String getFriendByName(String userId, String friendName) {
		List<Map<String,String>> friendships = getFriendships(userId);
		String friendUserId="";
		for (Map<String,String> friendship: friendships) {
			String firstName = friendship.get(FIRST_NAME_ATTR);
			String lastName = friendship.get(LAST_NAME_ATTR);
			if (friendName.equals(firstName+" "+lastName) ) {
				friendUserId = friendship.get(FRIEND_USER_ID_ATTR);
				break;
			}
		}
		return friendUserId;
	}
	
	@Override
	public boolean deleteFriendship(String userId, String friendUserId)
	{
		// Delete one way
		List<Map<String,String>> friendships = getFriendships(userId);
		String friendshipId="";
		for (Map<String,String> friendship: friendships) {
			String thisFriendUserId = friendship.get(FRIEND_USER_ID_ATTR);
			if (thisFriendUserId.equals(friendUserId) ) {
				friendshipId = friendship.get(IKeyValueStorage.KEYWORD_ATTR);
				break;
			}
		}
		if (!friendshipId.equals("")) {
			store.delete(friendshipId);
		}
		else {
			return false;
		}
		
		// Delete other way
		friendships = getFriendships(friendUserId);
		friendshipId="";
		for (Map<String,String> friendship: friendships) {
			String thisFriendUserId = friendship.get(FRIEND_USER_ID_ATTR);
			if (thisFriendUserId.equals(userId) ) {
				friendshipId = friendship.get(IKeyValueStorage.KEYWORD_ATTR);
				break;
			}
		}
		if (!friendshipId.equals("")) {
			store.delete(friendshipId);
		}
		else {
			return false;
		}
		
		return true;
	}
}