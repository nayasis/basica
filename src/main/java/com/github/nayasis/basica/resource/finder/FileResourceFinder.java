package com.github.nayasis.basica.resource.finder;

import com.github.nayasis.basica.resource.matcher.PathMatcher;
import com.github.nayasis.basica.resource.type.FileSystemResource;
import com.github.nayasis.basica.resource.type.interfaces.Resource;
import com.github.nayasis.basica.file.Files;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class FileResourceFinder {

    private PathMatcher pathMatcher;

    public FileResourceFinder( PathMatcher pathMatcher ) {
        this.pathMatcher = pathMatcher;
    }

    public void setPathMatcher( PathMatcher pathMatcher ) {
        this.pathMatcher = pathMatcher;
    }

    /**
     * Find all resources in the file system that match the given location pattern
     * via the Ant-style PathMatcher.
     * @param root the root directory as Resource
     * @param pattern the sub pattern to match (below the root directory)
     * @return a mutable Set of matching Resource instances
     * @throws IOException in case of I/O errors
     * @see #findFiles
     */
    public Set<Resource> find( Resource root, String pattern ) throws IOException {
        try {
            File rootDir = root.getFile().getAbsoluteFile();
            return toFileSystemResource( findFiles(rootDir, pattern) );
        } catch ( FileNotFoundException e ) {
            log.debug("Cannot search for matching files underneath {} in the file system: {}", root, e.getMessage() );
            return Collections.emptySet();
        } catch ( Exception e ) {
            log.info("Failed to resolve {} in the file system: {}", root, e );
            return Collections.emptySet();
        }
    }

    private Set<Resource> toFileSystemResource( Collection<File> files ) {
        Set<Resource> result = new LinkedHashSet<>( files.size() );
        for ( File file : files ) {
            result.add( new FileSystemResource(file) );
        }
        return result;
    }

    /**
     * Retrieve files that match the given path pattern,
     * checking the given directory and its subdirectories.
     * @param rootDir the directory to start from
     * @param pattern the pattern to match against,
     * relative to the root directory
     * @return a mutable Set of matching Resource instances
     * @throws IOException if directory contents could not be retrieved
     */
    private Set<File> findFiles( File rootDir, String pattern ) throws IOException {

        if( Files.notExists(rootDir) || ! rootDir.isDirectory() || ! rootDir.canRead() )
            return Collections.emptySet();

        String fullPattern = Files.normalizeSeparator( rootDir.getAbsolutePath() );

        if ( ! pattern.startsWith("/") ) {
            fullPattern += "/";
        }

        fullPattern = fullPattern + Files.normalizeSeparator( pattern );

        Set<File> result = new LinkedHashSet<>(8);
        findFiles( fullPattern, rootDir, result );

        return result;

    }

    /**
     * Recursively retrieve files that match the given pattern,
     * adding them to the given result list.
     *
     * @param pattern   pattern to match against,
     *                  with prepended root directory path
     * @param dir       current directory
     * @param result Set of matching File instances to add to
     */
    protected void findFiles( String pattern, File dir, Set<File> result ) {
        for( File content : listDirectory(dir) ) {
            String currPath =  Files.normalizeSeparator( content.getAbsolutePath() );
            if ( content.isDirectory() && pathMatcher.matchStart(pattern, currPath + "/") ) {
                if( content.canRead() ) {
                    findFiles(pattern, content, result);
                }
            }
            if ( pathMatcher.match(pattern, currPath) ) {
                result.add(content);
            }
        }
    }

    private File[] listDirectory( File dir ) {
        File[] files = dir.listFiles();
        if ( files == null )
            return new File[0];
        Arrays.sort( files, Comparator.comparing(File::getName) );
        return files;
    }

}
