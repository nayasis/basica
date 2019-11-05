package io.nayasis.common.basica.reflection.core;

import io.nayasis.common.basica.base.Classes;
import io.nayasis.common.basica.base.Types;
import io.nayasis.common.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.common.basica.reflection.Reflector;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
public class Cloner {

    private final Set<Class<?>> immutable = new HashSet() {{
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

    /**
     * creates and returns a copy of object
     *
     * @param object 	object to clone
     * @return cloned object
     */
    public <T> T clone( T object ) {
        return cloneObject( object, new HashMap<>() );
    }

    private <T> T cloneObject( T object, Map<Object,Object> cloned ) {

        if( object == null ) return null;
        if( cloned.containsKey(object) ) return (T) cloned.get( object );

        Class<?> klass = object.getClass();

        if( immutable.contains(klass) ) {
            return object;
        } else if( Types.isArray(klass) ) {
            return cloneArray( object, cloned );
        }

        Object clone = Classes.createInstance( klass );
        cloned.put( object, clone );

        for( Field field : ClassReflector.getFields(klass) ) {

            int modifiers = field.getModifiers();

            if( Modifier.isStatic(modifiers) ) continue;
            if( Modifier.isTransient(modifiers) ) continue;

            boolean prevAccessible = field.isAccessible();
            try {
                field.setAccessible(true);
                Object val = field.get( object );
                Object clonedVal = cloneObject( val, cloned );
                field.set( clone, clonedVal );
            } catch ( IllegalArgumentException | IllegalAccessException e ) {
                throw new RuntimeException(e);
            } finally {
                field.setAccessible( prevAccessible );
            }

        }

        return (T) clone;

    }

    private <T> T cloneArray( T object, Map<Object,Object> cloned ) {

        Class<?> klass   = object.getClass();
        Class<?> generic = klass.getComponentType();

        int length = Array.getLength( object );

        Object target = Array.newInstance( generic, length );

        if( length > 0 ) {
            if( immutable.contains(generic) ) {
                System.arraycopy( object, 0, target, 0, length );
            } else {
                for( int i = 0; i < length; i++ ) {
                    Object e = Array.get( object, i );
                    Array.set( target, i, cloneObject(e,cloned) );
                }
            }
        }

        cloned.put( object, target );
        return (T) target;

    }

    public <T> T cloneSerializable( T obj ) throws UncheckedIOException {

        if( obj == null ) return null;

        ObjectOutputStream oos  = null;
        ObjectInputStream ois  = null;

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( bos );
            oos.writeObject( obj );
            oos.flush();

            ois = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );

            return (T) ois.readObject();

        } catch ( IOException | ClassNotFoundException e ) {
            throw new UncheckedIOException( e );
        } finally {
            try { ois.close(); } catch (Exception e) {}
            try { oos.close(); } catch (Exception e) {}
        }

    }

    private boolean isAnonymousParent( Field field ) {
        return "this$0".equals(field.getName());
    }

    /**
     * copy properties from source to target.
     *
     * @param source  source object
     * @param target  target object
     */
    public void copyProperties( Object source, Object target ) {

        if( source == null || target == null ) return;

        if ( Types.isArray(source) ) {
            if ( ! Types.isArray(target) )
                throw new IllegalArgumentException( String.format("can not copy array to non-array class(%s)", target.getClass()) );

            Class srcType = source.getClass().getComponentType();
            Class trgType = target.getClass().getComponentType();

            boolean isSameType = srcType == trgType;

            for ( int i = 0, iCnt=Array.getLength(source); i < iCnt; i++ ) {
                Object srcVal = Array.get( source, i );
                if( isSameType ) {
                    Array.set( target, i, srcVal );
                } else {
                    Object trgVal = Array.get( target, i );
                    if( trgVal == null ) {
                        if( Types.isArray(srcVal) ) {
                            trgVal = Array.newInstance( trgType.getComponentType(), Array.getLength(srcVal) );
                        } else {
                            trgVal = Classes.createInstance( trgType );
                        }
                    }
                    copyProperties( srcVal, trgVal );
                    Array.set( target, i, trgVal );
                }
            }
            return;
        }

        Object  castedSource = castedSource( source, target );
        boolean casted       = source != castedSource;

        Map<String,Field> srcFields = new HashMap<>();
        ClassReflector.getFields(source).forEach( field -> {
            srcFields.put( field.getName(), field );
        });

        for ( Field trgField : ClassReflector.getFields(target) ) {

            Field srcField = srcFields.get( trgField.getName() );

            if( srcField == null ) continue;;
            if( srcField == trgField && ClassReflector.isStatic(srcField) ) continue;

            Object val = ClassReflector.getValue( castedSource, casted ? trgField : srcField );
            ClassReflector.setValue( target, trgField, val );

        }

    }

    private Object castedSource( Object source, Object target ) {
		Class<?> sourceClass = source.getClass();
		Class<?> targetClass = target.getClass();
		if( Classes.isExtendedBy(targetClass,sourceClass) || Classes.isExtendedBy(sourceClass,targetClass) ) {
		    return source;
		} else {
			return Reflector.toBeanFrom( source, targetClass );
		}
    }

}
