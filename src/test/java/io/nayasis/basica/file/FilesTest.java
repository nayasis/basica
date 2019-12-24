package io.nayasis.basica.file;

import io.nayasis.basica.base.Classes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class FilesTest {

    @Test
    public void readFromResource() throws MalformedURLException {

        URL path = Classes.getResource( "/xml/Deformed.xml" );

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        assertFalse( txt1.isEmpty() );
        assertFalse( txt2.isEmpty() );
        assertEquals( txt1, txt2 );

    }

    @Test
    public void readFromFile() {

        String path = Files.getRootPath() + "/xml/Grammer.xml";

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        assertFalse( txt1.isEmpty() );
        assertFalse( txt2.isEmpty() );
        assertEquals( txt1, txt2 );

    }

    @Test
    public void normalizeSeparator() {

        assertEquals( "a", Files.normalizeSeparator( "a\\" ) );
        assertEquals( "/", Files.normalizeSeparator( "/" ) );

    }


}