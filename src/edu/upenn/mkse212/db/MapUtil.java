package edu.upenn.mkse212.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;

/*Conversion between multimaps and maps with arraylist values,
 * since the multimaps cannot be serialized by GWT by default*/
public class MapUtil {
	
	public static Map<String, ArrayList<String>> convertMultimap(
			ListMultimap<String, String> multimap) {
		Map<String, ArrayList<String>> newMap = new HashMap<String, ArrayList<String>>();
		for (String key: multimap.keySet()) {
			List<String> list = multimap.get(key);
 			newMap.put(key,new ArrayList<String>(list));
		}
		return newMap;
	}
}
