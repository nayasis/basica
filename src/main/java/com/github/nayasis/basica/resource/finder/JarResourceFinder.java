package com.github.nayasis.basica.resource.finder;

import com.github.nayasis.basica.resource.matcher.PathMatcher;
import com.github.nayasis.basica.resource.type.interfaces.Resource;
import com.github.nayasis.basica.base.Types;
import com.github.nayasis.basica.resource.util.Resources;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import static com.github.nayasis.basica.resource.util.Resources.URL_SEPARATOR_JAR;
import static com.github.nayasis.basica.resource.util.Resources.URL_SEPARATOR_WAR;

@Slf4j
public class JarResourceFinder {

    private PathMatcher pathMatcher;

    public JarResourceFinder( PathMatcher pathMatcher ) {
        this.pathMatcher = pathMatcher;
    }

    public void setPathMatcher( PathMatcher pathMatcher ) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Find all resources in jar files that match the given location pattern
     * via the Ant-style PathMatcher.
     * @param root          root directory as Resource
     * @param rootDir       the pre-resolved root directory URL
     * @param pattern       pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @since 4.3
     */
    @SuppressWarnings("unchecked")
    public Set<Resource> find( Resource root, URL rootDir, String pattern ) throws IOException {

        URLConnection conn = rootDir.openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean closeJarFile;

        if ( conn instanceof JarURLConnection ) {

            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) conn;
            Resources.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
            closeJarFile = !jarCon.getUseCaches();

        } else {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDir.getFile();
            try {
                int separatorIndex = urlFile.indexOf( URL_SEPARATOR_WAR );
                if ( separatorIndex == -1 ) {
                    separatorIndex = urlFile.indexOf( URL_SEPARATOR_JAR );
                }
                if (separatorIndex != -1) {
                    jarFileUrl = urlFile.substring(0, separatorIndex);
                    rootEntryPath = urlFile.substring(separatorIndex + 2);  // both separators are 2 chars
                    jarFile = Resources.getJarFile( jarFileUrl );
                } else {
                    jarFile = new JarFile(urlFile);
                    rootEntryPath = "";
                }
                closeJarFile = true;
            } catch ( ZipException ex ) {
                log.debug("Skipping invalid jar classpath entry [{}]", urlFile );
                return Collections.emptySet();
            }
        }

        try {

            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }

            Set<Resource> result = new LinkedHashSet<>(8);
            
            for( JarEntry entry : (List<JarEntry>)Types.toList(jarFile.entries()) ) {
                String entryPath = entry.getName();
                if ( entryPath.startsWith(rootEntryPath) ) {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if ( pathMatcher.match(pattern, relativePath) ) {
                        Resource relative = root.createRelative( relativePath );
                        result.add( relative );
                    }
                }
            }

            return result;

        } finally {
            if ( closeJarFile ) {
                jarFile.close();
            }
        }
    }

}