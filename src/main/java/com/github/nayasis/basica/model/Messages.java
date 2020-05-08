package com.github.nayasis.basica.model;

import com.github.nayasis.basica.exception.unchecked.UncheckedIOException;
import com.github.nayasis.basica.base.Classes;
import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.file.Files;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Message utility class based on message code
 *
 * @author nayasis@gmail.com
 *
 */
@UtilityClass
public class Messages {

    // message pool ( code > locale > message text )
    protected Map<String,Map<Locale,String>> pool = new Hashtable<>();

    private Locale NULL_LOCALE = new Locale( "", "" );

    /**
     * get message corresponding code
     *
     * <ol>
     *   <li>
     *     <pre>
     *  '{}' in message are replaced with binding parameters.
     *
     * if message code "com.0001" is "{}는 사람입니다.", then
     *
     * Messages.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Messages.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * if message code is not defined, return code itself.
     *
     * if "merong" is just code and not defined, then
     *
     * Messages.get( "merong" ); → "merong"
     *     </pre>
     *   </li>
     * </ol>
     *
     *
     * @param locale    locale
     * @param code      message code
     * @param param     binding parameter replaced with '{}'
     * @return message corresponding to code
     */
    public String get( Locale locale, Object code, Object... param ) {
        return Strings.format( getMessage( code, locale ), param );
    }

    /**
     * get default locale's message corresponding to code.
     *
     * <ol>
     *   <li>
     *     <pre>
     *  '{}' in message are replaced with binding parameters.
     *
     * if message code "com.0001" is "{}는 사람입니다.", then
     *
     * Messages.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Messages.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * if message code is not defined, return code itself.
     *
     * if "merong" is just code and not defined, then
     *
     * Messages.get( "merong" ); → "merong"
     *     </pre>
     *   </li>
     * </ol>
     *
     *
     * @param code      message code
     * @param param     binding parameter replaced with '{}'
     * @return message corresponding to code
     */
    public String get( Object code, Object... param ) {
    	return get( Locale.getDefault(), code, param );
    }

    /**
     * get message from repository
     *
     * @param code      message code
     * @param locale    locale
     * @return message corresponding to code
     */
    private String getMessage( Object code, Locale locale ) {

        String cd = Strings.nvl( code );

        if( cd.isEmpty() || pool.isEmpty() ) return cd;

    	Map<Locale,String> messages = pool.get( code );
    	if( messages == null || messages.isEmpty() ) return cd;

    	if( locale == null )
    	    locale = Locale.getDefault();

        Locale localeKey = locale;

        if( ! messages.containsKey(localeKey) ) {
            localeKey = new Locale( locale.getLanguage() );
            if( ! messages.containsKey(localeKey) ) {
                if( ! messages.containsKey( NULL_LOCALE ) ) {
                    localeKey = messages.keySet().iterator().next();
                } else {
                    localeKey = NULL_LOCALE;
                }
            }
        }

    	return messages.get( localeKey );

    }

    /**
     *
     * load message file to memory
     *
     * @param resourcePath message file or resource path
     * @throws UncheckedIOException  if I/O exception occurs.
     */
    public void loadFromResource( String resourcePath ) throws UncheckedIOException {
        if( Strings.isEmpty(resourcePath) ) return;
        Classes.findResources(resourcePath).forEach( url -> {
            loadFromURL( url );
        });
    }

    /**
     *
     * load message file to memory
     *
     * @param filePath message file or resource path
     * @throws UncheckedIOException  if I/O exception occurs.
     */
    public void loadFromFile( String filePath ) throws UncheckedIOException {
        if( Strings.isEmpty(filePath) ) return;
        loadFromURL( Files.toURL(filePath) );
    }

    /**
     *
     * load message file to memory
     *
     * @param url URL path of message resource
     * @throws UncheckedIOException  if I/O exception occurs.
     */
    public void loadFromURL( URL url ) throws UncheckedIOException {
        Locale locale = getLocaleFrom( url );
        NProperties properties = new NProperties( url );
        for( Object key : properties.keySet() ) {
            if( ! pool.containsKey(key) ) {
                pool.put( key.toString(), new Hashtable<>() );
            }
            Map<Locale,String> messages = pool.get( key );
            messages.put( locale, properties.getProperty(key.toString()) );
        }
    }

    /**
     * clear message pool
     */
    public void clear() {
    	pool.clear();
    }

    private Locale getLocaleFrom( URL url ) {

    	String baseName = Files.removeExtension( new File(url.getFile()).getName() );

    	List<String> sentences = Strings.tokenize( baseName, "." );

    	int size = sentences.size();

    	if( size <= 1 ) return NULL_LOCALE;

    	String localeString = sentences.get( size - 1 );

    	String country  = Strings.extractUppers( localeString );
    	String language = Strings.extractLowers( localeString );

    	if( Strings.isEmpty( language ) ) language = Locale.getDefault().getLanguage();

    	return new Locale( language, country );

    }

    /**
     * get all messages
     *
     * @param locale    locale to extract message.
     * @return messages in pool
     */
    public Map<String,String> getAll( Locale locale ) {
        Map<String,String> messages = new HashMap<>();
        for( String code : new HashSet<String>(pool.keySet()) ) {
            messages.put( code, getMessage(code, locale) );
        }
        return messages;
    }

    /**
     * get all messages by default locale.
     *
     * @return messages in pool
     */
    public Map<String,String> getAll() {
        return getAll( Locale.getDefault() );
    }

}
