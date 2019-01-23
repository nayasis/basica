package io.nayasis.common.base;

import io.nayasis.common.model.NDate;
import io.nayasis.common.model.NList;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Type Check Utility
 *
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class Types {

    private static Set<Class<?>> PRIMITIVE = new HashSet() {{
        add( void.class ); add( Void.class );
        add( char.class ); add( Character.class );
        add( boolean.class ); add( Boolean.class );
        add( byte.class ); add( Byte.class );
        add( short.class ); add( Short.class );
        add( int.class ); add( Integer.class );
        add( long.class ); add( Long.class );
        add( float.class ); add( Float.class );
        add( double.class ); add( Double.class );
        add( String.class );
        add( StringBuilder.class );
        add( StringBuffer.class );
        add( BigDecimal.class );
        add( BigInteger.class );
        add( Date.class );
        add( Calendar.class );
        add( LocalDate.class );
        add( LocalDateTime.class );
        add( NDate.class );
        add( URI.class );
        add( URL.class );
        add( UUID.class );
        add( Pattern.class );
    }};

    private static boolean isEmpty( Class klass ) {
        return klass == null || klass == Object.class;
    }

    private static boolean isNotEmpty( Class klass ) {
        return ! isEmpty( klass );
    }

    private static boolean isEmpty( Object instance ) {
        return instance == null || isEmpty( instance.getClass() );
    }

    private static boolean isNotEmpty( Object instance ) {
        return ! isEmpty( instance );
    }

    private static boolean checkParents( Class<?> klass, Class<?>... checkTargets ) {
        if( isEmpty(klass) ) return false;
        Set<Class<?>> parents = Classes.findParents( klass );
        for( Class<?> target : checkTargets ) {
            if( parents.contains( target ) ) return true;
        }
        return false;
    }

    private static boolean checkEqual( Class<?> klass, Class<?>... checkTargets ) {
        if( isEmpty(klass) ) return false;
        for( Class<?> target : checkTargets ) {
            if( klass == target ) return true;
        }
        return false;
    }

    public static boolean isMap( Class klass ) {
        return checkParents( klass, Map.class, Dictionary.class );
    }

    public static boolean isNotMap( Class klass ) {
        return ! isMap( klass );
    }

    public static boolean isMap( Object instance ) {
        return isNotEmpty(instance) && isMap( instance.getClass() );
    }

    public static boolean isNotMap( Object instance ) {
        return ! isNotMap( instance );
    }

    public static boolean isCollection( Class klass ) {
        return checkParents( klass, AbstractCollection.class, NList.class );
    }

    public static boolean isNotCollection( Class klass ) {
        return ! isNotCollection( klass );
    }

    public static boolean isCollection( Object instance ) {
        return isNotEmpty(instance) && isCollection( instance.getClass() );
    }

    public static boolean isNotCollection( Object instance ) {
        return ! isCollection( instance );
    }

    public static boolean isArray( Class klass ) {
        return klass != null && klass.isArray();
    }

    public static boolean isArray( Object instance ) {
        return instance != null && isArray( instance.getClass() );
    }

    public static boolean isNotArray( Class klass ) {
        return ! isArray( klass );
    }

    public static boolean isNotArray( Object instance ) {
        return ! isArray( instance );
    }

    public static boolean isBoolean( Object instance ) {
        return instance != null && isBoolean( instance.getClass() );
    }

    public static boolean isNotBoolean( Object instance ) {
        return ! isBoolean( instance );
    }

    public static boolean isBoolean( Class klass ) {
        return checkEqual( klass, Boolean.class, boolean.class );
    }

    public static boolean isNotBoolean( Class klass ) {
        return ! isBoolean( klass );
    }

    public static boolean isInt( Class klass ) {
        return checkEqual( klass, Integer.class, int.class );
    }

    public static boolean isInt( Object instance ) {
        return instance != null && isInt( instance.getClass() );
    }

    public static boolean isShort( Class klass ) {
        return checkEqual( klass, Short.class, short.class );
    }

    public static boolean isShort( Object instance ) {
        return instance != null && isShort( instance.getClass() );
    }

    public static boolean isByte( Class klass ) {
        return checkEqual( klass, Byte.class, byte.class );
    }

    public static boolean isByte( Object instance ) {
        return instance != null && isByte( instance.getClass() );
    }

    public static boolean isIntLike( Class klass ) {
        // byte < short < int < long
        return isInt( klass ) || isLong( klass ) || isShort( klass ) || isByte(klass);
    }

    public static boolean isIntLike( Object instance ) {
        return instance != null && isIntLike( instance.getClass() );
    }

    public static boolean isLong( Class klass ) {
        return checkEqual( klass, Long.class, long.class );
    }

    public static boolean isLong( Object instance ) {
        return instance != null && isLong( instance.getClass() );
    }

    public static boolean isFloat( Class klass ) {
        return checkEqual( klass, Float.class, float.class );
    }

    public static boolean isFloat( Object instance ) {
        return instance != null && isFloat( instance.getClass() );
    }

    public static boolean isDouble( Class klass ) {
        return checkEqual( klass, Double.class, double.class );
    }

    public static boolean isDouble( Object instance ) {
        return instance != null && isDouble( instance.getClass() );
    }

    public static boolean isBigDecimal( Class klass ) {
        return checkEqual( klass, BigDecimal.class );
    }

    public static boolean isBigDecimal( Object instance ) {
        return instance != null && isBigDecimal( instance.getClass() );
    }

    public static boolean isBigInteger( Class klass ) {
        return checkEqual( klass, BigInteger.class );
    }

    public static boolean isBigInteger( Object instance ) {
        return instance != null && isBigInteger( instance.getClass() );
    }

    public static boolean isChar( Class klass ) {
        return checkEqual( klass, Characters.class, char.class );
    }

    public static boolean isChar( Object instance ) {
        return instance != null && isChar( instance.getClass() );
    }

    public static boolean isString( Class klass ) {
        return checkEqual( klass, String.class, StringBuffer.class, StringBuilder.class );
    }

    public static boolean isString( Object instance ) {
        return instance != null && isString( instance.getClass() );
    }

    public static boolean isNotString( Object value ) {
        return ! isString( value );
    }

    public static boolean isNumeric( Class klass ) {
        return isInt( klass ) || isLong( klass ) || isShort( klass ) || isByte(klass) || isFloat( klass ) || isDouble( klass ) || isBigDecimal( klass ) || isBigInteger( klass );
    }

    public static boolean isNumeric( Object instance ) {
        return instance != null && isNumeric( instance.getClass() );
    }

    public static boolean isPrimitive( Class klass ) {
        return PRIMITIVE.contains( klass );
    }

    public static boolean isNotPrimitive( Class klass ) {
        return ! isPrimitive( klass );
    }

    public static boolean isPrimitive( Object instance ) {
        return instance != null && isPrimitive( instance.getClass() );
    }

    public static boolean isNotPrimitive( Object instance ) {
        return ! isPrimitive( instance );
    }

    public static List toList( Object instance ) {

        List result = null;

        if( instance == null ) {
            result = new ArrayList();

        } else {

            if( instance instanceof List ) {
                result = new ArrayList<>( (List) instance );
            } else if( instance instanceof Set ) {
                result = new ArrayList<>( (Set) instance );
            } else if( instance instanceof NList ) {
                result = ((NList) instance).toList();
            } else if( instance.getClass().isArray() ) {
                result = Arrays.asList( (Object[]) instance );
            }

        }

        return result;

    }

    public static String toString( Object val ) {
        return val == null ? null : val.toString();
    }

    public static Integer toInt( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value) || isShort(value) || isByte(value) ) return (Integer) value;
        try {
            return Integer.parseInt( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Long toLong( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isLong(value)|| isInt(value) || isShort(value) || isByte(value) ) return (Long) value;
        try {
            return Long.parseLong( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Float toFloat( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isFloat(value) || isDouble(value) ) return (Float) value;
        try {
            return Float.parseFloat( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Double toDouble( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isDouble(value) || isFloat(value) ) return (Double) value;
        try {
            return Double.parseDouble( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Boolean toBoolean( Object value ) throws NumberFormatException {
        if( isBoolean(value) ) return (Boolean) value;
        return Strings.toBoolean( value );
    }

    public static Byte toByte( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isByte(value) ) return (Byte) value;
        try {
            return Byte.parseByte( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Short toShort( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isShort(value) || isByte(value) ) return (Short) value;
        try {
            return Short.parseShort( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Character toChar( Object value ) {
        if( value == null ) return null;
        if( isChar(value) ) return (Character) value;
        String string = value.toString();
        return  string.isEmpty() ? null : string.charAt( 0 );
    }

}
