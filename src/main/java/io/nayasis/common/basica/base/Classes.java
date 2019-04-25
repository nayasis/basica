package io.nayasis.common.basica.base;


import io.nayasis.common.basica.cache.implement.LruCache;
import io.nayasis.common.basica.exception.unchecked.UncheckedClassCastException;
import io.nayasis.common.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.common.basica.file.Files;
import io.nayasis.common.basica.file.handler.FileFinder;
import io.nayasis.common.basica.validation.Validator;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * Class Utility
 *
 * @author nayasis@gmail.com
 */
public class Classes {

	private static Logger log = LoggerFactory.getLogger( Classes.class );

	private static LruCache<Class<?>,Set<Class<?>>> CACHE_CONTAINED_PARENT = new LruCache<>( 256 );

	private static Objenesis factory = new ObjenesisStd();

	/**
	 * Get class for name
	 *
	 * @param className	class name
	 * @throws ClassNotFoundException if class is not founded in class loader.
	 * @return class for name
	 */
	public static Class<?> getClass( String className ) throws ClassNotFoundException {

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
	public static ClassLoader getClassLoader() {

		try {
			return Thread.currentThread().getContextClassLoader();
		} catch( Throwable e ) {
			// if current callstack is under Thread, cannot access Thread Context.
		}

		ClassLoader classLoader = Classes.class.getClassLoader();

		if( classLoader == null ) {
			try {
				classLoader = ClassLoader.getSystemClassLoader();
			} catch( Throwable e ) {
				// cannot access system ClassLoader.
			}
		}
		return classLoader;

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
	public static Class<?> getClass( Type type ) throws ClassNotFoundException {

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
	public static Class getGenericClass( Class klass ) {
		if( klass == null ) return null;
		try {
			Type genericSuperclass = klass.getGenericSuperclass();
			Type[] types = ( (ParameterizedType) genericSuperclass ).getActualTypeArguments();
			return (Class) types[ 0 ];
		} catch( Exception e ) {
			return Object.class;
		}
	}

	public static Class<?> getClass( Object object ) {
		return ( object == null ) ? null : object.getClass();
	}

	public static Set<Class<?>> findParents( Class<?> klass ) {

		if( CACHE_CONTAINED_PARENT.contains( klass ) )
			return CACHE_CONTAINED_PARENT.get( klass );

		Set<Class<?>> parents = new LinkedHashSet<>();
		findParents( klass, parents );
		CACHE_CONTAINED_PARENT.put( klass, parents );

		return parents;

	}

	private static void findParents( Class<?> klass, Set<Class<?>> set ) {
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

	public static <T> T createInstance( Class<T> klass ) throws UncheckedClassCastException {
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

    public static <T> T createInstance( Type type ) throws ClassNotFoundException {
		return (T) createInstance( getClass(type) );
	}

	/**
	 * Check if a class was extended or implemented by found class
	 *
	 * @param klass  		class to inspect
	 * @param extendKlass	class to be extended in inspect class
	 * @return true if inspect class is extended of implemented by found class
	 */
	public static boolean isExtendedBy( Class<?> klass, Class<?>... extendKlass ) {
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
	 * Check if an instnace was extended or implemented by found class
	 *
	 * @param instance  instance to inspect
	 * @param extendKlass	   class to be extended in inspect instance
	 * @return true if inspect instance is extended of implemented by found class
	 */
	public static boolean isExtendedBy( Object instance, Class<?>... extendKlass ) {
		return instance != null && isExtendedBy( instance.getClass(), extendKlass );
	}

	/**
	 * Check resource exists
	 * @param name resource name
	 * @return true if resource is exist in class path.
	 */
	public static boolean isResourceExisted( String name ) {
		return getClassLoader().getResource( refineResourceName(name) ) != null;
	}

	/**
	 * Get resource as stream
	 *
	 * @param name	resource name
	 * @return resource input stream
	 */
	public static InputStream getResourceAsStream( String name ) {
		return getClassLoader().getResourceAsStream( refineResourceName(name) );
	}

	/**
	 * revmoe first "/" character in resource name
	 *
	 * @param name resource name
	 * @return refined resource name
	 */
	private static String refineResourceName( String name ) {
		return Strings.nvl( name ).replaceFirst( "^/", "" );
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
	public static List<String> findResources( String... pattern ) {

		Set<String> resourcesInJar        = new HashSet<>();
		Set<String> resourcesInFileSystem = new HashSet<>();

		if( isRunningInJar() ) {

			URLClassLoader urlClassLoader = (URLClassLoader) getClassLoader();
			URL jarUrl = urlClassLoader.getURLs()[ 0 ];

			JarFile jar = getJarFile( jarUrl );

			Set<PathMatcher> matchers = FileFinder.toPathMacher( toJarPattern( pattern ) );
			boolean addAll = ( matchers.size() == 0 );

			if( log.isTraceEnabled() ) {
				log.trace( ">> Jar pathMatchers" );
				for( String p : toJarPattern(pattern) ) {
					log.trace( p );
				}
			}

			log.trace( ">> entry in jar" );
			for( JarEntry entry : Collections.list( jar.entries() ) ) {
				if( log.isTraceEnabled() ) {
					log.trace( entry.getName() );
				}
				if( addAll ) {
					resourcesInJar.add( entry.getName() );
				} else {
					Path targetPath = Paths.get( entry.getName().replaceAll( "^(BOOT|WEB)-INF/classes/", "" ) );
					for( PathMatcher matcher : matchers ) {
						if( matcher.matches( targetPath )) {
							resourcesInJar.add( entry.getName() );
							break;
						}
					}
				}
			}

		}

		log.trace( "pattern         : {}", pattern );
		log.trace( "toFilePattern   : {}", toFilePattern(pattern) );

		List<String> paths = Files.find( Files.getRootPath(), true, false, -1, toFilePattern( pattern ) );

		log.trace( "paths count : {}\npaths : {}", paths.size(), paths );

		for( String path : paths ) {
			String pathVal = Files.normalizeSeparator( path );
			resourcesInFileSystem.add( pathVal.replace( Files.getRootPath(), "" ).replaceFirst( "^/", "" ) );
		}

		log.trace( ">> resource in jar : {}", resourcesInJar );
		log.trace( ">> resource in file system : {}", resourcesInFileSystem );

		resourcesInJar.addAll( resourcesInFileSystem );

		log.trace( ">> all resource : {}", resourcesInJar );

		return new ArrayList<>( resourcesInJar );

	}

	/**
	 * Check if current application is running in Jar package.
	 *
	 * @return true if it is running in jar.
	 */
	public static boolean isRunningInJar() {
		URL root = getClassLoader().getResource( "" );
		if( root == null ) return true;
		String file = root.getFile();
		return Validator.isMatched( root.toString(), "(?i)^(jar|war):.*$" );
	}

	private static String[] toFilePattern( String[] pattern ) {
		String[] result = new String[ pattern.length ];
		String rootPath = Files.getRootPath();
		for( int i = 0, iCnt = pattern.length; i < iCnt; i++ ) {
            result[ i ] = ( rootPath + "/" + pattern[i].replaceFirst( "^" + rootPath + "/", "" ) )
				.replaceAll( "//", "/" )
				.replaceAll( "(/WEB-INF/classes)+", "/WEB-INF/classes" )
				.replaceAll( "(/BOOT-INF/classes)+", "/BOOT-INF/classes" )
			;
        }
		return result;
	}

	private static String[] toJarPattern( String[] pattern ) {
		String[] result = new String[ pattern.length ];
		for( int i = 0, iCnt = pattern.length; i < iCnt; i++ ) {
			result[ i ] = pattern[ i ].replaceAll( "//", "/" ).replaceFirst( "^/", "" );
        }
		return result;
	}

	private static JarFile getJarFile( URL url ) {
		try {
			String filePath = new File( url.toURI().getSchemeSpecificPart() ).getPath();
			filePath = Files.normalizeSeparator( filePath )
				.replaceFirst( "\\/(WEB-INF|BOOT-INF)\\/classes(!)?(\\/)?", "" )
				.replaceFirst( "!$", "" )
				.replaceFirst( "file:", "" );
			log.trace( "jar file : {}", filePath );
            return new JarFile( filePath );
        } catch( IOException | URISyntaxException e ) {
            throw new UncheckedIOException( e );
		}
	}

}