package com.valiantwolf.aoc22.day10;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day10Part1
{
    private static final Pattern PATTERN = Pattern.compile( "(\\w+) ?([\\-\\d]*)" );

    public static void main( String... args )
    {
        Stream<Instruction> input = argFileAsLineStream( args ).map( PATTERN::matcher )
                .filter( Matcher::find )
                .map( o -> new Instruction( o.group(1), !o.group(2).isEmpty() ? Integer.parseInt( o.group( 2 ) ) : 0 ) );

        Iterator<Instruction> iter = input.iterator();

        int register = 1;
        int result = 0;
        int cycles = 0;

        for ( int i = 20; i <= 220; i += 40 )
        {
            boolean breaker = false;
            while ( iter.hasNext() && !breaker )
            {
                Instruction current = iter.next();

                cycles += current.getCycles();

                breaker = cycles >= i;

                if (breaker)
                {
                    result += i * register;
                }

                register += current.getValue();
            }
        }

        System.out.println( result );
    }

    private static class Instruction
    {
        private enum Type
        {
            NOOP( 1 ),
            ADDX( 2 );

            private final int cycles;

            Type( int cycles )
            {
                this.cycles = cycles;
            }
        }

        private final Type type;
        private final int value;

        public Instruction( String type, int value )
        {
            this.value = value;
            this.type = switch ( type )
                    {
                        case "noop" -> Type.NOOP;
                        case "addx" -> Type.ADDX;
                        default -> throw new IllegalArgumentException( type );
                    };
        }

        public Type getType()
        {
            return type;
        }

        public int getCycles()
        {
            return type.cycles;
        }

        public int getValue()
        {
            return value;
        }
    }
}
