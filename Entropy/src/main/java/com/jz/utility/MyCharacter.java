package com.jz.utility;

import java.lang.Character.UnicodeBlock;
import java.util.HashSet;
import java.util.Set;

public class MyCharacter
{
	private static Set<Integer> PUNCTUATION_SET = new HashSet<Integer>();
	
	static{
		PUNCTUATION_SET.add((int)'.');
		PUNCTUATION_SET.add((int)',');	
		PUNCTUATION_SET.add((int)';');
		PUNCTUATION_SET.add((int)'?');
		PUNCTUATION_SET.add((int)'!');
		PUNCTUATION_SET.add((int)'~');
		PUNCTUATION_SET.add((int)'，');
		PUNCTUATION_SET.add((int)'；');
		PUNCTUATION_SET.add((int)'。');
		PUNCTUATION_SET.add((int)'？');
		PUNCTUATION_SET.add((int)'！');
		PUNCTUATION_SET.add((int)'、');
		PUNCTUATION_SET.add((int)'-');
		PUNCTUATION_SET.add((int)'=');
		PUNCTUATION_SET.add((int)'+');
		PUNCTUATION_SET.add((int)'*');
		PUNCTUATION_SET.add((int)'/');
		PUNCTUATION_SET.add((int)'\\');
		PUNCTUATION_SET.add((int)'&');
		PUNCTUATION_SET.add((int)'%');
		PUNCTUATION_SET.add((int)'$');
		PUNCTUATION_SET.add((int)'#');
		PUNCTUATION_SET.add((int)'@');
		PUNCTUATION_SET.add((int)'!');
		PUNCTUATION_SET.add((int)'`');
		PUNCTUATION_SET.add((int)'(');
		PUNCTUATION_SET.add((int)')');
		PUNCTUATION_SET.add((int)'[');
		PUNCTUATION_SET.add((int)']');
		PUNCTUATION_SET.add((int)'{');
		PUNCTUATION_SET.add((int)'}');
		PUNCTUATION_SET.add((int)'<');
		PUNCTUATION_SET.add((int)'>');
		PUNCTUATION_SET.add((int)'\'');
		PUNCTUATION_SET.add((int)'"');
		PUNCTUATION_SET.add((int)'【');
		PUNCTUATION_SET.add((int)'】');
		PUNCTUATION_SET.add((int)'《');
		PUNCTUATION_SET.add((int)'》');
		PUNCTUATION_SET.add((int)'‘');
		PUNCTUATION_SET.add((int)'’');
		PUNCTUATION_SET.add((int)'“');
		PUNCTUATION_SET.add((int)'”');
	};
	
	private MyCharacter()
	{		
	}
	
	public static boolean isPunctutaion( char ch )
	{
		if( PUNCTUATION_SET.contains((int)ch) )
			return true;
		
		return false;
	}

	private static String tag( UnicodeBlock ub )
	{
		String [] arr = ub.toString().split("_");
		
		StringBuffer tag = new StringBuffer();
		for( String text : arr )
		{
			tag.append(text.charAt(0));
		}
		
		return tag.toString();
 	}

	private static boolean isChineseDigit( char ch)
	{
		if( ch=='一' || ch=='二' ||ch=='三' || ch=='四' || ch=='五' || ch=='六' || ch=='七' || ch=='八' || ch=='九' )
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public static String of( char ch )
	{
		if( isPunctutaion(ch) )
		{
			return "PUNC";
		}
		
		if( Character.isDigit(ch) )
		{
			return "DIG";
		}
		
		if( (ch>='a'&&ch<='z')||(ch>='A'&&ch<='Z') )
		{
			return "EN";
		}
		
		if( Character.isWhitespace(ch))
		{
			return "SPC";
		}
		
		if( isChineseDigit(ch))
		{
			return "ZHDIG";
		}
		
		if( isChinese(ch))
		{
			return "ZH";
		}		
		
		return tag( Character.UnicodeBlock.of(ch) );
 	}
	
	public static boolean isChinese(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (
        		ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || 
        		ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
        		ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
        		ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
        		)
        {
            return true;
        }
        return false;
    }
		
    public static boolean isChinese2(char c)
    {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION)
        {
            return true;
        }
        return false;
    }
 
    public static boolean isChinese(String strName)
    {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++)
        {
            char c = ch[i];
            if (isChinese(c))
            {
                return true;
            }
        }
        return false;
    }
    
    public static String semiangle(String src)
    {
    	char[] arr = src.toCharArray();
    	
    	for (int i=0; i<arr.length; i++ )
    	{
    		if (arr[i] == 12288)
    		{
    			arr[i] = (char) 32;
    		}
    		else
    			if (arr[i] > 65280 && arr[i] < 65375)
    			{
    				arr[i] = (char) (arr[i] - 65248);
    			}
    	}
        
    	return String.valueOf(arr);
    }
    
    public static String replacePunctuation(String line, String replacer)
    {
    	return line.replaceAll("\\p{Punct}", replacer)
				.replaceAll("\\pP", replacer)
				.replaceAll("　", " ")
				.replaceAll("\\p{Blank}", replacer)
				.replaceAll("\\p{Space}", replacer)
				.replaceAll("\\p{Cntrl}", replacer)
				.toLowerCase();
    }
    
    public static void main(String args[])
    {
    	System.out.println(semiangle(new String("飞利浦（PHILIPS）空气净化器KJ145F-A01(AC4025套装款),，共两套滤网").toLowerCase()));
    }
}
