package com.jz.tcp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.sun.deploy.util.StringUtils;
import com.jz.seg.Segmentor;
import com.jz.utility.MyPair;

public class Main_Tag
{	   
	public static void main(String [] args) throws IOException
	{
		args = new String [] {"1"};

		
		if( args.length!=1 )
		{
			System.out.println("args[0] - 1/0, print message or not.");
			System.out.println("args[1] - <optional> customer dict file.");
			return;
		}
		
		boolean print_message = false;
		
		try
		{
			int arg = Integer.parseInt(args[0]);
			if( arg==1 )
			{
				print_message = true;
			}
			else
			{
				if(arg==0)
				{
					print_message = false;
				}
				else
				{
					System.out.println("args[0] - 1/0, print message or not.");
					return;
				}
			}
			
		}
		catch(Exception e)
		{
			System.out.println("args[0] - boolean, print message or not.");
			return;
		}
		
		Segmentor segger = null;
		
		if( args.length==1 )
		{
			segger = Segmentor.getInstance();	
		}
		else
		{
			InputStream is = new FileInputStream( new File( args[1]) );
			
			segger = Segmentor.getInstance(is);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in ));
		
		if( print_message )
		{
			System.out.println("input:");
			
			for(String line=br.readLine(); line!=null && !line.isEmpty(); line=br.readLine() )
			{
				String text = line.split("\t")[0].toLowerCase();

				ArrayList<List<MyPair<String,String>> > tags = segger.tag(text);
				
				List<String> list = new ArrayList<String>();
				
				for( MyPair<String,String> tag : tags.get(0) )
				{
					list.add(tag.key+"/"+tag.value);
				}
				
				System.out.println("output:");
				
				System.out.println(line+"\t"+StringUtils.join(list,";"));    //list.toString()
				System.out.flush();
				
				System.out.println("input:");
			}
		}
		else
		{
			for(String line=br.readLine(); line!=null && !line.isEmpty(); line=br.readLine() )
			{
				String text = line.split("\t")[0].toLowerCase();

				ArrayList<List<MyPair<String, String>>> tags = segger.tag(text);
				
				List<String> list = new ArrayList<String>();
				
				for( MyPair<String,String> tag : tags.get(0) )
				{
					list.add(tag.key+"/"+tag.value);
				}
				
				System.out.println(line+"\t"+StringUtils.join(list,","));  //list.toString()
			}	
		}
		br.close();
	}
}
