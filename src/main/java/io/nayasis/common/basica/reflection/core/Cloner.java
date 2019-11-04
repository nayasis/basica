package io.nayasis.common.basica.reflection.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nayasis.common.basica.base.Classes;
import io.nayasis.common.basica.base.Types;
import io.nayasis.common.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.common.basica.reflection.helper.mapper.NObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Slf4j
public class Cloner {

    private JsonConverter jsonCloner = new JsonConverter( new NObjectMapper(true,true,true) );

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

    private final ConcurrentHashMap<Class<?>,List<Field>> fieldsCache = new ConcurrentHashMap<Class<?>, List<Field>>();

    /**
     * creates and returns a copy of object
     *
     * @param object 	object to clone
     * @return cloned object
     */
    public <T> T clone( T object ) {

        if( object == null ) return null;

        Class<?> klass = object.getClass();

        if( immutable.contains( klass ) ) {
            return object;
        } else if( Types.isArray(klass) ) {
            return cloneArray( object );
        } else if( Types.isCollection(klass) ) {
            return cloneCollection( object );
        }

        if( object instanceof Serializable ) {
            try {
                return cloneSerializable( object );
            } catch ( Throwable e ) {}
        }

        ObjectMapper mapper = jsonCloner.getObjectMapper();
        try {
            String json = mapper.writeValueAsString( object );
            return (T) mapper.readValue( json, klass );
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    private <T> T cloneCollection( T object ) {

        Collection source = (Collection) object;
        Collection target = (Collection) Classes.createInstance( object.getClass() );

        source.forEach( e -> {
            target.add( clone(e) );
        });

        return (T) target;

    }

    private <T> T cloneArray( T object ) {

        Class<?> klass   = object.getClass();
        Class<?> generic = klass.getComponentType();

        int length = Array.getLength( object );

        T target = (T) Array.newInstance( generic, length );

        if( length == 0 ) return target;

        if( generic.isPrimitive() || immutable.contains(generic) ) {
            System.arraycopy( object, 0, target, 0, length );
        } else {
            for( int i = 0; i < length; i++ ) {
                Object e = Array.get( object, i );
                Array.set( target, i, clone(e) );
            }
        }

        return target;

    }

    private <T> T cloneSerializable( T obj ) throws UncheckedIOException {

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

    /**
     * copies all properties from source to target. source and target can be of different class, provided they contain same field names/types
     *
     * @param source  the source object
     * @param target  the destination object which must contain as minimum all the fields of source
     */
    public <T, E extends T> void copyProperties( final T source, final E target ) {

        if (source == null) throw new IllegalArgumentException( "source can't be null" );
        if (target == null) throw new IllegalArgumentException( "target can't be null" );

        final Class<? extends Object> klassSrc = source.getClass();
        final Class<? extends Object> klassTrg = target.getClass();

        if ( klassSrc.isArray() ) {
            if ( ! klassTrg.isArray() )
                throw new IllegalArgumentException("can't copy from array to non-array class " + klassTrg);
            final int length = Array.getLength( source );
            for (int i = 0; i < length; i++) {
                final Object v = Array.get( source, i );
                Array.set( target, i, v );
            }
            return;
        }

        final List<Field> fields     = allFields(klassSrc);
        final List<Field> destFields = allFields(target.getClass());

        for ( final Field field : fields ) {
            if( ! Modifier.isStatic(field.getModifiers())) {
                try {
                    final Object fieldObject = field.get(source);
                    field.setAccessible(true);
                    if (destFields.contains(field)) {
                        field.set(target, fieldObject);
                    }
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    protected List<Field> allFields( Class<?> klass ) {
        List<Field> fields = fieldsCache.get(klass);
        if( fields == null ) {
            fields = new LinkedList<>();
            addAll( fields, klass.getDeclaredFields() );
            Class<?> parent = klass;
            while ( (parent = parent.getSuperclass()) != Object.class && parent != null ) {
                addAll( fields, parent.getDeclaredFields() );
            }
            fieldsCache.putIfAbsent( klass, fields );
        }
        return fields;
    }

    private void addAll( List<Field> list, Field[] fields ) {
        for ( Field field : fields ) {
            if ( !field.isAccessible() ) {
                field.setAccessible(true);
            }
            list.add(field);
        }
    }

}
