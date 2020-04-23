package io.nayasis.basica.resource;

import io.nayasis.basica.resource.type.interfaces.Resource;
import io.nayasis.basica.resource.util.Resources;
import io.nayasis.basica.validation.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Set;

@Slf4j
public class PathMatchingResourceLoaderTest {

    @Test
    public void findResources() throws IOException {

        PathMatchingResourceLoader loader = new PathMatchingResourceLoader();

        Set<Resource> resources = loader.getResources( "classpath:/message/*.prop" );

        Assertions.assertTrue( resources.size() > 0, "there are no resources." );

    }

    @Test
    public void findResourcesInJar() throws IOException {

        boolean hasJarUrl = false;

        PathMatchingResourceLoader loader = new PathMatchingResourceLoader();

        Set<Resource> resources = loader.getResources( "classpath:/META-INF/LICENSE.md" );

        for( Resource resource : resources ) {
            if( ! Resources.isJarURL( resource.getURL() ) ) continue;
            hasJarUrl = true;
            log.debug( "resource : {}", resource.getURL() );
        }

        Assert.beTrue( hasJarUrl, "there are no resources in JAR." );

    }

}