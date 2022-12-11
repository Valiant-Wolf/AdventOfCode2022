package com.valiantwolf.aoc22.day09;

import com.valiantwolf.aoc22.util.MathUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day09Part2
{
    private static final Pattern PATTERN = Pattern.compile( "(\\w) (\\d+)" );
    private static final int COUNT_KNOTS = 10;

    public static void main( String... args )
    {
        Stream<String> input = argFileAsLineStream( args );

        Knot tail = new Knot( null );

        Knot tempHead = tail;
        for ( int i = 0; i < COUNT_KNOTS - 1; i++ )
        {
            tempHead = new Knot( tempHead );
        }
        final Knot head = tempHead;

        Set<Vector> visited = new HashSet<>();

        input.map( PATTERN::matcher )
             .filter( Matcher::find )
             .flatMap( o -> Stream.generate( () -> o.group( 1 ) )
                                  .limit( Integer.parseInt( o.group( 2 ) ) )
             )
             .map( o -> switch ( o )
                     {
                         case "U" -> new Vector( 0, -1 );
                         case "D" -> new Vector( 0, 1 );
                         case "L" -> new Vector( -1, 0 );
                         case "R" -> new Vector( 1, 0 );
                         default -> throw new IllegalArgumentException( o );
                     }
             )
             .forEachOrdered( o -> {
                 head.move( o );
                 visited.add( tail.getPosition() );
             } );

        System.out.println( visited.size() );
    }

    private static class Knot
    {
        private final Knot tail;
        private Vector position;

        public Knot( Knot tail )
        {
            this.tail = tail;
            position = new Vector( 0, 0 );
        }

        public Vector getPosition()
        {
            return position;
        }

        public void move( Vector move )
        {
            position = position.add( move );
            if ( tail != null )
            {
                tail.trail( position );
            }
        }

        public void trail( Vector target )
        {
            Vector delta = target.subtract( position );

            if ( delta.chebyshev() < 2 )
            {
                return;
            }

            move( delta.clamp( 1 ) );
        }
    }

    private static record Vector(int x, int y)
    {
        public Vector add( Vector that )
        {
            return new Vector( this.x + that.x, this.y + that.y );
        }

        public Vector subtract( Vector that )
        {
            return new Vector( this.x - that.x, this.y - that.y );
        }

        public int chebyshev()
        {
            return Math.max( Math.abs( x ), Math.abs( y ) );
        }

        public int manhattan()
        {
            return Math.abs( x ) + Math.abs( y );
        }

        public Vector clamp( int max )
        {
            int abs = Math.abs( max );
            return clamp( -abs, abs );
        }

        public Vector clamp( int min, int max )
        {
            return new Vector( MathUtil.clamp( x, min, max ), MathUtil.clamp( y, min, max ) );
        }
    }
}
