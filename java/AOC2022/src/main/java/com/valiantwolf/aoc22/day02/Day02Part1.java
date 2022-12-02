package com.valiantwolf.aoc22.day02;

import com.google.common.collect.ImmutableTable;
import org.apache.commons.lang3.tuple.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day02Part1
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
                         return Pair.<Shape,Shape>of( Shape.parse( matcher.group( 1 ) ), Shape.parse( matcher.group( 2 ) ) );
                     } )
                     .map( o -> o.getRight().playedAgainst( o.getLeft() ) )
                     .reduce( 0, Integer::sum )
        );
        System.out.println(i[0]);
    }

    private enum Shape
    {
        ROCK(1),
        PAPER(2),
        SCISSORS(3);

        private static final int SCORE_WIN = 6;
        private static final int SCORE_DRAW = 3;
        private static final int SCORE_LOSS = 0;

        private static final ImmutableTable<Shape, Shape, Integer> MATCHUP_TABLE =
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

        private final int score;

        private Shape(int score)
        {
            this.score = score;
        }

        public int playedAgainst( Shape opponentShape )
        {
            //noinspection ConstantConditions
            return score + MATCHUP_TABLE.get( opponentShape, this );
        }

        public static Shape parse( String string )
        {
            return switch ( string )
                    {
                        case "A", "X" -> ROCK;
                        case "B", "Y" -> PAPER;
                        case "C", "Z" -> SCISSORS;
                        default -> throw new IllegalArgumentException();
                    };
        }
    }
}
