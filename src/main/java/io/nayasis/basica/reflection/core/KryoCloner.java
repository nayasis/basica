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
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;
import de.javakaffee.kryoserializers.protobuf.ProtobufSerializer;
import org.objenesis.strategy.StdInstantiatorStrategy;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;

/**
 * Cloner based on Kryo
 *
 * @see <a href="https://github.com/EsotericSoftware/kryo">kryo</a>
 */
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
        try {
            return (T) decode( encode(source), source.getClass() );
        } catch ( Exception e ) {
            return Cloner.clone( source );
        }
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
            kryo.register( InvocationHandler.class, new JdkProxySerializer() );

            UnmodifiableCollectionsSerializer.registerSerializers( kryo );
            SynchronizedCollectionsSerializer.registerSerializers( kryo );

            // dexx
            try {
                de.javakaffee.kryoserializers.dexx.ListSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.dexx.MapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.dexx.SetSerializer.registerSerializers( kryo );
            } catch ( Throwable e ) {}

            // guava
            try {
                de.javakaffee.kryoserializers.guava.ImmutableListSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ImmutableSetSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ImmutableMapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ImmutableTableSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ReverseListSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.UnmodifiableNavigableSetSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ArrayListMultimapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.HashMultimapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.LinkedHashMultimapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.LinkedListMultimapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.TreeMultimapSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.ArrayTableSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.HashBasedTableSerializer.registerSerializers( kryo );
                de.javakaffee.kryoserializers.guava.TreeBasedTableSerializer.registerSerializers( kryo );
            } catch ( Throwable e ) {}

            return kryo;

        };

    }

    private Kryo createDefaultKryo() {
        return new Kryo() {
            @Override
            public Serializer<?> getDefaultSerializer( final Class klass ) {

                // cglib
                try {
                    if ( CGLibProxySerializer.canSerialize(klass) )
                        return getSerializer( CGLibProxySerializer.CGLibProxyMarker.class );
                } catch ( Throwable e ) {}

                // google protobuf
                try {
                    if ( GeneratedMessage.class.isAssignableFrom( klass ) )
                        return new ProtobufSerializer();
                } catch ( Throwable e ) {}

                return super.getDefaultSerializer( klass );

            }
        };
    }

}