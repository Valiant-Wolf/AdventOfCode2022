package com.valiantwolf.aoc22.day11;

import com.google.common.base.Splitter;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsMatchStream;

public class Day11Part2
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

        List<Monkey.Builder> monkeyBuilders = input.map( o -> Monkey.builder()
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
                                                                    .withDivisor( Integer.parseInt( o[ 3 ] ) )
                                                                    .withTrueIndex( Integer.parseInt( o[ 4 ] ) )
                                                                    .withFalseIndex( Integer.parseInt( o[ 5 ] ) )
                                                   )
                                                   .toList();

        List<Integer> moduli = monkeyBuilders.stream()
                                             .map( Monkey.Builder::getDivisor )
                                             .toList();

        List<Monkey> monkeys = monkeyBuilders.stream()
                                             .map( o -> o.build( moduli ) )
                                             .toList();

        for ( int i = 0; i < 10000; i++ )
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
                       .reduce( 1L, Math::multiplyExact )
        );
    }

    static class Monkey
    {
        private final Queue<ModularInteger> items;
        private final Function<ModularInteger, ModularInteger> operation;
        private final int divisor;
        private final int trueIndex;
        private final int falseIndex;

        private long activity = 0;

        private Monkey( List<ModularInteger> items, Function<ModularInteger, ModularInteger> operation, int divisor, int trueIndex, int falseIndex )
        {
            this.items = new LinkedList<>( items );
            this.operation = operation;
            this.divisor = divisor;
            this.trueIndex = trueIndex;
            this.falseIndex = falseIndex;
        }

        public long getActivity()
        {
            return activity;
        }

        public void business( List<Monkey> monkeys )
        {
            while ( !items.isEmpty() )
            {
                ModularInteger item = items.poll();
                item = operation.apply( item );
                monkeys.get( item.modulo( divisor ) == 0 ? trueIndex : falseIndex ).addItem( item );
                activity++;
            }
        }

        public void addItem( ModularInteger item )
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
            private Function<ModularInteger, ModularInteger> operation;
            private int divisor;
            private int trueIndex;
            private int falseIndex;

            private Builder() { }

            public Builder withItems( List<Integer> items )
            {
                this.items = items;
                return this;
            }

            public Builder withOperation( Function<ModularInteger, ModularInteger> operation )
            {
                this.operation = operation;
                return this;
            }

            public Builder withDivisor( int divisor )
            {
                this.divisor = divisor;
                return this;
            }

            public int getDivisor()
            {
                return divisor;
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

            public Monkey build( List<Integer> moduli )
            {
                List<ModularInteger> modularItems = items.stream()
                                                         .map( ModularInteger::builder )
                                                         .map( o -> o.withModuli( moduli ).build() )
                                                         .collect( Collectors.toList() );

                return new Monkey( modularItems, operation, divisor, trueIndex, falseIndex );
            }
        }
    }

    private static class ModularInteger
    {
        private final Map<Integer, Integer> moduloMap;

        private ModularInteger( Map<Integer, Integer> moduloMap )
        {
            this.moduloMap = moduloMap;
        }

        public ModularInteger add( int value )
        {
            Map<Integer, Integer> newMap = new HashMap<>( moduloMap.size() );

            for ( int key : moduloMap.keySet() )
            {
                newMap.put( key, ( moduloMap.get( key ) + ( value % key ) ) % key );
            }

            return new ModularInteger( newMap );
        }

        public ModularInteger add( ModularInteger that )
        {
            if ( !compatible( that ) )
            {
                throw new IllegalArgumentException();
            }

            Map<Integer, Integer> newMap = new HashMap<>( moduloMap.size() );

            for ( int key : this.moduloMap.keySet() )
            {
                newMap.put( key, ( this.moduloMap.get( key ) + that.moduloMap.get( key ) ) % key );
            }

            return new ModularInteger( newMap );
        }

        public ModularInteger multiply( int value )
        {
            Map<Integer, Integer> newMap = new HashMap<>( moduloMap.size() );

            for ( int key : moduloMap.keySet() )
            {
                newMap.put( key, ( moduloMap.get( key ) * ( value % key ) ) % key );
            }

            return new ModularInteger( newMap );
        }

        public ModularInteger multiply( ModularInteger that )
        {
            if ( !compatible( that ) )
            {
                throw new IllegalArgumentException();
            }

            Map<Integer, Integer> newMap = new HashMap<>( moduloMap.size() );

            for ( int key : this.moduloMap.keySet() )
            {
                newMap.put( key, ( this.moduloMap.get( key ) * that.moduloMap.get( key ) ) % key );
            }

            return new ModularInteger( newMap );
        }

        public int modulo( int modulus )
        {
            return moduloMap.get( modulus );
        }

        public boolean compatible( ModularInteger that )
        {
            return this.moduloMap.keySet().equals( that.moduloMap.keySet() );
        }

        public static Builder builder()
        {
            return new Builder();
        }

        public static Builder builder( int initialValue )
        {
            return new Builder().withInitialValue( initialValue );
        }

        public static class Builder
        {
            private int initialValue;
            private final Set<Integer> moduli = new HashSet<>();

            private Builder() { }

            public Builder withInitialValue( int value )
            {
                this.initialValue = value;
                return this;
            }

            public Builder withModulus( int modulus )
            {
                this.moduli.add( modulus );
                return this;
            }

            public Builder withModuli( Collection<Integer> moduli )
            {
                this.moduli.addAll( moduli );
                return this;
            }

            public ModularInteger build()
            {
                return new ModularInteger(
                        moduli.stream()
                              .collect( Collectors.toMap(
                                      Function.identity(),
                                      o -> initialValue % o
                              ) )
                );
            }
        }
    }

    private static class SelfMultiplyOperation implements Function<ModularInteger, ModularInteger>
    {
        @Override
        public ModularInteger apply( ModularInteger value )
        {
            return value.multiply( value );
        }
    }

    private static class MultiplyOperation implements Function<ModularInteger, ModularInteger>
    {
        private final int multiplier;

        public MultiplyOperation( int multiplier )
        {
            this.multiplier = multiplier;
        }

        @Override
        public ModularInteger apply( ModularInteger value )
        {
            return value.multiply( multiplier );
        }
    }

    private static class SelfAddOperation implements Function<ModularInteger, ModularInteger>
    {
        @Override
        public ModularInteger apply( ModularInteger value )
        {
            return value.add( value );
        }
    }

    private static class AddOperation implements Function<ModularInteger, ModularInteger>
    {
        private final int addend;

        public AddOperation( int addend )
        {
            this.addend = addend;
        }

        @Override
        public ModularInteger apply( ModularInteger value )
        {
            return value.add( addend );
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
