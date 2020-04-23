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
import io.nayasis.basica.validation.Assert;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import static io.nayasis.basica.resource.invocation.EquinoxInvocater.isEquinoxUrl;
import static io.nayasis.basica.resource.util.Resources.URL_PREFIX_CLASSPATH;
import static io.nayasis.basica.resource.util.Resources.isJarURL;
import static io.nayasis.basica.resource.util.Resources.isVfsURL;


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
	 * Set PathMatcher. (default is AntPathMatcher)
	 */
	public void setPathMatcher( PathMatcher pathMatcher ) {

		Assert.notNull( pathMatcher, "PathMatcher must not be null" );

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

		Assert.notNull( pattern, "Location pattern must not be null" );

		log.trace( "pattern : {}", pattern );

		if ( pattern.startsWith( URL_PREFIX_CLASSPATH ) ) {
			// a class path resource (multiple resources for same name possible)
			if ( pathMatcher.isPattern(pattern.substring(URL_PREFIX_CLASSPATH.length()))) {
				// a class path resource pattern
				return findPathMatchingResources(pattern);
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
				return findPathMatchingResources(pattern);
			} else {
				// a single resource with the given name
				Set<Resource> resources = new LinkedHashSet<>();
				resources.add( getResource( pattern ) );
				return resources;
			}
		}

	}


	/**
	 * Find all resources that match the given location pattern via the Ant-style PathMatcher.
	 * Supports resources in jar files and zip files and in the file system.
	 *
	 * @param pattern location pattern to match
	 * @return matched resources
	 * @throws IOException occurs I/O errors
	 */
	protected Set<Resource> findPathMatchingResources( String pattern ) throws IOException {

		String rootDir    = getRootDir( pattern );
		String subPattern = pattern.substring( rootDir.length() );

		Set<Resource> result = new LinkedHashSet<>(16 );

		for ( Resource root : getResources(rootDir) ) {

			URL rootUrl = root.getURL();

			if( isEquinoxUrl(rootUrl) ) {
				URL unwrapped = EquinoxInvocater.unwrap( rootUrl );
				if( unwrapped != null ) {
					rootUrl = unwrapped;
				}
				root = new UrlResource( rootUrl );
			}

			if( isVfsURL(rootUrl) ) {
				result.addAll( vfsFinder.find(rootUrl, subPattern) );
			} else if ( isJarURL(rootUrl) ) {
				result.addAll( jarFinder.find(root, rootUrl, subPattern) );
			} else {
				result.addAll( fileFinder.find(root, subPattern) );
			}
		}

		return result;

	}

	/**
	 * Determine the root directory for the given location.
	 * <p>Used for determining the starting point for file matching,
	 * resolving the root directory location to a {@code java.io.File}
	 * and passing it into {@code retrieveMatchingFiles}, with the
	 * remainder of the location as pattern.
	 * <p>Will return "/WEB-INF/" for the pattern "/WEB-INF/*.xml",
	 * for example.
	 * @param location the location to check
	 * @return the part of the location that denotes the root directory
	 */
	private String getRootDir( String location ) {
		int prefixEnd = location.indexOf(':') + 1;
		int rootDirEnd = location.length();
		while (rootDirEnd > prefixEnd && pathMatcher.isPattern(location.substring(prefixEnd, rootDirEnd))) {
			rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
		}
		if( rootDirEnd == 0 ) {
			rootDirEnd = prefixEnd;
		}
		return location.substring(0, rootDirEnd);
	}

}