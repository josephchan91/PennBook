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

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface FriendshipServiceAsync {
	public void getFriendships(String userId, AsyncCallback<List<Map<String,String>>> callback);
	
	public void addFriend(String userId, String friendUserId, String userName, String friendName, String requestId, AsyncCallback<Void> callback);
	
	public void getFriendIds(String posterId, AsyncCallback<List<String>> callback);
	
	public void getNetwork(String userId, AsyncCallback<ArrayList<String>> callback);

	public void getOnlineFriendships(String userId, AsyncCallback<List<Map<String,String>>> callback);
	
	public void getFriendByName(String userId, String friendName, AsyncCallback<String> callback);

	public void deleteFriendship(String userId, String friendUserId, AsyncCallback<Boolean> callback);
}