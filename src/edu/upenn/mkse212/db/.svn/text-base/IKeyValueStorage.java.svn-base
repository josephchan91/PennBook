package edu.upenn.mkse212.db;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ListMultimap;

/**
 * Extended IKeyValueStorage for more flexible database access, including storing/retrieving maps or multimaps
 * 
 * @author zives
 *
 */
public interface IKeyValueStorage {
	public static final String KEYWORD_ATTR = "keyword";

	/**
	 * Initialize storage system
	 * @param dbName
	 */
	public void init(String dbName, String path, String userID, String authKey);
	
	/**
	 * Get result set by key
	 * @param search
	 * @return
	 */
	public Set<String> get(String search);

	public Map<String,String> getMap(String search);
	
	/**
	 * Test if search key has a match
	 * 
	 * @param search
	 * @return
	 */
	public boolean exists(String search);
	
	/**
	 * Put/replace a single attribute for a key
	 * @param keyword
	 * @param category
	 */
	public void replace(String keyword, String attribute, String value);
	
	/**
	 * Shut down storage system
	 */
	public void close();
	
	public void sync();
	
	/**
	 * Put key/value pair with multiple attributes
	 * @param keyword
	 * @param attributes
	 */
	public void put(String keyword, HashMap<String,String> attributes);
	
	/**
	 * Get all items that match a select query. Items are represented as string maps.
	 * @param query 
	 * @return 
	 */
	public List<Map<String,String>> select(String query);
	
	public List<ListMultimap<String,String>> selectMulti(String query);
	
	
	/**
	 * Get a multimap of attributes stored associated with a name (key).
	 * @param key
	 * @return
	 */
	public ListMultimap<String,String> getMulti(String key);
	
	public void delete(String key);

	/*
	 * Store attributes inputted as a multimap (multiple values per attribute)
	 */
	public void put(String keyword, ListMultimap<String,String> attributes);

	/*replace a matching attribute with a string*/
	public void replace(String expectedAttribute, String expectedValue, String attribute, String value);

}
