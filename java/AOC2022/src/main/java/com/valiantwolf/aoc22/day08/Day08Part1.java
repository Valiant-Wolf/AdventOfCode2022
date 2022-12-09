package com.valiantwolf.aoc22.day08;

import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day08Part1
{
    public static void main( String... args )
    {
        Stream<String> input = argFileAsLineStream( args );

        Splitter splitter = Splitter.fixedLength( 1 );

        //noinspection UnstableApiUsage
        List<List<Tree>> rows = input.map( splitter::splitToStream )
                                     .map( o -> o.map( Tree::new )
                                                 .toList()
                                     )
                                     .toList();

        int columnCount = rows.get( 0 ).size();
        List<List<Tree>> columns = new ArrayList<>( columnCount );

        for ( int i = 0; i < columnCount; i++ )
        {
            columns.add( new ArrayList<Tree>( rows.size() ) );
        }

        for ( List<Tree> row : rows )
        {
            for ( int i = 0; i < columnCount; i++ )
            {
                columns.get( i ).add( row.get( i ) );
            }
        }

        Set<Tree> visible = new HashSet<>();

        for ( List<Tree> row : rows )
        {
            forEachForwardsAndBackwardsIf( row, visible::add, CurrentMaximumPredicate::new );
        }

        for ( List<Tree> column : columns )
        {
            forEachForwardsAndBackwardsIf( column, visible::add, CurrentMaximumPredicate::new );
        }

        System.out.println( visible.size() );
    }

    private static <T> void forEachForwardsAndBackwardsIf( List<T> list, Consumer<T> consumer, Supplier<Predicate<T>> predicateSupplier )
    {
        var iter = list.listIterator();
        Predicate<T> predicate = predicateSupplier.get();

        while ( iter.hasNext() )
        {
            var current = iter.next();
            if ( predicate.test( current ) )
            {
                consumer.accept( current );
            }
        }

        predicate = predicateSupplier.get();
        while ( iter.hasPrevious() )
        {
            var current = iter.previous();
            if ( predicate.test( current ) )
            {
                consumer.accept( current );
            }
        }
    }

    private static class CurrentMaximumPredicate<T extends Comparable<T>> implements Predicate<T>
    {
        private T previous = null;

        @Override
        public boolean test( T input )
        {
            if ( previous == null || previous.compareTo( input ) < 0 )
            {
                previous = input;
                return true;
            }
            return false;
        }
    }

    private static class Tree implements Comparable<Tree>
    {

        private final int height;

        Tree( String height )
        {
            this( Integer.parseInt( height ) );
        }

        Tree( int height )
        {
            this.height = height;
        }

        @Override
        public int compareTo( Tree that )
        {
            return this.height - that.height;
        }
    }
}
