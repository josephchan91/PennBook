package edu.upenn.mkse212.pennbook.server;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.db.KeyValueStoreFactory;
import edu.upenn.mkse212.db.Settings;
import edu.upenn.mkse212.pennbook.client.RecommendationService;

public class RecommendationServiceImpl extends RemoteServiceServlet implements RecommendationService {
	public static final String DB_DOMAIN = "pennbook_recommendations";
	public static final String RECOMMENDATIONS_ATTR = "recommendations";
	
	private IKeyValueStorage store;
	public RecommendationServiceImpl() {
		super();
		store = KeyValueStoreFactory.getKeyValueStore(Settings.storeType, DB_DOMAIN, Settings.path, Settings.userId, Settings.authKey, false);
	}
	
	@Override
	public ArrayList<Map<String, String>> getRecommendations(String userId) {
		ArrayList<Map<String, String>> recReturn =  new ArrayList<Map<String, String>>();
		Map<String,String> rec = store.getMap(userId);
		String recString = rec.get(RECOMMENDATIONS_ATTR);
		String[] recs = recString.split(",");
		UserServiceImpl userServ = UserServiceImpl.getServerInstance();
		for (int i=0; i<recs.length; i++)
		{
			Map<String, ArrayList<String>> user = userServ.get(recs[i]);
			Map<String,String> recItem = new HashMap<String,String>();
			recItem.put("id", recs[i]);
			recItem.put("name",user.get(UserServiceImpl.FIRST_NAME_ATTR).get(0)+" "+user.get(UserServiceImpl.LAST_NAME_ATTR).get(0));
			recReturn.add(recItem);
		}
		return recReturn;
	}
	
	/*set the ordered, comma-separated string of userIds recommended
	 * for a given user
	 */
	public void setRecommendations(String userId, String recString)
	{
		HashMap<String,String> attrs = new HashMap<String,String>();
		attrs.put(RECOMMENDATIONS_ATTR,recString);
		store.put(userId, attrs);
	}

}
