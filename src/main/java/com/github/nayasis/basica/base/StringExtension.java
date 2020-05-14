package com.github.nayasis.basica.base;

import com.github.nayasis.basica.reflection.Reflector;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * String extension
 *
 * @author nayasis@gmail.com
 * @since 2019-10-16
 */
public class StringExtension {

    public static boolean isEmpty( String string ) {
        return Strings.isEmpty( string );
    }

    public static boolean isNotEmpty( String string ) {
        return Strings.isNotEmpty( string );
    }

    public static boolean isBlank( String string ) {
        return Strings.isBlank( string );
    }

    public static boolean isNotBlank( String string ) {
        return Strings.isNotBlank( string );
    }

    public static boolean isNumeric( String string ) {
        return Types.isNumeric( string );
    }

    public static boolean eq( String string, String other ) {
        return Strings.equals( string, other );
    }

    public static boolean eqIgnoreCase( String string, String other ) {
        return Strings.equalsIgnoreCase( string, other );
    }

    public static double similarity( String string, String other ) {
        return Strings.similarity( string, other );
    }

    public static String fmt( String string, Object ... args ) {
        return Strings.format( string, args );
    }

    public static String lpad( String string, int length, char padChar ) {
        return Strings.lpad( string, length, padChar );
    }

    public static String rpad( String string, int length, char padChar ) {
        return Strings.rpad( string, length, padChar );
    }

    public static String dplpad( String string, int length, char padChar ) {
        return Strings.dplpad( string, length, padChar );
    }

    public static String dprpad( String string, int length, char padChar ) {
        return Strings.dprpad( string, length, padChar );
    }

    public static String trim( String string ) {
        return Strings.trim( string );
    }

    public static String nvl( String string, Object replaceValue ) {
        return Strings.nvl( string, replaceValue );
    }

    public static String toCamel( String string ) {
        return Strings.toCamel( string );
    }

    public static String toSnake( String string ) {
        return Strings.toSnake( string );
    }

    public static String escape( String string ) {
        return Strings.escape( string );
    }

    public static String unescape( String string ) {
        return Strings.unescape( string );
    }

    public static String clearXss( String string ) {
        return Strings.clearXss( string );
    }

    public static String restoreXss( String string ) {
        return Strings.restoreXss( string );
    }

    public static String upcapitalize( String string ) {
        return Strings.uncapitalize( string );
    }

    public static String capitalize( String string ) {
        return Strings.capitalize( string );
    }

    public static String mask( String string, String word ) {
        return Strings.mask( string, word );
    }

    public static String compressSpace( String string ) {
        return Strings.compressSpace( string );
    }

    public static String compressBlank( String string ) {
        return Strings.compressBlank( string );
    }

    public static String compressEnter( String string ) {
        return Strings.compressEnter( string );
    }

    public static String encode( String string ) {
        return Strings.encode( string );
    }

    public static <T> T decode( String string ) {
        return Strings.decode( string );
    }

    public static String encodeUrl( String string ) {
        return Strings.encodeUrl( string );
    }

    public static String decodeUrl( String string ) {
        return Strings.decodeUrl( string );
    }

    public static String toDigit( String string ) {
        return Strings.toDigit( string );
    }

    public static String toLowerCase( String string ) {
        return Strings.toLowerCase( string );
    }

    public static String toUpperCase( String string ) {
        return Strings.toUpperCase( string );
    }

    public static String extractUppers( String string ) {
        return Strings.extractUppers( string );
    }

    public static String extractLowers( String string ) {
        return Strings.extractLowers( string );
    }

    public static Object zip( String string ) {
        return Strings.zip( string );
    }

    public static Object unzip( String string ) {
        return Strings.unzip( string );
    }

    public static String toYn( String string ) {
        return Strings.toYn( string );
    }

    public static boolean toBoolean( String string ) {
        return Strings.toBoolean( string );
    }

    public static boolean like( String string, String pattern ) {
        return Strings.like( string, pattern );
    }

    public static boolean notLike( String string, String pattern ) {
        return Strings.notLike( string, pattern );
    }

    public static String escapeRegexp( String string ) {
        return Strings.escapeRegexp( string );
    }

    public static List<String> divide( String string, String regexDelimiter ) {
        return Strings.split( string, regexDelimiter );
    }

    public static List<String> divide( String string, String regexDelimiter, boolean includeSeparator ) {
        return Strings.split( string, regexDelimiter, includeSeparator );
    }

    public static List<String> tokenize( String string, String separator ) {
        return Strings.tokenize( string, separator );
    }

    public static List<String> tokenize( String string, String separator, boolean includeSeparator ) {
        return Strings.tokenize( string, separator, includeSeparator );
    }

    public static List<String> capture( String string, String pattern ) {
        return Strings.capture( string, pattern );
    }

    public static List<String> capture( String string, Pattern pattern ) {
        return Strings.capture( string, pattern );
    }

    public static boolean isJson( String string ) {
        return Reflector.isJson( string );
    }

    public static <T> T toBean( String string, Class<T> toClass ) {
        return Reflector.toBeanFrom( string, toClass );
    }

    public static Map toMap( String string ) {
        return Reflector.toMapFrom( string );
    }

    public static Map toFlattenMap( String string ) {
        return Reflector.toFlattenMap( string );
    }

    public static Map toUnflattenMap( String string ) {
        return Reflector.toUnflattenMap( string );
    }

    public static <T> List<T> toList( String string, Class<T> generic ) {
        return Reflector.toListFrom( string, generic );
    }

    public static List toList( String string ) {
        return Reflector.toListFrom( string );
    }

}
