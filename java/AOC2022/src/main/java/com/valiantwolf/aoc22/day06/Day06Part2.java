package com.valiantwolf.aoc22.day06;

import com.google.common.collect.EvictingQueue;

import java.util.HashSet;
import java.util.Iterator;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsString;

@SuppressWarnings( "UnstableApiUsage" )
public class Day06Part2
{
    private static final int BUFFER_SIZE = 14;

    public static void main( String... args )
    {
        String input = argFileAsString( args );

        EvictingQueue<Integer> circularBuffer = EvictingQueue.create( BUFFER_SIZE );

        Iterator<Integer> iter = input.chars().iterator();

        int count = 0;

        while ( !isDistinct( circularBuffer ) || circularBuffer.remainingCapacity() > 0 )
        {
            circularBuffer.add( iter.next() );
            count++;
        }

        System.out.println( count );
    }

    private static <T> boolean isDistinct( EvictingQueue<T> queue )
    {
        return new HashSet<T>( queue ).size() == queue.size();
    }
}
