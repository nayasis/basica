package io.nayasis.common.file.handler;

import io.nayasis.common.base.Strings;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * File Visitor
 *
 * @author nayasis@gmail.com
 */
public class FileFinder extends SimpleFileVisitor<Path> {

    private boolean           checkPattern;
    private boolean           includeDir;
    private boolean           includeFile;
    private Set<PathMatcher>  matchers;
    private List<String>      result = new ArrayList<>();

    /**
     * 기본 생성자
     *
     * @param includeFile   파일 포함여부
     * @param includeDir    디렉토리 포함여부
     * @param pattern       이름을 검사할 패턴식
     */
    public FileFinder( boolean includeFile, boolean includeDir, String... pattern ) {

        this.matchers     = toPathMacher( pattern );
        this.checkPattern = ( matchers.size() != 0 );
        this.includeFile  = includeFile;
        this.includeDir   = includeDir;

        // 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
        // 2. *.* if file contains a dot, pattern will be matched.
        // 3. *.{java,txt} If file is either java or txt, path will be matched.
        // 4. abc.? matches a file which start with abc and it has extension with only single character.

    }

    /**
     * 파일명칭의 패턴을 검사한다.
     *
     * @param file  검사할 파일명
     */
    private void find( Path file ) {
        if( checkPattern ) {
            for( PathMatcher matcher : matchers ) {
        		if( matcher.matches( file ) ) {
                    add( file );
        			return;
        	    }
            }
        } else {
        	add( file );
        }
    }

    private void add( Path path ) {
    	boolean isDir = Files.isDirectory( path );
    	if( (includeFile && ! isDir) || (includeDir && isDir) ) {
    		result.add( path.toAbsolutePath().toString() );
    	}
    }

    /**
     * 파일 검색결과를 얻는다.
     *
     * @return 검색결과
     */
    public List<String> getFoundPaths() {
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.file.SimpleFileVisitor#preVisitDirectory(java.lang.Object,
     * java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) {
        if( includeDir ) find( dir );
        return FileVisitResult.CONTINUE;
    }

    /*
     * (non-Javadoc)
     * @see java.nio.file.SimpleFileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
     */
    @Override
    public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) {
        if( includeFile ) find( file );
        return FileVisitResult.CONTINUE;
    }

    /**
     * convert patterns to glob PathMatcher
     *
     * @param patterns patterns
     * @return glob PathMatcher
     */
    public static Set<PathMatcher> toPathMacher( String... patterns ) {

        Set<PathMatcher> matchers = new HashSet<>();

        for( String pattern : new HashSet<>( Arrays.asList( patterns )) ) {
            if( Strings.isEmpty( pattern ) ) continue;
            if( ! pattern.contains("/") && ! pattern.contains("\\") ) {
                pattern = "**/" + pattern;
            }
            matchers.add( FileSystems.getDefault().getPathMatcher( "glob:" + pattern ) );
        }

        return matchers;

    }

}
