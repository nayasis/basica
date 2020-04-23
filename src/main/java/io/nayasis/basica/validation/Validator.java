package io.nayasis.basica.validation;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.base.Types;
import io.nayasis.basica.exception.unchecked.ParseException;
import io.nayasis.basica.model.NDate;
import io.nayasis.basica.model.NList;
import io.nayasis.basica.reflection.Reflector;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator to check value's validation
 *
 * @author nayasis@gmail.com
 */
public class Validator {

    /**
     * check whether value is valid date format.
     *
     * @param value value (format is supposed to 'YYYY-MM-DD')
     * @return true value is valid date format.
     */
    public static boolean isDate( String value ) {
        return isDate( value, null );
    }

    /**
     * check whether value is valid date format.
     *
     * @param value  date text
     * @param format date format (ex: YYYY-MM-DD HH:MI:SS)
     * @return true value is valid date format.
     */
    public static boolean isDate( String value, String format ) {
        try {
            new NDate( value, format );
            return true;
        } catch ( ParseException e ) {
            return false;
        }
    }

    /**
     * check whether value is null.
     * @param value check value
     * @return true if value is null.
     */
    public static boolean isNull( Object value ) {
        return value == null;
    }

    /**
     * check whether value is not null.
     * @param value check value
     * @return true if value is not null.
     */
    public static boolean isNotNull( Object value ) {
        return ! isNull( value );
    }

    /**
     * check whether value is null or empty or consists with only spaces.
     * @param value check value
     * @return true if value is null or empty or consists with only spaces.
     */
    public static boolean isBlank( Object value ) {
    	return Strings.isBlank( value );
    }

    /**
     * check whether value is not null nor not empty or not consists with only spaces.
     * @param value check value
     * @return true if value is not null nor not empty or not consists with only spaces.
     */
    public static boolean isNotBlank( String value ) {
        return ! isBlank( value );
    }

    /**
     * check whether value is null or empty.<br>
     *
     * Condition to judge empty is different from type of instance.
     * <pre>
     *     1. String, StringBuffer, StringBuilder : empty
     *     2. Map, Collection : empty
     *     3. Array : size is zero.
     *     4. Any
     * </pre>
     * @param value check value
     * @return true if value is null or empty.
     */
    public static boolean isEmpty( Object value ) {

        if( value == null ) return true;

        if( value instanceof String ) {
            return ( (String) value ).length() == 0;
        } else if( value instanceof StringBuffer ) {
            return ( (StringBuffer) value ).length() == 0;
        } else if( value instanceof StringBuilder ) {
            return ( (StringBuilder) value ).length() == 0;
        } else if( value instanceof Map ) {
            return ( (Map) value ).isEmpty();
        } else if( value instanceof Collection ) {
            return ( (Collection) value ).isEmpty();
        } else if( Types.isArray(value) ) {
            return Array.getLength( value ) == 0;
        } else if( value instanceof InputStream ) {
            try {
                return ((InputStream) value).available() == 0;
            } catch( IOException e ) {}
        } else if( value instanceof NList ) {
            return ((NList) value).size() == 0;
        }

        return false;

    }

    /**
     * check whether value is not null nor not empty.<br>
     *
     * Condition to judge empty is different from type of instance.
     * <pre>
     *     1. String, StringBuffer, StringBuilder : empty
     *     2. Map, Collection : empty
     *     3. Array : size is zero.
     *     4. Any
     * </pre>
     * @param value check value
     * @return true if value is not null nor not empty.
     */
    public static boolean isNotEmpty( Object value ) {
        return ! isEmpty( value );
    }

    /**
     * check whether value is matched with regular expression pattern.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if value is matched with regular expression pattern.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isMatched( String value, String pattern ) {
        return value != null && pattern != null && Pattern.matches( pattern, value );
    }

    /**
     * check whether value is matched with regular expression pattern.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if value is matched with regular expression pattern.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isMatched( String value, Pattern pattern ) {
        return value != null && pattern != null && pattern.matcher( value ).matches();
    }

    /**
     * check whether value is not matched with regular expression pattern.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if value is not matched with regular expression pattern.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotMatched( String value, String pattern ) {
        return ! isMatched( value, pattern );
    }

    /**
     * check whether value is not matched with regular expression pattern.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if value is not matched with regular expression pattern.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotMatched( String value, Pattern pattern ) {
        return ! isMatched( value, pattern );
    }

    /**
     * check whether regular expression pattern is found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if regular expression pattern is found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isFound( String value, String pattern ) {
    	if( value == null || pattern == null ) return false;
    	Pattern regexp  = Pattern.compile( pattern, Pattern.MULTILINE | Pattern.DOTALL );
    	return isFound( value, regexp );
    }

    /**
     * check whether regular expression pattern is found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * @return true if regular expression pattern is found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isFound( String value, Pattern pattern ) {
        if( pattern == null || value == null ) return false;
        Matcher matcher = pattern.matcher( value );
        return matcher.find();
    }

    /**
     * check whether regular expression pattern is not found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if regular expression pattern is not found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotFound( String value, String pattern ) {
        return ! isFound( value, pattern );
    }

    /**
     * check whether regular expression pattern is not found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * @return true if regular expression pattern is not found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotFound( String value, Pattern pattern ) {
        return ! isFound( value, pattern );
    }

    /**
     * check whether regular expression pattern is found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * @param flags   Match flags, a bit mask that may include
     *         {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *         {@link Pattern#UNICODE_CASE},     {@link Pattern#CANON_EQ},  {@link Pattern#UNIX_LINES},
     *         {@link Pattern#LITERAL},          {@link Pattern#COMMENTS},  {@link Pattern#UNICODE_CHARACTER_CLASS}
     * @return true if regular expression pattern is found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isFound( String value, String pattern, int flags ) {
    	if( value == null || pattern == null ) return false;
    	Pattern regexp  = Pattern.compile( pattern, flags );
    	Matcher matcher = regexp.matcher( value );
    	return matcher.find();
    }

    /**
     * check whether regular expression pattern is not found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * @param flags   Match flags, a bit mask that may include
     *         {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *         {@link Pattern#UNICODE_CASE},     {@link Pattern#CANON_EQ},  {@link Pattern#UNIX_LINES},
     *         {@link Pattern#LITERAL},          {@link Pattern#COMMENTS},  {@link Pattern#UNICODE_CHARACTER_CLASS}
     * @return true if regular expression pattern is not found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotFound( String value, String pattern, int flags ) {
        return ! isFound( value, pattern, flags );
    }

    /**
     * check if value is Fixed number (numeric integer).
     *
     * @param value check value
     * @return true if value is Fixed number (numeric integer).
     */
    public static boolean isInt( String value ) {
        return Types.isInt( value );
    }

    /**
     * check if value is positive Fixed number (numeric integer).
     *
     * @param value check value
     * @return true if value is positive Fixed number (numeric integer).
     */
    public static boolean isPositiveInt( String value ) {
        return Types.isPositiveInt( value );
    }

     /**
     * Let you replace null (or empty)  with another value.
     *
     * if value is null or empty, examine replaceValue.
     * if replaceValue is null, examine next anotherReplaceValue.
     * if anotherReplaceValue is not null, it is returned as result.
     *
     * @param value                 value to examine not null or not empty.
     * @param replaceValue          other value to examine not null.
     * @param anotherReplaceValue   another values to examine not null.
     * @param <T> 			        expected class of return
     * @return not null value from begin with.
     */
    public static <T> T nvl( T value, T replaceValue, T... anotherReplaceValue ) {
        if( isNotEmpty(value) )       return value;
        if( isNotNull(replaceValue) ) return replaceValue;
        for( T val : anotherReplaceValue ) {
            if( isNotNull( val ) ) return val;
        }
        return null;
    }

    /**
     * check text is valid json type
     *
     * @param text	text to check format
     * @return valid or not
     */
    public static boolean isJson( String text ) {
        return Reflector.isJson( text );
    }

    /**
     * determine if the given objects are equal.
     * <ul>
     *   <li>it is null safe.</li>
     *   <li>it can check equality between arrays.</li>
     * </ul>
     *
     * @param o1    object
     * @param o2    another object
     * @return true if the given objects are equal.
     */
    public static boolean isEqual( Object o1, Object o2 ) {
        return Objects.deepEquals( o1, o2 );
    }

}
