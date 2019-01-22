package io.nayasis.common.base;

import io.nayasis.common.model.NDate;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.ClassUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Primitive {

	private static final Map<Class<?>,Class<?>> TO_PRIMITIVE = new HashMap<>( 32 );
	private static final Map<Class<?>,Class<?>> TO_WRAPPER   = new HashMap<>( 32 );

	static {
		add( void.class,          Void.class          );
		add( String.class,        String.class        );
		add( char.class,          Character.class     );
		add( int.class,           Integer.class       );
		add( long.class,          Long.class          );
		add( float.class,         Float.class         );
		add( double.class,        Double.class        );
		add( BigDecimal.class,    BigDecimal.class    );
		add( BigInteger.class,    BigInteger.class    );
		add( byte.class,          Byte.class          );
		add( short.class,         Short.class         );
		add( boolean.class,       Boolean.class       );
		add( Date.class,          Date.class          );
		add( Calendar.class,      Calendar.class      );
		add( LocalDate.class,     LocalDate.class     );
		add( LocalDateTime.class, LocalDateTime.class );
		add( NDate.class,         NDate.class         );
		add( Void.class,          Void.class          );
		add( URI.class,           URI.class           );
		add( URL.class,           URL.class           );
		add( UUID.class,          UUID.class          );
		add( Pattern.class,       Pattern.class       );
	}

	private static void add( Class<?> primitiveClass, Class<?> wrapperClass ) {
		TO_PRIMITIVE.put( wrapperClass, primitiveClass );
		TO_WRAPPER.put( primitiveClass, wrapperClass );
	}

	public static String toString( Object val ) {
		return val == null ? null : val.toString();
	}

	public static Integer toInt( Object val ) {
		return Integer.parseInt( toString(val) );
	}

	public static long toLong( Object val ) {
		return Long.parseLong( toString(val) );
	}

	public static float toFloat( Object val ) {
		return Float.parseFloat( toString(val) );
	}

	public double toDouble() {
		try {
			return Double.parseDouble( nvlVal );
		} catch( NumberFormatException e ) {
        	if( ! ignoreCastingError ) throw e;
        	return 0.;
		}
	}

	public boolean toBoolean() {

		if( "true".equalsIgnoreCase(nvlVal) ) return true;

		if( ignoreCastingError ) {
			if( "y".equalsIgnoreCase(nvlVal)   ) return true;
			if( "yes".equalsIgnoreCase(nvlVal) ) return true;
		}

		return false;

	}

	public byte toByte() {
		try {
			return Byte.parseByte( nvlVal );
		} catch( NumberFormatException e ) {
			if( ! ignoreCastingError ) throw e;
			return 0;
		}
	}

	public short toShort() {
		try {
			return Short.parseShort( nvlVal );
		} catch( NumberFormatException e ) {
			if( ! ignoreCastingError ) throw e;
			return (short) 0;
		}
	}

	public char toChar() {
		if( isEmpty() ) {
			return Character.MIN_VALUE;
		} else {
			return nvlVal.charAt( 0 );
		}
	}

	public NDate toNDate() {

		if( isEmpty() ) return ignoreCastingError ? NDate.MIN_DATE : null;

		Class<?> klass = val.getClass();

		if( klass == NDate.class    ) return (NDate) val;
		if( klass == Date.class     ) return new NDate( (Date) val );
		if( klass == Calendar.class ) return new NDate( (Calendar) val );

		try {
	        return new NDate( nvlVal );
        } catch( ParseException e ) {
        	if( ! ignoreCastingError ) throw e;
        	return NDate.MIN_DATE;
        }

	}

	public Date toDate() {

		if( isEmpty() ) return ignoreCastingError ? NDate.MIN_DATE.toDate() : null;

		Class<?> klass = val.getClass();

		if( klass == Date.class     ) return (Date) val;
		if( klass == NDate.class    ) return ((NDate)val).toDate();
		if( klass == Calendar.class ) return new NDate( (Calendar) val ).toDate();

		NDate date = toNDate();
		return date == null ? null : date.toDate();

	}

	public Calendar toCalendar() {

		if( isEmpty() ) return ignoreCastingError ? NDate.MIN_DATE.toCalendar() : null;

		Class<?> klass = val.getClass();

		if( klass == Calendar.class ) return (Calendar) val;
		if( klass == NDate.class    ) return ((NDate)val).toCalendar();
		if( klass == Date.class     ) return new NDate( (Date) val ).toCalendar();

		NDate date = toNDate();
		return date == null ? null : date.toCalendar();
	}

	public BigInteger toBigInt() {
		try {
			return new BigInteger( nvlVal );
		} catch( NumberFormatException e ) {
        	if( ! ignoreCastingError ) throw e;
        	return BigInteger.ZERO;
		}
	}

	public BigDecimal toBigDecimal() {
		try {
			return new BigDecimal( nvlVal );
		} catch( NumberFormatException e ) {
        	if( ! ignoreCastingError ) throw e;
        	return BigDecimal.ZERO;
		}
	}

	public Void toVoid() {
		return null;
	}

	public URI toURI() {
		try {
	        return new URI( nvlVal );
        } catch( URISyntaxException e ) {
        	if( ! ignoreCastingError ) throw new IllegalArgumentException( e );
        	return null;
        }
	}

	public URL toURL() {
		try {
			return new URL( nvlVal );
		} catch( MalformedURLException e ) {
			if( ! ignoreCastingError ) throw new IllegalArgumentException( e );
			return null;
		}
	}

	public UUID toUUID() {
        try {
            return UUID.fromString(nvlVal);
        } catch(IllegalArgumentException e) {
        	if( ! ignoreCastingError ) throw e;
            return null;
        }
	}

	public Pattern toPattern() {
		try {
			return Pattern.compile( nvlVal );
		} catch( PatternSyntaxException e ) {
			return Pattern.compile( "" );
		}
	}

	private boolean isEmpty() {
		return val == null || val.toString().length() == 0;
	}

	public Map<String, Object> toMap() {
		return Reflector.toMapFrom( nvlVal );
	}

	public <T> T toBean( Class<T> klass ) {
		return Reflector.toBeanFrom( toMap(), klass );
	}

	@SuppressWarnings( "unchecked" )
    public <T> Object cast( Class<T> klass ) {

		if( val == null && ! ignoreCastingError ) return null;

		Class<?> klassWrapped = wrap( klass );

		if( wrap(val) == klassWrapped ) return val;

		if( ! isPrimitive(klass) ) return val;

		if( klassWrapped == String.class     ) return toString();
		if( klassWrapped == Character.class  ) return toChar();
		if( klassWrapped == Integer.class    ) return toInt();
		if( klassWrapped == Long.class       ) return toLong();
		if( klassWrapped == Double.class     ) return toDouble();
		if( klassWrapped == Float.class      ) return toFloat();
		if( klassWrapped == BigDecimal.class ) return toBigDecimal();
		if( klassWrapped == BigInteger.class ) return toBigInt();
		if( klassWrapped == Boolean.class    ) return toBoolean();
		if( klassWrapped == Byte.class       ) return toByte();
		if( klassWrapped == Short.class      ) return toShort();
		if( klassWrapped == Date.class       ) return toDate();
		if( klassWrapped == Calendar.class   ) return toCalendar();
		if( klassWrapped == NDate.class      ) return toNDate();
		if( klassWrapped == Date.class       ) return toDate();
		if( klassWrapped == Calendar.class   ) return toCalendar();
		if( klassWrapped == Void.class       ) return toVoid();
		if( klassWrapped == URI.class        ) return toURI();
		if( klassWrapped == URL.class        ) return toURL();
		if( klassWrapped == UUID.class       ) return toUUID();
		if( klassWrapped == Pattern.class    ) return toPattern();

		if( val == null ) return null;

		try {
			if( ClassUtil.isExtendedBy( val, Map.class ) ) return toMap();
			return toBean( klass );
		} catch( Exception e ) {
			return val;
		}

	}

	public static boolean isPrimitive( Class<?> klass ) {
		return TO_PRIMITIVE.containsKey( klass );
	}

	public static boolean isWrapper( Class<?> klass ) {
		return TO_WRAPPER.containsKey( klass );
	}

    private static <T> Class<T> wrap( T value ) {
		if( value == null ) return null;
		return (Class<T>) wrap( value.getClass() );
	}

	private static <T> Class<T> wrap( Class<T> klass ) {
		if( klass == null ) return klass;
		Class<T> wrapped = (Class<T>) TO_WRAPPER.get( klass );
		return ( wrapped == null ) ? klass : wrapped;
	}

}
