package com.github.nayasis.basica.file;

import com.github.nayasis.basica.base.Classes;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FilesTest {

    private String ROOT = Files.userHome() + "/basica/filetest";

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

        String path = Files.rootPath(getClass()) + "/xml/Grammar.xml";

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        assertFalse( txt1.isEmpty() );
        assertFalse( txt2.isEmpty() );
        assertEquals( txt1, txt2 );

    }

    @Test
    public void writeFile() {

        String tempFile = Files.userHome() + "/" + getClass().getName();

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

        String result = Files.relativePath(root, path);

        log.debug( "{}", result );

        assertEquals( "Game & Watch - Zelda", result );

    }

    @Test
    public void copy() {

        String root = ROOT + "/copy";

        log.debug( "root : {}", root );

        try {

            String src       = root + "/src";
            String trg       = root + "/trg";
            String file      = root + "/sample.txt";
            String emptyFile = root + "/sample2.txt";
            String emptyTrg  = root + "/trg2";

            Files.makeDir( src );
            Files.makeDir( trg );
            Files.writeTo( file, "merong" );

            Files.copy( file, src );
            Files.copy( src, trg );
            Files.copy( src, emptyTrg );
            Files.copy( file, emptyFile );

            assertTrue( Files.isFile( emptyFile ) );
            assertTrue( Files.isFile( src + "/sample.txt" ) );
            assertTrue( Files.isDirectory( emptyTrg ) );
            assertTrue( Files.isDirectory( trg + "/src" ) );
            assertTrue( Files.isFile( trg + "/src/sample.txt" ) );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void copyDir() {

        String root = ROOT + "/copydir";

        log.debug( "root : {}", root );

        String src         = root + "/src";
        String trgNotExist = root + "/trg-not-exist";
        String trgExist    = root + "/trg-exist";
        String file        = src  + "/sample.txt";

        try {

            Files.writeTo( file, "merong" );
            Files.makeDir( trgExist );

            // copy [src] to [trgNotExist]
            Files.copy( src, trgNotExist );

            // copy [src] to [trgExist/src]
            Files.copy( src, trgExist );

            assertTrue( Files.isFile( trgExist + "/src/sample.txt" ) );
            assertTrue( Files.isFile( trgNotExist + "/sample.txt" ) );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void copyTree() {

        String root = ROOT + "/copytree";

        log.debug( "root : {}", root );

        String src     = root + "/src";
        String srcSub1 = root + "/src/1";
        String srcSub2 = root + "/src/2";
        String trg     = root + "/trg";
        String file    = root + "/src/2/sample.txt";

        try {

            Files.makeDir( srcSub1 );
            Files.makeDir( srcSub2 );
            Files.makeDir( trg );
            Files.writeTo( file, "merong" );

            // copy [src] to [trgNotExist]
            Files.copyTree( src, trg );

            assertTrue( Files.isDirectory( trg + "/1" ) );
            assertTrue( Files.isDirectory( trg + "/2" ) );
            assertTrue( Files.isFile( trg + "/2/sample.txt" ) );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void symbolicLink() {

        String root = ROOT + "/symbolic-link";

        log.debug( "root : {}", root );

        String src = root + "/src/sample.txt";
        String trg = root + "/trg/sample.txt";

        try {

            Files.writeTo( src, "merong" );

            Files.makeSymbolicLink( src, trg, true );

            assertTrue( Files.isSymbolicLink(trg) );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void hardLink() {

        String root = ROOT + "/hard-link";

        log.debug( "root : {}", root );

        String src = root + "/src/sample.txt";
        String trg = root + "/trg/sample.txt";

        try {

            Files.writeTo( src, "merong" );

            Files.makeHardLink( src, trg, true );

            Files.writeTo( src, "changed" );

            // if contents of original file are changed, contents of hardlink file must be changed.
            assertEquals( "changed", Files.readFrom(trg).trim() );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void toPath() {
        assertEquals( "a.txt", Files.toPath( "a.txt" ).toString() );
        assertEquals( "\\\\NAS\\b\\", Files.toPath( "\\\\NAS\\b" ).toString() );
        assertEquals( "b", Files.toPath( new File("b") ).toString() );
    }

    @Test
    public void resolvePath() {

        String root = "/root/bin/";

        assertEquals( "/root/temp", Files.resolvePath( root, ".././temp" ) );
        assertEquals( "/root/bin/temp", Files.resolvePath( root, "./temp" ) );
        assertEquals( "/root/bin/temp", Files.resolvePath( root, "temp" ) );
        assertEquals( "/temp", Files.resolvePath( root, "/./temp" ) );

    }

}