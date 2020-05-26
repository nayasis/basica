package com.github.nayasis.basica.file;

import com.github.nayasis.basica.base.Classes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        String path = Files.getRootPath(getClass()) + "/xml/Grammer.xml";

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        assertFalse( txt1.isEmpty() );
        assertFalse( txt2.isEmpty() );
        assertEquals( txt1, txt2 );

    }

    @Test
    public void writeFile() {

        String tempFile = Files.getUserHome() + "/" + getClass().getName();

        try {

            Files.writeTo( tempFile, writer -> {
                writer.write( "merong-" );
                writer.write( "nayasis" );
            });

            String written = Files.readFrom( tempFile );

            assertEquals( "merong-nayasis", written.trim() );

        } finally {
            Files.delete( tempFile );
        }

    }

    @Test
    public void normalizeSeparator() {

        assertEquals( "//NAS/Game & Watch - Zelda", Files.normalizeSeparator( "\\\\NAS\\Game & Watch - Zelda" ) );
        assertEquals( "a", Files.normalizeSeparator( "a\\" ) );
        assertEquals( "/", Files.normalizeSeparator( "/" ) );

    }

    @Test
    public void toRelativePath() {

        String root = "//NAS/emul/ArcadeMame";
        String path = "\\\\NAS\\emul\\ArcadeMame\\Game & Watch - Zelda";

        String result = Files.toRelativePath(root, path);

        assertEquals( "ArcadeMame/Game & Watch - Zelda", result );

    }

    @Test
    public void copy() {

        String root = Files.getUserHome() + "/basica/filetest";

        String src       = root + "/src";
        String trg       = root + "/trg";
        String file      = root + "/sample.txt";
        String emptyFile = root + "/sample2.txt";
        String emptyTrg  = root + "/trg2";;

        log.debug( "root : {}", root );

        Files.makeDir( src );
        Files.makeDir( trg );
        Files.writeTo( file, "merong" );

        Files.copy( src, trg, true );
        Files.copy( file, src, true );
        Files.copy( src, emptyTrg, true );
        Files.copy( file, emptyFile, true );

        assertTrue( Files.isFile( emptyFile ) );
        assertTrue( Files.isFile( src + "/sample.txt" ) );
        assertTrue( Files.isDirectory( emptyTrg ) );
        assertTrue( Files.isDirectory( trg + "/src" ) );
        assertTrue( Files.isFile( trg + "/src/sample.txt" ) );

    }

    @Test
    public void toPath() {

        Path p = Files.toPath( "a.txt" );
        log.debug( "{}", p );

    }

}