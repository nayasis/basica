package io.nayasis.basica.base;

import io.nayasis.basica.model.NList;

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
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

import static java.math.BigDecimal.ONE;

/**
 * Type Check Utility
 *
 * @author nayasis@gmail.com
 * @since 2015-08-20
 */
public class Types {

    private final static Set<Class<?>> IMMUTABLE = new HashSet() {{
        add( void.class );       add( Void.class );
        add( char.class );       add( Character.class );
        add( boolean.class );    add( Boolean.class );
        add( byte.class );       add( Byte.class );
        add( short.class );      add( Short.class );
        add( int.class );        add( Integer.class );
        add( long.class );       add( Long.class );
        add( float.class );      add( Float.class );
        add( double.class );     add( Double.class );
        add( BigDecimal.class ); add( BigInteger.class );
        add( LocalDate.class );  add( LocalDateTime.class );
        add( String.class );
        add( URI.class );
        add( URL.class );
        add( UUID.class );
        add( Pattern.class );
        add( Class.class );
    }};

    private static boolean isEmpty( Class klass ) {
        return klass == null || klass == Object.class;
    }

    private static boolean isEmpty( Object instance ) {
        return instance == null || isEmpty( instance.getClass() );
    }

    private static boolean checkParents( Class<?> klass, Class<?>... checkTargets ) {
        if( isEmpty(klass) ) return false;
        Set<Class<?>> parents = Classes.findParents( klass );
        for( Class<?> target : checkTargets ) {
            if( parents.contains( target ) ) return true;
        }
        return false;
    }

    private static boolean checkType( Class<?> klass, Class<?>... types ) {
        if( isEmpty(klass) ) return false;
        for( Class type : types ) {
            if( klass == type ) return true;
        }
        return false;
    }

    public static boolean isMap( Class klass ) {
        return checkParents( klass, Map.class, Dictionary.class );
    }

    public static boolean isMap( Object instance ) {
        return ! isEmpty(instance) && isMap( instance.getClass() );
    }

    public static boolean isCollection( Class klass ) {
        return checkParents( klass, AbstractCollection.class, NList.class );
    }

    public static boolean isCollection( Object instance ) {
        return ! isEmpty(instance) && isCollection( instance.getClass() );
    }

    public static boolean isArray( Class klass ) {
        return klass != null && klass.isArray();
    }

    public static boolean isArray( Object instance ) {
        return instance != null && isArray( instance.getClass() );
    }

    public static boolean isArrayOrCollection( Class klass ) {
        return isArray( klass ) || isCollection( klass );
    }

    public static boolean isArrayOrCollection( Object instance ) {
        return isArray( instance ) || isCollection( instance );
    }

    public static boolean isBoolean( Object instance ) {
        return instance != null && isBoolean( instance.getClass() );
    }

    public static boolean isBoolean( Class klass ) {
        return checkType( klass, Boolean.class, boolean.class );
    }

    public static boolean isInt( Class klass ) {
        return checkType( klass, Integer.class, int.class );
    }

    public static boolean isInt( Object instance ) {
        return instance != null && isInt( instance.getClass() );
    }

    public static boolean isShort( Class klass ) {
        return checkType( klass, Short.class, short.class );
    }

    public static boolean isShort( Object instance ) {
        return instance != null && isShort( instance.getClass() );
    }

    public static boolean isByte( Class klass ) {
        return checkType( klass, Byte.class, byte.class );
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

    public static boolean isInt( String value ) {
        try {
            return new BigDecimal( value ).remainder( ONE )
                .compareTo( BigDecimal.ZERO ) == 0;
        } catch( Exception e ) {
            return false;
        }
    }

    public static boolean isPositiveInt( String value ) {
        try {
            BigDecimal number = new BigDecimal( value );
            if( number.compareTo( BigDecimal.ZERO ) <= 0 ) return false;
            return number.remainder( ONE ).compareTo( BigDecimal.ZERO ) == 0;
        } catch( Exception e ) {
            return false;
        }
    }

    public static boolean isLong( Class klass ) {
        return checkType( klass, Long.class, long.class );
    }

    public static boolean isLong( Object instance ) {
        return instance != null && isLong( instance.getClass() );
    }

    public static boolean isFloat( Class klass ) {
        return checkType( klass, Float.class, float.class );
    }

    public static boolean isFloat( Object instance ) {
        return instance != null && isFloat( instance.getClass() );
    }

    public static boolean isDouble( Class klass ) {
        return checkType( klass, Double.class, double.class );
    }

    public static boolean isDouble( Object instance ) {
        return instance != null && isDouble( instance.getClass() );
    }

    public static boolean isBigDecimal( Class klass ) {
        return checkType( klass, BigDecimal.class );
    }

    public static boolean isBigDecimal( Object instance ) {
        return instance != null && isBigDecimal( instance.getClass() );
    }

    public static boolean isBigInteger( Class klass ) {
        return checkType( klass, BigInteger.class );
    }

    public static boolean isBigInteger( Object instance ) {
        return instance != null && isBigInteger( instance.getClass() );
    }

    public static boolean isChar( Class klass ) {
        return checkType( klass, Characters.class, char.class );
    }

    public static boolean isChar( Object instance ) {
        return instance != null && isChar( instance.getClass() );
    }

    public static boolean isStringLike( Class klass ) {
        return checkType( klass, String.class, StringBuffer.class, StringBuilder.class );
    }

    public static boolean isStringLike( Object instance ) {
        return instance != null && isStringLike( instance.getClass() );
    }

    public static boolean isString( Class klass ) {
        return checkType( klass, String.class );
    }

    public static boolean isString( Object instance ) {
        return instance != null && isStringLike( instance.getClass() );
    }

    public static boolean isNumeric( Class klass ) {
        return isInt( klass ) || isLong( klass ) || isShort( klass ) || isByte(klass) || isFloat( klass ) || isDouble( klass ) || isBigDecimal( klass ) || isBigInteger( klass );
    }

    public static boolean isNumeric( Object instance ) {
        return instance != null && isNumeric( instance.getClass() );
    }

    public static boolean isNumeric( String value ) {
        try {
            new BigDecimal( value );
            return true;
        } catch( Exception e ) {
            return false;
        }
    }

    public static boolean isImmutable( Class klass ) {
        return klass != null && IMMUTABLE.contains( klass );
    }

    public static boolean isImmutable( Object object ) {
        return object != null && IMMUTABLE.contains( object.getClass() );
    }

    public static boolean isPrimitive( Class klass ) {
        return klass != null && klass.isPrimitive();
    }

    public static boolean isPrimitive( Object instance ) {
        return instance != null && isPrimitive( instance.getClass() );
    }

    public static boolean isEnum( Class klass ) {
        return klass.isEnum();
    }

    public static boolean isEnum( Object instance ) {
        return instance != null && isEnum( instance.getClass() );
    }

    public static <T> List<T> toList( Enumeration<T> instance ) {
        if( instance == null ) return new ArrayList();
        List<T> list = new ArrayList<>();
        while ( instance.hasMoreElements() ) {
            list.add( instance.nextElement() );
        }
        return list;
    }

    public static <T> List<T> toList( Iterator<T> instance ) {
        if( instance == null ) return new ArrayList();
        List<T> list = new ArrayList<>();
        while ( instance.hasNext() ) {
            list.add( instance.next() );
        }
        return list;
    }

    public static <T> List<T> toList( Collection<T> instance ) {
        if( instance == null ) return new ArrayList();
        return new ArrayList<>( instance );
    }

    public static <T> List<T> toList( Iterable<T> instance ) {
        if( instance == null ) return new ArrayList();
        return toList( instance.iterator() );
    }

    public static <T> List<T> toList( NList instance ) {
        if( instance == null ) return new ArrayList();
        return (List<T>) instance.toList();
    }

    public static List toList( Object instance ) {
        if( instance == null ) return new ArrayList();
        if( isArray(instance) ) {
            return arrayToList( instance );
        } else if( instance instanceof Enumeration ) {
            return toList( (Enumeration) instance );
        } else if( instance instanceof Iterator ) {
            return toList( (Iterator) instance );
        } else if( instance instanceof Iterable ) {
            return toList( (Iterable) instance );
        } else {
            return new ArrayList( Arrays.asList(instance) );
        }
    }

    public static <T> Collection<T> toCollection( Collection<T> instance ) {
        return toList( instance );
    }

    public static <T> Collection<T> toCollection( Iterable<T> instance ) {
        return toList( instance );
    }

    public static <T> Collection<T> toCollection( NList instance ) {
        return toList( instance );
    }

    public static Collection toCollection( Object value ) {
        return toList( value );
    }

    private static List arrayToList( Object object ) {
        List list = new ArrayList();
        int size = Array.getLength( object );
        for( int i=0; i < size; i++ ) {
            list.add( Array.get( object, i ) );
        }
        return list;
    }

    public static <T> T[] toArray( Collection<T> list, Class<T> returnType ) {
        T[] array = (T[]) Array.newInstance( returnType, 0 );
        return list.toArray( array );
    }

    public static String toString( Object val ) {
        if( val == null ) return null;
        if( isEnum(val) ) {
            return ((Enum)val).name();
        } else {
            return val.toString();
        }
    }

    public static Integer toInt( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value)        ) return (Integer)value;
        if( isLong(value)       ) return ((Long)value).intValue();
        if( isShort(value)      ) return ((Short)value).intValue();
        if( isByte(value)       ) return ((Byte)value).intValue();
        if( isChar(value)       ) return Integer.valueOf(((Character)value).charValue());
        if( isFloat(value)      ) return ((Float)value).intValue();
        if( isDouble(value)     ) return ((Double)value).intValue();
        if( isBigDecimal(value) ) return ((BigDecimal)value).intValue();
        if( isBigInteger(value) ) return ((BigInteger)value).intValue();
        if( isBoolean(value)    ) return toBoolean(value) ? 1 : 0;
        try {
            return Integer.parseInt( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Long toLong( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value)        ) return ((Integer)value).longValue();
        if( isLong(value)       ) return (Long)value;
        if( isShort(value)      ) return ((Short)value).longValue();
        if( isByte(value)       ) return ((Byte)value).longValue();
        if( isChar(value)       ) return Long.valueOf(((Character)value).charValue());
        if( isFloat(value)      ) return ((Float)value).longValue();
        if( isDouble(value)     ) return ((Double)value).longValue();
        if( isBigDecimal(value) ) return ((BigDecimal)value).longValue();
        if( isBigInteger(value) ) return ((BigInteger)value).longValue();
        if( isBoolean(value)    ) return toBoolean(value) ? 1L : 0;
        try {
            return Long.parseLong( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Float toFloat( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value)        ) return ((Integer)value).floatValue();
        if( isLong(value)       ) return ((Long)value).floatValue();
        if( isShort(value)      ) return ((Short)value).floatValue();
        if( isByte(value)       ) return ((Byte)value).floatValue();
        if( isChar(value)       ) return Float.valueOf(((Character)value).charValue());
        if( isFloat(value)      ) return (Float)value;
        if( isDouble(value)     ) return ((Double)value).floatValue();
        if( isBigDecimal(value) ) return ((BigDecimal)value).floatValue();
        if( isBigInteger(value) ) return ((BigInteger)value).floatValue();
        if( isBoolean(value)    ) return toBoolean(value) ? 1F : 0;
        try {
            return Float.parseFloat( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Double toDouble( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value)        ) return ((Integer)value).doubleValue();
        if( isLong(value)       ) return ((Long)value).doubleValue();
        if( isShort(value)      ) return ((Short)value).doubleValue();
        if( isByte(value)       ) return ((Byte)value).doubleValue();
        if( isChar(value)       ) return Double.valueOf(((Character)value).charValue());
        if( isDouble(value)     ) return (Double)value;
        if( isFloat(value)      ) return ((Float)value).doubleValue();
        if( isBigDecimal(value) ) return ((BigDecimal)value).doubleValue();
        if( isBigInteger(value) ) return ((BigInteger)value).doubleValue();
        if( isBoolean(value)    ) return toBoolean(value) ? 1D : 0;
        try {
            return Double.parseDouble( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Byte toByte( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value)        ) return ((Integer)value).byteValue();
        if( isLong(value)       ) return ((Long)value).byteValue();
        if( isShort(value)      ) return ((Short)value).byteValue();
        if( isByte(value)       ) return (Byte)value;
        if( isChar(value)       ) return Byte.valueOf((byte)((Character)value).charValue());
        if( isDouble(value)     ) return ((Double)value).byteValue();
        if( isFloat(value)      ) return ((Float)value).byteValue();
        if( isBigDecimal(value) ) return ((BigDecimal)value).byteValue();
        if( isBigInteger(value) ) return ((BigInteger)value).byteValue();
        if( isBoolean(value)    ) return toInt(value).byteValue();
        try {
            return Byte.parseByte( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Short toShort( Object value ) throws NumberFormatException {
        if( value == null ) return null;
        if( isInt(value)        ) return ((Integer)value).shortValue();
        if( isLong(value)       ) return ((Long)value).shortValue();
        if( isShort(value)      ) return (Short)value;
        if( isByte(value)       ) return ((Byte)value).shortValue();
        if( isChar(value)       ) return Short.valueOf((short) ((Character)value).charValue());
        if( isDouble(value)     ) return ((Double)value).shortValue();
        if( isFloat(value)      ) return ((Float)value).shortValue();
        if( isBigDecimal(value) ) return ((BigDecimal)value).shortValue();
        if( isBigInteger(value) ) return ((BigInteger)value).shortValue();
        if( isBoolean(value)    ) return toInt(value).shortValue();
        try {
            return Short.parseShort( value.toString() );
        } catch ( NumberFormatException e ) {
            return null;
        }
    }

    public static Character toChar( Object value ) {
        if( value == null ) return null;
        if( isInt(value)        ) return Character.valueOf((char)((Integer)value).intValue());
        if( isLong(value)       ) return Character.valueOf((char)((Long)value).intValue());
        if( isShort(value)      ) return Character.valueOf((char)((Short)value).intValue());
        if( isByte(value)       ) return Character.valueOf((char)((Byte)value).intValue());
        if( isChar(value)       ) return (Character)value;
        if( isDouble(value)     ) return Character.valueOf((char)((Double)value).intValue());
        if( isFloat(value)      ) return Character.valueOf((char)((Float)value).intValue());
        if( isBigDecimal(value) ) return Character.valueOf((char)((BigDecimal)value).intValue());
        if( isBigInteger(value) ) return Character.valueOf((char)((BigInteger)value).intValue());
        if( isBoolean(value)    ) return Character.valueOf((char)(toInt(value).intValue()));
        String string = value.toString();
        return  string.isEmpty() ? null : string.charAt(0);
    }

    public static BigDecimal toBigDecimal( Object value ) {
        if( value == null ) return null;
        if( isInt(value)        ) return new BigDecimal((Integer)value);
        if( isLong(value)       ) return new BigDecimal((Long)value);
        if( isShort(value)      ) return new BigDecimal((Short)value);
        if( isByte(value)       ) return new BigDecimal((Byte)value);
        if( isChar(value)       ) return new BigDecimal((Character)value);
        if( isDouble(value)     ) return new BigDecimal((Double)value);
        if( isFloat(value)      ) return new BigDecimal((Float)value);
        if( isBigDecimal(value) ) return (BigDecimal)value;
        if( isBigInteger(value) ) return new BigDecimal((BigInteger)value);
        if( isBoolean(value)    ) return new BigDecimal(toInt(value));
        try {
            return new BigDecimal( value.toString() );
        } catch( Exception e ) {
            return null;
        }
    }

    public static BigInteger toBigInteger( Object value ) {
        if( value == null ) return null;
        if( isNumeric(value) ) return BigInteger.valueOf( toLong(value) );
        try {
            return new BigInteger( value.toString() );
        } catch( Exception e ) {
            return null;
        }
    }

    public static Boolean toBoolean( Object value ) throws NumberFormatException {
        return toBoolean( value, false );
    }

    public static Boolean toBoolean( Object value, boolean emptyToTrue ) throws NumberFormatException {
        if( value == null ) {
            return emptyToTrue ? true : false;
        }
        if( isBoolean(value) ) return (Boolean) value;
        if( isIntLike(value) || isBigInteger(value) ) return toInt(value) == 1;
        if( isFloat(value) ) return toFloat(value) == 1;
        if( isDouble(value) ) return toDouble(value) == 1;
        if( isBigDecimal(value) ) return toBigDecimal(value).compareTo(ONE) == 0;
        return Strings.toBoolean( value, emptyToTrue );
    }

    public static Object castPrimitive( Object val, Class castType ) {

        if( isString(castType) ) {
            return val.toString();
        } else if( isBoolean(castType) ) {
            return toBoolean(val);
        } else if( isInt(castType) ) {
            return toInt(val);
        } else if( isLong(castType) ) {
            return toLong(val);
        } else if( isShort(castType) ) {
            return toShort(val);
        } else if( isByte(castType) ) {
            return toByte(val);
        } else if( isChar(castType) ) {
            return toChar(val);
        } else if( isDouble(castType) ) {
            return toDouble(val);
        } else if( isFloat(castType) ) {
            return toFloat(val);
        } else if( isBigDecimal(castType) ) {
            return toBigDecimal(val);
        } else if( isBigInteger(castType) ) {
            return toBigInteger(val);
        } else {
            return val;
        }

    }

    public static <T> Class<T> wrap( Class<T> type ) {
        if (type == int.class)     return (Class<T>) Integer.class;
        if (type == long.class)    return (Class<T>) Long.class;
        if (type == byte.class)    return (Class<T>) Byte.class;
        if (type == boolean.class) return (Class<T>) Boolean.class;
        if (type == double.class)  return (Class<T>) Double.class;
        if (type == char.class)    return (Class<T>) Character.class;
        if (type == float.class)   return (Class<T>) Float.class;
        if (type == short.class)   return (Class<T>) Short.class;
        if (type == void.class)    return (Class<T>) Void.class;
        return type;
    }

    public static <T> Class<T> unwrap( Class<T> type ) {
        if (type == Integer.class)   return (Class<T>) int.class;
        if (type == Long.class)      return (Class<T>) long.class;
        if (type == Byte.class)      return (Class<T>) byte.class;
        if (type == Boolean.class)   return (Class<T>) boolean.class;
        if (type == Double.class)    return (Class<T>) double.class;
        if (type == Float.class)     return (Class<T>) float.class;
        if (type == Character.class) return (Class<T>) char.class;
        if (type == Short.class)     return (Class<T>) short.class;
        if (type == Void.class)      return (Class<T>) void.class;
        return type;
    }

}
