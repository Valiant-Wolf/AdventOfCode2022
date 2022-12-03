package com.valiantwolf.aoc22.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    public static Stream<String[]> argFileAsMatchStream( Pattern pattern, String... args )
    {
        Matcher matcher = pattern.matcher( argFileAsString( args ) );

        Supplier<String[]> supplier = () -> {
            if (!matcher.find())
                return null;

            int groups = matcher.groupCount();

            String[] result = new String[groups];

            for (int i = 0; i < groups; i++)
            {
                result[i] = matcher.group(i + 1);
            }

            return result;
        };

        return Stream.generate( supplier )
                     .takeWhile( Objects::nonNull );
    }
}
