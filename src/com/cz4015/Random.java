package com.cz4015;

/**
 * Created by mdl94 on 22/03/2016.
 */
public class Random
{

    public static final long INCREMENT = 11;
    public static final long MULTIPLIER = 25214903917l;
    public static final long MODULUS = Integer.MAX_VALUE;


    private long _current = (long) System.nanoTime();

    public Random(long seed)
    {
        _current = seed;
    }
    
    public Random()
    {

    }

    public double uniform()
    {
        _current = (_current * MULTIPLIER + INCREMENT) % MODULUS;

        return ((double) (int)_current) / Integer.MAX_VALUE;
    }


}
