package com.jz.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class BuildDictionary_BigramMarkovModel
{
	public static void main(String [] args ) throws IOException
	{
		Map<String,Double> bigram_model = calculate_markov();
		
		BufferedWriter bw = new BufferedWriter( new FileWriter("data/word2product.data") );		
		
		BufferedReader br = new BufferedReader( new FileReader("data/word_len5.dict") );
		for(String line=br.readLine(); line!=null; line=br.readLine() )
		{
			String [] arr = line.split("\t");
			String product = arr[0];
			double score = Double.parseDouble(arr[4]);			
			
			if( score>2 && product.length()>1 && product.length()<5)
			{
				Double prob = prob(bigram_model, product);
				if( prob>-18.5 )
				{
					bw.append(line).append("\t").append(prob.toString()).append("\n").flush();;
				}
			}
		}
	
		br.close();
		bw.close();
	}
	
	public static Map<String,Double> calculate_markov() throws IOException
	{
		Set<String> product_dict = DictReader.getSetDict("data/product_high.dict",2); // SJP just read product words
		
		Counter<String> unigram_counter = new Counter<String>();
		
		Counter<String> bigram_counter = new Counter<String>();
				
		for( String product : product_dict )
		{
			product = "#"+product+"@"; // eg. #越南鞋@
			
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
	public static Double prob( Map<String,Double> bigram_model, String product )
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
				value = -20.0;
			}
			
			prob+=value;
		}
		
		return prob;
	}
}
