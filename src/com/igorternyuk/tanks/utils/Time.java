package com.igorternyuk.tanks.utils;

/**
 *
 * @author igor
 */
public class Time {
    public static final long SECOND = 1000000000l;
    public static final long MILLISECOND = 1000000l;
    
    public static final long get(){
        return System.nanoTime();
    }
}