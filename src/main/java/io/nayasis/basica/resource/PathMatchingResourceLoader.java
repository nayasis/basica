/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.nayasis.basica.resource;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.resource.finder.ClasspathResourceFinder;
import io.nayasis.basica.resource.finder.FileResourceFinder;
import io.nayasis.basica.resource.finder.JarResourceFinder;
import io.nayasis.basica.resource.finder.VfsResourceFinder;
import io.nayasis.basica.resource.invocation.EquinoxInvocater;
import io.nayasis.basica.resource.loader.DefaultResourceLoader;
import io.nayasis.basica.resource.loader.ResourceLoader;
import io.nayasis.basica.resource.matcher.AntPathMatcher;
import io.nayasis.basica.resource.matcher.PathMatcher;
import io.nayasis.basica.resource.resolver.ResourcePatternResolver;
import io.nayasis.basica.resource.type.UrlResource;
import io.nayasis.basica.resource.type.interfaces.Resource;
import io.nayasis.basica.resource.util.Resources;
import io.nayasis.basica.validation.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import static io.nayasis.basica.resource.util.Resources.URL_PREFIX_CLASSPATH;

@Slf4j
public class PathMatchingResourceLoader implements ResourcePatternResolver {

	private ResourceLoader resourceLoader = new DefaultResourceLoader();
	private PathMatcher    pathMatcher    = new AntPathMatcher();

	private FileResourceFinder      fileFinder      = new FileResourceFinder( pathMatcher );
	private VfsResourceFinder       vfsFinder       = new VfsResourceFinder( pathMatcher );
	private JarResourceFinder       jarFinder       = new JarResourceFinder( pathMatcher );
	private ClasspathResourceFinder classpathFinder = new ClasspathResourceFinder( resourceLoader );

	@Override
	public ClassLoader getClassLoader() {
		return resourceLoader.getClassLoader();
	}

	/**
	 * Set PathMatcher.
	 * default is AntPathMatcher.
	 */
	public void setPathMatcher( PathMatcher pathMatcher ) {
		Assert.notNull( pathMatcher, "[pathMatcher] must not be null" );
		this.pathMatcher = pathMatcher;
		fileFinder.setPathMatcher( pathMatcher );
		vfsFinder.setPathMatcher( pathMatcher );
		jarFinder.setPathMatcher( pathMatcher );
	}

	@Override
	public Resource getResource( String location ) {
		return resourceLoader.getResource(location);
	}

	@Override
	public Set<Resource> getResources( String pattern ) throws IOException {

		if( Strings.isEmpty(pattern) ) return new LinkedHashSet<>();

		if ( isClasspath(pattern) ) {
			// a class path resource (multiple resources for same name possible)
			if ( pathMatcher.isPattern(pattern.substring(URL_PREFIX_CLASSPATH.length()))) {
				// a class path resource pattern
				return findResources(pattern);
			} else {
				// all class path resources with the given name
				return classpathFinder.findAll( pattern.substring( URL_PREFIX_CLASSPATH.length()) );
			}

		} else {
			// Generally only look for a pattern after a prefix here,
			// and on Tomcat only after the "*/" separator for its "war:" protocol.
			int prefixEnd = ( pattern.startsWith("war:") ? pattern.indexOf("*/") + 1 :
				pattern.indexOf(':') + 1);
			if( pathMatcher.isPattern(pattern.substring(prefixEnd)) ) {
				// a file pattern
				return findResources(pattern);
			} else {
				// a single resource with the given name
				Set<Resource> resources = new LinkedHashSet<>();
				resources.add( getResource(pattern) );
				return resources;
			}
		}

	}

	private boolean isClasspath( String pattern ) {
		return pattern.startsWith( URL_PREFIX_CLASSPATH );
	}

	/**
	 * find all resources matched with given pattern.
	 *
	 * <p>it could find resources in JAR, ZIP, File system.
	 *
	 * @param pattern 	pattern to match
	 * @return	matched resources
	 * @throws IOException occurs I/O errors
	 */
	private Set<Resource> findResources( String pattern ) throws IOException {

		String ptnRoot   = getRootDir( pattern );
		String ptnRemain = pattern.substring( ptnRoot.length() );

		Set<Resource> result = new LinkedHashSet<>(16 );

		for ( Resource root : getResources(ptnRoot) ) {

			URL rootUrl = root.getURL();

			if( EquinoxInvocater.isEquinoxUrl(rootUrl) ) {
				URL unwrapped = EquinoxInvocater.unwrap( rootUrl );
				if( unwrapped != null ) {
					rootUrl = unwrapped;
				}
				root = new UrlResource( rootUrl );
			}

			if( Resources.isVfsURL(rootUrl) ) {
				result.addAll( vfsFinder.find(rootUrl, ptnRemain) );
			} else if ( Resources.isJarURL(rootUrl) ) {
				result.addAll( jarFinder.find(root, rootUrl, ptnRemain) );
			} else {
				result.addAll( fileFinder.find(root, ptnRemain) );
			}

		}

		return result;

	}

	/**
	 * extract root directory for given path to determine file matching starting point.
	 *
	 * <p>ex. "classpath:/WEB-INF/*.xml" returns "classpath:/WEB-INF"
	 *
	 * @param path
	 * @return
	 */
	public String getRootDir( String path ) {

		int prefixEnd  = path.indexOf(':') + 1;
		int rootDirEnd = path.length();

		// climb up directory until remain path is not matched with pattern
		while( rootDirEnd > prefixEnd && pathMatcher.isPattern(path.substring(prefixEnd, rootDirEnd)) ) {
			rootDirEnd = path.lastIndexOf('/', rootDirEnd - 2) + 1;
		}

		if( rootDirEnd == 0 )
			rootDirEnd = prefixEnd;

		return path.substring( 0, rootDirEnd );

	}

}