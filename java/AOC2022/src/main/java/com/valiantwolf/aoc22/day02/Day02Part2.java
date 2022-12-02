package com.valiantwolf.aoc22.day02;

import com.google.common.collect.ImmutableTable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day02Part2
{
    public static void main( String... args )
    {
        Stream<String> input = argFileAsLineStream( args );

        Pattern pattern = Pattern.compile( "(\\w)\\s(\\w)" );

        int[] i = { 0 };

        System.out.println(
                input.map( o -> {
                         Matcher matcher = pattern.matcher( o );
                         //noinspection ResultOfMethodCallIgnored
                         matcher.find();
                         return Pair.<Shape, Command>of( Shape.parse( matcher.group( 1 ) ), Command.parse( matcher.group( 2 ) ) );
                     } )
                     .map( o -> o.getLeft().responseFor( o.getRight() ).playedAgainst( o.getLeft() ) )
                     .reduce( 0, Integer::sum )
        );
        System.out.println(i[0]);
    }

    private static enum Command
    {
        WIN,
        DRAW,
        LOSE;

        public static Command parse(String string)
        {
            return switch ( string )
                    {
                        case "X" -> LOSE;
                        case "Y" -> DRAW;
                        case "Z" -> WIN;
                        default -> throw new IllegalArgumentException();
                    };
        }
    }

    private static enum Shape
    {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        private static final int SCORE_WIN = 6;
        private static final int SCORE_DRAW = 3;
        private static final int SCORE_LOSS = 0;

        private static final ImmutableTable<Shape, Shape, Integer> TABLE_SCORE =
                ImmutableTable.<Shape, Shape, Integer>builder()
                              .put( ROCK, ROCK, SCORE_DRAW )
                              .put( ROCK, PAPER, SCORE_WIN )
                              .put( ROCK, SCISSORS, SCORE_LOSS )
                              .put( PAPER, ROCK, SCORE_LOSS )
                              .put( PAPER, PAPER, SCORE_DRAW )
                              .put( PAPER, SCISSORS, SCORE_WIN )
                              .put( SCISSORS, ROCK, SCORE_WIN )
                              .put( SCISSORS, PAPER, SCORE_LOSS )
                              .put( SCISSORS, SCISSORS, SCORE_DRAW )
                              .build();

        private static final ImmutableTable<Shape, Command, Shape> TABLE_RESPONSE =
                ImmutableTable.<Shape, Command, Shape>builder()
                              .put( ROCK, Command.LOSE, SCISSORS )
                              .put( ROCK, Command.DRAW, ROCK )
                              .put( ROCK, Command.WIN, PAPER )
                              .put( PAPER, Command.LOSE, ROCK )
                              .put( PAPER, Command.DRAW, PAPER )
                              .put( PAPER, Command.WIN, SCISSORS )
                              .put( SCISSORS, Command.LOSE, PAPER )
                              .put( SCISSORS, Command.DRAW, SCISSORS )
                              .put( SCISSORS, Command.WIN, ROCK )
                              .build();

        private final int score;

        private Shape(int score)
        {
            this.score = score;
        }

        public Shape responseFor( Command command )
        {
            return TABLE_RESPONSE.get( this, command );
        }

        public int playedAgainst( Shape opponentShape )
        {
            //noinspection ConstantConditions
            return score + TABLE_SCORE.get( opponentShape, this );
        }

        public static Shape parse( String string )
        {
            return switch ( string )
                    {
                        case "A" -> ROCK;
                        case "B" -> PAPER;
                        case "C" -> SCISSORS;
                        default -> throw new IllegalArgumentException();
                    };
        }
    }
}
