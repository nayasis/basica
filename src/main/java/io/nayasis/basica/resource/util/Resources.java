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

package io.nayasis.basica.resource.util;

import io.nayasis.basica.base.Classes;
import io.nayasis.basica.base.Strings;
import io.nayasis.basica.validation.Assert;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;

@UtilityClass
public class Resources {

	public static final String URL_PREFIX_CLASSPATH = "classpath:";
	public static final String URL_PREFIX_FILE      = "file:";
	public static final String URL_PREFIX_JAR       = "jar:";
	public static final String URL_PREFIX_WAR       = "war:";
	public static final String URL_PROTOCOL_FILE    = "file";
	public static final String URL_PROTOCOL_JAR     = "jar";
	public static final String URL_PROTOCOL_WAR     = "war";
	public static final String URL_PROTOCOL_ZIP     = "zip";
	public static final String URL_PROTOCOL_WSJAR   = "wsjar";
	public static final String URL_PROTOCOL_VFSZIP  = "vfszip";
	public static final String URL_PROTOCOL_VFSFILE = "vfsfile";
	public static final String URL_PROTOCOL_VFS     = "vfs";
	public static final String FILE_EXTENSION_JAR   = ".jar";
	public static final String URL_SEPARATOR_JAR    = "!/";
	public static final String URL_SEPARATOR_WAR    = "*/";

	/**
	 * Return whether the given resource location is a URL:
	 * either a special "classpath" pseudo URL or a standard URL.
	 * @param resourceLocation the location String to check
	 * @return whether the location qualifies as a URL
	 * @see #URL_PREFIX_CLASSPATH
	 * @see URL
	 */
	public boolean isUrl( String resourceLocation ) {
		if ( resourceLocation == null ) {
			return false;
		}
		if ( resourceLocation.startsWith( URL_PREFIX_CLASSPATH ) ) {
			return true;
		}
		try {
			new URL(resourceLocation);
			return true;
		} catch ( MalformedURLException ex ) {
			return false;
		}
	}

	/**
	 * Resolve the given resource location to a {@code java.net.URL}.
	 * <p>Does not check whether the URL actually exists; simply returns
	 * the URL that the given location would correspond to.
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding URL object
	 * @throws FileNotFoundException if the resource cannot be resolved to a URL
	 */
	public URL getURL( String resourceLocation ) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith( URL_PREFIX_CLASSPATH )) {
			String path = resourceLocation.substring( URL_PREFIX_CLASSPATH.length());
			ClassLoader cl = Classes.getClassLoader();
			URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
			if (url == null) {
				String description = "class path resource [" + path + "]";
				throw new FileNotFoundException(description +
						" cannot be resolved to URL because it does not exist");
			}
			return url;
		}
		try {
			// try URL
			return new URL(resourceLocation);
		} catch ( MalformedURLException ex ) {
			// no URL -> treat as file path
			try {
				return new File(resourceLocation).toURI().toURL();
			} catch ( MalformedURLException ex2 ) {
				throw new FileNotFoundException("Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path" );
			}
		}
	}

	/**
	 * Resolve the given resource location to a {@code java.io.File},
	 * i.e. to a file in the file system.
	 * <p>Does not check whether the file actually exists; simply returns
	 * the File that the given location would correspond to.
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the resource cannot be resolved to
	 * a file in the file system
	 */
	public File getFile( String resourceLocation ) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if ( resourceLocation.startsWith( URL_PREFIX_CLASSPATH ) ) {
			String path = resourceLocation.substring( URL_PREFIX_CLASSPATH.length());
			String description = "class path resource [" + path + "]";
			ClassLoader cl = Classes.getClassLoader();
			URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
			if (url == null) {
				throw new FileNotFoundException(description +
						" cannot be resolved to absolute file path because it does not exist");
			}
			return getFile(url, description);
		}
		try {
			// try URL
			return getFile(new URL(resourceLocation));
		} catch (MalformedURLException ex) {
			// no URL -> treat as file path
			return new File(resourceLocation);
		}
	}

	/**
	 * Resolve the given resource URL to a {@code java.io.File},
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public File getFile( URL resourceUrl ) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	/**
	 * Resolve the given resource URL to a {@code java.io.File},
	 * i.e. to a file in the file system.
	 * @param resourceUrl the resource URL to resolve
	 * @param description a description of the original resource that
	 * the URL was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 */
	public File getFile( URL resourceUrl, String description ) throws FileNotFoundException {
		Assert.notNull(resourceUrl, "Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUrl);
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		} catch ( URISyntaxException ex ) {
			// Fallback for URLs that are not valid URIs (should hardly ever happen).
			return new File(resourceUrl.getFile());
		}
	}

	/**
	 * Resolve the given resource URI to a {@code java.io.File},
	 * i.e. to a file in the file system.
	 * @param resourceUri the resource URI to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 * @since 2.5
	 */
	public File getFile( URI resourceUri ) throws FileNotFoundException {
		return getFile( resourceUri, "URI" );
	}

	/**
	 * Resolve the given resource URI to a {@code java.io.File},
	 * i.e. to a file in the file system.
	 * @param resourceUri the resource URI to resolve
	 * @param description a description of the original resource that
	 * the URI was created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to
	 * a file in the file system
	 * @since 2.5
	 */
	public File getFile(URI resourceUri, String description) throws FileNotFoundException {
		Assert.notNull(resourceUri, "Resource URI must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(
					description + " cannot be resolved to absolute file path " +
					"because it does not reside in the file system: " + resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}

	/**
	 * Determine whether the given URL points to a resource in the file system,
	 * i.e. has protocol "file", "vfsfile" or "vfs".
	 * @param url the URL to check
	 * @return whether the URL has been identified as a file system URL
	 */
	public boolean isFileURL( URL url ) {
		String protocol = url.getProtocol();
		return URL_PROTOCOL_FILE.equals(protocol)
			|| URL_PROTOCOL_VFSFILE.equals(protocol)
			|| URL_PROTOCOL_VFS.equals(protocol);
	}

	/**
	 * Determine whether the given URL points to a resource in a jar file.
	 * i.e. has protocol "jar", "war, ""zip", "vfszip" or "wsjar".
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return URL_PROTOCOL_JAR.equals(protocol)
			|| URL_PROTOCOL_WAR.equals(protocol)
			|| URL_PROTOCOL_ZIP.equals(protocol)
			|| URL_PROTOCOL_VFSZIP.equals(protocol)
			|| URL_PROTOCOL_WSJAR.equals(protocol);
	}

	public boolean isVfsURL( URL url ) {
		return url != null && url.getProtocol().startsWith(URL_PROTOCOL_VFS);
	}

	/**
	 * Determine whether the given URL points to a jar file itself,
	 * that is, has protocol "file" and ends with the ".jar" extension.
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR file URL
	 * @since 4.1
	 */
	public boolean isJarFileURL( URL url ) {
		return url != null && URL_PROTOCOL_FILE.equals(url.getProtocol())
			&& url.getPath().toLowerCase().endsWith( FILE_EXTENSION_JAR );
	}

	/**
	 * Extract the URL for the actual jar file from the given URL
	 * (which may point to a resource in a jar file or to a jar file itself).
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 */
	public URL extractJarFileURL( URL jarUrl ) throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf( URL_SEPARATOR_JAR );
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);
			try {
				return new URL(jarFile);
			}
			catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like "jar:C:/mypath/myjar.jar".
				// This usually indicates that the jar file resides in the file system.
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}
				return new URL( URL_PREFIX_FILE + jarFile);
			}
		} else {
			return jarUrl;
		}
	}

	/**
	 * Extract the URL for the outermost archive from the given jar/war URL
	 * (which may point to a resource in a jar file or to a jar file itself).
	 * <p>In the case of a jar file nested within a war file, this will return
	 * a URL to the war file since that is the one resolvable in the file system.
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 * @since 4.1.8
	 * @see #extractJarFileURL(URL)
	 */
	public URL extractArchiveURL( URL jarUrl ) throws MalformedURLException {

		String urlFile = jarUrl.getFile();

		int endIndex = urlFile.indexOf( URL_SEPARATOR_WAR );
		if (endIndex != -1) {
			// Tomcat's "war:file:...mywar.war*/WEB-INF/lib/myjar.jar!/myentry.txt"
			String warFile = urlFile.substring(0, endIndex);
			if (URL_PROTOCOL_WAR.equals(jarUrl.getProtocol())) {
				return new URL(warFile);
			}
			int startIndex = warFile.indexOf( URL_PREFIX_WAR );
			if (startIndex != -1) {
				return new URL(warFile.substring(startIndex + URL_PREFIX_WAR.length()));
			}
		}

		// Regular "jar:file:...myjar.jar!/myentry.txt"
		return extractJarFileURL(jarUrl);

	}

	/**
	 * Create a URI instance for the given URL,
	 * replacing spaces with "%20" URI encoding first.
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see URL#toURI()
	 */
	public URI toURI( URL url ) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String,
	 * replacing spaces with "%20" URI encoding first.
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public URI toURI( String location ) throws URISyntaxException {
		return new URI( Strings.nvl(location).replace(" ", "%20"));
	}

	/**
	 * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the
	 * given connection, preferring {@code false} but leaving the
	 * flag at {@code true} for JNLP based resources.
	 * @param connection the URLConnection to set the flag on
	 */
	public void useCachesIfNecessary( URLConnection connection ) {
		connection.setUseCaches( connection.getClass().getSimpleName().startsWith("JNLP") );
	}

	public JarFile getJarFile( String url ) throws IOException {
		if( url.startsWith( URL_PREFIX_FILE ) ) {
			try {
				return new JarFile( Resources.toURI(url).getSchemeSpecificPart());
			} catch ( URISyntaxException e ) {
				// Fallback for URLs that are not valid URIs (should hardly ever happen).
				return new JarFile( url.substring( URL_PREFIX_FILE.length()) );
			}
		} else {
			return new JarFile(url);
		}
	}

}
