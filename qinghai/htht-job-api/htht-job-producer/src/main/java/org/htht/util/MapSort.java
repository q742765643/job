package org.htht.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * 
 * @author heshibing
 * @data   2016-8-19
 */
public class MapSort{

	/**
	 * 求Map<K,V>中Key(键)的最小值
	 * @param map
	 * @return
	*/
	 public static Object getMinKey(Map<Integer, Integer> map) {
	 if (map == null) return null;
	 Set<Integer> set = map.keySet();
	 Object[] obj = set.toArray();
	 Arrays.sort(obj);
	 return obj[0];
	 }
		/**
		 * 求Map<K,V>中Key(键)的最小值
		 * @param map
		 * @return
		*/
	 public static Object getMaxKey(Map<Integer, Object> map) {
		 if (map == null) return null;
		 Set<Integer> set = map.keySet();
		 Object[] obj = set.toArray();
		 Arrays.sort(obj);
		 return obj[obj.length-1];
	 }
	 /**
	 * 求Map<K,V>中Value(值)的最小值
	 * @param map
	 * @return
	 */
	 public static Object getMinValue(Map<String, Integer> map) {
	 if (map == null) return null;
	 Collection<Integer> c = map.values();
	 Object[] obj = c.toArray();
	 Arrays.sort(obj);
	 return obj[0];
	 }
	 /**
	  * 
	  * @param map
	  * @return
	  */
	 public static Object getMinsValue(Map<Integer, Integer> map) {
		 if (map == null) return null;
		 Collection<Integer> c = map.values();
		 Object[] obj = c.toArray();
		 Arrays.sort(obj);
		 return obj[0];
		 }
	 
	 public static Map<Integer,Object> MapStringToInteger(Map<String, Object> map) {
		 if (map == null) return null;
		 
		 Map<Integer, Object>  maps=new HashMap<Integer,Object>();
			Set<String> set =map.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()) {  
				String  key=(String)it.next();
				Integer intKey = Integer.valueOf(key);
				maps.put(intKey,map.get(key));
			}
		return  maps;
		 }
	
}
