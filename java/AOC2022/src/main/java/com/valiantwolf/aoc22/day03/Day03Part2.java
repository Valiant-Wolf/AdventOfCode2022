package com.valiantwolf.aoc22.day03;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsMatchStream;

public class Day03Part2
{
    public static void main( String... args )
    {
        Stream<String[]> input = argFileAsMatchStream( Pattern.compile( "([^\\n]*)\\n([^\\n]*)\\n([^\\n]*)\\n" ), args );

        //noinspection OptionalGetWithoutIsPresent
        System.out.println(
                input.map( o -> Stream.of( o )
                                      .map( Day03Part2::toCharacterSet )
                                      .reduce( Sets::intersection )
                                      .get()
                                      .toArray( new Character[ 1 ] )[ 0 ]
                     )
                     .mapToInt( Day03Part2::toPriority )
                     .sum()
        );
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
