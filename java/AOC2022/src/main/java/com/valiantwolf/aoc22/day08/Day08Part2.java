package com.valiantwolf.aoc22.day08;

import com.google.common.base.Splitter;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day08Part2
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

        for ( List<Tree> row : rows )
        {
            var crawler = new TreeCrawler( Tree::getViewEast, Tree::incrementViewEast, Tree::setViewWest );
            row.forEach( crawler );
        }

        for ( List<Tree> column : columns )
        {
            var crawler = new TreeCrawler( Tree::getViewSouth, Tree::incrementViewSouth, Tree::setViewNorth );
            column.forEach( crawler );
        }

        //noinspection OptionalGetWithoutIsPresent
        System.out.println(
                rows.stream()
                        .flatMap( List::stream )
                        .mapToInt( Tree::getScenicScore )
                        .max()
                        .getAsInt()
        );
    }

    private static class TreeCrawler implements Consumer<Tree>
    {
        private static final int HEIGHT_SENTINEL = 10;

        private final Tree sentinel;
        private final PriorityQueue<Tree> queue;

        private final Function<Tree, Integer> viewGetter;
        private final Consumer<Tree> viewIncrementer;
        private final BiConsumer<Tree, Integer> viewSetter;

        TreeCrawler( Function<Tree, Integer> viewGetter, Consumer<Tree> viewIncrementer, BiConsumer<Tree, Integer> viewSetter )
        {
            this.viewGetter = viewGetter;
            this.viewIncrementer = viewIncrementer;
            this.viewSetter = viewSetter;

            sentinel = new Tree( HEIGHT_SENTINEL );

            queue = new PriorityQueue<>();
            queue.add( sentinel );
        }

        @SuppressWarnings( "ConstantConditions" )
        @Override
        public void accept( Tree tree )
        {
            queue.forEach( viewIncrementer );

            while( queue.peek().getHeight() < tree.getHeight() )
            {
                queue.poll();
            }

            Tree that = queue.peek();
            int view = viewGetter.apply( that );

            viewSetter.accept( tree, that == sentinel ? view - 1 : view );

            if ( queue.peek().getHeight() == tree.getHeight() )
            {
                queue.poll();
            }

            queue.add( tree );
        }
    }

    private static class Tree implements Comparable<Tree>
    {
        private final int height;
        private int viewWest = 0;
        private int viewNorth = 0;
        private int viewEast = 0;
        private int viewSouth = 0;

        Tree( String height )
        {
            this( Integer.parseInt( height ) );
        }

        Tree( int height )
        {
            this.height = height;
        }

        public int getHeight()
        {
            return height;
        }

        public void setViewWest( int viewWest )
        {
            this.viewWest = viewWest;
        }

        public void setViewNorth( int viewNorth )
        {
            this.viewNorth = viewNorth;
        }

        public int getViewEast()
        {
            return viewEast;
        }

        public void incrementViewEast()
        {
            viewEast++;
        }

        public int getViewSouth()
        {
            return viewSouth;
        }

        public void incrementViewSouth()
        {
            viewSouth++;
        }

        private int getScenicScore()
        {
            return viewEast * viewWest * viewNorth * viewSouth;
        }

        @Override
        public int compareTo( Tree that )
        {
            return this.height - that.height;
        }
    }
}
