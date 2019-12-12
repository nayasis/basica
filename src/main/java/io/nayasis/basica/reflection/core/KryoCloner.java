package io.nayasis.basica.reflection.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.protobuf.GeneratedMessage;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;
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
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import jxl.write.DateTime;
import org.objenesis.strategy.StdInstantiatorStrategy;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationHandler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;

public class KryoCloner {

    private KryoPool pool = null;

    public KryoCloner( KryoFactory factory ) {
        setPool( factory );
    }

    public KryoCloner() {
        setPool( createDefaultFactory() );
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

    private KryoCloner setPool( KryoFactory factory ) {
        pool = new KryoPool.Builder(factory).softReferences().build();
        return this;
    }

    /**
     * @see <a href="https://github.com/magro/kryo-serializers">kryo-serializers</a>
     * @return
     */
    private KryoFactory createDefaultFactory() {
        return () -> {

            Kryo kryo = createDefaultKryo();

            kryo.setInstantiatorStrategy( new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()) );

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
            try {
                ListSerializer.registerSerializers( kryo );
                MapSerializer.registerSerializers( kryo );
                SetSerializer.registerSerializers( kryo );
            } catch ( Throwable e ) {}

            // joda DateTime, LocalDate, LocalDateTime and LocalTime
            try {
                kryo.register( DateTime.class, new JodaDateTimeSerializer() );
                kryo.register( LocalDate.class, new JodaLocalDateSerializer() );
                kryo.register( LocalDateTime.class, new JodaLocalDateTimeSerializer() );
                kryo.register( LocalDateTime.class, new JodaLocalTimeSerializer() );
            } catch ( Throwable e ) {}

            // guava
            try {
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
            } catch ( Throwable e ) {}

            return kryo;

        };

    }

    private Kryo createDefaultKryo() {
        return new KryoReflectionFactorySupport() {
            @Override
            public Serializer<?> getDefaultSerializer( final Class klass ) {

                if ( CGLibProxySerializer.canSerialize(klass) )
                    return getSerializer( CGLibProxySerializer.CGLibProxyMarker.class );
                if ( GeneratedMessage.class.isAssignableFrom( klass ) )
                    return new ProtobufSerializer();

                return super.getDefaultSerializer( klass );

            }
        };
    }

}