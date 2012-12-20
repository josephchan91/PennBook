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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.upenn.mkse212.pennbook.shared.FeedItemType;

@RemoteServiceRelativePath("FeedService")
public interface FeedService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static FeedServiceAsync instance;
		public static FeedServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(FeedService.class);
			}
			return instance;
		}
	}
	
	public ArrayList<Map<String,ArrayList<String>>>  getFeedItems(String userId);

	public void postFeedItem(String message, String posterId, String receiverId,
			String posterName, String receiverName, FeedItemType type);

	public ArrayList<Map<String,ArrayList<String>>> getWall(String userId);
	public ArrayList<Map<String,ArrayList<String>>> getNotifications(String userId);
	
	public String addComment(String postId, String posterId, String posterName, String message);

}
