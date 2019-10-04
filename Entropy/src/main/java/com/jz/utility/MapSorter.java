package com.jz.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapSorter 
{
	public static <K,V extends Comparable<V>> List<Entry<K, V>> sortByValue(Map<K, V> oriMap)
	{
		if (oriMap != null && !oriMap.isEmpty())
		{
			List<Map.Entry<K, V>> entryList = new ArrayList<Map.Entry<K, V>>(oriMap.entrySet());
			
			Collections.sort(entryList,	new Comparator<Map.Entry<K, V>>(){
					public int compare(Entry<K, V> entry1,	Entry<K, V> entry2)
					{
						return entry2.getValue().compareTo(entry1.getValue());						
					}
				}
			);

			return entryList;
		}
		
		return null;
	}
}
