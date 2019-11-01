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

    private JsonConverter           jsonCloner = new JsonConverter( new NObjectMapper(true,true,true) );
    private com.rits.cloning.Cloner cloner     = new com.rits.cloning.Cloner();

    private final Set<Class<?>> immutable = new HashSet<Class<?>>();

    private final ConcurrentHashMap<Class<?>, List<Field>> fieldsCache = new ConcurrentHashMap<Class<?>, List<Field>>();

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
        add( LocalDate.class );
        add( LocalDateTime.class );
        add( URI.class );
        add( URL.class );
        add( UUID.class );
        add( Pattern.class );
    }};

    /**
     * creates and returns a copy of object
     *
     * @param object 	object to clone
     * @return cloned object
     */
    public <T> T clone( T object ) {

        if( object == null ) return null;

        if( Types.isArray(object) ) {
            return cloneArray( object );
        } else if( Types.isCollection(object) ) {
            return cloneCollection( object );
        }

        if( immutable.contains(object.getClass()) ) {
            return object;
        }

        if( object instanceof Serializable ) {
            try {
                return cloneSerializable( object );
            } catch ( Throwable e ) {}
        }

        ObjectMapper mapper = jsonCloner.getObjectMapper();

        try {
            String json = mapper.writeValueAsString( object );
            return (T) mapper.readValue( json, object.getClass() );
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

    protected void registerKnownJdkImmutableClasses() {

        immutable.add( String.class );
        immutable.add( Integer.class );
        immutable.add( Long.class );
        immutable.add( Boolean.class );
        immutable.add( Class.class );
        immutable.add( Float.class );
        immutable.add( Double.class );
        immutable.add( Character.class );
        immutable.add( Byte.class );
        immutable.add( Short.class );
        immutable.add( Void.class );

        immutable.add( BigDecimal.class );
        immutable.add( BigInteger.class );
        immutable.add( URI.class );
        immutable.add( URL.class );
        immutable.add( UUID.class );
        immutable.add( Pattern.class );

    }

    /**
     * copies all properties from src to dest. Src and dest can be of different class, provided they contain same field names/types
     *
     * @param src  the source object
     * @param dest the destination object which must contain as minimum all the fields of src
     */
    public <T, E extends T> void copyPropertiesOfInheritedClass(final T src, final E dest) {
        if (src == null) throw new IllegalArgumentException("src can't be null");
        if (dest == null) throw new IllegalArgumentException("dest can't be null");
        final Class<? extends Object> srcClz = src.getClass();
        final Class<? extends Object> destClz = dest.getClass();
        if (srcClz.isArray()) {
            if (!destClz.isArray())
                throw new IllegalArgumentException("can't copy from array to non-array class " + destClz);
            final int length = Array.getLength(src);
            for (int i = 0; i < length; i++) {
                final Object v = Array.get(src, i);
                Array.set(dest, i, v);
            }
            return;
        }
        final List<Field> fields = allFields(srcClz);
        final List<Field> destFields = allFields(dest.getClass());
        for (final Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    final Object fieldObject = field.get(src);
                    field.setAccessible(true);
                    if (destFields.contains(field)) {
                        field.set(dest, fieldObject);
                    }
                } catch (final IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } catch (final IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * reflection utils, override this to choose which fields to clone
     */
    protected List<Field> allFields(final Class<?> c) {
        List<Field> l = fieldsCache.get(c);
        if (l == null) {
            l = new LinkedList<Field>();
            final Field[] fields = c.getDeclaredFields();
            addAll(l, fields);
            Class<?> sc = c;
            while ((sc = sc.getSuperclass()) != Object.class && sc != null) {
                addAll(l, sc.getDeclaredFields());
            }
            fieldsCache.putIfAbsent(c, l);
        }
        return l;
    }

    /**
     * reflection utils
     */
    private void addAll(final List<Field> l, final Field[] fields) {
        for (final Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            l.add(field);
        }
    }

}
