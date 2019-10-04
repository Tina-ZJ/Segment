package com.jz.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SortMap 
{
	public static <K,V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> oriMap)
	{
		Map<K, V> sortedMap = new LinkedHashMap<K, V>();
		
		if (oriMap != null && !oriMap.isEmpty())
		{
			List<Map.Entry<K, V>> entryList = new ArrayList<Map.Entry<K, V>>(oriMap.entrySet());
			
			Collections.sort(entryList,	new Comparator<Map.Entry<K, V>>(){
				public int compare(Entry<K, V> entry1,	Entry<K, V> entry2)
				{
					return entry2.getValue().compareTo(entry1.getValue());						
				}
			});
			
			Iterator<Map.Entry<K, V>> iter = entryList.iterator();
			
			Map.Entry<K, V> tmpEntry = null;
			
			while (iter.hasNext())
			{
				tmpEntry = iter.next();
				sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
			}
		}
		return sortedMap;
	}
}
