package com.valiantwolf.aoc22.day11;

import com.google.common.base.Splitter;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsMatchStream;

public class Day11Part1
{
    private static final Pattern PATTERN = Pattern.compile(
            ".*Monkey.*\\n" +
            ".*items: (.*)\\n" +
            ".*old (.) (\\w+).*\\n" +
            ".*divisible by (\\d+).*\\n" +
            ".*monkey (\\d+).*\\n" +
            ".*monkey (\\d+).*\\n"
    );

    private static final Splitter SPLITTER = Splitter.on( ", " );

    @SuppressWarnings( "UnstableApiUsage" )
    public static void main( String... args )
    {
        Stream<String[]> input = argFileAsMatchStream( PATTERN, args );

        List<Monkey> monkeys = input.map( o -> Monkey.builder()
                                                     .withItems( SPLITTER.splitToStream( o[ 0 ] )
                                                                         .map( Integer::parseInt )
                                                                         .toList()
                                                     )
                                                     .withOperation( o[ 1 ].equals( "+" )
                                                                     ? o[ 2 ].equals( "old" )
                                                                       ? new SelfAddOperation()
                                                                       : new AddOperation( Integer.parseInt( o[ 2 ] ) )
                                                                     : o[ 2 ].equals( "old" )
                                                                       ? new SelfMultiplyOperation()
                                                                       : new MultiplyOperation( Integer.parseInt( o[ 2 ] ) )
                                                     )
                                                     .withTest( new Test( Integer.parseInt( o[ 3 ] ) ) )
                                                     .withTrueIndex( Integer.parseInt( o[ 4 ] ) )
                                                     .withFalseIndex( Integer.parseInt( o[ 5 ] ) )
                                                     .build()
                                    )
                                    .toList();

        for ( int i = 0; i < 20; i++ )
        {
            for ( Monkey monkey : monkeys )
            {
                monkey.business( monkeys );
            }
        }

        System.out.println(
                monkeys.stream()
                       .map( Monkey::getActivity )
                       .sorted( Comparator.reverseOrder() )
                       .limit( 2 )
                       .reduce( 1, Math::multiplyExact )
        );
    }

    static class Monkey
    {
        private final Queue<Integer> items;
        private final Function<Integer, Integer> operation;
        private final Predicate<Integer> test;
        private final int trueIndex;
        private final int falseIndex;

        private int activity = 0;

        public Monkey( List<Integer> items, Function<Integer, Integer> operation, Predicate<Integer> test, int trueIndex, int falseIndex )
        {
            this.items = new LinkedList<>( items );
            this.operation = operation;
            this.test = test;
            this.trueIndex = trueIndex;
            this.falseIndex = falseIndex;
        }

        public int getActivity()
        {
            return activity;
        }

        public void business( List<Monkey> monkeys )
        {
            while ( !items.isEmpty() )
            {
                int item = items.poll();
                item = operation.apply( item );
                item = item / 3;
                monkeys.get( test.test( item ) ? trueIndex : falseIndex ).addItem( item );
                activity++;
            }
        }

        public void addItem( int item )
        {
            items.add( item );
        }

        public static Builder builder()
        {
            return new Builder();
        }

        public static class Builder
        {
            private List<Integer> items;
            private Function<Integer, Integer> operation;
            private Predicate<Integer> test;
            private int trueIndex;
            private int falseIndex;

            private Builder() { }

            public Builder withItems( List<Integer> items )
            {
                this.items = items;
                return this;
            }

            public Builder withOperation( Function<Integer, Integer> operation )
            {
                this.operation = operation;
                return this;
            }

            public Builder withTest( Predicate<Integer> test )
            {
                this.test = test;
                return this;
            }

            public Builder withTrueIndex( int trueIndex )
            {
                this.trueIndex = trueIndex;
                return this;
            }

            public Builder withFalseIndex( int falseIndex )
            {
                this.falseIndex = falseIndex;
                return this;
            }

            public Monkey build()
            {
                return new Monkey( items, operation, test, trueIndex, falseIndex );
            }
        }
    }

    private static class SelfMultiplyOperation implements Function<Integer, Integer>
    {
        @Override
        public Integer apply( Integer value )
        {
            return value * value;
        }
    }

    private static class MultiplyOperation implements Function<Integer, Integer>
    {
        private final int multiplier;

        public MultiplyOperation( int multiplier )
        {
            this.multiplier = multiplier;
        }

        @Override
        public Integer apply( Integer value )
        {
            return value * multiplier;
        }
    }

    private static class SelfAddOperation implements Function<Integer, Integer>
    {
        @Override
        public Integer apply( Integer value )
        {
            return value + value;
        }
    }

    private static class AddOperation implements Function<Integer, Integer>
    {
        private final int addend;

        public AddOperation( int addend )
        {
            this.addend = addend;
        }

        @Override
        public Integer apply( Integer value )
        {
            return value + addend;
        }
    }

    private static class Test implements Predicate<Integer>
    {
        private final int divisor;

        public Test( int divisor )
        {
            this.divisor = divisor;
        }

        @Override
        public boolean test( Integer value )
        {
            return value % divisor == 0;
        }
    }
}
