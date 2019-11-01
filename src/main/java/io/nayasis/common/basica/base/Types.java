package io.nayasis.common.basica.base;

import io.nayasis.common.basica.model.NDate;
import io.nayasis.common.basica.model.NList;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Type Check Utility
 *
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
@UtilityClass
public class Types {

    private Set<Class<?>> PRIMITIVE = new HashSet() {{
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

    private boolean isEmpty( Class klass ) {
        return klass == null || klass == Object.class;
    }

    private boolean isEmpty( Object instance ) {
        return instance == null || isEmpty( instance.getClass() );
    }

    private boolean checkParents( Class<?> klass, Class<?>... checkTargets ) {
        if( isEmpty(klass) ) return false;
        Set<Class<?>> parents = Classes.findParents( klass );
        for( Class<?> target : checkTargets ) {
            if( parents.contains( target ) ) return true;
        }
        return false;
    }

    private boolean checkEqual( Class<?> klass, Class<?>... checkTargets ) {
        if( isEmpty(klass) ) return false;
        for( Class<?> target : checkTargets ) {
            if( klass == target ) return true;
        }
        return false;
    }

    public boolean isMap( Class klass ) {
        return checkParents( klass, Map.class, Dictionary.class );
    }

    public boolean isMap( Object instance ) {
        return ! isEmpty(instance) && isMap( instance.getClass() );
    }

    public boolean isCollection( Class klass ) {
        return checkParents( klass, AbstractCollection.class, NList.class );
    }

    public boolean isCollection( Object instance ) {
        return ! isEmpty(instance) && isCollection( instance.getClass() );
    }

    public boolean isArray( Class klass ) {
        return klass != null && klass.isArray();
    }

    public boolean isArray( Object instance ) {
        return instance != null && isArray( instance.getClass() );
    }

    public boolean isArrayOrCollection( Class klass ) {
        return isArray( klass ) || isCollection( klass );
    }

    public boolean isArrayOrCollection( Object instance ) {
        return isArray( instance ) || isCollection( instance );
    }

    public boolean isBoolean( Object instance ) {
        return instance != null && isBoolean( instance.getClass() );
    }

    public boolean isBoolean( Class klass ) {
        return checkEqual( klass, Boolean.class, boolean.class );
    }

    public boolean isInt( Class klass ) {
        return checkEqual( klass, Integer.class, int.class );
    }

    public boolean isInt( Object instance ) {
        return instance != null && isInt( instance.getClass() );
    }

    public boolean isShort( Class klass ) {
        return checkEqual( klass, Short.class, short.class );
    }

    public boolean isShort( Object instance ) {
        return instance != null && isShort( instance.getClass() );
    }

    public boolean isByte( Class klass ) {
        return checkEqual( klass, Byte.class, byte.class );
    }

    public boolean isByte( Object instance ) {
        return instance != null && isByte( instance.getClass() );
    }

    public boolean isIntLike( Class klass ) {
        // byte < short < int < long
        return isInt( klass ) || isLong( klass ) || isShort( klass ) || isByte(klass);
    }

    public boolean isIntLike( Object instance ) {
        return instance != null && isIntLike( instance.getClass() );
    }

    public boolean isInt( String value ) {
        try {
            return new BigDecimal( value ).remainder( BigDecimal.ONE )
                .compareTo( BigDecimal.ZERO ) == 0;
        } catch( Exception e ) {
            return false;
        }
    }

    public boolean isPositiveInt( String value ) {
        try {
            BigDecimal number = new BigDecimal( value );
            if( number.compareTo( BigDecimal.ZERO ) <= 0 ) return false;
            return number.remainder( BigDecimal.ONE ).compareTo( BigDecimal.ZERO ) == 0;
        } catch( Exception e ) {
            return false;
        }
    }

    public boolean isLong( Class klass ) {
        return checkEqual( klass, Long.class, long.class );
    }

    public boolean isLong( Object instance ) {
        return instance != null && isLong( instance.getClass() );
    }

    public boolean isFloat( Class klass ) {
        return checkEqual( klass, Float.class, float.class );
    }

    public boolean isFloat( Object instance ) {
        return instance != null && isFloat( instance.getClass() );
    }

    public boolean isDouble( Class klass ) {
        return checkEqual( klass, Double.class, double.class );
    }

    public boolean isDouble( Object instance ) {
        return instance != null && isDouble( instance.getClass() );
    }

    public boolean isBigDecimal( Class klass ) {
        return checkEqual( klass, BigDecimal.class );
    }

    public boolean isBigDecimal( Object instance ) {
        return instance != null && isBigDecimal( instance.getClass() );
    }

    public boolean isBigInteger( Class klass ) {
        return checkEqual( klass, BigInteger.class );
    }

    public boolean isBigInteger( Object instance ) {
        return instance != null && isBigInteger( instance.getClass() );
    }

    public boolean isChar( Class klass ) {
        return checkEqual( klass, Characters.class, char.class );
    }

    public boolean isChar( Object instance ) {
        return instance != null && isChar( instance.getClass() );
    }

    public boolean isString( Class klass ) {
        return checkEqual( klass, String.class, StringBuffer.class, StringBuilder.class );
    }

    public boolean isString( Object instance ) {
        return instance != null && isString( instance.getClass() );
    }

    public boolean isNumeric( Class klass ) {
        return isInt( klass ) || isLong( klass ) || isShort( klass ) || isByte(klass) || isFloat( klass ) || isDouble( klass ) || isBigDecimal( klass ) || isBigInteger( klass );
    }

    public boolean isNumeric( Object instance ) {
        return instance != null && isNumeric( instance.getClass() );
    }

    public boolean isNumeric( String value ) {
        try {
            new BigDecimal( value );
            return true;
        } catch( Exception e ) {
            return false;
        }
    }

    public boolean isPrimitive( Class klass ) {
        return PRIMITIVE.contains( klass );
    }

    public boolean isPrimitive( Object instance ) {
        return instance != null && isPrimitive( instance.getClass() );
    }

    public boolean isEnum( Class klass ) {
        return klass.isEnum();
    }

    public boolean isEnum( Object instance ) {
        return instance != null && isEnum( instance.getClass() );
    }

    public boolean isNotPrimitive( Object instance ) {
        return ! isPrimitive( instance );
    }

    public List toList( Object instance ) {

        if( instance == null ) return new ArrayList();

        if( instance instanceof Collection ) {
            return new ArrayList<>( (Collection) instance );
        } else if( instance instanceof NList ) {
            return ((NList) instance).toList();
        } else if( isArray(instance) ) {
            return arrayToList( instance );
        } else {
            return new ArrayList( Arrays.asList(instance) );
        }
    }

    private List arrayToList( Object object ) {
        List list = new ArrayList();
        int size = Array.getLength( object );
        for( int i=0; i < size; i++ ) {
            list.add( Array.get( object, i ) );
        }
        return list;
    }

    public Collection toCollection( Object value ) {
        return toList( value );
    }

    public String toString( Object val ) {
        if( val == null ) return null;
        if( isEnum(val) ) {
            return ((Enum)val).name();
        } else {
            return val.toString();
        }
    }

    public Integer toInt( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value) || isShort(value) || isByte(value) ) return (Integer) value;
        try {
            return Integer.parseInt( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public Long toLong( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isLong(value)|| isInt(value) || isShort(value) || isByte(value) ) return (Long) value;
        try {
            return Long.parseLong( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public Float toFloat( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isFloat(value) || isDouble(value) ) return (Float) value;
        try {
            return Float.parseFloat( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public Double toDouble( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isDouble(value) || isFloat(value) ) return (Double) value;
        try {
            return Double.parseDouble( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public Boolean toBoolean( Object value ) throws NumberFormatException {
        if( isBoolean(value) ) return (Boolean) value;
        return Strings.toBoolean( value );
    }

    public Byte toByte( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isByte(value) ) return (Byte) value;
        try {
            return Byte.parseByte( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public Short toShort( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isShort(value) || isByte(value) ) return (Short) value;
        try {
            return Short.parseShort( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public Character toChar( Object value ) {
        if( value == null ) return null;
        if( isChar(value) ) return (Character) value;
        String string = value.toString();
        return  string.isEmpty() ? null : string.charAt( 0 );
    }

}
