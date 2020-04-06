package io.nayasis.basica.base;

import io.nayasis.basica.file.Files;
import io.nayasis.basica.resource.PathMatchingResourcePatternResolver;
import io.nayasis.basica.resource.type.interfaces.Resource;
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
        assertTrue( Classes.isResourceExisted( "/xml/Deformed.xml" ) );
        assertFalse( Classes.isResourceExisted( "/xml/Deformed.xm" ) );
    }

    @Test
    public void findResources() throws IOException, URISyntaxException {

        List<URL> urls = Classes.findResources( "/message/*.prop" );
        Assertions.assertEquals( 2, urls.size() );

        for ( URL url : urls ) {
            InputStream inputStream = Classes.getResourceAsStream(url);
            Assertions.assertNotNull( inputStream );
            Assertions.assertTrue( Strings.isNotEmpty(Files.readFrom(inputStream)) );
        }

        urls = Classes.findResources( "*" );
        Assertions.assertTrue( 5 <= urls.size() );

//        List<URL> urls = Classes.findResources( "/*" );
//        List<URL> urls = Classes.findResources( "**/*" );
//        List<URL> urls = Classes.findResources( "**/LICENSE.md" );
//        List<URL> urls = Classes.findResources( "/META-INF/LICENSE.md" );

    }

    @Test
    public void findResourcesWithPath() {

        PathMatchingResourcePatternResolver resourceLoader = new PathMatchingResourcePatternResolver();

        try {
            Resource[] resources = resourceLoader.getResources( "classpath*:message/*.prop" );

            for( Resource resource : resources ) {
                try {
                    InputStream inputStream = resource.getInputStream();
                    String text = Files.readFrom( inputStream );
                    System.out.println( ">> localization.ko.prop" );
                    System.out.println( text );
                } catch ( IOException e ) {
                    e.printStackTrace();
                }
            }

        } catch ( IOException e ) {
            e.printStackTrace();
        }

    }

}