package io.nayasis.basica.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ClassesTest {

    @Test
    public void isResourceExisted() {
        assertTrue( Classes.isResourceExisted( "/xml/Deformed.xml" ) );
        assertFalse( Classes.isResourceExisted( "/xml/Deformed.xm" ) );
    }

    @Test
    public void findResources() throws IOException, URISyntaxException {

        List<URL> urls = Classes.findResources( "/message/*.prop" );
        Assertions.assertEquals( 2, urls.size() );

        urls = Classes.findResources( "*" );
        Assertions.assertTrue( 50 <= urls.size() );

//        List<URL> urls = Classes.findResources( "/*" );
//        List<URL> urls = Classes.findResources( "**/*" );
//        List<URL> urls = Classes.findResources( "**/LICENSE.md" );
//        List<URL> urls = Classes.findResources( "/META-INF/LICENSE.md" );

    }

}