package com.valiantwolf.aoc22.day05;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;
import static java.util.Optional.ofNullable;

public class Day05Part1
{
    private static final Pattern PATTERN_MOVE = Pattern.compile( "^move (\\d+) from (\\d) to (\\d)$" );
    private static final Pattern PATTERN_LABEL = Pattern.compile( "(\\d+)\\s*$" );

    public static void main( String... args )
    {
        // initialise stacks
        Stream<String> input = argFileAsLineStream( args );

        //noinspection OptionalGetWithoutIsPresent
        int stackCount = input.map( PATTERN_LABEL::matcher )
                              .filter( Matcher::find )
                              .findFirst()
                              .map( o -> o.group( 1 ) )
                              .map( Integer::parseInt )
                              .get();

        ArrayList<Deque<Character>> stacks = new ArrayList<>();

        for ( int i = 0; i < stackCount; i++ )
            stacks.add( new LinkedList<>() );

        // load crates into stacks
        input = argFileAsLineStream( args );

        Splitter splitter = Splitter.fixedLength( 4 );

        input.takeWhile( o -> !PATTERN_LABEL.matcher( o ).find() )
             .map( splitter::split )
             .forEachOrdered( o -> {
                 int i = 0;
                 for ( String s : o )
                 {
                     Character crate = parseCrate( s );

                     if ( crate != null )
                         stacks.get( i ).addLast( crate );

                     i++;
                 }
             } );

        // execute moves
        input = argFileAsLineStream( args );

        input.filter( o -> PATTERN_MOVE.matcher( o ).matches() )
             .map( Move::new )
             .forEachOrdered( o -> {
                 o.accept( stacks );
             } );

        System.out.println(
                stacks.stream()
                      .map( Deque::peek )
                      .map( o -> ofNullable( o ).map( Object::toString ).orElse( " " ) )
                      .collect( Collectors.joining() )
        );
    }

    private static Character parseCrate( String string )
    {
        return string.isBlank()
               ? null
               : string.charAt( 1 );
    }

    private static class Move implements Consumer<ArrayList<Deque<Character>>>
    {
        private final int count;
        private final int source;
        private final int destination;

        private Move( String line )
        {
            Matcher matcher = PATTERN_MOVE.matcher( line );
            matcher.matches();

            count = Integer.parseInt( matcher.group( 1 ) );
            source = Integer.parseInt( matcher.group( 2 ) ) - 1;
            destination = Integer.parseInt( matcher.group( 3 ) ) - 1;
        }

        @Override
        public void accept( ArrayList<Deque<Character>> stacks )
        {
            var src = stacks.get( source );
            var dst = stacks.get( destination );

            for ( int i = 0; i < count; i++ )
            {
                dst.push( src.pop() );
            }
        }
    }
}
