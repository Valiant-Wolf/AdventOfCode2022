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

        System.out.println(
                input.map( o -> {
                         Matcher matcher = pattern.matcher( o );
                         //noinspection ResultOfMethodCallIgnored
                         matcher.find();
                         return Pair.of( Shape.parse( matcher.group( 1 ) ), Command.parse( matcher.group( 2 ) ) );
                     } )
                     .map( o -> {
                         Command command = o.getRight();
                         Shape response = o.getLeft().responseFor( command );
                         return command.getScore() + response.getScore();
                     } )
                     .reduce( 0, Integer::sum )
        );
    }

    private enum Command
    {
        WIN(6),
        DRAW(3),
        LOSE(0);

        private final int score;

        Command(int score)
        {
            this.score = score;
        }

        public int getScore()
        {
            return score;
        }

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

        Shape(int score)
        {
            this.score = score;
        }

        public Shape responseFor( Command command )
        {
            return TABLE_RESPONSE.get( this, command );
        }

        public int getScore()
        {
            return score;
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
