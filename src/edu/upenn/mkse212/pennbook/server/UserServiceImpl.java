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

import java.util.Map;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.db.KeyValueStoreFactory;
import edu.upenn.mkse212.db.MapUtil;
import edu.upenn.mkse212.db.Settings;
import edu.upenn.mkse212.pennbook.client.UserService;
import edu.upenn.mkse212.pennbook.shared.Credentials;
import edu.upenn.mkse212.pennbook.shared.FeedItemType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.ListMultimap;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class UserServiceImpl extends RemoteServiceServlet implements UserService {
	//firstName, lastName, email(unique), affiliation, interests, 
	//birthday, relationship, hometown, recommendation, password hash
	public static final String USER_ID_ATTR="userId";
	public static final String FIRST_NAME_ATTR="firstName";
	public static final String LAST_NAME_ATTR="lastName";
	public static final String EMAIL_ATTR="email";
	public static final String AFFILIATION_ATTR="affiliation";
	public static final String BIRTHDAY_ATTR="birthday";
	public static final String HOMETOWN_ATTR="hometown";
	public static final String PICTURE_ATTR="pictureURL";
	public static final String PASSWORD_HASH_ATTR="passwordHash";
	public static final String SALT_ATTR = "salt";
	public static final String INTEREST_ATTR = "interest";
	public static final String LAST_PRESENT_ATTR = "lastPresent";

	public static final String DB_DOMAIN = "pennbook_users";
	
	private IKeyValueStorage store;
	private ObjectMapper mapper;
	
	private static UserServiceImpl instance;
	
	public UserServiceImpl() {
		super();
		store = KeyValueStoreFactory.getKeyValueStore(Settings.storeType, DB_DOMAIN, Settings.path, Settings.userId, Settings.authKey, false);
		mapper = new ObjectMapper();
		mapper.registerModule(new GuavaModule());
	}
	
	public static UserServiceImpl getServerInstance(){
		if (instance == null) {
			instance = new UserServiceImpl();
		}
		return instance;
	}
	
	/*Set/get various user attributes from DB*/
	
	@Override
	public HashMap<String,String> createUser(String firstName, String lastName, String email,
			String password, String birthday) {
		
		if (emailExists(email)) {
			return null;
		}
		
		Credentials credentials;
		HashMap<String,String> attrs = new HashMap<String,String>();
		
		try {
			credentials = new Credentials(password);
			attrs.put(PICTURE_ATTR, "");
			attrs.put(BIRTHDAY_ATTR, birthday);
			attrs.put(FIRST_NAME_ATTR, firstName);
			attrs.put(LAST_NAME_ATTR, lastName);
			attrs.put(EMAIL_ATTR, email);
			attrs.put(HOMETOWN_ATTR, "");
			attrs.put(AFFILIATION_ATTR, "");
			attrs.put(INTEREST_ATTR, "");
			attrs.put(PASSWORD_HASH_ATTR,credentials.getPasswordHash());
			attrs.put(SALT_ATTR, credentials.getSalt());
			
			String userId = credentials.getUserId();
			store.put(userId, attrs);
			attrs.put(IKeyValueStorage.KEYWORD_ATTR, userId);
			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		return attrs;
	}
	
	// Get all user pics
	@Override
	public Map<String,String> getPics() {
		String query = String.format("SELECT * FROM %s",DB_DOMAIN);
		List<Map<String,ArrayList<String>>> users = getMultiResults(query);
		Map<String,String> pics = new HashMap<String,String>();
		for (Map<String,ArrayList<String>> u : users) {
			String userId = u.get(IKeyValueStorage.KEYWORD_ATTR).get(0);
			pics.put(userId, getPictureURL(userId));
		}
		return pics;
	}
	
	public List<Map<String, ArrayList<String>>> getByAffiliation(String affiliation) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'",DB_DOMAIN, AFFILIATION_ATTR, affiliation);
		return getMultiResults(query);
	}

	List<Map<String, ArrayList<String>>> getAll() {
		String query = String.format("SELECT * FROM %s'",DB_DOMAIN);
		return getMultiResults(query);
	}
	
	private ArrayList<Map<String, ArrayList<String>>> getMultiResults(String query) {
		List<ListMultimap<String, String>>  results = store.selectMulti(query);
		ArrayList<Map<String, ArrayList<String>>> convResults = new ArrayList<Map<String,ArrayList<String>>>();
		for (ListMultimap<String, String> multimap : results) 
			convResults.add( MapUtil.convertMultimap(multimap) );
		return convResults;
	}

	// Logs the user in, returning his profile info
	@Override
	public Map<String,ArrayList<String>> login(String email, String password) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'",DB_DOMAIN,EMAIL_ATTR,email);
		String userAsJson = "";
		List<ListMultimap<String, String>> userAttrs = store.selectMulti(query);
		Map<String,ArrayList<String>> convAttrs = null;
		for (ListMultimap<String,String> userMap : userAttrs)
		{
			String salt = userMap.get(SALT_ATTR).get(0);
			String receivedPasswordHash;
			try {
				receivedPasswordHash = Credentials.hashPassword(password, salt);
				String dbPasswordHash = userMap.get(PASSWORD_HASH_ATTR).get(0);
				if (dbPasswordHash.equals(receivedPasswordHash)) {
					convAttrs = MapUtil.convertMultimap(userMap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return convAttrs;
	}
	
	ListMultimap<String,String> getMultimap(String userId) {
		return store.getMulti(userId);
	}
	
	@Override
	public Map<String, ArrayList<String>> get(String userId) {
		ListMultimap<String,String> lm = getMultimap(userId);
		
		return MapUtil.convertMultimap(lm);
	}
	
	// Get info for network visualization, to see info of nodes
	@Override
	public HashMap<String, String> getPublicDetails(String userId) {
		HashMap<String,String> publicDetails = new HashMap<String,String>();
		ListMultimap<String,String> lm = getMultimap(userId);
		Map<String,ArrayList<String>> u = MapUtil.convertMultimap(lm);
		publicDetails.put(UserServiceImpl.USER_ID_ATTR, u.get(IKeyValueStorage.KEYWORD_ATTR).get(0));
		publicDetails.put(UserServiceImpl.FIRST_NAME_ATTR, u.get(UserServiceImpl.FIRST_NAME_ATTR).get(0));
		publicDetails.put(UserServiceImpl.LAST_NAME_ATTR, u.get(UserServiceImpl.LAST_NAME_ATTR).get(0));
		publicDetails.put(UserServiceImpl.EMAIL_ATTR, u.get(UserServiceImpl.EMAIL_ATTR).get(0));
		publicDetails.put(UserServiceImpl.BIRTHDAY_ATTR, u.get(UserServiceImpl.BIRTHDAY_ATTR).get(0));
		publicDetails.put(UserServiceImpl.AFFILIATION_ATTR, u.get(UserServiceImpl.AFFILIATION_ATTR).get(0));
		publicDetails.put(UserServiceImpl.INTEREST_ATTR, u.get(UserServiceImpl.INTEREST_ATTR).get(0));
		publicDetails.put(UserServiceImpl.PICTURE_ATTR, u.get(UserServiceImpl.PICTURE_ATTR).get(0));
		return publicDetails;
	}

	// Checker for registration to make sure emails are unique
	@Override
	public boolean emailExists(String email) {
		String query = String.format("SELECT * FROM %s WHERE %s='%s'", DB_DOMAIN, EMAIL_ATTR,email );
		int resultCount = store.select(query).size();
		if (resultCount>0)
			return true;
		else
			return false;
	}

	@Override
	public void setInterests(String userId, String userName, String userPicURL, String interest) {
		store.replace(userId, INTEREST_ATTR, interest);
		FeedServiceImpl feedService = FeedServiceImpl.getServerInstance();
		feedService.postFeedItem("", userId, "", userName, "", FeedItemType.PROFILE_UPDATE);
	}

	@Override
	public void setHometown(String userId, String userName, String userPicURL, String hometown) {
		store.replace(userId, HOMETOWN_ATTR, hometown);
		FeedServiceImpl feedService = FeedServiceImpl.getServerInstance();
		feedService.postFeedItem("", userId, "", userName, "", FeedItemType.PROFILE_UPDATE);
	}

	@Override
	public void setAffiliations(String userId, String userName, String userPicURL, String affiliation) {
		store.replace(userId, AFFILIATION_ATTR, affiliation);
		FeedServiceImpl feedService = FeedServiceImpl.getServerInstance();
		feedService.postFeedItem("", userId, "", userName, "", FeedItemType.PROFILE_UPDATE);
	}

	@Override
	public void setBirthday(String userId, long birthdayTime) {
		store.replace(userId, BIRTHDAY_ATTR, birthdayTime+"");
	}

	@Override
	public void setPictureURL(String userId, String userName, String pictureURL) {
		store.replace(userId, PICTURE_ATTR, pictureURL);
		FeedServiceImpl feedService = FeedServiceImpl.getServerInstance();
		feedService.postFeedItem("", userId, "", userName, "", FeedItemType.PROFILE_UPDATE);
	}

	@Override
	public String getPictureURL(String userId) {
		ListMultimap<String,String> results = getMultimap(userId);
		Map<String,ArrayList<String>> m = MapUtil.convertMultimap(results);
		if (m.get(PICTURE_ATTR) != null) {
			return m.get(PICTURE_ATTR).get(0);
		}
		else {
			return "";
		}
	}

	/*Search for users whose first or last names match
	 * (non-Javadoc)
	 * @see edu.upenn.mkse212.pennbook.client.UserService#search(java.lang.String)
	 */
	@Override
	public List<Map<String, ArrayList<String>>> search(String term) {
		String likeClauses = likeClauses(term);
		String query = String.format("SELECT * FROM %s WHERE %s",DB_DOMAIN,likeClauses);
		return getMultiResults(query);
	}
	
	/*
	 * Create the "LIKE " clauses in select query to match
	 * first name attributes and lowercase/uppercase forms for the term
	 */
	private String likeClauses(String term) {
		final String TEMPLATE = "%s LIKE '%s%%'";
		String[] subterms = term.split(" ");
		ArrayList<String> variants = new ArrayList<String>();  
		for (int i=0; i<subterms.length && i<4; i++) {
			String[] clauses = new String[4];
			//lowercase and capitalized (but not all uppercase) forms for first name and last name
			clauses[0] = String.format(TEMPLATE,LAST_NAME_ATTR,StringUtils.capitalize(subterms[i]));
			clauses[1] = String.format(TEMPLATE,LAST_NAME_ATTR,StringUtils.lowerCase(subterms[i]));
			clauses[2] = String.format(TEMPLATE,FIRST_NAME_ATTR,StringUtils.capitalize(subterms[i]));
			clauses[3] = String.format(TEMPLATE,FIRST_NAME_ATTR,StringUtils.lowerCase(subterms[i]));
			variants.add("("+StringUtils.join(clauses," OR ") +")");
		}
		return StringUtils.join(variants," AND ");
	}
	
	/*set last time a user showed activity (polling activity)
	 * used for online status*/
	void setUserPresence(String userId) {
		store.replace(userId, LAST_PRESENT_ATTR, ""+System.currentTimeMillis());
	}
	
	HashSet<String> getPresentUsers() {
		String query = String.format("SELECT %s FROM %s WHERE %s > '%d'",USER_ID_ATTR,DB_DOMAIN,LAST_PRESENT_ATTR,System.currentTimeMillis()-20*1000);	
		HashSet<String> onlineUserIds = new HashSet<String>();
		List<Map<String, String>> onlineUserMaps = store.select(query);
		for (Map<String,String> user : onlineUserMaps) {
			onlineUserIds.add(user.get(IKeyValueStorage.KEYWORD_ATTR));
		}
		return onlineUserIds;
	}
		
	
}
