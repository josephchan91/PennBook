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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {
	public void createUser(String firstName, String lastName, 
			String email, String password, String birthday, AsyncCallback<HashMap<String,String>> callback);
	
	/*
	 * Returns user matching email and password
	 */
	public void login(String email, String password, AsyncCallback<Map<String,ArrayList<String>>> callback);
	
	public void get(String userId, AsyncCallback<Map<String,ArrayList<String>>> callback);
	
	public void getPublicDetails(String userId, AsyncCallback<Map<String,String>> callback);
	
	public void emailExists(String email, AsyncCallback<Boolean> callback);
	
	public void setInterests(String userId, String userName, String userPicURL, String interest, AsyncCallback<Void> callback);
	
	public void setHometown(String userId, String userName, String userPicURL, String hometown, AsyncCallback<Void> callback);
	
	public void setAffiliations(String userId, String userName, String userPicURL, String affiliation, AsyncCallback<Void> callback);
	
	public void setBirthday(String userId, long birthdayTime, AsyncCallback<Void> callback);
	
	public void setPictureURL(String userId, String userName, String pictureURL, AsyncCallback<Void> callback);
	
	public void getPictureURL(String userId, AsyncCallback<String> callback);
	
	public void getPics(AsyncCallback<Map<String,String>> callback);
	
	public void getByAffiliation(String affiliation, AsyncCallback<List<Map<String, ArrayList<String>>>> callback);
	
	public void search(String term, AsyncCallback<List<Map<String, ArrayList<String>>>> callback);
}
