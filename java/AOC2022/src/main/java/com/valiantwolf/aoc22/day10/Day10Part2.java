package com.valiantwolf.aoc22.day10;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day10Part2
{
    private static final Pattern PATTERN = Pattern.compile( "(\\w+) ?([\\-\\d]*)" );

    public static void main( String... args )
    {
        Stream<Instruction> input = argFileAsLineStream( args ).map( PATTERN::matcher )
                                                               .filter( Matcher::find )
                                                               .map( o -> new Instruction( o.group( 1 ), !o.group( 2 ).isEmpty() ? Integer.parseInt( o.group( 2 ) ) : 0 ) );

        Processor cpu = new Processor();

        input.forEachOrdered( cpu::process );

        boolean[][] screen = cpu.getScreen();

        StringBuilder builder = new StringBuilder();
        for (boolean[] row : screen)
        {
            for (boolean pixel : row)
            {
                builder.append( pixel ? "#" : "." );
            }
            builder.append( "\n" );
        }

        System.out.println( builder.toString().trim() );
    }

    private static class Processor
    {
        private static final int WIDTH_SPRITE = 3;

        private int register = 0;
        private int clock = 0;
        private final boolean[][] screen = new boolean[ 6 ][ 40 ];

        public void process( Instruction instruction )
        {
            for ( int i = 0; i < instruction.getCycles(); i++ )
            {
                tick();
            }

            register += instruction.getValue();
        }

        public void tick()
        {
            int beam = clock % 40;

            screen[ (clock / 40) % 6 ][ beam ] = register <= beam
                                           && beam < register + WIDTH_SPRITE;

            clock++;
        }

        public boolean[][] getScreen()
        {
            return screen;
        }
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
