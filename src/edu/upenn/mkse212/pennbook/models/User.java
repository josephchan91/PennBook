package edu.upenn.mkse212.pennbook.models;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;


import edu.upenn.mkse212.db.IKeyValueStorage;
import edu.upenn.mkse212.pennbook.server.UserServiceImpl;

public class User {
	//model class for user; only ever used server-side due to GWT serialization hassles
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String affiliation;
	private String birthday;
	private String hometown;
	private List<String> interests;//TO DO
	
	public User(Map<String,ArrayList<String>> attrs)
	{
		this.id = attrs.get(IKeyValueStorage.KEYWORD_ATTR).get(0);
		this.firstName = attrs.get(UserServiceImpl.FIRST_NAME_ATTR).get(0);
		this.lastName = attrs.get(UserServiceImpl.LAST_NAME_ATTR).get(0);
		this.email = attrs.get(UserServiceImpl.EMAIL_ATTR).get(0);
		if (attrs.containsKey(UserServiceImpl.HOMETOWN_ATTR))
			this.hometown = attrs.get(UserServiceImpl.HOMETOWN_ATTR).get(0);
		if (attrs.containsKey(UserServiceImpl.BIRTHDAY_ATTR))
			this.birthday = attrs.get(UserServiceImpl.BIRTHDAY_ATTR).get(0); 
		if (attrs.containsKey(UserServiceImpl.AFFILIATION_ATTR))
			this.affiliation = attrs.get(UserServiceImpl.AFFILIATION_ATTR).get(0);
		else
			this.affiliation = "";
		
		if (attrs.containsKey(UserServiceImpl.INTEREST_ATTR))
			this.interests = attrs.get(UserServiceImpl.INTEREST_ATTR);
		else
			this.interests = new ArrayList<String>();
	}
	
	public String getFirstName() 
	{
		return firstName;
	}
	
	public String getLastName()
	{
		return lastName;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getHometown()
	{
		return hometown;
	}
	
	public String getBirthday()
	{
		return birthday;
	}
	
	public String affiliation()
	{
		return affiliation;
	}

	public String getId() {
		return id;
	}
	
	public List<String> getInterests() {
		return interests;
	}

	public String getAffiliation() {
		return affiliation;
	}

}
