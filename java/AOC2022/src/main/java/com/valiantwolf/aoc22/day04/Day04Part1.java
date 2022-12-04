package com.valiantwolf.aoc22.day04;

import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day04Part1
{
    public static void main( String... args )
    {
        Stream<String> input = argFileAsLineStream( args );

        Pattern pattern = Pattern.compile( "(\\d*)-(\\d*),(\\d*)-(\\d*)" );

        System.out.println(
                input.map( pattern::matcher )
                     .map( o -> {
                         //noinspection ResultOfMethodCallIgnored
                         o.find();
                         return new Object()
                         {
                             int p = Integer.parseInt( o.group( 1 ) );
                             int q = Integer.parseInt( o.group( 2 ) );
                             int r = Integer.parseInt( o.group( 3 ) );
                             int s = Integer.parseInt( o.group( 4 ) );
                         };
                     } )
                     .filter( o -> o.p >= o.r && o.q <= o.s
                                   || o.r >= o.p && o.s <= o.q )
                     .count()
        );
    }
}
