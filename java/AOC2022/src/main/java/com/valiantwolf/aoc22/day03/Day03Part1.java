package com.valiantwolf.aoc22.day03;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day03Part1
{
    public static void main( String... args )
    {
        Stream<String> input = argFileAsLineStream( args );

        System.out.println(
                input.map( Day03Part1::stringToHalves )
                        .map( o -> Pair.of( toCharacterSet( o.getLeft() ), toCharacterSet( o.getRight() ) ) )
                        .map( o -> Sets.intersection( o.getLeft(), o.getRight() ).toArray( new Character[1] )[0] )
                        .mapToInt( Day03Part1::toPriority )
                        .sum()
        );
    }

    private static Pair<String, String> stringToHalves( String string )
    {
        int length = string.length();
        int half = length / 2;
        return Pair.of( string.substring( 0, half ), string.substring( half, length ) );
    }

    private static Set<Character> toCharacterSet( String string )
    {
        return string.chars()
                     .mapToObj( o -> (char)o )
                     .collect( Collectors.toSet() );
    }

    private static int toPriority( Character character )
    {
        return character < 'a'
                 ? 27 + character - 'A'
                 : 1 + character - 'a';
    }
}
