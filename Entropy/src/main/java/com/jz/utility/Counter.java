/**
 * 
 */
package com.jz.utility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Counter<T>
{
    private HashMap<T, CountInteger> hm = null;

    public Counter()
    {
        hm = new HashMap<T, CountInteger>();
    }

    public Counter(int initialCapacity)
    {
        hm = new HashMap<T, CountInteger>(initialCapacity);
    }

    public void add(T t, int n)
    {
        CountInteger newCount = new CountInteger(n);
        
        CountInteger oldCount = hm.put(t, newCount);

        if (oldCount != null)
        {
            newCount.set(oldCount.value() + n);
        }
    }

    public void add(T t)
    {
        this.add(t, 1);
    }

    public int get(T t)
    {
        CountInteger count = hm.get(t);
        if (count == null)
        {
            return 0;
        }
        else
        {
            return count.value();
        }
    }
    
    public Map<T,Integer> toMap()
    {
    	Map<T, Integer> map = new HashMap<T,Integer>();
    	
    	for(Entry<T, CountInteger> entry : hm.entrySet())
    	{
    		map.put(entry.getKey(),entry.getValue().value());
    	}
    	return map;
    }
    
    public Map<T,Integer> toTreeMap()
    {
    	Map<T, Integer> map = new TreeMap<T,Integer>();
    	
    	for(Entry<T, CountInteger> entry : hm.entrySet())
    	{
    		map.put(entry.getKey(),entry.getValue().value());
    	}
    	return map;
    }

    public int size()
    {
        return hm.size();
    }

    public void remove(T t)
    {
        hm.remove(t);
    }

    public Set<T> keySet()
    {
        return hm.keySet();
    }

    @Override
    public String toString()
    {
        Iterator<Entry<T, CountInteger>> iterator = this.hm.entrySet().iterator() ;
        StringBuilder sb = new StringBuilder() ;
        Entry<T, CountInteger> next = null ;
        while(iterator.hasNext())
        {
            next = iterator.next() ;
            sb.append(next.getKey()) ;
            sb.append("\t") ;
            sb.append(next.getValue()) ;
            sb.append("\n") ;
        }
        return sb.toString() ;
    }
}
