package com.jz.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.jz.seg.Segmentor;

public class DictReader
{
			
	public static HashMap<String,String> getMapDict( String file ) throws IOException
	{
		HashMap<String,String> dict = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
        for( String line=br.readLine(); line!=null; line=br.readLine())
      	{
        	line = line.trim();
        	
        	if( line.isEmpty())
        		continue;
      		
      		String [] arr = line.split("\t");
      		
      		if( arr.length==2 )
      		{
      			String key = arr[0].toLowerCase();
      			String value = arr[1];

      			dict.put(key, value );
      		}
      	}
      	br.close();
      	
      	return dict;
	}
	
	public static Hashtable<String,String> getMapDict( String file, String tag, int len ) throws IOException
	{
		Hashtable<String,String> dict = new Hashtable<String,String>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
        for( String line=br.readLine(); line!=null; line=br.readLine())
      	{
        	line = line.trim();
        	
        	if( line.isEmpty())
        		continue;
			
//        	if( line.endsWith("衣") )
//        		System.out.println(line);
        	
        	String []arr = line.split("\t");
        	
        	line=arr[0];
        	
        	if( arr.length==2 )
        	{
            	if(line.length()>=len )
            	{
            		dict.put(line, tag+"-"+arr[1] );        	
          		}
        	}
        	else
        	{
            	if(line.length()>=len )
            	{
            		dict.put(line, tag );        	
          		}
        	}
      	}
      	br.close();
      	
      	return dict;
	}
	
	public static HashSet<String> getSetDict( String file, int len ) throws IOException
	{
		HashSet<String> dict = new HashSet<String>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
        for( String line=br.readLine(); line!=null; line=br.readLine())
      	{
        	line = line.trim();
        	
        	if( line.isEmpty() )
        		continue;
        	
        	line = line.split("\t")[0];
        	
      		line = line.toLowerCase();
      		
      		if( line.length()>=len )
      		{
      			dict.add( line );
      		}
      	}
      	br.close();
      	
      	return dict;
	}	
	
    public static Map<String, Set<String>> readSynonyms( String file , String product_dict_file, int level) throws Exception
    {   
		HashSet<String> product_dict = getSetDict( product_dict_file, 0 );

    	Map<String, Set<String>> map = new HashMap<String, Set<String>>();
    	
    	BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(file),"utf8"));

    	for(String line=br.readLine(); line!=null; line=br.readLine() )
    	{
    		String [] line_arr = line.split("\t");
    		
    		if( line_arr.length<6 )
    			continue;
    		
    		Set<String> set = new HashSet<String>();
    		
    		String word = line_arr[0];

    		if( level >=1 && line_arr[2].length()>2 )
    		{
    			String synonyms = line_arr[2].substring(1, line_arr[2].length()-1);
	    		    			
	    		String [] synonym_arr = synonyms.split(", ");
	
	    		for(String synonym : synonym_arr )
	    		{			
	    			String product = synonym.split(" ")[0];
	    			if( product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		if( level >=2 && line_arr[3].length()>2 )
    		{
    			String synonyms = line_arr[3].substring(1, line_arr[3].length()-1);
	    		
	    		String [] synonym_arr = synonyms.split(", ");
	    		
	    		for(String synonym : synonym_arr )
	    		{					
	    			String product = synonym.split(" ")[0];
	    			if( product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		if( level >=3 && line_arr[4].length()>2 )
    		{
    			String synonyms = line_arr[4].substring(1, line_arr[4].length()-1);
	    		
	    		String [] synonym_arr = synonyms.split(", ");
	    		
	    		for(String synonym : synonym_arr )
	    		{					
	    			String [] arr = synonym.split(" ");
	 	    			
	    			String product = arr[0];
	    			double score = Double.parseDouble(arr[1]);
	    			
	    			if( score>0.8 && product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		if( level>=4 && line_arr[5].length()>2 )
    		{
    			String synonyms = line_arr[5].substring(1, line_arr[5].length()-1);
	    		
	    		String [] synonym_arr = synonyms.split(", ");
	    		
	    		for(String synonym : synonym_arr )
	    		{		
	    			String [] arr = synonym.split(" ");
	    			String product = arr[0];
	    			double score = Double.parseDouble(arr[1]);
	    			
	    			if( score>0.8 && product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		map.put(word, set);
    	}
    	
    	br.close();    	
    	return map;
    }
    
    public static Map<String, Set<String>> readSynonyms() throws Exception
    {   
    	int level = 2;
    	
		HashSet<String> product_dict = new HashSet<String>();

		BufferedReader br = new BufferedReader( new InputStreamReader( Segmentor.class.getResourceAsStream("/product_high.dict") ) );
		
		String word=null;
		while( (word=br.readLine())!=null )
		{			
      		word=word.trim();
			
      		product_dict.add(word);
		}

        br.close();
		
    	Map<String, Set<String>> map = new HashMap<String, Set<String>>();
    	
    	br = new BufferedReader( new InputStreamReader( Segmentor.class.getResourceAsStream("/product_expand.syn"),"utf8"));

    	for(String line=br.readLine(); line!=null; line=br.readLine() )
    	{
    		String [] line_arr = line.split("\t");
    		
    		if( line_arr.length<6 )
    			continue;
    		
    		Set<String> set = new HashSet<String>();
    		
    		word = line_arr[0];

    		if( level >=1 && line_arr[2].length()>2 )
    		{
    			String synonyms = line_arr[2].substring(1, line_arr[2].length()-1);
	    		    			
	    		String [] synonym_arr = synonyms.split(", ");
	
	    		for(String synonym : synonym_arr )
	    		{			
	    			String product = synonym.split(" ")[0];
	    			if( product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		if( level >=2 && line_arr[3].length()>2 )
    		{
    			String synonyms = line_arr[3].substring(1, line_arr[3].length()-1);
	    		
	    		String [] synonym_arr = synonyms.split(", ");
	    		
	    		for(String synonym : synonym_arr )
	    		{					
	    			String product = synonym.split(" ")[0];
	    			if( product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		if( level >=3 && line_arr[4].length()>2 )
    		{
    			String synonyms = line_arr[4].substring(1, line_arr[4].length()-1);
	    		
	    		String [] synonym_arr = synonyms.split(", ");
	    		
	    		for(String synonym : synonym_arr )
	    		{					
	    			String [] arr = synonym.split(" ");
	 	    			
	    			String product = arr[0];
	    			double score = Double.parseDouble(arr[1]);
	    			
	    			if( score>0.8 && product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		if( level>=4 && line_arr[5].length()>2 )
    		{
    			String synonyms = line_arr[5].substring(1, line_arr[5].length()-1);
	    		
	    		String [] synonym_arr = synonyms.split(", ");
	    		
	    		for(String synonym : synonym_arr )
	    		{		
	    			String [] arr = synonym.split(" ");
	    			String product = arr[0];
	    			double score = Double.parseDouble(arr[1]);
	    			
	    			if( score>0.8 && product_dict.contains(product) )
	    			{
	    				if( word.endsWith(product.substring(product.length()-1)) || LCS.similarity(word, product)>0.6 )
	    					set.add(product);
	    			}
	    		}
    		}
    		
    		map.put(word, set);
    	}
    	
    	br.close();    	
    	return map;
    }
    
    public static Map<String, Integer> hotquery_freq(String file) throws IOException
    {
    	Map<String,Integer> dict = new HashMap<String,Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));		
		
        for( String line=br.readLine(); line!=null; line=br.readLine())
      	{
        	line = line.trim();
        	
        	if( line.isEmpty() )
        		continue;
        	
        	String [] arr = line.split("\t");
        	
      		String query = arr[0].toLowerCase();
      		int freq = Integer.parseInt(arr[1]);
      		dict.put(query, freq);      		
      	}
      	br.close();
      	
      	return dict;
    }
    
	public static HashMap<String,String> hotquery_cid3( String file ) throws IOException
	{
		HashMap<String,String> dict = new HashMap<String,String>();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
        for( String line=br.readLine(); line!=null; line=br.readLine())
      	{
        	line = line.trim();
        	
        	if( line.isEmpty() )
        		continue;
      		
      		String [] arr = line.split("\t");
      		
      		if( arr.length>=5 )
      		{
      			String query = arr[0].toLowerCase();
      			String cid = arr[4].split(",")[0];
      			dict.put(query, cid );
      		}
      	}
      	br.close();
      	
      	return dict;
	}
    
    public static Map<String,Integer> product_suffix(String product_dict_file) throws IOException
    {
		Map<String,Integer> suffix_counter = new HashMap<String,Integer>();
		
		BufferedReader br = new BufferedReader(new FileReader(product_dict_file));
		
        for( String line=br.readLine(); line!=null; line=br.readLine())
      	{
        	line = line.trim();
        	
        	if( line.isEmpty())
        		continue;
      		
        	int len = line.length();
        	if( len > 1 )
        	{
        		String suffix = line.substring(len-2);
        		Integer cnt = suffix_counter.get(suffix);
        		if( cnt==null )
        		{
        			cnt = 0;
        		}
        		
        		suffix_counter.put(suffix, cnt+1);
        	}
        	
    		String suffix = line.substring(len-1);
    		Integer cnt = suffix_counter.get(suffix);
    		if( cnt==null )
    		{
    			cnt = 0;
    		}
    		
    		suffix_counter.put(suffix, cnt+1);
      	}
      	br.close();
      	
      	return suffix_counter;
    }
}
