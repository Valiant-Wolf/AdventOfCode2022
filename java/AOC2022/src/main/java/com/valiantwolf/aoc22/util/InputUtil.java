package com.valiantwolf.aoc22.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public abstract class InputUtil
{
    public static String argFileAsString( String... args )
    {
        try
        {
            return Files.readString( Paths.get( args[ 0 ] ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static Stream<String> argFileAsLineStream( String... args )
    {
        try
        {
            return Files.lines( Paths.get( args[ 0 ] ) );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }
}
