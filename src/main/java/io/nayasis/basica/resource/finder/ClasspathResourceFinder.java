package io.nayasis.basica.resource.finder;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.resource.loader.ResourceLoader;
import io.nayasis.basica.resource.type.UrlResource;
import io.nayasis.basica.resource.type.interfaces.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

import static io.nayasis.basica.resource.util.Resources.URL_PREFIX_FILE;
import static io.nayasis.basica.resource.util.Resources.URL_PREFIX_JAR;
import static io.nayasis.basica.resource.util.Resources.URL_SEPARATOR_JAR;

@Slf4j
public class ClasspathResourceFinder {

    private ResourceLoader resourceLoader;

    public ClasspathResourceFinder( ResourceLoader resourceLoader ) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Find all class location resources with the given location via the ClassLoader.
     * Delegates to {@link #findAllClassPathResources(String)}.
     * @param location the absolute path within the classpath
     * @return the result as Resource array
     * @throws IOException in case of I/O errors
     * @see ClassLoader#getResources
     */
    public Set<Resource> findAll( String location ) throws IOException {

        String path = location;
        if ( path.startsWith("/") ) {
            path = path.substring(1);
        }

        Set<Resource> result = findAllClassPathResources( path );

        log.trace( "Resolved classpath location [{}] to resources {}", location, result );

        return result;
    }

    /**
     * Find all class location resources with the given path via the ClassLoader.
     * @param path the absolute path within the classpath (never a leading slash)
     * @return a mutable Set of matching Resource instances
     * @since 4.1.1
     */
    private Set<Resource> findAllClassPathResources( String path ) throws IOException {
        Set<Resource> result = new LinkedHashSet<>(16);
        ClassLoader cl = getClassLoader();
        Enumeration<URL> urls = (cl != null ? cl.getResources(path) : ClassLoader.getSystemResources(path));
        while ( urls.hasMoreElements() ) {
            result.add( new UrlResource(urls.nextElement()) );
        }
        if ( "".equals(path) ) {
            // The above result is likely to be incomplete, i.e. only containing file system references.
            // We need to have pointers to each of the jar files on the classpath as well...
            addAllClassLoaderJarRoots( cl, result );
        }
        return result;
    }

    /**
     * Search all {@link URLClassLoader} URLs for jar file references and add them to the
     * given set of resources in the form of pointers to the root of the jar file content.
     * @param classLoader the ClassLoader to search (including its ancestors)
     * @param result the set of resources to add jar roots to
     * @since 4.1.1
     */
    protected void addAllClassLoaderJarRoots( ClassLoader classLoader, Set<Resource> result ) {

        if( classLoader instanceof URLClassLoader ) {
            try {
                for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                    try {
                        UrlResource jarResource = new UrlResource( URL_PREFIX_JAR + url + URL_SEPARATOR_JAR );
                        if ( jarResource.exists() ) {
                            result.add(jarResource);
                        }
                    } catch ( MalformedURLException e ) {
                        log.debug( "Cannot search for matching files underneath [{}] because it cannot be converted to a valid 'jar:' URL: {}",
                            url, e.getMessage() );
                    }
                }
            } catch ( Exception e ) {
                log.debug( "Cannot introspect jar files since ClassLoader [{}] does not support 'getURLs()': {}", classLoader, e );
            }
        }

        if( classLoader == ClassLoader.getSystemClassLoader() ) {
            // "java.class.path" manifest evaluation...
            addClassPathManifestEntries( result );
        }

        if( classLoader != null ) {
            try {
                addAllClassLoaderJarRoots( classLoader.getParent(), result );
            } catch ( Exception e ) {
                log.debug( "Cannot introspect jar files in parent ClassLoader since [{}] does not support 'getParent()': {}", classLoader, e );
            }
        }

    }

    /**
     * Determine jar file references from the "java.class.path." manifest property and add them
     * to the given set of resources in the form of pointers to the root of the jar file content.
     * @param result the set of resources to add jar roots to
     * @since 4.3
     */
    protected void addClassPathManifestEntries( Set<Resource> result ) {
        try {
            String javaClassPathProperty = System.getProperty("java.class.path");
            for (String path : Strings.split( javaClassPathProperty, File.separator.replace( "\\", "\\\\" )) ) {
                try {
                    String filePath = new File(path).getAbsolutePath();
                    int prefixIndex = filePath.indexOf(':');
                    if (prefixIndex == 1) {
                        // Possibly "c:" drive prefix on Windows, to be upper-cased for proper duplicate detection
                        filePath = Strings.capitalize(filePath);
                    }
                    UrlResource jarResource = new UrlResource( URL_PREFIX_JAR +
                        URL_PREFIX_FILE + filePath + URL_SEPARATOR_JAR );
                    // Potentially overlapping with URLClassLoader.getURLs() result above!
                    if (!result.contains(jarResource) && !hasDuplicate(filePath, result) && jarResource.exists()) {
                        result.add(jarResource);
                    }
                } catch ( MalformedURLException e ) {
                    log.debug( "Cannot search for matching files underneath [{}] because it cannot be converted to a valid 'jar:' URL: {}", path, e.getMessage() );
                }
            }
        } catch ( Exception ex ) {
            log.debug( "Failed to evaluate 'java.class.path' manifest entries: {}", ex );
        }
    }

    /**
     * Check whether the given file path has a duplicate but differently structured entry
     * in the existing result, i.e. with or without a leading slash.
     * @param filePath the file path (with or without a leading slash)
     * @param result the current result
     * @return {@code true} if there is a duplicate (i.e. to ignore the given file path),
     * {@code false} to proceed with adding a corresponding resource to the current result
     */
    private boolean hasDuplicate( String filePath, Set<Resource> result ) {
        if( result.isEmpty() )
            return false;
        String duplicatePath = filePath.startsWith("/") ? filePath.substring(1) : "/" + filePath;
        try {
            return result.contains(new UrlResource( URL_PREFIX_JAR + URL_PREFIX_FILE +
                duplicatePath + URL_SEPARATOR_JAR ));
        } catch ( MalformedURLException e ) {
            return false;
        }
    }

    private ClassLoader getClassLoader() {
        return resourceLoader.getClassLoader();
    }

}
