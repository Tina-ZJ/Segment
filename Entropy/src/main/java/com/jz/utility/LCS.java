package com.jz.utility;

public class LCS
{
    public static int LCSLen(char[] str1, char[] str2)
    {
        int substringLength1 = str1.length;
        int substringLength2 = str2.length;
 
        int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];
 
        for (int i = substringLength1 - 1; i >= 0; i--)
        {
            for (int j = substringLength2 - 1; j >= 0; j--)
            {
                if (str1[i] == str2[j])
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        return opt[0][0];
    }    
    
    public static int LCSLen(String str1, String str2)
    {
        return LCSLen(str1.toCharArray(), str2.toCharArray());
    }
    
    public static double similarity(String str1, String str2)
    {
    	int len = LCSLen(str1,str2);
    	
    	return (double)len/Math.max(str1.length(),str2.length());
    }
        
    public static String LCSequence(char[] str1, char[] str2)
    {
        int substringLength1 = str1.length;
        int substringLength2 = str2.length;

        int[][] opt = new int[substringLength1 + 1][substringLength2 + 1];
 
        for (int i = substringLength1 - 1; i >= 0; i--)
        {
            for (int j = substringLength2 - 1; j >= 0; j--)
            {
                if (str1[i] == str2[j])
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        StringBuffer sb = new StringBuffer();
        
        int i = 0, j = 0;
        while (i < substringLength1 && j < substringLength2)
        {
            if (str1[i] == str2[j])
            {
                sb.append(str1[i]);
                i++;
                j++;
            }
            else if (opt[i + 1][j] >= opt[i][j + 1])
                i++;
            else
                j++;
        }
        
        return sb.toString();
    }
 
    public static String LCSequence(String str1, String str2)
    {
    	return LCSequence(str1.toCharArray(), str2.toCharArray());
    }

    public static void main(String[] args)
    {  
        String a = "abcd";
        String b = "acdb";
        System.out.println(LCS.LCSequence(a, b));
    }
}