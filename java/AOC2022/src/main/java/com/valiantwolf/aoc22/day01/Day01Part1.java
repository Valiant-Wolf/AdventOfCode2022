package com.valiantwolf.aoc22.day01;

import java.util.regex.Pattern;

import static com.valiantwolf.aoc22.util.InputUtil.fileArgAsString;

public class Day01Part1
{
    public static void main( String... args )
    {
        String input = fileArgAsString( args );

        System.out.println(
                Pattern.compile( "\n\n" )
                       .splitAsStream( input )
                       .map( o -> Pattern.compile( "\n" )
                                         .splitAsStream( o )
                                         .map( Integer::parseInt )
                                         .reduce( 0, Integer::sum )
                       )
                       .reduce( 0, Integer::max )
        );
    }
}
