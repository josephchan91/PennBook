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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.db.KeyValueStoreFactory;
import edu.upenn.mkse212.db.MapUtil;
import edu.upenn.mkse212.db.Settings;
import edu.upenn.mkse212.pennbook.client.FeedService;
import edu.upenn.mkse212.pennbook.shared.FeedItemType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ListMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FeedServiceImpl extends RemoteServiceServlet implements FeedService {

	public static final String MESSAGE_ATTR="message";
	public static final String POSTER_ID_ATTR="posterId";
	public static final String RECEIVER_ID_ATTR="receiverId";
	public static final String VIEWER_ID_ATTR="viewerId";
	public static final String POSTER_NAME_ATTR="posterName";
	public static final String RECEIVER_NAME_ATTR="receiverName";
	public static final String COMMENT_ATTR = "comment";
	public static final String TIMESTAMP_ATTR = "timestamp";
	public static final String POST_ID_ATTR = "postId"; //id common to all copies of a post
	public static final String TYPE_ATTR = "type";
	
	private static final String DB_DOMAIN = "pennbook_feed";
	private ObjectMapper mapper;
	
	private IKeyValueStorage store;
	
	private static FeedServiceImpl instance;
	
	public FeedServiceImpl() {
		super();
		store = KeyValueStoreFactory.getKeyValueStore(Settings.storeType, DB_DOMAIN, Settings.path, Settings.userId, Settings.authKey, false);
		mapper = new ObjectMapper();
		mapper.registerModule(new GuavaModule());
	}
	
	public static FeedServiceImpl getServerInstance(){
		if (instance == null) {
			instance = new FeedServiceImpl();
		}
		return instance;
	}
	
	/*Create post and all necessary copies to display for 
	 * all users who should see the post on feed(non-Javadoc)
	 * @see edu.upenn.mkse212.pennbook.client.FeedService#postFeedItem(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, edu.upenn.mkse212.pennbook.shared.FeedItemType)
	 */
	@Override
	public void postFeedItem(String message, String posterId,
			String receiverId, String posterName, String receiverName, FeedItemType type) {
		//put given attributes into a hashmap
		HashMap<String,String> m = new HashMap<String,String>();
		String postId = UUID.randomUUID().toString();//random unique ID 
		m.put(MESSAGE_ATTR, message);
		m.put(POSTER_ID_ATTR, posterId);
		m.put(RECEIVER_ID_ATTR,receiverId);
		m.put(POSTER_NAME_ATTR,posterName);
		m.put(RECEIVER_NAME_ATTR, receiverName);
		m.put(TYPE_ATTR, type.ordinal()+"");
		m.put(TIMESTAMP_ATTR, System.currentTimeMillis()+"");
		m.put(POST_ID_ATTR, postId);
		
		FriendshipServiceImpl frServ = FriendshipServiceImpl.getServerInstance();
		
		//list of users who should see item on their feeds, i.e.,
		//poster/receiver+all friends
		List<String> viewerIds = frServ.getFriendIds(posterId);
		viewerIds.add(posterId);//poster should also be a viewer
		
		//store in DB
		for (String id : viewerIds) {
			m.put(VIEWER_ID_ATTR,id);
			String feedItemId = UUID.randomUUID().toString();
			store.put(feedItemId,m);
		}
	}

	@Override
	public ArrayList<Map<String, ArrayList<String>>> getFeedItems(String userId) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s' AND %s>'0' ORDER BY %s DESC", DB_DOMAIN,VIEWER_ID_ATTR,userId,TIMESTAMP_ATTR,TIMESTAMP_ATTR);
		return getItems(query);
	}
	
	//Get all posts relevant to a user's wall
	@Override
	public ArrayList<Map<String,ArrayList<String>>> getWall(String userId) {
		String query = String.format("SELECT * FROM %s WHERE (%s='%s' OR %s='%s') AND %s='%s' AND %s>'0' ORDER BY %s DESC", DB_DOMAIN,POSTER_ID_ATTR,userId,RECEIVER_ID_ATTR,userId,VIEWER_ID_ATTR,userId,TIMESTAMP_ATTR,TIMESTAMP_ATTR);
		return getItems(query);
	}
	
	ArrayList<Map<String,ArrayList<String>>> getItems(String query) {
		List<ListMultimap<String,String>> maps = store.selectMulti(query);
		ArrayList<Map<String,ArrayList<String>>> newMaps = new ArrayList<Map<String,ArrayList<String>>>();
		Map<String, ArrayList<String>> newMap;
		for (ListMultimap<String,String> multimap : maps) {
			newMap = MapUtil.convertMultimap(multimap);
			newMaps.add(newMap);
		}
		return newMaps;
	}

	@Override
	// Get notifications for a specfici user (for notifications popup)
	public ArrayList<Map<String,ArrayList<String>>> getNotifications(String userId) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s' AND %s='%s' AND %s>'0' ORDER BY %s DESC", DB_DOMAIN,RECEIVER_ID_ATTR,userId,VIEWER_ID_ATTR,userId,TIMESTAMP_ATTR,TIMESTAMP_ATTR);
		return getItems(query);
	}


	//attach comment copies to all posts
	@Override
	public String addComment(String postId, String posterId, String posterName,
			String message) {
		HashMap<String,Object> commentMap = new HashMap<String,Object>();
		commentMap.put(TIMESTAMP_ATTR, System.currentTimeMillis());
		commentMap.put(POSTER_ID_ATTR, posterId);
		commentMap.put(POSTER_NAME_ATTR, posterName);
		commentMap.put(MESSAGE_ATTR, message);
		
		String commentAsJson = "";
		try {
			//comments will be stored as JSON
			commentAsJson = mapper.writeValueAsString(commentMap);
		} catch (JsonProcessingException e) {
			System.exit(1);
			e.printStackTrace();
		}
		HashMap<String,String> map = new HashMap<String,String>();
		map.put(COMMENT_ATTR, commentAsJson); 
		
		List<String> feedItemIds = feedItemIdsByPostId(postId);
		for (String feedItemId : feedItemIds) //add comment to each copy of the post
		{
			store.put(feedItemId,map);
		}
		
		return commentAsJson;
	}
	
	//get all copies of a certain post (i.e., all the copies that were made for each user)
	//"postId" is the same for all the copies of a post
	List<String> feedItemIdsByPostId(String postId) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'", DB_DOMAIN,POST_ID_ATTR, postId);
		List<Map<String, String>> feedItems = store.select(query);
		List<String> ids = new ArrayList<String>();
		
		for (Map<String,String> feedItem : feedItems)
		{
			ids.add(feedItem.get(IKeyValueStorage.KEYWORD_ATTR)); //get post key
		}
		return ids;
	}

}
