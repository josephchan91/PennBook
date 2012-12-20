package edu.upenn.mkse212.db;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.ListMultimap;

import edu.upenn.mkse212.pennbook.server.FriendshipServiceImpl;
import edu.upenn.mkse212.pennbook.server.UserServiceImpl;

/**Dumps database contents into text file for processing with Hadoop*/
public class HadoopConvert {
	
	private String outputFile;
	private FileWriter fstream;
	private BufferedWriter out;
	
	public HadoopConvert(String output) throws IOException {
		outputFile = output;
		fstream = new FileWriter(outputFile,true);
		out = new BufferedWriter(fstream);
	}
	
	
	//write userId,interest and userId,affiliation pairs (and vice versa)
	public void write(List<ListMultimap<String,String>> userData) throws IOException {
		String aff;
		for (ListMultimap<String,String> user : userData)
		{
			String userId = user.get(IKeyValueStorage.KEYWORD_ATTR).get(0);
			List<String> interests = user.get(UserServiceImpl.INTEREST_ATTR);
			List<String> affiliations = user.get(UserServiceImpl.AFFILIATION_ATTR);
			
			if (affiliations.size()>0) {
				aff = affiliations.get(0);
				if (!aff.equals("")) {
					out.write(aff+",user:"+userId+"\n");
					out.write("user:"+userId+","+aff+"\n");
				}
			}

			for (String interest : interests) {
				System.out.println(interest+"i");
				String[] interestsParts = interest.split(",");
				if (interestsParts.length!=1) {
					for (int i=0; i< interestsParts.length;i++) {
						out.write(interestsParts[i]+",user:"+userId+"\n");
						out.write("user:"+userId+","+interestsParts[i]+"\n");
					}
				} else if (!interest.equals("")) {
					out.write(interest+",user:"+userId+"\n");
					out.write("user:"+userId+","+interest+"\n");
				}
			}
		}
	}
	//write friendships in format userId,userId
	public void writeFriendData(List<ListMultimap<String,String>> friendData) throws IOException {
		String aff;
		for (ListMultimap<String,String> friend : friendData)
		{
			String userId = friend.get(FriendshipServiceImpl.USER_ID_ATTR).get(0);
			String friendId = friend.get(FriendshipServiceImpl.FRIEND_USER_ID_ATTR).get(0);
			out.write("user:"+userId+","+"user:"+friendId+"\n");
		}
	}
	
	/*Gets all pairings of users and interests/affiliations from user domain.*/
	public void getAllInterestsAffiliations() throws IOException {
		SimpleDBStorage store = new SimpleDBStorage("pennbook_users", null,Settings.userId, Settings.authKey, false);
		Object[] result = store.selectMultiPage("SELECT * FROM pennbook_users LIMIT 200", null);
		
		List<ListMultimap<String,String>> userData = (List<ListMultimap<String, String>>) result[0];
		String nextToken = (String)result[1];
		write(userData);
		
		while (nextToken!=null) {
			result = store.selectMultiPage("SELECT * FROM pennbook_users LIMIT 1", nextToken);
			nextToken = (String)result[1];
			userData = (List<ListMultimap<String, String>>) result[0];
			write(userData);
			
			System.out.println(userData.size());
			System.out.println(nextToken);
		}
	}
	
	/*Gets all friendships from friendships domain; uses paging (next token) to handle potentially large numbers of items.*/
	public void getAllFriendships() throws IOException {
		SimpleDBStorage store = new SimpleDBStorage("pennbook_friendships", null,Settings.userId, Settings.authKey, false);
		Object[] result = store.selectMultiPage("SELECT * FROM pennbook_friendships LIMIT 1", null);
		
		List<ListMultimap<String,String>> friendData = (List<ListMultimap<String, String>>) result[0];
		String nextToken = (String)result[1];

		while (nextToken!=null) {
			result = store.selectMultiPage("SELECT * FROM pennbook_friendships LIMIT 200", nextToken);
			nextToken = (String)result[1];
			friendData = (List<ListMultimap<String, String>>) result[0];
			writeFriendData(friendData);
			
			System.out.println(friendData.size());
			System.out.println(nextToken);
		}
	}
	
	public static void main(String[] args) throws IOException
	{
		HadoopConvert hc = new HadoopConvert("/home/mkse212/pb_in/1");
		hc.getAllInterestsAffiliations();
		hc.getAllFriendships();
		hc.close();
	}
	
	public void close() throws IOException
	{
		out.close();
	}

}
