package com.github.nayasis.basica.file;

import com.github.nayasis.basica.base.Classes;
import com.github.nayasis.basica.exception.unchecked.InvalidArgumentException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class FilesTest {

    private String ROOT = Files.userHome() + "/basica/filetest";

    @Test
    public void checkParameterTypeToPath() {

        Files.toPath( "/user/home" );
        Files.toPath( new StringBuffer("/user/home") );
        Files.toPath( new StringBuilder("/user/home") );
        Files.toPath( new File("/user/home") );
        Files.toPath( Paths.get("/user/home") );
        Files.toPath( null );

        assertThrows( InvalidArgumentException.class, () -> {
            Files.toPath( LocalDate.now() );
        });

    }

    @Test
    public void checkParameterTypeToURL() throws MalformedURLException {

        Files.toURL( new URL( "https://www.google.com" ) );
        Files.toURL( "/user/home" );
        Files.toURL( new StringBuffer("/user/home") );
        Files.toURL( new StringBuilder("/user/home") );
        Files.toURL( new File("/user/home") );
        Files.toURL( Paths.get("/user/home") );
        Files.toURL( null );

        assertThrows( InvalidArgumentException.class, () -> {
            Files.toURL( LocalDate.now() );
        });

    }

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

            Files.makeDir( src );
            Files.makeDir( trg );
            Files.writeTo( file, "merong" );

            Files.copy( file, src );
            assertTrue( Files.isFile( src + "/sample.txt" ) );

            Files.copy( src, trg );
            assertTrue( Files.isDirectory( trg + "/src" ) );
            assertTrue( Files.isFile( trg + "/src/sample.txt" ) );

            Files.copy( src, root + "/trg2" );
            assertTrue( Files.isDirectory( root + "/trg2" ) );

            Files.copy( file, root + "/sample2.txt" );
            assertTrue( Files.isFile( root + "/sample2.txt" ) );

            Files.copy( file, root + "/new/child/clone.txt" );
            assertTrue( Files.isFile( root + "/new/child/clone.txt" ) );


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
        String file        = root + "/src/sample.txt";

        try {

            Files.writeTo( file, "merong" );
            Files.makeDir( trgExist );

            // copy [src] to [trgNotExist]
            Path target1 = Files.copy( src, trgNotExist );

            // copy [src] to [trgExist/src]
            Path target2 = Files.copy( src, trgExist );

            assertTrue( Files.isFile( trgExist + "/src/sample.txt" ) );
            assertTrue( Files.isFile( trgNotExist + "/sample.txt" ) );

            assertEquals( trgNotExist, Files.normalizeSeparator(target1) );
            assertEquals( trgExist + "/src", Files.normalizeSeparator(target2) );

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
    public void moveDir() {

        String root = ROOT + "/move-dir";

        log.debug( "root : {}", root );

        String src  = root + "/src";
        String trg  = root + "/trg";
        String file = root + "/src/sample.txt";

        try {

            Files.writeTo( file, "merong" );
            Files.makeDir( trg );

            Path target = Files.move( src, trg );

            assertTrue( Files.isFile( trg + "/src/sample.txt" ) );

            assertEquals( trg + "/src", Files.normalizeSeparator(target) );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void moveDirToNotExist() {

        String root = ROOT + "/move-dir-not-exist";

        log.debug( "root : {}", root );

        String src  = root + "/src";
        String trg  = root + "/trg";
        String file = root + "/src/sample.txt";

        try {

            Files.writeTo( file, "merong" );

            Path target = Files.move( src, trg );

            assertTrue( Files.isFile( trg + "/sample.txt" ) );

            assertEquals( trg, Files.normalizeSeparator(target) );

        } finally {
            Files.delete( root );
        }

    }

    @Test
    public void moveFile() {

        String root = ROOT + "/move-file";

        log.debug( "root : {}", root );

        String src   = root + "/src";
        String trg   = root + "/trg";
        String file1 = root + "/src/sample1.txt";
        String file2 = root + "/src/sample2.txt";

        try {

            Files.writeTo( file1, "merong" );
            Files.writeTo( file2, "merong" );

            Files.move( file1, trg + "/sample1.txt" );
            Files.move( file2, trg + "/children/sample2.txt" );

            assertTrue( Files.isFile( trg + "/sample1.txt" ) );
            assertTrue( Files.isFile( trg + "/children/sample2.txt" ) );

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