package com.jz.utility;

import java.util.Objects;

public class MyPair<K, V>
{
    public K key;
    public V value;

    /**
     * Constructor for a Pair.
     *
     * @param first the first object in the Pair
     * @param second the second object in the pair
     */
    public MyPair(K key, V value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Checks the two objects for equality by delegating to their respective
     * {@link Object#equals(Object)} methods.
     *
     * @param o the {@link Pair} to which this one is to be checked for equality
     * @return true if the underlying objects of the Pair are both considered
     *         equal
     */
    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof MyPair))
        {
            return false;
        }
        MyPair<?, ?> p = (MyPair<?, ?>) o;
        return Objects.equals(p.key, key) && Objects.equals(p.value, value);
    }

    /**
     * Compute a hash code using the hash codes of the underlying objects
     *
     * @return a hashcode of the Pair
     */
    @Override
    public int hashCode()
    {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }

    /**
     * Convenience method for creating an appropriately typed pair.
     * @param a the first object in the Pair
     * @param b the second object in the pair
     * @return a Pair that is templatized with the types of a and b
     */
    public static <A, B> MyPair <A, B> create(A a, B b)
    {
        return new MyPair<A, B>(a, b);
    }
}