package com.jz.utility;

public class CountInteger
{
    private int count;

    public CountInteger(int initCount)
    {
        count = initCount;
    }

    public void set(int num)
    {
        count = num;
    }

    public int value()
    {
        return count;
    }

    public String toString()
    {
        return "Count: " + String.valueOf(count);
    }
}
