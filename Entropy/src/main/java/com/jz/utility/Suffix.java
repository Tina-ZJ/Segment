package com.jz.utility;

public class Suffix
{	
	public static String getSuffix(String product )
	{
		int len = product.length();
		
		String suffix = product.substring( len-1);
		if( suffix.equals("子") )
			suffix = product.substring(len-2,len-1);
		
		if( suffix.equals("机") || suffix.equals("器") || suffix.equals("套") || suffix.equals("壳") )
			suffix = product.substring(len-2);
		
		return suffix;
	}

	public static boolean suffixEquals(String suffix1, String suffix2 )
	{
		if( suffix1.length()==2 && suffix2.length()==2  )
		{			
			if( ( ( suffix1.endsWith("套") || suffix1.endsWith("壳") ) && ( suffix2.endsWith("套") || suffix2.endsWith("壳") ) ) )
				return true;
			
			suffix1 = suffix1.substring(0,1);
			suffix2 = suffix2.substring(0,1);
		}
		
		if( suffix1.equals(suffix2) )
			return true;
		
		return false;
	}
}
