package io.nayasis.basica.base;

import io.nayasis.basica.model.NList;
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
@UtilityClass
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

    private boolean checkType( Class<?> klass, Class<?>... types ) {
        if( isEmpty(klass) ) return false;
        for( Class type : types ) {
            if( klass == type ) return true;
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
        return checkType( klass, Boolean.class, boolean.class );
    }

    public boolean isInt( Class klass ) {
        return checkType( klass, Integer.class, int.class );
    }

    public boolean isInt( Object instance ) {
        return instance != null && isInt( instance.getClass() );
    }

    public boolean isShort( Class klass ) {
        return checkType( klass, Short.class, short.class );
    }

    public boolean isShort( Object instance ) {
        return instance != null && isShort( instance.getClass() );
    }

    public boolean isByte( Class klass ) {
        return checkType( klass, Byte.class, byte.class );
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
            return new BigDecimal( value ).remainder( ONE )
                .compareTo( BigDecimal.ZERO ) == 0;
        } catch( Exception e ) {
            return false;
        }
    }

    public boolean isPositiveInt( String value ) {
        try {
            BigDecimal number = new BigDecimal( value );
            if( number.compareTo( BigDecimal.ZERO ) <= 0 ) return false;
            return number.remainder( ONE ).compareTo( BigDecimal.ZERO ) == 0;
        } catch( Exception e ) {
            return false;
        }
    }

    public boolean isLong( Class klass ) {
        return checkType( klass, Long.class, long.class );
    }

    public boolean isLong( Object instance ) {
        return instance != null && isLong( instance.getClass() );
    }

    public boolean isFloat( Class klass ) {
        return checkType( klass, Float.class, float.class );
    }

    public boolean isFloat( Object instance ) {
        return instance != null && isFloat( instance.getClass() );
    }

    public boolean isDouble( Class klass ) {
        return checkType( klass, Double.class, double.class );
    }

    public boolean isDouble( Object instance ) {
        return instance != null && isDouble( instance.getClass() );
    }

    public boolean isBigDecimal( Class klass ) {
        return checkType( klass, BigDecimal.class );
    }

    public boolean isBigDecimal( Object instance ) {
        return instance != null && isBigDecimal( instance.getClass() );
    }

    public boolean isBigInteger( Class klass ) {
        return checkType( klass, BigInteger.class );
    }

    public boolean isBigInteger( Object instance ) {
        return instance != null && isBigInteger( instance.getClass() );
    }

    public boolean isChar( Class klass ) {
        return checkType( klass, Characters.class, char.class );
    }

    public boolean isChar( Object instance ) {
        return instance != null && isChar( instance.getClass() );
    }

    public boolean isStringLike( Class klass ) {
        return checkType( klass, String.class, StringBuffer.class, StringBuilder.class );
    }

    public boolean isStringLike( Object instance ) {
        return instance != null && isStringLike( instance.getClass() );
    }

    public boolean isString( Class klass ) {
        return checkType( klass, String.class );
    }

    public boolean isString( Object instance ) {
        return instance != null && isStringLike( instance.getClass() );
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

    public boolean isImmutable( Class klass ) {
        return klass != null && IMMUTABLE.contains( klass );
    }

    public boolean isImmutable( Object object ) {
        return object != null && IMMUTABLE.contains( object.getClass() );
    }

    public boolean isPrimitive( Class klass ) {
        return klass != null && klass.isPrimitive();
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

    public List toList( Object instance ) {

        if( instance == null ) return new ArrayList();

        if( instance instanceof Collection ) {
            return new ArrayList<>( (Collection) instance );
        } else if( instance instanceof NList ) {
            return ((NList) instance).toList();
        } else if( isArray(instance) ) {
            return arrayToList( instance );
        } else if( instance instanceof Enumeration ) {
            Enumeration enumeration = (Enumeration) instance;
            List list = new ArrayList();
            while ( enumeration.hasMoreElements() ) {
                list.add( enumeration.nextElement() );
            }
            return list;
        } else if( instance instanceof Iterator ) {
            Iterator iterator = (Iterator) instance;
            List list = new ArrayList();
            while( iterator.hasNext() ) {
                list.add( iterator.next() );
            }
            return list;
        } else if( instance instanceof Iterable ) {
            Iterator iterator = ((Iterable) instance).iterator();
            List list = new ArrayList();
            while( iterator.hasNext() ) {
                list.add( iterator.next() );
            }
            return list;
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

    public <T> T[] toArray( Collection<T> list, Class<T> returnType ) {
        T[] array = (T[]) Array.newInstance( returnType, 0 );
        return list.toArray( array );
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

    public Long toLong( Object value ) throws NumberFormatException {
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

    public Float toFloat( Object value ) throws NumberFormatException {
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

    public Double toDouble( Object value ) throws NumberFormatException {
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

    public Byte toByte( Object value ) throws NumberFormatException {
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

    public Short toShort( Object value ) throws NumberFormatException {
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

    public Character toChar( Object value ) {
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

    public BigDecimal toBigDecimal( Object value ) {
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

    public BigInteger toBigInteger( Object value ) {
        if( value == null ) return null;
        if( isNumeric(value) ) return BigInteger.valueOf( toLong(value) );
        try {
            return new BigInteger( value.toString() );
        } catch( Exception e ) {
            return null;
        }
    }

    public Boolean toBoolean( Object value ) throws NumberFormatException {
        return toBoolean( value, false );
    }

    public Boolean toBoolean( Object value, boolean emptyToTrue ) throws NumberFormatException {
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

    public <T> T castPrimitive( Object val, Class<T> castType ) {

        if( isString(castType) ) {
            return (T) val.toString();
        } else if( isBoolean(castType) ) {
            return (T) toBoolean(val);
        } else if( isInt(castType) ) {
            return (T) toInt(val);
        } else if( isLong(castType) ) {
            return (T) toLong(val);
        } else if( isShort(castType) ) {
            return (T) toShort(val);
        } else if( isByte(castType) ) {
            return (T) toByte(val);
        } else if( isChar(castType) ) {
            return (T) toChar(val);
        } else if( isDouble(castType) ) {
            return (T) toDouble(val);
        } else if( isFloat(castType) ) {
            return (T) toFloat(val);
        } else if( isBigDecimal(castType) ) {
            return (T) toBigDecimal(val);
        } else if( isBigInteger(castType) ) {
            return (T) toBigInteger(val);
        } else {
            return (T) val;
        }

    }

    public <T> Class<T> wrap( Class<T> type ) {
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
