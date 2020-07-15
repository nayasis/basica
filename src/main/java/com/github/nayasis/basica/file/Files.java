package com.github.nayasis.basica.file;

import com.github.nayasis.basica.base.Classes;
import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.base.Types;
import com.github.nayasis.basica.exception.unchecked.InvalidArgumentException;
import com.github.nayasis.basica.exception.unchecked.UncheckedClassNotFoundException;
import com.github.nayasis.basica.exception.unchecked.UncheckedIOException;
import com.github.nayasis.basica.exception.unchecked.UncheckedMalformedUrlException;
import com.github.nayasis.basica.file.handler.FileFinder;
import com.github.nayasis.basica.file.handler.ZipFileHandler;
import com.github.nayasis.basica.file.worker.BufferWriter;
import com.github.nayasis.basica.file.worker.LineReader;
import com.github.nayasis.basica.model.NList;
import com.github.nayasis.basica.validation.Validator;
import lombok.experimental.UtilityClass;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

/**
 * File Utility
 *
 * @author nayasis@gmail.com
 *
 */
@UtilityClass
@SuppressWarnings( "rawtypes" )
public class Files {

    public final String FOLDER_SEPARATOR          = "/";
    public final String FOLDER_SEPARATOR_WINDOWS  = "\\";

    private final String UTF_8                    = StandardCharsets.UTF_8.toString();
    private final String CHARSET_AUTO_DETECT      = "";
    private final String REGEX_SEPARATOR_MULTIPLE = "(?!^)" + FOLDER_SEPARATOR + "+";
    private final String REGEX_SEPARATOR_LAST     = "(.+)" + FOLDER_SEPARATOR + "$";

    /**
     * delete file or directory
     *
     * @param path  file or directory path
     * @throws UncheckedIOException  if an I/O error occurs
     */
	public void delete( Object path ) throws UncheckedIOException {

        if( notExists(path) ) return;

        Path p = toPath( path );

		try {
            if( isDirectory(p) ) {
                java.nio.file.Files.walkFileTree( p, new SimpleFileVisitor<Path>() {
                    public FileVisitResult visitFile( Path file, BasicFileAttributes attributes ) throws IOException {
                        java.nio.file.Files.delete(file);
                        return CONTINUE;
                    }
                    public FileVisitResult postVisitDirectory( Path dir, IOException e ) throws IOException {
                        if ( e != null) throw e;
                        java.nio.file.Files.delete( dir );
                        return CONTINUE;
                    }
                });
            } else {
                java.nio.file.Files.delete( p );
            }
        } catch( IOException e ) {
        	throw new UncheckedIOException( e );
        }
	}

    /**
     * get file extension
     *
     * @param filepath  file name or full path
     * @return file extension
     */
    public String extension( Object filepath ) {

        String fileName = Strings.trim( filepath );

        int index = fileName.lastIndexOf( '.' );
        if( index < 0 ) return "";

        String ext = fileName.substring( index + 1 );
        if( ext.contains(File.pathSeparator) || ext.contains(FOLDER_SEPARATOR) ) return "";

        return ext;

    }

    /**
     * get file name
     *
     * @param filePath  file name or full path
     * @return file name only
     */
    public String name( Object filePath ) {
        Path p = toPath( filePath );
        try {
            return p.getName( p.getNameCount() - 1 ).toString();
        } catch ( Exception e ) {
            return Strings.trim( filePath );
        }
    }

    /**
     * get file name without it's file extension.
     *
     * @param filePath  file name or full path
     * @return file name only
     */
    public String nameWithoutExtension( Object filePath ) {
        String filename = name( filePath );
        int dot = filename.lastIndexOf( '.' );
        return dot < 0 ? filename : filename.substring( 0, dot );
    }

    /**
     * get parent path
     *
     * @param path  file or directory path
     * @return parent path
     */
    public Path parent( Object path ) {
        Path p = toPath( path );
        return p == null ? null : p.getParent();
    }
    
    /**
     * get current path's directory
     *
     * @param path File or Directory
     * @return return parent directory if path is file, return itself it path is directory.
     * @throws UncheckedIOException path is not invalid
     */
    public Path directory( Object path ) throws UncheckedIOException {
        if( notExists(path) ) return null;
        Path p = toPath( path );
        return isDirectory(p) ? p : p.getParent();
    }

    /**
     * normalize file separator to "/" and remove last "/" separator.
     *
     * <pre>
     *     Files.normalizeSeparator( "c:\\document/a/b\\c" );
     *       -&gt; "c:/document/a/b/c"
     * </pre>
     *
     * @param filePath  file path
     * @return file path having "/" separator only.
     */
    public String normalizeSeparator( Object filePath ) {
        return normalizeSeparator( filePath, true );
    }

    /**
     * normalize file separator to "/" and remove last "/" separator.
     *
     * <pre>
     *     Files.normalizeSeparator( "c:\\document/a/b\\c" );
     *       -&gt; "c:/document/a/b/c"
     * </pre>
     *
     * @param filePath              file path
     * @param removeLastSeparator   switch to remove last separator in file path
     * @return file path having "/" separator only.
     */
    public String normalizeSeparator( Object filePath, boolean removeLastSeparator ) {
        String path = Strings.trim( filePath )
            .replace( FOLDER_SEPARATOR_WINDOWS, FOLDER_SEPARATOR )
            .replaceAll( REGEX_SEPARATOR_MULTIPLE, FOLDER_SEPARATOR );
        if( removeLastSeparator )
            path = path.replaceFirst( REGEX_SEPARATOR_LAST, "$1" );
        return path;
    }

    /**
     * search files or directories.
     *
     * @param searchDir         root directory to search
     * @param includeFile       include file
     * @param includeDirectory  include directory
     * @param scanDepth         depth to scan
     * <pre>
     *   -1 : infinite
     *    0 : in searchDir itself
     *    1 : from searchDir to 1 depth sub directory
     *    2 : from searchDir to 2 depth sub directory
     *    ...
     * </pre>
     * @param matchingPattern   path matching pattern (glob expression. if not exists, add all result)
     * <pre>
     * ** : ignore directory variation
     * *  : filename LIKE search
     *
     * 1. **.xml           : all files having "xml" extension below searchDir and it's all sub directories.
     * 2. *.xml            : all files having "xml" extension in searchDir
     * 3. c:\home\*\*.xml  : all files having "xml" extension below 'c:\home\' and it's just 1 depth below directories.
     * 4. c:\home\**\*.xml : all files having "xml" extension below 'c:\home\' and it's all sub directories.
     *
     * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.
     * 2. ** It does the same as * but it crosses the directory boundaries.
     * 3. ?  It matches only one character for the given name.
     * 4. \  It helps to avoid characters to be interpreted as special characters.
     * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.
     * 6. {} It helps to matches the group of sub patterns.
     *
     * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
     * 2. *.* if file contains a dot, pattern will be matched.
     * 3. *.{java,txt} If file is either java or txt, path will be matched.
     * 4. abc.? matches a file which start with abc and it has extension with only single character.
     * </pre>
     *
     * @return  file or directory paths
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public List<Path> find( Object searchDir, boolean includeFile, boolean includeDirectory, int scanDepth, String... matchingPattern ) throws UncheckedIOException {

        if( notExists(searchDir) ) return new ArrayList<>();

        Path root = toPath( searchDir );
        root = isFile( root ) ? root.getParent() : root;

        FileFinder finder = new FileFinder( includeFile, includeDirectory, matchingPattern );
        scanDepth = ( scanDepth < 0 ) ? Integer.MAX_VALUE : ++scanDepth;

        try {
        	java.nio.file.Files.walkFileTree( root, EnumSet.noneOf(FileVisitOption.class), scanDepth, finder );
        } catch( IOException e ) {
	        throw new UncheckedIOException( e );
        }

        return finder.getFoundPaths();

    }

    /**
     * search files.
     *
     * @param searchDir         root directory to search
     * @param scanDepth         depth to scan
     * <pre>
     *   -1 : infinite
     *    0 : in searchDir itself
     *    1 : from searchDir to 1 depth sub directory
     *    2 : from searchDir to 2 depth sub directory
     *    ...
     * </pre>
     * @param matchingPattern   path matching pattern (glob expression. if not exists, add all result)
     * <pre>
     * ** : ignore directory variation
     * *  : filename LIKE search
     *
     * 1. **.xml           : all files having "xml" extension below searchDir and it's all sub directories.
     * 2. *.xml            : all files having "xml" extension in searchDir
     * 3. c:\home\*\*.xml  : all files having "xml" extension below 'c:\home\' and it's just 1 depth below directories.
     * 4. c:\home\**\*.xml : all files having "xml" extension below 'c:\home\' and it's all sub directories.
     *
     * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.
     * 2. ** It does the same as * but it crosses the directory boundaries.
     * 3. ?  It matches only one character for the given name.
     * 4. \  It helps to avoid characters to be interpreted as special characters.
     * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.
     * 6. {} It helps to matches the group of sub patterns.
     *
     * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
     * 2. *.* if file contains a dot, pattern will be matched.
     * 3. *.{java,txt} If file is either java or txt, path will be matched.
     * 4. abc.? matches a file which start with abc and it has extension with only single character.
     * </pre>
     *
     * @return  file or directory paths
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public List<Path> findFile( Object searchDir, int scanDepth, String... matchingPattern ) throws UncheckedIOException {
        return find( searchDir, true, false, scanDepth, matchingPattern );
    }

    /**
     * search directories.
     *
     * @param searchDir         root directory to search
     * @param scanDepth         depth to scan
     * <pre>
     *   -1 : infinite
     *    0 : in searchDir itself
     *    1 : from searchDir to 1 depth sub directory
     *    2 : from searchDir to 2 depth sub directory
     *    ...
     * </pre>
     * @param matchingPattern   path matching pattern (glob expression. if not exists, add all result)
     * <pre>
     * ** : ignore directory variation
     * *  : filename LIKE search
     *
     * 1. **.xml           : all files having "xml" extension below searchDir and it's all sub directories.
     * 2. *.xml            : all files having "xml" extension in searchDir
     * 3. c:\home\*\*.xml  : all files having "xml" extension below 'c:\home\' and it's just 1 depth below directories.
     * 4. c:\home\**\*.xml : all files having "xml" extension below 'c:\home\' and it's all sub directories.
     *
     * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.
     * 2. ** It does the same as * but it crosses the directory boundaries.
     * 3. ?  It matches only one character for the given name.
     * 4. \  It helps to avoid characters to be interpreted as special characters.
     * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.
     * 6. {} It helps to matches the group of sub patterns.
     *
     * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
     * 2. *.* if file contains a dot, pattern will be matched.
     * 3. *.{java,txt} If file is either java or txt, path will be matched.
     * 4. abc.? matches a file which start with abc and it has extension with only single character.
     * </pre>
     *
     * @return  file or directory paths
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public List<Path> findDirectory( Object searchDir, int scanDepth, String... matchingPattern ) throws UncheckedIOException {
        return find( searchDir, false, true, scanDepth, matchingPattern );
    }

    /**
     * search files or directories.
     *
     * @param searchDir         root directory to search
     * @param scanDepth         depth to scan
     * <pre>
     *   -1 : infinite
     *    0 : in searchDir itself
     *    1 : from searchDir to 1 depth sub directory
     *    2 : from searchDir to 2 depth sub directory
     *    ...
     * </pre>
     * @param matchingPattern   path matching pattern (glob expression. if not exists, add all result)
     * <pre>
     * ** : ignore directory variation
     * *  : filename LIKE search
     *
     * 1. **.xml           : all files having "xml" extension below searchDir and it's all sub directories.
     * 2. *.xml            : all files having "xml" extension in searchDir
     * 3. c:\home\*\*.xml  : all files having "xml" extension below 'c:\home\' and it's just 1 depth below directories.
     * 4. c:\home\**\*.xml : all files having "xml" extension below 'c:\home\' and it's all sub directories.
     *
     * 1. *  It matches zero , one or more than one characters. While matching, it will not cross directories boundaries.
     * 2. ** It does the same as * but it crosses the directory boundaries.
     * 3. ?  It matches only one character for the given name.
     * 4. \  It helps to avoid characters to be interpreted as special characters.
     * 5. [] In a set of characters, only single character is matched. If (-) hyphen is used then, it matches a range of characters. Example: [efg] matches "e","f" or "g" . [a-d] matches a range from a to d.
     * 6. {} It helps to matches the group of sub patterns.
     *
     * 1. *.java when given path is java , we will get true by PathMatcher.matches(path).
     * 2. *.* if file contains a dot, pattern will be matched.
     * 3. *.{java,txt} If file is either java or txt, path will be matched.
     * 4. abc.? matches a file which start with abc and it has extension with only single character.
     * </pre>
     *
     * @return  file or directory paths
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public List<Path> findAll( Object searchDir, int scanDepth, String... matchingPattern ) throws UncheckedIOException {
        return find( searchDir, true, true, scanDepth, matchingPattern );
    }

    /**
     * walk file tree.
     *
     * @param startPath start point to walk tree. (file or directory)
     * @param options   traversal options
     * @return Path stream
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Stream<Path> walk( Object startPath, FileVisitOption... options ) throws UncheckedIOException {
        return walk( startPath, Integer.MAX_VALUE, options );
    }

    /**
     * walk file tree.
     *
     * @param startPath start point to walk tree. (file or directory)
     * @param scanDepth depth to scan
     * <pre>
     *   -1 : infinite
     *    0 : in searchDir itself
     *    1 : from searchDir to 1 depth sub directory
     *    2 : from searchDir to 2 depth sub directory
     *    ...
     * </pre>
     * @param options   traversal options
     * @return Path stream
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Stream<Path> walk( Object startPath, int scanDepth, FileVisitOption... options ) throws UncheckedIOException {
        try {
            return java.nio.file.Files.walk( toPath(startPath), scanDepth, options );
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * check path exists
     *
     * @param   path    file or directory path
     * @param   options options indicating how symbolic links are handled
     * @return true if path exists
     */
    public boolean exists( Object path, LinkOption... options ) {
        try {
            Path p = toPath( path );
            return p != null && java.nio.file.Files.exists( p, options );
        } catch ( InvalidPathException e ) {
            return false;
        }
    }

    /**
     * check path exists
     *
     * @param   path    file or directory path
     * @param   options options indicating how symbolic links are handled
     * @return true if path exists
     */
    public boolean notExists( Object path, LinkOption... options ) {
        return ! exists( path, options );
    }

    /**
     * check path is file or not
     * @param path      path to check
     * @param options   options indicating how symbolic links are handled
     * @return true if path is file.
     */
    public boolean isFile( Object path, LinkOption... options ) {
        return exists(path) && java.nio.file.Files.isRegularFile( toPath(path), options );
    }

    /**
     * check path is directory or not
     * @param path      path to check
     * @param options   options indicating how symbolic links are handled
     * @return true if path is directory.
     */
    public boolean isDirectory( Object path, LinkOption... options ) {
        return exists(path) && java.nio.file.Files.isDirectory( toPath(path), options );
    }

    /**
     * make directory
     *
     * @param path directory path
     * @return created directory
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public Path makeDir( Object path ) throws UncheckedIOException {
        Path p = toPath( path );
    	if( isDirectory(p) ) return p;
    	try {
    		return java.nio.file.Files.createDirectories(p);
    	} catch( IOException e ) {
    		throw new UncheckedIOException( e );
    	}
    }

    /**
     * make file (if directory path is not exists, create it additionally.)
     *
     * @param filepath file path
     * @return created file
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public Path makeFile( Object filepath ) throws UncheckedIOException {
        Path p = toPath( filepath );
        if( isFile(p) ) return p;
        makeDir( p.getParent() );
    	try {
    		return java.nio.file.Files.createFile(p);
    	} catch( IOException e ) {
    		throw new UncheckedIOException( e );
    	}
    }

    /**
     * create empty file or update 'last updated timestamp' on file
     *
     * @param path  file path
     * @return  touched file
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path touchFile( Object path ) throws UncheckedIOException {
        Path p = toPath( path );
        if( notExists(p) ) {
            makeFile(p);
        } else {
            if( ! p.toFile().setLastModified( System.currentTimeMillis() ) );
                throw new UncheckedIOException( "Unable to modify timestamp of [{}]", p );
        }
        return p;
    }

    /**
     * make symbolic link.
     *
     * @param  source     source file
     * @param  target     link path
     * @return symbolic link
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public Path makeSymbolicLink( Object source, Object target ) throws UncheckedIOException {
        return makeSymbolicLink( source, target, false );
    }

    /**
     * make symbolic link.
     *
     * @param  source     source file
     * @param  target     link path
     * @param  overwrite  overwrite if given link exists
     * @return symbolic link
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public Path makeSymbolicLink( Object source, Object target, boolean overwrite ) throws UncheckedIOException {

        if( notExists(source) )
            throw new UncheckedIOException( "source({}) must be existed", source );

        if( overwrite ) delete(target);

        try {
            makeDir( parent(target) );
            return java.nio.file.Files.createSymbolicLink(toPath(target), toPath(source));
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    /**
     * make hard link.
     *
     * @param  source     source file
     * @param  target     link path
     * @return hard link
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public Path makeHardLink( Object source, Object target, boolean overwrite ) throws UncheckedIOException {

        if( notExists(source) )
            throw new UncheckedIOException( "source({}) must be existed", source );

        if( overwrite ) delete( target );
        try {
            makeDir( parent(target) );
            return java.nio.file.Files.createLink(toPath(target), toPath(source));
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    /**
     * Move file or directory
     *
     * @param  source     file or directory path to move
     * @param  target     file or directory path of target
     * @return target path
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path move( Object source, Object target ) throws UncheckedIOException {
        return move( source, target, false );
    }

    /**
     * Move file or directory
     *
     * @param  source     file or directory path to move
     * @param  target     file or directory path of target
     * @param  overwrite  overwrite if the target file exists
     * @return target path
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path move( Object source, Object target, boolean overwrite ) throws UncheckedIOException {

        if( notExists(source) )
            throw new UncheckedIOException( "source({}) must be existed", source );

        CopyOption[] option = overwrite
                ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }
                : new CopyOption[] {};

        Path src = toPath( source );
        Path trg = toPath( target );

        try {
            if( isDirectory(src) && exists(trg) ) {
                if( isFile(trg) ) {
                    throw new UncheckedIOException( "cannot overwrite non-directory '{}' with directory '{}'", target, source );
                } else {
                    return java.nio.file.Files.move( src, trg.resolve(src.getFileName()), option );
                }
            } else {
                makeDir( parent(trg) );
                return java.nio.file.Files.move( src, trg, option );
            }
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    /**
     * Copy file or directory
     *
     * @param  source     file or directory path to copy
     * @param  target     file or directory path of target
     * @return target path
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path copy( Object source, Object target ) throws UncheckedIOException {
        return copy( source, target, false );
    }

    /**
     * Copy file or directory
     *
     * @param  source     file or directory path to copy
     * @param  target     file or directory path of target
     * @param  overwrite  overwrite if the target file exists
     * @return target path
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path copy( Object source, Object target, boolean overwrite ) throws UncheckedIOException {

        if( notExists(source) )
            throw new UncheckedIOException( "source({}) is not existed.", source );

        CopyOption[] option = overwrite
                ? new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING }
                : new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES };

        Path src = toPath( source );
        Path trg = toPath( target );

        try {
            if( isDirectory(src) ) {
                if( isFile(trg) )
                    throw new UncheckedIOException( "cannot overwrite non-directory '{}' with directory '{}'", target, source );
                if( exists(trg) ) {
                    return copyTree( src, makeDir(trg.resolve(src.getFileName())), overwrite );
                } else {
                    return copyTree( src, makeDir(trg), overwrite );
                }
            } else {
                if( isDirectory(trg) ) {
                    return java.nio.file.Files.copy( src, trg.resolve( src.getFileName() ), option );
                } else {
                    makeDir( parent(trg) );
                    return java.nio.file.Files.copy( src, trg, option );
                }
            }
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    /**
     * Copy directory tree
     *
     * @param  source     root directory to copy
     * @param  target     root directory to be copied
     * @return target path
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path copyTree( Object source, Object target ) throws UncheckedIOException {
        return copyTree( source, target, false );
    }

    /**
     * Copy directory tree
     *
     * @param  source     root directory to copy
     * @param  target     root directory to be copied
     * @param  overwrite  overwrite if the target file exists
     * @return target path
     * @throws UncheckedIOException if an I/O error occurs
     */
    public Path copyTree( Object source, Object target, boolean overwrite ) throws UncheckedIOException {

        if( ! isDirectory(source) )
            throw new UncheckedIOException( "source path({}) must be directory", source );

        CopyOption[] option = overwrite
            ? new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING }
            : new CopyOption[] { StandardCopyOption.COPY_ATTRIBUTES };

        Path src = toPath( source );
        Path trg = toPath( target );

        try {
            java.nio.file.Files.walkFileTree( src, new SimpleFileVisitor<Path>() {
                public FileVisitResult preVisitDirectory( Path dir, BasicFileAttributes attrs ) throws IOException {
                    makeDir( trg.resolve( src.relativize( dir ) ) );
                    return CONTINUE;
                }
                public FileVisitResult visitFile( Path file, BasicFileAttributes attrs ) throws IOException {
                    java.nio.file.Files.copy( file, trg.resolve( src.relativize(file) ), option );
                    return CONTINUE;
                }
            });

            return trg;

        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    /**
     * Read serialized objects data stored in file
     *
     * @param filePath  path of file which store serialized objects data.
     * @param <T>       expected class of return
     * @return object bean to serialize
     * @throws UncheckedClassNotFoundException   if object's class is not defined
     * @throws UncheckedIOException     if an I/O error occurs
     */
    public <T> T readObject( Object filePath ) throws UncheckedClassNotFoundException, UncheckedIOException {

        if( ! isFile(filePath) )
            throw new UncheckedIOException( "file path({}) must be existed", filePath );

        try(
            InputStream file   = new FileInputStream( toPath(filePath).toFile() );
            InputStream buffer = new GZIPInputStream( file );
            ObjectInput input  = new ObjectInputStream( buffer )
        ) {
            Object val = input.readObject();
            return val == null ? null : (T) val;
        } catch( ClassNotFoundException e ) {
            throw new UncheckedClassNotFoundException( e );
        } catch( IOException e ) {
        	throw new UncheckedIOException( e );
        }

    }

    /**
     * write bean to file
     *
     * @param filePath  full file path
     * @param bean      bean to write
     * @throws UncheckedIOException  file I/O exception
     */
    public void writeObject( Object filePath, Object bean ) throws UncheckedIOException {

        makeFile( filePath );

        try(
            OutputStream file   = new FileOutputStream( toPath(filePath).toFile() );
            OutputStream buffer = new GZIPOutputStream( file );
            ObjectOutput output = new ObjectOutputStream( buffer )
        ) {
            output.writeObject( bean );
            output.flush();
        } catch( IOException e ) {
            delete( filePath );
            throw new UncheckedIOException( e );
        } catch ( Exception e ) {
            delete( filePath );
            throw e;
        }

    }

    /**
     * remove extension from path
     *
     * @param filePath  file path (or file name)
     * @return file path which has no extension.
     */
    public String removeExtension( Object filePath ) {

        Path p = toPath( filePath );
        if( p == null ) return null;

        String path = p.toString();

        int index = path.lastIndexOf( '.' );
        if( index < 0 ) return path;

        String ext = path.substring( index + 1 );
        if( ext.contains(File.pathSeparator) || ext.contains("/") ) return path;

        return path.substring( 0, index );

    }

    /**
     * Read text from file
     *
     * @param path file path or URL or inputstream
     * @return text
     * @throws UncheckedIOException if I/O error occurs
     */
    public String readFrom( Object path ) throws UncheckedIOException {
        return readFrom( toInputStream(path), CHARSET_AUTO_DETECT );
    }

    /**
     * Read text from file
     *
     * @param path      file path or URL or inputstream
     * @param charset   character set (UTF-8, EUC-KR, ... )
     * @return text
     * @throws UncheckedIOException if I/O error occurs
     */
    public String readFrom( Object path, String charset ) throws UncheckedIOException {
        return readFrom( toInputStream(path), charset );
    }

    /**
     * Read text from file
     *
     * <pre>
     *   StringBuilder sb = new StringBuilder();
     *
     *   readFrom( filePath, readLine -&gt; {
     *     sb.append( readLine ).append( '\n' );
     *   });
     *
     *   System.out.println( sb );
     * </pre>
     *
     * @param path      file path or URL or inputstream
     * @param reader    reader to treat line in text
     * @throws UncheckedIOException if I/O error occurs
     */
    public void readFrom( Object path, LineReader reader ) throws UncheckedIOException {
        readFrom( toInputStream(path), reader, CHARSET_AUTO_DETECT );
    }

    /**
     * Read text from file
     *
     * <pre>
     *   StringBuilder sb = new StringBuilder();
     *
     *   readFrom( filePath, readLine -&gt; {
     *     sb.append( readLine ).append( '\n' );
     *   }, charset );
     *
     *   System.out.println( sb );
     * </pre>
     *
     * @param path      file path or URL or inputstream
     * @param reader    worker to treat line in text
     * @param charset   character set (UTF-8, EUC-KR, ... )
     * @throws UncheckedIOException if I/O error occurs
     */
    public void readFrom( Object path, LineReader reader, String charset ) throws UncheckedIOException {
        readFrom( toInputStream(path), reader, charset );
    }

    /**
     * convert to inputstream
     *
     * @param path  filepath or URL
     * @return inputstream
     */
    public InputStream toInputStream( Object path ) {
        if( Validator.isEmpty(path) ) return null;
        try {
            if( path instanceof InputStream ) {
                return (InputStream) path;
            } else if( path instanceof URL ) {
                return ((URL)path).openStream();
            } else {
                return new FileInputStream( Strings.trim(path) );
            }
        } catch ( IOException e ) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * convert to outputstream
     *
     * @param file filepath (if file is not exist, makes new one.)
     * @return outputstream
     */
    public FileOutputStream toOutputStream( Object file ) {
        try {
            return new FileOutputStream( makeFile(file).toFile() );
        } catch( FileNotFoundException e ) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * read contents from stream
     *
     * @param inputStream   inputstream to read
     * @return whole text contents
     * @throws UncheckedIOException if I/O error occurs.
     */
    public String readFrom( InputStream inputStream ) throws UncheckedIOException {
        return readFrom( inputStream, CHARSET_AUTO_DETECT );
    }

    /**
     * read contents from stream
     *
     * @param inputStream   inputstream to read
     * @param charset       character set (default: auto detect)
     * @return whole text contents
     * @throws UncheckedIOException if I/O error occurs.
     */
    public String readFrom( InputStream inputStream, String charset ) throws UncheckedIOException {
        StringBuilder sb = new StringBuilder();
        readFrom( inputStream, readLine -> sb.append( readLine ).append( '\n' ), charset );
        return sb.toString();
    }

    /**
     * read contents from stream
     *
     * @param inputStream   inputstream to read
     * @param reader        worker to read line text
     * @throws UncheckedIOException if I/O error occurs.
     */
    public void readFrom( InputStream inputStream, LineReader reader ) throws UncheckedIOException {
        readFrom( inputStream, reader, null );
    }

    /**
     * read contents from stream
     *
     * @param inputStream   inputstream to read
     * @param reader        worker to read line text
     * @param charset       character set (default: auto detect)
     * @throws UncheckedIOException if I/O error occurs.
     */
    public void readFrom( InputStream inputStream, LineReader reader, String charset ) throws UncheckedIOException {

        if( inputStream == null ) return;

        BufferedInputStream mis = new BufferedInputStream( inputStream );

        if( Strings.isEmpty(charset) ) {
            charset = getCharset( mis );
        }

        try(
            BufferedReader br = new BufferedReader( new InputStreamReader(mis, charset) )
        ) {
            String line;
            while( ( line = br.readLine() ) != null ) {
                reader.read( line );
            }
        } catch( IOException e ) {
            throw new UncheckedIOException(e);
        } finally {
            Files.close( mis );
        }
    }

    /**
     * read contents from ClassLoader resource
     *
     * @param url  classloader URL or URL path
     * @return whole text contents
     */
    public String readResourceFrom( Object url ) {
        return readResourceFrom( url, CHARSET_AUTO_DETECT );
    }

    /**
     * read contents from ClassLoader resource
     *
     * @param url       classloader URL or URL path
     * @param charset   character set (default: auto detect)
     * @return whole text contents
     */
    public String readResourceFrom( Object url, String charset ) {
        StringBuilder sb = new StringBuilder();
        readResourceFrom( url, readLine -> sb.append( readLine ).append( '\n' ), charset );
        return sb.toString();
    }

    /**
     * read contents from ClassLoader resource
     *
     * @param url       classloader URL or URL path
     * @param reader    worker to read line text
     */
    public void readResourceFrom( Object url, LineReader reader ) {
        readResourceFrom( url, reader, CHARSET_AUTO_DETECT );
    }

    /**
     * read contents from ClassLoader resource
     *
     * @param url       classloader URL or URL path
     * @param reader    worker to read line text
     * @param charset   character set (default: auto detect)
     */
    public void readResourceFrom( Object url, LineReader reader, String charset ) {
        InputStream inputStream = toResourceStream( url );
        if( inputStream != null ) {
            readFrom( inputStream, reader, charset );
        }
    }

    private static InputStream toResourceStream( Object url ) {
        if( url == null ) {
            return null;
        } else if( url instanceof URL ) {
            return Classes.getResourceStream( (URL) url );
        } else if( Types.isStringLike(url) ) {
            return Classes.getResourceStream( Classes.getResource( Strings.trim(url)) );
        } else {
            return Classes.getResourceStream( toURL(url) );
        }
    }

    /**
     * Write text to file
     *
     * <pre>
     *  writeTo( filePath, writer -&gt; {
     *    writer.write( text );
     *  }, "EUC-KR" );
     * </pre>
     *
     * @param filePath  file path to write text
     * @param writer    writer to write buffer
     * @param charset   encoding character set (default : UTF-8)
     * @throws UncheckedIOException if an I/O exception occurs
     */
    public void writeTo( Object filePath, BufferWriter writer, String charset ) throws UncheckedIOException {
        makeFile( filePath );
        try(
            FileOutputStream fos    = new FileOutputStream( toPath(filePath).toFile() );
            BufferedWriter   buffer = new BufferedWriter( new OutputStreamWriter( fos, Validator.nvl(charset,UTF_8) ) )
        ) {
            writer.write( buffer );
        } catch( IOException e ) {
            throw new UncheckedIOException(e);
        }

    }

    /**
     * Write text to file (encoding character set is "UTF-8")
     *
     * <pre>
     *  writeTo( filePath, writer -&gt; {
     *    writer.write( text );
     *  });
     * </pre>
     *
     * @param filePath  file path to write text
     * @param writer   writer to write line
     * @throws UncheckedIOException if an I/O exception occurs
     */
    public void writeTo( Object filePath, BufferWriter writer ) throws UncheckedIOException {
        writeTo( filePath, writer, null );
    }

    /**
     * Write text to file
     *
     * @param filePath  file full path
     * @param text      text to write
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public void writeTo( Object filePath, String text ) throws UncheckedIOException {
        writeTo( filePath, text, null );
    }

    /**
     * Write text to file
     *
     * @param filePath  file full path
     * @param text      text to write
     * @param charset   encoding character set
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public void writeTo( Object filePath, final String text, String charset ) throws UncheckedIOException {
        writeTo( filePath, writer -> writer.write( text ), charset );
    }

    /**
     * Write binary data to file
     *
     * @param filepath  file to write
     * @param binary    binary data to write
     * @throws UncheckedIOException  if an I/O error occurs
     */
    public void writeTo( Object filepath, byte[] binary ) throws UncheckedIOException {
    	FileOutputStream stream = null;
    	try {
    		stream = new FileOutputStream( makeFile(filepath).toFile() );
    	    stream.write( binary );
    	    stream.flush();
    	} catch( IOException e ) {
	        throw new UncheckedIOException(e);
        } finally {
    	    close( stream );
    	}
    }

    /**
     * read byte array from stream
     *
     * @param path  file path or URL
     * @return byte array
     * @throws UncheckedIOException if I/O error occurs.
     */
    public byte[] readBytes( Object path ) throws UncheckedIOException {
        try {
            return java.nio.file.Files.readAllBytes( toPath(path) );
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * Write data as CSV file
     *
     * @param filepath  file to write
     * @param data      data represents excel sheet
     * @param delimiter delimiter to seperate data
     * @param charset   character set
     * @throws UncheckedIOException if an I/O exception occurs
     */
    public void writeCsvTo( Object filepath, List data, String delimiter, String charset ) throws UncheckedIOException {
        writeCsvTo( filepath, new NList( data ), delimiter, charset );
    }

    /**
     * Write data as CSV file
     *
     * @param file      file to write
     * @param data      data represents excel sheet
     * @param delimiter delimiter to seperate data
     * @param charset   character set
     * @throws UncheckedIOException if an I/O exception occurs
     */
    public void writeCsvTo( Object file, NList data, String delimiter, String charset ) throws UncheckedIOException {

    	writeTo( file, writer -> {

            writer.write( Strings.join( data.keySet(), delimiter ) );
            writer.write( '\n' );

            for( int row = 0; row < data.size(); row++ ) {

                List<String> temp = new ArrayList<>();

                for( int col = 0; col < data.keySize(); col++ ) {
                    temp.add( Strings.nvl( data.getData( row, col ) ) );
                }

                writer.write( Strings.join( temp, delimiter ) );
                writer.write( '\n' );

            }

        }, charset );

    }

    /**
     * resolve path
     *
     * <pre>
     *   Files.resolvePath( "/root/bin", ".././temp" ); -&gt; "/root/temp"
     *   Files.resolvePath( "/root/bin", "./temp" );    -&gt; "/root/bin/temp"
     *   Files.resolvePath( "/root/bin", "temp" );      -&gt; "/root/bin/temp
     *   Files.resolvePath( "/root/bin", "/temp" );     -&gt; "/temp"
     * </pre>
     *
     * @param basePath   base path
     * @param targetPath other path
     * @return absolute path
     * @throws UncheckedIOException base path is not existed
     */
    public String resolvePath( Object basePath, Object targetPath ) throws UncheckedIOException {
    	return normalizeSeparator( toPath(basePath).resolve(Strings.trim(targetPath)).normalize().toString() );
    }

    /**
     * Convert absolute path to relative path
     *
     * <pre>
     * Files.toRelativePath( "/home/user/nayasis", "/home/user/test/abc" );
     *
     * → "../test/abc"
     * </pre>
     *
     * @param basePath   base path
     * @param targetPath target path to convert
     * @return relative path
     * @throws UncheckedIOException    base path is not existed
     */
    public String relativePath( Object basePath, Object targetPath ) throws UncheckedIOException {
    	return normalizeSeparator( toPath(basePath).relativize( toPath(targetPath) ).toString() );
    }

    /**
     * Zip file or directory
     *
     * @param source    file or directory to zip
     * @param zipfile   archive file
     * @param charset   character set (default : OS default)
     */
    public void zip( Object source, Object zipfile, Charset charset ) {
        zipHandler().zip( toPath(source).toFile(), makeFile(zipfile).toFile(), Validator.nvl(charset,Charset.defaultCharset()) );
    }

    /**
     * Zip file or directory
     *
     * @param source    file or directory to zip
     * @param zipfile   archive file
     */
    public void zip( Object source, Object zipfile ) {
        zip( source, zipfile, null );
    }

    /**
     * Unzip file
     *
     * @param zipfile       zip file
     * @param decompressDir directory to write decompressed files
     * @param charset       character set (default : OS default)
     */
    public void unzip( Object zipfile, Object decompressDir, Charset charset ) {
        zipHandler().unzip( toPath(zipfile).toFile(), makeDir(decompressDir).toFile(), Validator.nvl(charset,Charset.defaultCharset()) );
    }

    /**
     * Unzip file
     *
     * @param zipfile       zip file
     * @param decompressDir directory to write decompressed files
     */
    public void unzip( Object zipfile, Object decompressDir ) {
        unzip( zipfile, decompressDir, null );
    }

    private ZipFileHandler zipHandler() throws NoClassDefFoundError {
        try {
            return new ZipFileHandler();
        } catch( Throwable e ) {
            String errorMessage =
                "you must import [Apache Common Compress Library] to handle zip file.\n" +
                        "\t- Maven dependency is like below.\n" +
                        "\t\t<dependency>\n" +
                        "\t\t  <groupId>org.apache.commons</groupId>\n" +
                        "\t\t  <artifactId>commons-compress</artifactId>\n" +
                        "\t\t  <version>1.19</version>\n" +
                        "\t\t</dependency>\n";
            throw new NoClassDefFoundError( errorMessage );
        }
    }

    /**
     * read file attributes
     *
     * @param filePath  file path
     * @return basic attributes
     * @throws UncheckedIOException occurs when raise file I/O errir
     */
    public BasicFileAttributes attributes( Object filePath ) throws UncheckedIOException {
        if( notExists(filePath) ) return null;
        try {
            return java.nio.file.Files.readAttributes( toPath(filePath), BasicFileAttributes.class );
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * get user home directory path
     *
     * @return user home path
     */
    public String userHome() {
        return normalizeSeparator( System.getProperty("user.home") );
    }

    /**
     * get root directory where program is running
     *
     * @return root directory
     */
    public String rootPath() {
        return normalizeSeparator( Paths.get("").toAbsolutePath() );
    }

    /**
     * get root directory where given class is running
     *
     * @param klass given class to detect root path
     * @return root directory
     */
    public String rootPath( Class klass ) {
        URL location = Classes.getRootLocation( klass );
        return new File( location.getFile() ).getAbsolutePath();
    }

    /**
     * detect file's character set
     *
     * @param filePath  file path
     * @return  detected character set (UTF-8, EUC-KR, EUC-JP, ... )
     */
    public String getCharset( Object filePath ) throws UncheckedIOException {
        InputStream inputStream = null;
        try {
            inputStream = toInputStream( filePath );
            return getCharset( new BufferedInputStream(inputStream) );
        } finally {
            close( inputStream );
        }
    }

    /**
     * detect file's character set.
     *
     * @param inputStream  input stream
     * @return  detected character set (UTF-8, EUC-KR, EUC-JP, ... )
     */
    public String getCharset( InputStream inputStream ) throws UncheckedIOException {

        byte[] buf = new byte[4096];

        UniversalDetector detector = null;

        try {

            inputStream.mark( 1 << 24 );

            detector = new UniversalDetector( null );

            int nread;
            while ( (nread = inputStream.read(buf)) > 0 && ! detector.isDone() ) {
                detector.handleData( buf, 0, nread );
            }
            detector.dataEnd();

            String charset = detector.getDetectedCharset();

            return charset == null ? "UTF-8" : charset;

        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        } finally {
            if( detector    != null ) try { detector.reset(); } catch( Exception ignored ) {}
            if( inputStream != null ) try { inputStream.reset(); } catch( IOException ignored ) {}
        }

    }

    /**
     * check if file is hidden.
     *
     * @param path  file or directory path
     * @return  true if file is hidden
     * @throws UncheckedIOException if I/O error occurs.
     */
    public boolean isHidden( Object path ) throws UncheckedIOException {
        try {
            return exists(path) && java.nio.file.Files.isHidden( toPath(path) );
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    /**
     * check if file is readable.
     *
     * @param path  file path
     * @return  true if file is readable
     * @throws UncheckedIOException if I/O error occurs.
     */
    public boolean isReadable( Object path ) {
        return exists(path) && java.nio.file.Files.isReadable( toPath(path) );
    }

    /**
     * check if file is writable.
     *
     * @param path  file path
     * @return  true if file is writable
     * @throws UncheckedIOException if I/O error occurs.
     */
    public boolean isWritable( Object path ) {
        return exists(path) && java.nio.file.Files.isWritable( toPath(path) );
    }

    /**
     * check if file is executable.
     *
     * @param path  file path
     * @return  true if file is executable
     * @throws UncheckedIOException if I/O error occurs.
     */
    public boolean isExecutable( Object path ) {
        return exists(path) && java.nio.file.Files.isExecutable( toPath(path) );
    }

    /**
     * check if file is symbolic link.
     *
     * @param path  file or directory path
     * @return  true if file is symbolic link.
     * @throws UncheckedIOException if I/O error occurs.
     */
    public boolean isSymbolicLink( Object path ) {
        return exists(path) && java.nio.file.Files.isSymbolicLink( toPath(path) );
    }

    /**
     * close the given closable and swallow I/O exception that may occur.
     *
     * @param c closeable
     */
    public void close( Closeable c ) {
        if( c != null ) {
            try {
                c.close();
            } catch ( IOException ignored ) {}
        }
    }

    /**
     * convert to Path
     *
     * @param path  file or directory path (acceptable: File,Path,String,StringBuffer,StringBuilder)
     * @return converted path
     * @throws InvalidPathException if path expression is not valid.
     * @throws InvalidArgumentException if type of path parameter is not acceptable.
     */
    public Path toPath( Object path ) throws InvalidPathException, InvalidArgumentException {

        if( path == null ) return null;

        if( path instanceof Path ) return (Path) path;
        if( path instanceof File ) return ((File)path).toPath();
        if( Types.isStringLike(path) ) return Paths.get(path.toString().trim());

        if( path instanceof URI ) return Paths.get( ((URI)path) );
        try {
            if( path instanceof URL  ) return Paths.get( ((URL)path).toURI() );
        } catch ( URISyntaxException e ) {
            throw new InvalidArgumentException( "Could not cast to Path type (from:{})", path );
        }

        throw new InvalidArgumentException( "Invalid type.(current: {}, acceptable: Path,File,String)", path.getClass() );

    }

    /**
     * convert to URL
     *
     * @param path  URL path (acceptable: URL,File,Path,String,StringBuffer,StringBuilder)
     * @return converted URL
     * @throws UncheckedMalformedUrlException   if URL is malformed.
     * @throws InvalidArgumentException if type of path parameter is not acceptable.
     */
    public URL toURL( Object path ) throws UncheckedMalformedUrlException, InvalidArgumentException {

        if( path == null ) return null;

        if( path instanceof URL ) return (URL) path;

        Path p;
        try {
            p = toPath( path );
        } catch ( InvalidPathException e ) {
            throw new InvalidArgumentException( "Invalid type.(current: {}, acceptable: URL,Path,File,String)", path.getClass() );
        }
        if( p == null ) return null;

        try {
            return p.toUri().toURL();
        } catch ( MalformedURLException e ) {
            throw new UncheckedMalformedUrlException(e);
        }

    }

}