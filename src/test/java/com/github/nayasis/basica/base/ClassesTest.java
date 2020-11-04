package com.github.nayasis.basica.base;

import com.github.nayasis.basica.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ClassesTest {

    @Test
    public void isResourceExisted() {
        assertTrue( Classes.hasResource( "/xml/Deformed.xml" ) );
        assertFalse( Classes.hasResource( "/xml/Deformed.xm" ) );
    }

    @Test
    public void findResources() throws IOException, URISyntaxException {

        List<URL> urls = Classes.findResources( "/message/*.prop" );
        Assertions.assertEquals( 2, urls.size() );

        for ( URL url : urls ) {
            log.debug( "url : {}", url );
            InputStream inputStream = Classes.getResourceStream(url);
            Assertions.assertNotNull( inputStream );
            Assertions.assertTrue( Strings.isNotEmpty(Files.read(inputStream)) );
        }

        urls = Classes.findResources( "*" );
        Assertions.assertTrue( 5 <= urls.size() );

//        List<URL> urls = Classes.findResources( "/*" );
//        List<URL> urls = Classes.findResources( "**/*" );
//        List<URL> urls = Classes.findResources( "**/LICENSE.md" );
//        List<URL> urls = Classes.findResources( "/META-INF/LICENSE.md" );

    }

    @Test
    public void grepResources() {

        // grep from root path
        printResources("*.properties");
        printResources("**.properties");

        // grep from all path
        printResources("**/*.properties");
        printResources("/**/*.properties");

    }

    private void printResources( String pattern ) {
        log.debug( ">> FIND PATTERN !! : {}", pattern );
        Classes.findResources( pattern).forEach(url -> { log.debug("- {}",url);});
    }

}