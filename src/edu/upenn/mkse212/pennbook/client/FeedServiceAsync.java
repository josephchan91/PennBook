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

import java.util.ArrayList;
import java.util.Map;

import edu.upenn.mkse212.pennbook.shared.FeedItemType;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FeedServiceAsync {
	public void  getFeedItems(String userId, AsyncCallback<ArrayList<Map<String,ArrayList<String>>>> callback);

	public void postFeedItem(String message, String posterId, String receiverId,
			String posterName, String receiverName, FeedItemType type, AsyncCallback<Void> callback);

	public void getWall(String userId, AsyncCallback<ArrayList<Map<String,ArrayList<String>>>> callback);
	public void getNotifications(String userId, AsyncCallback<ArrayList<Map<String,ArrayList<String>>>> callback);
	
	public void addComment(String postId, String posterId, String posterName, String message, AsyncCallback<String> callback);

}
