package com.github.nayasis.basica.base;


import com.github.nayasis.basica.cache.implement.LruCache;
import com.github.nayasis.basica.exception.unchecked.UncheckedClassCastException;
import com.github.nayasis.basica.exception.unchecked.UncheckedIOException;
import com.github.nayasis.basica.file.Files;
import com.github.nayasis.basica.resource.PathMatchingResourceLoader;
import com.github.nayasis.basica.resource.type.interfaces.Resource;
import com.github.nayasis.basica.resource.util.Resources;
import com.github.nayasis.basica.validation.Assert;
import com.github.nayasis.basica.validation.Validator;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
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

		ClassLoader classLoader = null;

		try {
			classLoader = Thread.currentThread().getContextClassLoader();
		} catch( Throwable e ) {}

		if( classLoader == null ) {
			classLoader = Classes.class.getClassLoader();
			if( classLoader == null ) {
				try {
					return ClassLoader.getSystemClassLoader();
				} catch( Throwable e ) {}
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

	@SuppressWarnings("unchecked")
    public <T> T createInstance( Type type ) throws ClassNotFoundException {
		return (T) createInstance( getClass(type) );
	}

	/**
	 * Check if a class was extended or implemented by found class
	 *
	 * @param klass  	class to inspect
	 * @param extend	class to be extended in inspect class
	 * @return true if inspect class is extended of implemented by found class
	 */
	public boolean hasExtend( Class<?> klass, Class<?>... extend ) {
		if( klass == null || extend.length == 0 ) return false;
		Set<Class<?>> parents = findParents( klass );
		for( Class<?> target : extend ) {
			if( klass == target ) return true;
			if( Modifier.isFinal(target.getModifiers()) ) continue;
			if( parents.contains( target ) ) return true;
		}
		return false;
	}

	/**
	 * Check if a class was extended or implemented by found class
	 *
	 * @param klass  	class to inspect
	 * @param extend	class to be extended in inspect class
	 * @return true if inspect class is extended of implemented by found class
	 */
	public boolean hasExtend( Class<?> klass, Collection<Class<?>> extend ) {
		if( klass == null || Validator.isEmpty(extend) ) return false;
		Set<Class<?>> parents = findParents( klass );
		for( Class<?> target : extend ) {
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
	 * @param extend	class to be extended in inspect instance
	 * @return true if inspect instance is extended of implemented by found class
	 */
	public boolean hasExtend( Object instance, Class<?>... extend ) {
		return instance != null && hasExtend( instance.getClass(), extend );
	}

	/**
	 * Check if an instnace was extended or implemented by found class
	 *
	 * @param instance  instance to inspect
	 * @param extend	class to be extended in inspect instance
	 * @return true if inspect instance is extended of implemented by found class
	 */
	public boolean hasExtend( Object instance, Collection<Class<?>> extend ) {
		return instance != null && hasExtend( instance.getClass(), extend );
	}

	/**
	 * Check resource exists
	 * @param path resource path
	 * @return true if resource is exist in class path.
	 */
	public boolean hasResource( String path ) {
		return getClassLoader().getResource( refineResourceName(path) ) != null;
	}

	/**
	 * Get resource as stream
	 *
	 * @param path	resource path
	 * @return resource input stream
	 */
	public InputStream getResourceStream( String path ) {
		return getClassLoader().getResourceAsStream( refineResourceName(path) );
	}

	/**
	 * Get resource as stream
	 *
	 * @param url	resource URL
	 * @return resource input stream
	 */
	public InputStream getResourceStream( URL url ) throws UncheckedIOException {
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
	 * @return resource URL
	 */
	public URL getResource( String path ) {
		return getResource( null, path );
	}

	/**
	 * get resource
	 *
	 * @param classLoader	classLoader to find
	 * @param path	        resource path
	 * @return resource URL
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

		List<URL> urls = new ArrayList<>();

		PathMatchingResourceLoader resolver = new PathMatchingResourceLoader();

		for( String ptn : pattern ) {
			try {
				Set<Resource> resources = resolver.getResources( Resources.URL_PREFIX_CLASSPATH + ptn );
				for( Resource resource : resources ) {
					urls.add( resource.getURL() );
				}
			} catch ( IOException e ) {
				log.error( e.getMessage(), e );
			}
		}

		return urls;

	}

	/**
	 * Check if application is running in Jar package.
	 *
	 * @param klass given class to detect running in Jar or War.
	 * @return true if given class is running in jar.
	 */
	public boolean isRunningInJar( Class klass ) {
		URL location = getRootLocation( klass );
		if( Validator.isMatched( location.getProtocol(), "(?i)^(jar|war)$" ) ) return true;
		if( Validator.isMatched( Files.getExtension( location.getPath() ), "(?i)^(jar|war)$" ) ) return true;
		return false;
	}

	public URL getRootLocation( Class klass ) {
		return klass.getProtectionDomain().getCodeSource().getLocation();
	}

	/**
	 * get class for name.
	 * it could understand array class name (ex. "String[]") and inner class's source name (ex. java.lang.Thread.State instread of "java.lang.Thread@State" )
	 *
	 * @param name	class name
	 * @return	class instance
	 * @throws ClassNotFoundException	if class was not found
	 * @throws LinkageError	if class file could not be loaded
	 */
	public Class<?> forName( String name ) throws ClassNotFoundException, LinkageError {
		return forName( name, null );
	}

	/**
	 * get class for name.
	 * it could understand array class name (ex. "String[]") and inner class's source name (ex. java.lang.Thread.State instread of "java.lang.Thread@State" )
	 *
	 * @param name			class name
	 * @param classLoader	class loader
	 * @return	class instance
	 * @throws ClassNotFoundException	if class was not found
	 * @throws LinkageError	if class file could not be loaded
	 */
	public Class<?> forName( String name, ClassLoader classLoader ) throws ClassNotFoundException, LinkageError {

		Assert.notNull( name, "[name] can't be null" );

		if( name.endsWith( SUFFIX_ARRAY ) ) {
			String elementClassName = name.substring(0, name.length() - SUFFIX_ARRAY.length());
			Class<?> elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		if( name.startsWith( PREFIX_NON_PRIMITIVE_ARRAY ) && name.endsWith(";") ) {
			String elementName = name.substring( PREFIX_NON_PRIMITIVE_ARRAY.length(), name.length() - 1);
			Class<?> elementClass = forName(elementName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		if( name.startsWith( PREFIX_INTERNAL_ARRAY ) ) {
			String elementName = name.substring( PREFIX_INTERNAL_ARRAY.length());
			Class<?> elementClass = forName( elementName, classLoader );
			return Array.newInstance(elementClass, 0).getClass();
		}

		if( classLoader == null )
			classLoader = Classes.getClassLoader();

		try {
			return Class.forName( name, false, classLoader );
		} catch ( ClassNotFoundException ex ) {
			int lastDotIndex = name.lastIndexOf( SEPARATOR_PACKAGE );
			if ( lastDotIndex != -1 ) {
				String innerClassName =
					name.substring(0, lastDotIndex) + SEPARATOR_INNER_CLASS + name.substring(lastDotIndex + 1);
				try {
					return Class.forName( innerClassName, false, classLoader );
				} catch ( ClassNotFoundException ex2 ) {
					// let original exception get through
				}
			}
			throw ex;
		}

	}

	/** Suffix for array class names: {@code "[]"}. */
	private static final String SUFFIX_ARRAY = "[]";

	/** Prefix for internal non-primitive array class names: {@code "[L"}. */
	private static final String PREFIX_NON_PRIMITIVE_ARRAY = "[L";

	/** Prefix for internal array class names: {@code "["}. */
	private static final String PREFIX_INTERNAL_ARRAY = "[";

	/** The package separator character: {@code '.'}. */
	public static final char SEPARATOR_PACKAGE = '.';

	/** The path separator character: {@code '/'}. */
	public static final char SEPARATOR_PATH = '/';

	/** The inner class separator character: {@code '$'}. */
	public static final char SEPARATOR_INNER_CLASS = '$';

}