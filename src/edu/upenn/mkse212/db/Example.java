package edu.upenn.mkse212.db;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import edu.upenn.mkse212.db.KeyValueStoreFactory.STORETYPE;
import edu.upenn.mkse212.pennbook.client.FriendshipService;
import edu.upenn.mkse212.pennbook.client.RecommendationService;
import edu.upenn.mkse212.pennbook.client.UserService;
import edu.upenn.mkse212.pennbook.server.FeedServiceImpl;
import edu.upenn.mkse212.pennbook.server.FriendRequestServiceImpl;
import edu.upenn.mkse212.pennbook.server.FriendshipServiceImpl;
import edu.upenn.mkse212.pennbook.server.RecommendationServiceImpl;
import edu.upenn.mkse212.pennbook.server.UserServiceImpl;

/*
 * Just trying out the db and service interfaces
 */
public class Example {
	static String A="57c0f494-cf7a-4d1c-9825-8334e616cda6";
	static String B="8aa0bba4-3b22-4224-8d6a-330a3e486881";
	static String C="5ae4f446-fbe2-49db-a36f-887a4f82817e";
	static String D="55175388-4b79-4277-803c-f8445f0bf73e";
	static String E="407bc281-e609-4d5b-aa82-20fb65701644";
	
	public static void printListMap(Map<String,ArrayList<String>> map)
	{
		System.out.println("{");
		for (String k : map.keySet())
		{
			System.out.print(k+":");
			for (String v : map.get(k))
				System.out.print(v+",");
			System.out.println();
		}
		System.out.println("}");
	}
	
	public static void printMap(Map<String,List<String>> map)
	{
		System.out.println("{");
		for (String k : map.keySet())
		{
			System.out.print(k+":"+map.get(k));
		}
		System.out.println("}");
	}
	
	public static void users()
	{
		UserServiceImpl userServ = new UserServiceImpl();
		//Map<String,String> u1 = userServ.createUser("Harald","Hrargaard", "abc@mailinator.com", "12345");
		//System.out.println(u1.get(IKeyValueStorage.KEYWORD_ATTR));
		
		Map<String, ArrayList<String>> u = userServ.login("joechan@test.com", "12345");
		//Map<String, List<String>> u = userServ.get("89a359b6-27b9-44d5-9cb9-6faec0f3a4ed");
		//printListMap(u);
		System.out.println(u.toString());
		System.out.print(userServ.emailExists("joechan@test.com"));

	}
	
	
	public static void friendships()
	{
		//System.out.println(u)
		FriendshipServiceImpl friendshipServ = new FriendshipServiceImpl();
		//friendshipServ.addFriend("080e19b1-a979-46d8-8b09-59de9c35576e", "52c02f33-77ce-433e-95f7-c5a8ae296352");
		//System.out.println(friendshipServ.getFriendships("89a359b6-27b9-44d5-9cb9-6faec0f3a4ed") );
		//List<String> friends = friendshipServ.getFriendIds("080e19b1-a979-46d8-8b09-59de9c35576e");
		//System.out.println(friends.toString());
		//System.out.println("Size: "+friends.size());
	}
	
	public static void feed()
	{
		FeedServiceImpl feedServ = new FeedServiceImpl();
		//feedServ.postFeedItem("I support voluntary human extinction.", "8341759a-3a87-4013-ad4e-a56b183f4f2d", "89a359b6-27b9-44d5-9cb9-6faec0f3a4ed","John Doe", "Askldf Asdfl", FeedItemType.POST);
		//System.out.println(feedServ.getFeedItems("cd807083-618d-49c3-8ee6-b394cf8776cb"));
		for (Map<String,ArrayList<String>> m : feedServ.getFeedItems("cd807083-618d-49c3-8ee6-b394cf8776cb"))
			printListMap(m);
		//String comment = feedServ.addComment("b594832d-098e-4b12-abc8-ab2740297fad", "8341759a-3a87-4013-ad4e-a56b183f4f2d", "John Doe", "Enkidu is better than Quetzacoatl.");
		//System.out.println(comment);
	}
	
	public static void friendRequest()
	{
		FriendRequestServiceImpl frServ = new FriendRequestServiceImpl();
		frServ.createFriendRequest("d139b92e-d59f-4751-adfc-9d8939b4eb73", "ea616051-f527-4afd-bce5-5418bbecaa74", "Joseph Chan");
		//frServ.createFriendRequest("89a359b6-27b9-44d5-9cb9-6faec0f3a4ed", "cd807083-618d-49c3-8ee6-b394cf8776cb", "Askldf Asdfl");
		
		//List<Map<String, String>> reqs = frServ.getFriendRequests("cd807083-618d-49c3-8ee6-b394cf8776cb");
		//System.out.println(reqs);
		//System.out.println(reqs.get(1).getId());
		
		//frServ.deleteFriendRequest("a1a086fd-fc64-403c-b248-f36cac91ea8e");
		
		//frServ.getFriendRequests();
	}
	
	public static void email()
	{
		UserServiceImpl userServ = new UserServiceImpl();
		System.out.println(userServ.emailExists("123@mailinator.com"));
		System.out.println(userServ.emailExists("obama@whitehouse.gov"));
	}
	
	public static void wall()
	{
		FeedServiceImpl feedServ = new FeedServiceImpl();
		System.out.println(feedServ.getWall("89a359b6-27b9-44d5-9cb9-6faec0f3a4ed"));
		System.out.println(feedServ.getNotifications("89a359b6-27b9-44d5-9cb9-6faec0f3a4ed"));
		
	}
	
	public static void createTestNetwork() {
		UserServiceImpl userServ = new UserServiceImpl();
		
		//Map<String,String> u1 = userServ.createUser("Xiao","Li", "xiaoli@example.com", "12345");
		//Map<String,String> u2 = userServ.createUser("Joseph","Chan", "josephchan@example.com", "12345");
		
	}
	
	public static void userOptional() {
		UserService userServ = new UserServiceImpl();
		//userServ.setAffiliation("ea616051-f527-4afd-bce5-5418bbecaa74", "UPenn");
		userServ.setBirthday("ea616051-f527-4afd-bce5-5418bbecaa74", 675388800);
		//userServ.setHometown("ea616051-f527-4afd-bce5-5418bbecaa74", "Lanzhou");
		System.out.println(userServ.get("ea616051-f527-4afd-bce5-5418bbecaa74").get(UserServiceImpl.AFFILIATION_ATTR).get(0));
	}

	public static void moreUsers() {
		UserService userServ = new UserServiceImpl();
		/*
		
		String[] names = {"A","B","C","D","E"};
		
		for (int i=0; i<names.length; i++) {
			Map<String,String> u1 = userServ.createUser(names[i], names[i], names[i]+"@test.com", "123");
			System.out.println(u1.keySet());
			System.out.println(names[i]+":"+u1.get(IKeyValueStorage.KEYWORD_ATTR) );
		}
		
		*/

		

		FriendshipService friendServ = new FriendshipServiceImpl();
		
		userServ.setAffiliations(E, "E E", "","Fremont University" );
		userServ.setAffiliations(A, "A A", "","Fremont University" );
		userServ.setAffiliations(C, "C C", "","Fremont University" );
		
		

		
		friendServ.addFriend(A, B, "A A", "A A", "Fremont University");
		friendServ.addFriend(A, C, "A A", "A A", "Fremont University");
		friendServ.addFriend(B, D, "B B", "D D", "Fremont University");
		friendServ.addFriend(D, E, "D D", "E E", "Fremont University");
		friendServ.addFriend(C, B, "C C", "B B", "Fremont University");
		
		
		
		System.out.println(friendServ.getNetwork(A));
	}
	
	public static void search(String term)
	{
		UserService userServ = new UserServiceImpl();
		for (Map<String,ArrayList<String>>result : userServ.search(term))
			printListMap(result);
	}
	
	public static void online()
	{
		FriendshipServiceImpl friendServ = new FriendshipServiceImpl();
		friendServ.getOnlineFriendships(A);
		List<Map<String,String>> onlineFr = friendServ.getOnlineFriendships(B);
		
		for (Map fr: onlineFr)
			printMap(fr);
	}
	
	public static void getFriendByName()
	{
		FriendshipServiceImpl friendServ = new FriendshipServiceImpl();
		System.out.println(friendServ.getFriendByName(A, "C C"));
		
	}
	
	public static void deleteFriendship()
	{
		FriendshipServiceImpl friendServ = new FriendshipServiceImpl();
		System.out.println(friendServ.deleteFriendship(A, "5ae4f446-fbe2-49db-a36f-887a4f82817e"));
	}
	
	public static void getRecs()
	{
		RecommendationService recServ = new RecommendationServiceImpl();
		List<Map<String,String>> recs = recServ.getRecommendations("user:8e8cf89a-c6e9-4acc-8e5b-5b1da8eb3f8c");
		System.out.println(recs.get(0).get("name"));
	}
	
	public static void main(String[] args) {
		//friendships();
		//users();
		//feed();
		//friendRequest();
		//email();
		//wall();
		//createTestNetwork();
		//userOptional();
		//moreUsers();
		//search("l xiao");
		//online();
		//getFriendByName();
		//
		deleteFriendship();
		getRecs();
	}

}
