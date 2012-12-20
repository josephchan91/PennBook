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
package edu.upenn.mkse212.pennbook.client;

import java.util.List;


import java.util.ArrayList;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("FriendshipService")
public interface FriendshipService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static FriendshipServiceAsync instance;
		public static FriendshipServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(FriendshipService.class);
			}
			return instance;
		}
	}
	
	public List<Map<String,String>> getFriendships(String userId);
	
	public void addFriend(String userId, String friendUserId, String userName, String friendName, String requestId);
	
	public List<String> getFriendIds(String posterId);
	
	public ArrayList<String> getNetwork(String userId);

	public List<Map<String,String>> getOnlineFriendships(String userId);
	
	public String getFriendByName(String userId, String friendName);

	public boolean deleteFriendship(String userId, String friendUserId);
}
