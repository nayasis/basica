package io.nayasis.common.model;

import io.nayasis.common.base.Classes;
import io.nayasis.common.base.Strings;
import io.nayasis.common.exception.unchecked.UncheckedIOException;
import io.nayasis.common.file.Files;

import java.io.File;
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
public class Messages {

    // message pool ( code > locale > message text )
    protected static Map<String,Map<Locale,String>> pool = new Hashtable<>();

    private static Locale NULL_LOCALE = new Locale( "", "" );

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
     * Message.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Message.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * if message code is not defined, return code itself.
     *
     * if "merong" is just code and not defined, then
     *
     * Message.get( "merong" ); → "merong"
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
    public static String get( Locale locale, Object code, Object... param ) {
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
     * Message.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Message.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * if message code is not defined, return code itself.
     *
     * if "merong" is just code and not defined, then
     *
     * Message.get( "merong" ); → "merong"
     *     </pre>
     *   </li>
     * </ol>
     *
     *
     * @param code      message code
     * @param param     binding parameter replaced with '{}'
     * @return message corresponding to code
     */
    public static String get( Object code, Object... param ) {
    	return get( Locale.getDefault(), code, param );
    }

    /**
     * get message from repository
     *
     * @param code      message code
     * @param locale    locale
     * @return message corresponding to code
     */
    private static String getMessage( Object code, Locale locale ) {

        if( code == null || pool.isEmpty() ) return "";

    	Map<Locale,String> messages = pool.get( code );
    	if( messages.isEmpty() ) return "";

        Locale localeKey = locale;

        if( ! messages.containsKey(localeKey) ) {
            localeKey = new Locale( locale.getLanguage() );
            if( ! messages.containsKey(localeKey) ) {
                if( ! messages.containsKey( NULL_LOCALE ) ) {
                    localeKey = messages.keySet().iterator().next();
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
    public static void load( String resourcePath ) throws UncheckedIOException {
        if( Strings.isEmpty(resourcePath) ) return;
        if( resourcePath.contains("*") ) {
            List<String> resources = Classes.findResources( resourcePath );
            resources.forEach( resource -> loadPool( resource ) );
        } else {
            loadPool(resourcePath);
        }
    }

    private static void loadPool( String filePath ) throws UncheckedIOException {
        Locale locale = getLocaleFrom( filePath );
        NProperties properties = new NProperties( filePath );
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
    public static void clear() {
    	pool.clear();
    }

    private static Locale getLocaleFrom( String filePath ) {

    	String baseName = Files.removeExtension( new File(filePath).getName() );

    	List<String> sentences = Strings.tokenize( baseName, "." );

    	int size = sentences.size();

    	if( size <= 1 ) return NULL_LOCALE;

    	String localeString = sentences.get( size - 1 );

    	String country  = Strings.extractUpperCharacters( localeString );
    	String language = Strings.extractLowerCharacters( localeString );

    	if( Strings.isEmpty( language ) ) language = Locale.getDefault().getLanguage();

    	return new Locale( language, country );

    }

}
