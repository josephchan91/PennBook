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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("UserService")
/*Create, read, update, and delete user data*/
public interface UserService extends RemoteService {
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static UserServiceAsync instance;
		public static UserServiceAsync getInstance(){
			if (instance == null) {
				instance = GWT.create(UserService.class);
			}
			return instance;
		}
	}
	
	public HashMap<String,String> createUser(String firstName, String lastName, 
			String email, String password, String birthday);
	
	/*
	 * Returns user matching email and password
	 */
	public Map<String,ArrayList<String>> login(String email, String password);
	
	public Map<String,ArrayList<String>> get(String userId);
	
	public Map<String,String> getPublicDetails(String userId);
	
	public boolean emailExists(String email);
	
	public void setInterests(String userId, String userName, String userPicURL, String interest);
	
	public void setHometown(String userId, String userName, String userPicURL, String hometown);
	
	public void setAffiliations(String userId, String userName, String userPicURL, String affiliation);
	
	public void setBirthday(String userId, long birthdayTime);
	
	public void setPictureURL(String userId, String userName, String pictureURL);
	
	public String getPictureURL(String userId);
	
	public Map<String,String> getPics();
	
	public List<Map<String, ArrayList<String>>> getByAffiliation(String affiliation);
	
	public List<Map<String, ArrayList<String>>> search(String term);
}
