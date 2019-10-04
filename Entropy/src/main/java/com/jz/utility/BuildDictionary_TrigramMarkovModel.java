package com.jz.utility;;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BuildDictionary_TrigramMarkovModel
{
	public static void main(String [] args ) throws IOException
	{
		Set<String> product_dict_set = DictReader.getSetDict("data/qualifier_high.dict",2);
		
		Map<String,Double> trigram_model = calculate_trigram_markov(product_dict_set);
				
		Map<String,Double> bigram_model = calculate_bigram_markov(product_dict_set);
		
		BufferedWriter bw = new BufferedWriter( new FileWriter("data/word2qualifier_trigram.data") );		
		
		BufferedReader br = new BufferedReader( new FileReader("data/qualifier_high.dict") );
		for(String line=br.readLine(); line!=null; line=br.readLine() )
		{
			String [] arr = line.split("\t");
			String product = arr[0];
//			double score = Double.parseDouble(arr[4]);			
			
			if( product.length()>2 && product.length()<5)
			{
				Double trigram_prob = trigram_prob(trigram_model, product);
				Double bigram_prob = bigram_prob(bigram_model, product);
				
				if( trigram_prob>-28 && bigram_prob>-18 )
				{
					bw.append(line).append("\t").append(bigram_prob.toString()).append("\t").append(trigram_prob.toString()).append("\t").append(String.valueOf(bigram_prob+trigram_prob)).append("\n").flush();;
				}
			}
		}	
		br.close();
		
		bw.close();
	}
	
	public static Map<String,Double> calculate_trigram_markov( Set<String> product_dict ) throws IOException
	{		
		Counter<String> bigram_counter = new Counter<String>();
		
		Counter<String> trigram_counter = new Counter<String>();
		
		for( String product : product_dict )
		{
			product = "##"+product+"@@";
			
			int len = product.length()-2;
			
			for( int i=0; i<len; i++  )
			{				
				bigram_counter.add(product.substring(i,i+2));
				trigram_counter.add(product.substring(i,i+3));
			}
		}
		
		Map<String,Double> trigram_model = new HashMap<String,Double>();
				
		for( Entry<String, Integer> entry : trigram_counter.toMap().entrySet() )
		{
			String trigram = entry.getKey();
			String bigram = trigram.substring(0,2);			
			
			double trigram_frequency = entry.getValue().doubleValue();
			double bigram_frequency = bigram_counter.get(bigram);
			
			trigram_model.put(trigram, Math.log(trigram_frequency/bigram_frequency));
		}
		
		return trigram_model;
	}
	
	public static Map<String,Double> calculate_bigram_markov(Set<String> product_dict) throws IOException
	{		
		Counter<String> unigram_counter = new Counter<String>();
		
		Counter<String> bigram_counter = new Counter<String>();
				
		for( String product : product_dict )
		{
			product = "#"+product+"@";
			
			int len = product.length()-1;
			
			for( int i=0; i<len; i++  )
			{
				unigram_counter.add(product.substring(i,i+1));
				bigram_counter.add(product.substring(i,i+2));
			}
		}
		
		Map<String,Double> bigram_model = new HashMap<String,Double>();
				
		for( Entry<String, Integer> entry : bigram_counter.toMap().entrySet() )
		{
			String bigram = entry.getKey();
			String unigram = bigram.substring(0,1);			
			
			double bigram_frequency = entry.getValue().doubleValue();
			double unigram_frequency = unigram_counter.get(unigram);
			
			bigram_model.put(bigram, Math.log(bigram_frequency/unigram_frequency));
		}
		
		return bigram_model;
	}
	
	//gkm: log probability
	public static Double trigram_prob( Map<String,Double> trigram_model, String product )
	{
		product = "##"+product+"@@";
		
		int len = product.length()-2;
		
		double prob = 0;
		
		for( int i=0; i<len; i++  )
		{
			String trigram = product.substring(i,i+3);
			Double value = trigram_model.get(trigram);
			if( value==null )
			{
				value = -15.0;
			}
			
			prob+=value;
		}
		
		return prob;
	}
	
	//gkm: log probability
	public static Double bigram_prob( Map<String,Double> bigram_model, String product )
	{
		product = "#"+product+"@";
		
		int len = product.length()-1;
		
		double prob = 0;
		
		for( int i=0; i<len; i++  )
		{
			String bigram = product.substring(i,i+2);
			Double value = bigram_model.get(bigram);
			if( value==null )
			{
				value = -15.0;
			}
			
			prob+=value;
		}
		
		return prob;
	}
}
