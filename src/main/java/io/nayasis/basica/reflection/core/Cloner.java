package io.nayasis.basica.reflection.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryo.serializers.EnumNameSerializer;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.dexx.ListSerializer;
import de.javakaffee.kryoserializers.dexx.MapSerializer;
import de.javakaffee.kryoserializers.dexx.SetSerializer;
import de.javakaffee.kryoserializers.guava.ArrayListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ArrayTableSerializer;
import de.javakaffee.kryoserializers.guava.HashBasedTableSerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableTableSerializer;
import de.javakaffee.kryoserializers.guava.LinkedHashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.LinkedListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ReverseListSerializer;
import de.javakaffee.kryoserializers.guava.TreeBasedTableSerializer;
import de.javakaffee.kryoserializers.guava.TreeMultimapSerializer;
import de.javakaffee.kryoserializers.guava.UnmodifiableNavigableSetSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalDateTimeSerializer;
import de.javakaffee.kryoserializers.jodatime.JodaLocalTimeSerializer;
import io.nayasis.basica.base.Classes;
import io.nayasis.basica.base.Types;
import io.nayasis.basica.reflection.Reflector;
import jxl.write.DateTime;
import org.objenesis.strategy.StdInstantiatorStrategy;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class Cloner {

    private KryoPool pool = null;

    public Cloner( KryoFactory factory ) {
        setPool( factory );
    }

    public Cloner() {
        setPool( getDefaultFactory() );
    }

    public byte[] encode( Object source ) {
        if( source == null ) return null;
        try (
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Output output = new Output( bos );
        ) {
            pool.run( (KryoCallback) kryo -> {
                kryo.writeObject( output, source );
                return null;
            });
            return output.toBytes();
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    public <T> T decode( byte[] encoded, Class<T> type ) {
        if( encoded == null ) return null;
        try (
            ByteArrayInputStream bis = new ByteArrayInputStream( encoded );
            Input input = new Input( bis );
        ) {
            return pool.run( kryo -> kryo.readObject(input, type) );
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    public <T> T cloneDeep( T source ) {
        if( source == null ) return null;
        return (T) decode( encode(source), source.getClass() );
    }

    public <T> T cloneShallow( T source ) {
        if( source == null ) return null;
        return pool.run( kryo -> kryo.copyShallow(source) );
    }

    public String encodeToString( Object source ) {
        if( source == null ) return null;
        return DatatypeConverter.printBase64Binary( encode(source) );
    }

    public <T> T decodeFromString( String encoded, Class<T> type ) {
        if( encoded == null ) return null;
        return decode( DatatypeConverter.parseBase64Binary(encoded), type );
    }

    private Cloner setPool( KryoFactory factory ) {
        pool = new KryoPool.Builder(factory).softReferences().build();
        return this;
    }

    private KryoFactory getDefaultFactory() {
        return () -> {

            Kryo kryo = new Kryo();

            kryo.setInstantiatorStrategy( new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()) );
            kryo.addDefaultSerializer( Enum.class, EnumNameSerializer.class );

            kryo.register( Arrays.asList("").getClass(), new ArraysAsListSerializer() );
            kryo.register( Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer() );
            kryo.register( Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer() );
            kryo.register( Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer() );
            kryo.register( Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer() );
            kryo.register( Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer() );
            kryo.register( Collections.singletonMap("","").getClass(), new CollectionsSingletonMapSerializer() );
            kryo.register( GregorianCalendar.class, new GregorianCalendarSerializer() );
            kryo.register( InvocationHandler.class, new JdkProxySerializer() );
            UnmodifiableCollectionsSerializer.registerSerializers( kryo );
            SynchronizedCollectionsSerializer.registerSerializers( kryo );

            // dexx
            ListSerializer.registerSerializers( kryo );
            MapSerializer.registerSerializers( kryo );
            SetSerializer.registerSerializers( kryo );

            // joda DateTime, LocalDate, LocalDateTime and LocalTime
            kryo.register( DateTime.class, new JodaDateTimeSerializer() );
            kryo.register( LocalDate.class, new JodaLocalDateSerializer() );
            kryo.register( LocalDateTime.class, new JodaLocalDateTimeSerializer() );
            kryo.register( LocalDateTime.class, new JodaLocalTimeSerializer() );

            // guava
            ImmutableListSerializer.registerSerializers( kryo );
            ImmutableSetSerializer.registerSerializers( kryo );
            ImmutableMapSerializer.registerSerializers( kryo );
            ImmutableMultimapSerializer.registerSerializers( kryo );
            ImmutableTableSerializer.registerSerializers( kryo );
            ReverseListSerializer.registerSerializers( kryo );
            UnmodifiableNavigableSetSerializer.registerSerializers( kryo );
            ArrayListMultimapSerializer.registerSerializers( kryo );
            HashMultimapSerializer.registerSerializers( kryo );
            LinkedHashMultimapSerializer.registerSerializers( kryo );
            LinkedListMultimapSerializer.registerSerializers( kryo );
            TreeMultimapSerializer.registerSerializers( kryo );
            ArrayTableSerializer.registerSerializers( kryo );
            HashBasedTableSerializer.registerSerializers( kryo );
            TreeBasedTableSerializer.registerSerializers( kryo );

            return kryo;

        };

    }

    /**
     * copy properties from source to target.
     *
     * @param source  source object
     * @param target  target object
     */
    public void copy( Object source, Object target ) {

        if( source == null || target == null ) return;

        if ( Types.isArray(source) ) {
            copyArray(source, target);
            return;
        }

        Object  castedSource = castedSource( source, target );
        boolean casted       = source != castedSource;

        Map<String, Field> srcFields = new HashMap<>();
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

    private void copyArray( Object source, Object target ) {

        if ( ! Types.isArray(target) )
            throw new IllegalArgumentException( String.format("can not copy array to non-array class(%s)", target.getClass()) );

        Class srcType = source.getClass().getComponentType();
        Class trgType = target.getClass().getComponentType();

        for ( int i = 0, iCnt = Array.getLength(source); i < iCnt; i++ ) {

            Object srcVal = Array.get( source, i );

            if( srcVal == null ) {
                continue;
            } else if( srcType == trgType ) {
                Array.set( target, i, srcVal );
            } else {

                Object trgVal;

                if( Types.isImmutable(srcVal) ) {
                    trgVal = Types.castPrimitive( srcVal, trgType );
                } else {
                    trgVal = Array.get( target, i );
                    if( trgVal == null ) {
                        if( Types.isArray(srcVal) ) {
                            trgVal = Array.newInstance( trgType.getComponentType(), Array.getLength(srcVal) );
                        } else {
                            trgVal = Classes.createInstance( trgType );
                        }
                    }
                    copy( srcVal, trgVal );
                }

                Array.set( target, i, trgVal );

            }
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