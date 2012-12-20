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
import java.util.Map;
import java.util.UUID;

import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.db.KeyValueStoreFactory;
import edu.upenn.mkse212.db.Settings;
import edu.upenn.mkse212.pennbook.client.FriendRequestService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FriendRequestServiceImpl extends RemoteServiceServlet implements FriendRequestService {
	public static final String SENDER_ID_ATTR = "senderId";
	public static final String RECEIVER_ID_ATTR = "receiverId";
	public static final String SENDER_NAME_ATTR = "senderName";
	public static final String TIMESTAMP_ATTR = "timestamp";
	
	private static final String DB_DOMAIN = "pennbook_friendrequests";

	private IKeyValueStorage store;
	private ObjectMapper mapper;
	
	private static FriendRequestServiceImpl instance;
	
	public FriendRequestServiceImpl() {
		super();
		store = KeyValueStoreFactory.getKeyValueStore(Settings.storeType, DB_DOMAIN, Settings.path, Settings.userId, Settings.authKey, false);
		mapper = new ObjectMapper();
	}

	public static FriendRequestServiceImpl getServerInstance(){
		if (instance == null) {
			instance = new FriendRequestServiceImpl();
		}
		return instance;
	}
	
	//Put a friend request's attributes into the DB
	@Override
	public HashMap<String, String> createFriendRequest(String senderId, String receiverId, String senderName) {
		HashMap<String, String> map = new HashMap<String,String>();
		map.put(SENDER_ID_ATTR, senderId);
		map.put(RECEIVER_ID_ATTR, receiverId);
		map.put(SENDER_NAME_ATTR, senderName);
		map.put(TIMESTAMP_ATTR, System.currentTimeMillis()+"");
		
		String id = UUID.randomUUID().toString();
		store.put(id,map);
		
		//the name/key is a randomly generated UUID for uniqueness
		map.put(IKeyValueStorage.KEYWORD_ATTR,id);
		
		return map;
	}

	public void deleteFriendRequest(String requestId) {
		store.delete(requestId);
	}

	/* get list of all friend requests for a given user*/
	@Override
	public ArrayList<Map<String,String>> getFriendRequests(String receiverId) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'", DB_DOMAIN, RECEIVER_ID_ATTR, receiverId);
		ArrayList<Map<String,String>> maps = new ArrayList<Map<String,String>>(store.select(query));
		return maps;
	}
	
	

}
