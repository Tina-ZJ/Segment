package com.jz.utility;

public class MyLetter
{	
	public static boolean isLetter(char ch)
	{
		if( (ch<='z' && ch>='a') || (ch<='Z' && ch>='A') || ch=='.')
		{
			return true;
		}
		
		return false;
	}
		
	public static boolean bothLetter(char ch1, char ch2)
	{
		return isLetter(ch1) && isLetter(ch2);
	}
	
	public static boolean allLetter(String word)
	{
		char [] arr = word.toCharArray();
		
		for( char ch : arr )
		{
			if( !isLetter(ch) )
				return false;			
		}
		
		return true;
	}
	
	public static boolean isDigit(char ch)
	{
		if( (ch<='9' && ch>='0') ||ch=='.' )
		{
			return true;
		}
	
		return false;
	}
	
	public static boolean bothDigit(char ch1, char ch2)
	{
		return isDigit(ch1) && isDigit(ch2);
	}
	
	public static boolean allDigit(String word)
	{
		char [] arr = word.toCharArray();
		
		for( char ch : arr )
		{
			if( !isDigit(ch) )
				return false;			
		}
		
		return true;
	}
	
	public boolean allLetterOrDigit( String word )
	{
		char [] arr = word.toCharArray();
		for( char ch : arr )
		{
			if( isLetter(ch) || isDigit(ch) )
			{
				return false;
			}
		}
		
		return true;
	}
}
