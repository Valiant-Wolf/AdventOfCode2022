package com.valiantwolf.aoc22.day07;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.valiantwolf.aoc22.util.InputUtil.argFileAsLineStream;

public class Day07Part1
{
    private static final Pattern PATTERN_CD = Pattern.compile( "cd (\\S*)" );
    private static final Pattern PATTERN_FILE = Pattern.compile( "(\\d*) (.*)" );
    private static final Pattern PATTERN_DIR = Pattern.compile( "dir (.*)" );

    public static void main( String... args )
    {
        Stream<String> input = argFileAsLineStream( args );

        Directory root = new Directory( "/" );
        Stack<Directory> dirStack = new Stack<>();
        dirStack.push( root );

        Iterator<String> iter = input.iterator();

        int lNumber = 0;

        while ( iter.hasNext() )
        {
            lNumber++;
            String line = iter.next();
            char first = line.charAt( 0 );

            Directory current = dirStack.peek();

            switch ( first )
            {
                // command
                case '$' ->
                {
                    // change directory
                    Matcher matcher = PATTERN_CD.matcher( line );
                    if ( matcher.find() )
                    {
                        String dir = matcher.group( 1 );
                        switch ( dir )
                        {
                            case "/" ->
                            {
                                dirStack.clear();
                                dirStack.push( root );
                            }
                            case ".." -> dirStack.pop();
                            default -> dirStack.push( current.getDirectory( dir ) );
                        }
                    }
                }
                // file
                case '1', '2', '3', '4', '5', '6', '7', '8', '9', '0' ->
                {
                    Matcher matcher = PATTERN_FILE.matcher( line );
                    matcher.find();

                    current.addFile( matcher.group( 2 ), Integer.parseInt( matcher.group( 1 ) ) );
                }
                // directory
                default ->
                {
                    Matcher matcher = PATTERN_DIR.matcher( line );

                    if ( !matcher.find() )
                        continue;

                    current.addDirectory( matcher.group( 1 ) );
                }
            }
        }

        Queue<Directory> queue = new LinkedList<>();
        queue.add( root );

        int result = 0;

        while ( !queue.isEmpty() )
        {
            Directory current = queue.poll();

            int size = current.size();

            if ( size <= 100000 )
            {
                result += size;
            }

            current.streamDirectories()
                   .forEach( queue::add );
        }

        System.out.println( result );
    }

    private interface FileSystemObject
    {
        String name();

        int size();
    }

    private static class Directory implements FileSystemObject
    {
        private final String name;
        private final Map<String, File> files = new HashMap<>();
        private final Map<String, Directory> dirs = new HashMap<>();
        private Integer size = null;

        public Directory( String name )
        {
            this.name = name;
        }

        @Override
        public String name()
        {
            return name;
        }

        @Override
        public int size()
        {
            if ( size == null )
                size = Stream.concat( files.values().stream(), dirs.values().stream() )
                             .mapToInt( FileSystemObject::size )
                             .sum();

            return size;
        }

        public void addFile( String name, int size )
        {
            files.put( name, new File( name, size ) );
            this.size = null;
        }

        public void addDirectory( String name )
        {
            dirs.put( name, new Directory( name ) );
            this.size = null;
        }

        public Directory getDirectory( String name )
        {
            return dirs.get( name );
        }

        public Stream<Directory> streamDirectories()
        {
            return dirs.values().stream();
        }
    }

    private record File(String name, int size) implements FileSystemObject { }
}
