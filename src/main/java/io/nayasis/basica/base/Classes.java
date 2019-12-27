package io.nayasis.basica.base;


import io.nayasis.basica.cache.implement.LruCache;
import io.nayasis.basica.exception.unchecked.UncheckedClassCastException;
import io.nayasis.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.basica.file.Files;
import io.nayasis.basica.validation.Validator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Class Utility
 *
 * @author nayasis@gmail.com
 */
@Slf4j
@UtilityClass
public class Classes {

	private LruCache<Class<?>,Set<Class<?>>> CACHE_CONTAINED_PARENT = new LruCache<>( 256 );

	private Objenesis factory = new ObjenesisStd();

	/**
	 * Get class for name
	 *
	 * @param className	class name
	 * @throws ClassNotFoundException if class is not founded in class loader.
	 * @return class for name
	 */
	public Class<?> getClass( String className ) throws ClassNotFoundException {

		if( Strings.isEmpty(className) ) throw new ClassNotFoundException( String.format( "Expected class name is [%s].", className ) );

		className = className.replaceAll( " ", "" );

		int invalidCharacterIndex = className.indexOf( '<' );
		if( invalidCharacterIndex >= 0 ) {
			className = className.substring( 0, invalidCharacterIndex );
		}

		ClassLoader classLoader = getClassLoader();
		try {
	        return classLoader.loadClass( className );
        } catch( ClassNotFoundException e ) {
        	throw new ClassNotFoundException( String.format( "Expected class name is [%s].", className ), e );
        }

	}

	/**
	 * Get class loader
	 *
	 * @return get class loader in current thread.
	 */
	public ClassLoader getClassLoader() {

		try {
			return Thread.currentThread().getContextClassLoader();
		} catch( Throwable e ) {}

		// if can not access thread context
		try {
			return Classes.class.getClassLoader();
		} catch( Throwable e ) {}

		// if bootstrap classloader
		try {
			return ClassLoader.getSystemClassLoader();
		} catch( Throwable e ) {
			// maybe caller can live with null.
			return null;
		}

	}

	/**
	 * Get Class from Type like class's generic type.
	 *
	 * <pre>
	 *
	 * Type type = this.getClass().getGenericSuperclass();
	 *
	 * Class&lt;?&gt; klass = Classes.getClass( type );
	 * </pre>
	 *
	 * @param type class type
	 * @return class by generic type
	 * @throws ClassNotFoundException if class is not founded in class loader.
	 */
	public Class<?> getClass( Type type ) throws ClassNotFoundException {

		if( type == null ) return Object.class;

		String typeInfo = type.toString();
		int startIndex = typeInfo.indexOf( '<' );
		if( startIndex < 0 ) return Object.class;

		String typeClassName = typeInfo.substring( startIndex + 1, typeInfo.length() - 1 );
		startIndex = typeClassName.indexOf( '<' );
		if( startIndex >= 0 ) typeClassName = typeClassName.substring( 0, startIndex );

		return getClass( typeClassName );

	}

	/**
	 * get generic class from another class.
	 *
	 * it only works when used in class itself.
	 *
	 * <pre>
	 * public class Test&lt;T&gt; {
	 *     public Test() {
	 *         Class genericClass = Classes.getGenericClass( this.getClass() );
	 *         -&gt; it returns type of <b>T</b> exactly.
	 *     }
	 * }
	 *
	 * Test&lt;HashMap&gt; test = new Test&lt;&gt;();
	 * Class genericClass = Classes.getGenericClass( test.getClass() );
	 * -&gt; it returns <b>Object.class</b> only because instance has no information about Generic.
	 * </pre>
	 *
	 * @param klass class to inspect
	 * @return generic class of klass
     */
	public Class getGenericClass( Class klass ) {
		if( klass == null ) return null;
		try {
			Type genericSuperclass = klass.getGenericSuperclass();
			Type[] types = ( (ParameterizedType) genericSuperclass ).getActualTypeArguments();
			return (Class) types[ 0 ];
		} catch( Exception e ) {
			return Object.class;
		}
	}

	public Class<?> getClass( Object object ) {
		return ( object == null ) ? null : object.getClass();
	}

	public Set<Class<?>> findParents( Class<?> klass ) {

		if( CACHE_CONTAINED_PARENT.contains( klass ) )
			return CACHE_CONTAINED_PARENT.get( klass );

		Set<Class<?>> parents = new LinkedHashSet<>();
		findParents( klass, parents );
		CACHE_CONTAINED_PARENT.put( klass, parents );

		return parents;

	}

	private void findParents( Class<?> klass, Set<Class<?>> set ) {
		Class<?> superclass = klass.getSuperclass();
		if( superclass != null && superclass != Object.class && ! set.contains(superclass) ) {
			set.add( superclass );
			findParents( superclass, set );
		}
		Class<?>[] interfaces = klass.getInterfaces();
		if( interfaces.length != 0 ) {
			for( Class<?> i : interfaces ) {
				set.add( i );
				findParents( i, set );
			}
		}
	}

	public <T> T createInstance( Class<T> klass ) throws UncheckedClassCastException {
		try {
			return klass.newInstance();
		} catch( Exception e ) {
			try {
				return factory.newInstance( klass );
			} catch( Exception finalException ) {
				throw new UncheckedClassCastException( finalException );
			}
        }
	}

    public <T> T createInstance( Type type ) throws ClassNotFoundException {
		return (T) createInstance( getClass(type) );
	}

	/**
	 * Check if a class was extended or implemented by found class
	 *
	 * @param klass  		class to inspect
	 * @param extendKlass	class to be extended in inspect class
	 * @return true if inspect class is extended of implemented by found class
	 */
	public boolean isExtendedBy( Class<?> klass, Class<?>... extendKlass ) {
		if( klass == null || extendKlass.length == 0 ) return false;
		Set<Class<?>> parents = findParents( klass );
		for( Class<?> target : extendKlass ) {
			if( klass == target ) return true;
			if( Modifier.isFinal(target.getModifiers()) ) continue;
			if( parents.contains( target ) ) return true;
		}
		return false;
	}

	/**
	 * Check if a class was extended or implemented by found class
	 *
	 * @param klass  		class to inspect
	 * @param extendKlass	class to be extended in inspect class
	 * @return true if inspect class is extended of implemented by found class
	 */
	public boolean isExtendedBy( Class<?> klass, Collection<Class<?>> extendKlass ) {
		if( klass == null || Validator.isEmpty(extendKlass) ) return false;
		Set<Class<?>> parents = findParents( klass );
		for( Class<?> target : extendKlass ) {
			if( klass == target ) return true;
			if( Modifier.isFinal(target.getModifiers()) ) continue;
			if( parents.contains( target ) ) return true;
		}
		return false;
	}

	/**
	 * Check if an instnace was extended or implemented by found class
	 *
	 * @param instance  instance to inspect
	 * @param extendKlass	   class to be extended in inspect instance
	 * @return true if inspect instance is extended of implemented by found class
	 */
	public boolean isExtendedBy( Object instance, Class<?>... extendKlass ) {
		return instance != null && isExtendedBy( instance.getClass(), extendKlass );
	}

	/**
	 * Check if an instnace was extended or implemented by found class
	 *
	 * @param instance  instance to inspect
	 * @param extendKlass	   class to be extended in inspect instance
	 * @return true if inspect instance is extended of implemented by found class
	 */
	public boolean isExtendedBy( Object instance, Collection<Class<?>> extendKlass ) {
		return instance != null && isExtendedBy( instance.getClass(), extendKlass );
	}

	/**
	 * Check resource exists
	 * @param path resource path
	 * @return true if resource is exist in class path.
	 */
	public boolean isResourceExisted( String path ) {
		return getClassLoader().getResource( refineResourceName(path) ) != null;
	}

	/**
	 * Get resource as stream
	 *
	 * @param path	resource path
	 * @return resource input stream
	 */
	public InputStream getResourceAsStream( String path ) {
		return getClassLoader().getResourceAsStream( refineResourceName(path) );
	}

	/**
	 * Get resource as stream
	 *
	 * @param url	resource URL
	 * @return resource input stream
	 */
	public InputStream getResourceAsStream( URL url ) throws UncheckedIOException {
		if( url == null ) return null;
		try {
			return url.openStream();
		} catch ( IOException e ) {
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * get resource
	 *
	 * @param path	resource path
	 * @return
	 */
	public URL getResource( String path ) {
		return getResource( null, path );
	}

	/**
	 * get resource
	 *
	 * @param classLoader	classLoader to find
	 * @param path	        resource path
	 * @return resource
	 */
	public URL getResource( ClassLoader classLoader, String path ) {
		if( classLoader == null )
			classLoader = getClassLoader();
		return classLoader.getResource( refineResourceName(path) );
	}

	/**
	 * remove first "/" character in resource name
	 *
	 * @param name resource name
	 * @return refined resource name
	 */
	private String refineResourceName( Object name ) {
		return Strings.nvl(name).replaceFirst( "^/", "" );
	}

	/**
	 * find resources
	 *
	 * @param pattern   path matching pattern (glob expression. if not exists, add all result)
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
	 * @return found resource names
	 */
	public List<URL> findResources( String... pattern ) {

		Map<String,URL> fileUrls = new LinkedHashMap<>();
		Map<String,URL> jarUrls  = new LinkedHashMap<>();

		for( URL root : getRootResources() ) {

			try {

				FileSystem fileSystem = getFileSystem( root );

				if( "file".equals(root.getProtocol()) ) {

					Set<PathMatcher> pathMatchers = toPathMatcher( fileSystem, pattern );

					Path rootPath = Paths.get( root.toURI() );
					Files.walk( rootPath ).forEach( path -> {
						Path curr = rootPath.relativize( path );
						if( match(pathMatchers, curr) ) {
							fileUrls.putIfAbsent( "/" + Files.normalizeSeparator(curr), Files.toURL(path) );
						};
					});

				} else {

					Set<PathMatcher> pathMatchers = toPathMatcher( fileSystem, pattern );

					Files.walk(fileSystem.getPath( "/" )).forEach( path -> {
						if( match(pathMatchers, path) ) {
							jarUrls.putIfAbsent( path.toString(), Files.toURL(path) );

						}
					});

				}

			} catch ( URISyntaxException | UncheckedIOException e ) {
				log.error( e.getMessage(), e );
				continue;
			}
		}

		Map<String,URL> result = new LinkedHashMap<>();
		result.putAll( jarUrls );
		result.putAll( fileUrls );

		return new ArrayList<>( result.values() );

	}

	private FileSystem getFileSystem( URL url ) throws URISyntaxException {
		if( "file".equals(url.getProtocol()) ) {
			return FileSystems.getDefault();
		} else {
			try {
				return FileSystems.getFileSystem( url.toURI() );
			} catch ( FileSystemNotFoundException e ) {
				try {
					return FileSystems.newFileSystem( url.toURI(), Collections.emptyMap() );
				} catch ( IOException ex ) {
					throw new UncheckedIOException( ex );
				}
			}
		}
	}

	private boolean match( Collection<PathMatcher> matchers, Path path ) {
		for( PathMatcher matcher : matchers ) {
			if( matcher.matches(path) )
				return true;
		}
		return false;
	}

	private List<URL> getRootResources() {
		List<URL> urls = new ArrayList<>();
		try {
			Enumeration<URL> resources = getClassLoader().getResources( "" );
			while( resources.hasMoreElements() ) {
				urls.add( resources.nextElement() );
			}
		} catch ( IOException e ) {
			log.error( e.getMessage(), e );
		}
		return urls;
	}

	private Set<PathMatcher> toPathMatcher( FileSystem fileSystem, String... patterns ) {

		Set<PathMatcher> matchers = new HashSet<>();
		boolean inFile = fileSystem == FileSystems.getDefault();

		for( String pattern : new HashSet<>( Arrays.asList( patterns )) ) {
			if( Strings.isEmpty( pattern ) ) continue;
			if( inFile )
				pattern = refineResourceName( pattern );
			matchers.add( fileSystem.getPathMatcher( "glob:" + pattern ) );
		}

		return matchers;

	}

	/**
	 * Check if current application is running in Jar package.
	 *
	 * @return true if it is running in jar.
	 */
	public boolean isRunningInJar() {
		URL root = getResource( "" );
		if( root == null ) return true;
		return Validator.isMatched( root.getProtocol(), "^(jar|war)$" );
	}

}