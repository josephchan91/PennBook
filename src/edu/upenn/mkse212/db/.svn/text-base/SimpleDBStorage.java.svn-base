package edu.upenn.mkse212.db;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import com.amazonaws.services.simpledb.*;
import com.amazonaws.services.simpledb.model.*;
import com.amazonaws.auth.*;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

/**
 * Amazon SimpleDB key/value storage class
 * @author zives
 * @author ahae
 * Extended to allow storage and retrieval of maps
 *  and multimaps for convenient access of SimpleDB attributes, as well as
 *  select and replace queries
 * 
 */
public class SimpleDBStorage implements IKeyValueStorage {

	AmazonSimpleDB db = null;
	boolean compress = false;
	String dbName;

	public SimpleDBStorage(String dbName, String path, String userID, String authKey, boolean compress) {
		init(dbName, path, userID, authKey);
		this.compress = compress;
	}

	@Override
	public void init(String dbNameArg, String path, String userID, String authKey) {
		db = new AmazonSimpleDBClient(new BasicAWSCredentials(userID, authKey));
		dbName = dbNameArg;

		try {
			ListDomainsResult listDomainsResult = db.listDomains();
			java.util.List<String> domainNameList  =  listDomainsResult.getDomainNames();

			if (domainNameList.contains(dbName))
				return;

			db.createDomain(new CreateDomainRequest(dbName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	//get the attributes corresponding to a given name represented as
	//a single-valued map of strings
	@Override
	public Map<String,String> getMap(String search) {
		List<Attribute> attributeList = getAttributeList(search);
		return attributesToHashMap(search,attributeList);
	}

	//provided functionality
	@Override
	public Set<String> get(String search) {
		Set<String> result = new HashSet<String>();
		List<Attribute> attributeList = getAttributeList(search);
			// Get all matches
		for (Attribute a : attributeList) {
			if (a.getName().equals("match"))
				result.add(a.getValue());
		}

		return result;
	}
	
	//method that returns SimpleDB results matching a key to a list of SimpleDB attribute objects
	protected List<Attribute> getAttributeList(String search) {
		try {
			GetAttributesResult attribs = db.getAttributes(new GetAttributesRequest(dbName, search));
			java.util.List<Attribute> attributeList = attribs.getAttributes();
			return attributeList;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

	//does a certain key (name) exist in the DB?
	@Override
	public boolean exists(String search) {
		return get(search).size() > 0;
	}

	//convert a key value pair to an attribute list for use with AWS API
	protected List<ReplaceableAttribute> replaceAttributeList(String attribute, String value) {
		ReplaceableAttribute attr0 = new ReplaceableAttribute(attribute, value, false);
		attr0.setReplace(true);
		List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>(); 
		list.add(attr0);
		return list;
	}
	
	@Override
	public void replace(String keyword, String attribute, String value) {
		try {
			List<ReplaceableAttribute> list = replaceAttributeList(attribute,value);
			db.putAttributes(new PutAttributesRequest(dbName, keyword, list, new UpdateCondition()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void close() {
	}

	@Override
	public void sync() {
	}

	/*put all a hashmap of attributes into simpleDB under name keyword;
	--each key is an attribute name and the corresponding value is the attribute value*/
	@Override
	public void put(String keyword, HashMap<String, String> attributes) {
		try {
			List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>(); 
			for ( String attrName : attributes.keySet() )
			{
				ReplaceableAttribute attr = new ReplaceableAttribute(attrName,
						attributes.get(attrName), false);
				list.add(attr);//put attributes of hashmap into list of AWS API attributes
			}
			db.putAttributes(new PutAttributesRequest(dbName, keyword, list, new UpdateCondition()));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/* select all sets of attributes that match the select expression
	 * 
	 * (non-Javadoc)
	 * @see edu.upenn.mkse212.db.IKeyValueStorage#select(java.lang.String)
	 */
	@Override
	public List<Map<String, String>> select(String query) {
		List<Map<String,String>> attributeMaps = new ArrayList<Map<String,String>>();
		SelectResult result = db.select(new SelectRequest(query));
		List<Item> simpleDbItems = result.getItems();
		HashMap<String,String> itemMap;
		for (Item item : simpleDbItems) {
			itemMap = attributesToHashMap(item.getName(),item.getAttributes());
			attributeMaps.add(itemMap);
		}	
		return attributeMaps;
	}
	
	/*similar to above, but returns list of multimaps to represent multiple attributes with the same attribute name in SimpleDB
	 * (non-Javadoc)
	 * @see edu.upenn.mkse212.db.IKeyValueStorage#selectMulti(java.lang.String)
	 */
	@Override
	public List<ListMultimap<String,String>> selectMulti(String query) {
		return itemsToListOfMultiMaps(selectItems(query));
	}
	
	
	/*convert list of items to list of multimaps for more convenient retrieval*/
	private List<ListMultimap<String, String>> itemsToListOfMultiMaps(List<Item> items) {
		ListMultimap<String,String> itemMap;
		List<ListMultimap<String,String>> attributeMaps = new ArrayList<ListMultimap<String,String>>();
		for (Item item : items) {
			itemMap = attributesToMultimap(item.getName(), item.getAttributes());
			attributeMaps.add(itemMap);
		}
		return attributeMaps;
	}

	//get a multimap of attribute values corresponding to a key
	@Override
	public ListMultimap<String,String> getMulti(String search) {
		List<Attribute> attributeList = getAttributeList(search);
		return attributesToMultimap(search, attributeList);
	}
	
	public void delete(String key)
	{
		db.deleteAttributes(new DeleteAttributesRequest(this.dbName,key));
	}
	

	//get the list of Items matching a select epxression
	private List<Item> selectItems(String query)
	{
		return db.select(new SelectRequest(query)).getItems();
	}
	
	/*Convert AWS attributes format to hashmap*/
	private HashMap<String,String> attributesToHashMap(String name, List<Attribute> attributes) {
		HashMap<String,String> itemMap = new HashMap<String,String>();
		for (Attribute a : attributes)
		{
			itemMap.put(a.getName(), a.getValue());
		}
		itemMap.put(KEYWORD_ATTR,name);
		return itemMap;
	}
	
	/*Convert AWS attributes format to multimap*/
	private ListMultimap<String,String> attributesToMultimap(String name, List<Attribute> attributes) {
		ListMultimap<String,String> itemMap = ArrayListMultimap.create();
		for (Attribute a : attributes)
		{
			itemMap.put(a.getName(), a.getValue());
		}
		itemMap.put(KEYWORD_ATTR,name);
		return itemMap;
	}
	
	
	//put all key-values in a multimap into SimpleDB attributes
	@Override
	public void put(String keyword, ListMultimap<String,String> attributes)
	{
		List<ReplaceableAttribute> list = new ArrayList<ReplaceableAttribute>(); 
		for (String attrName : attributes.keySet()) {
			for ( String value : attributes.get(attrName) ) 
			{
				ReplaceableAttribute attr = new ReplaceableAttribute(attrName,
						value, false);
				list.add(attr);
			}
		}
		db.putAttributes(new PutAttributesRequest(dbName, keyword, list, new UpdateCondition()));
	}

	/*replaces a value of attribute name expectedAttribute equal to expectedValue
	with value*/
	@Override
	public void replace(String expectedAttribute, String expectedValue,
			String attribute, String value) {
			try {
				List<ReplaceableAttribute> list = replaceAttributeList(attribute,value);
				db.putAttributes(new PutAttributesRequest(dbName, null, list, new UpdateCondition()));
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	
	//select method that allows paging, returning the nextToken and also accepting nextTokens for getting the next page of results;
	//returns attributes as multimaps
	public Object[] selectMultiPage(String query, String nextToken) {
		SelectRequest request = new SelectRequest(query);
		request.setNextToken(nextToken);
		SelectResult results = db.select(request);
		List<ListMultimap<String,String>> list = itemsToListOfMultiMaps(results.getItems());
		String newToken = results.getNextToken();
		Object[] returnVals = new Object[2];
		returnVals[0] = list;
		returnVals[1] = newToken;
		return returnVals;
	}
}
