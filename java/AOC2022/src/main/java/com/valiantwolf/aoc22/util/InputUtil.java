package com.valiantwolf.aoc22.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class InputUtil
{
    public static String fileArgAsString( String... args )
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
}
