package io.nayasis.common.clone;

import io.nayasis.common.base.Classes;
import io.nayasis.common.clone.checker.IgnorableChecker;
import io.nayasis.common.clone.checker.ImmutableChecker;
import io.nayasis.common.clone.fastcloner.FastCloners;
import io.nayasis.common.clone.fastcloner.interfaces.DeepCloner;
import io.nayasis.common.exception.unchecked.CloningException;
import io.nayasis.common.exception.unchecked.UncheckedIOException;
import io.nayasis.common.reflection.core.CoreReflector;
import sun.plugin.dom.exception.InvalidAccessException;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bean Cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class Cloner {

    private ImmutableChecker immutableChecker = new   ImmutableChecker();
    private IgnorableChecker ignorableChecker = new   IgnorableChecker();
    private FastCloners      fastCloners      = new   FastCloners();
    private CoreReflector    reflector        = null;

    public Cloner(){
        this.reflector = new CoreReflector();
    }

    public Cloner( CoreReflector reflector ) {
        this.reflector = reflector;
    }

    public <T> T deepClone( T object ) throws CloningException {
        if( object == null ) return null;
        if( object instanceof Serializable ) {
            Object cloned = serializableClone(object);
            return cloned == null ? null : (T) cloned;
        } else {
            Map valueReference = new IdentityHashMap<>();
            try {
                return cloneObject( object, valueReference );
            } catch( InvalidAccessException e ) {
                throw new CloningException( "error on cloning object : {}", object, e );
            }
        }
    }

    private Object serializableClone( Object obj ) {

        ObjectOutputStream oos  = null;
        ObjectInputStream  ois   = null;
        Object             copy;

        try {

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream( bos );
            oos.writeObject( obj );
            oos.flush();

            ois = new ObjectInputStream( new ByteArrayInputStream( bos.toByteArray() ) );

            copy = ois.readObject();

        } catch ( IOException | ClassNotFoundException e ) {
            throw new UncheckedIOException( e );
        } finally {
            try { ois.close();  } catch (Exception e) {}
            try { oos.close(); } catch (Exception e) {}
        }

        return copy;

    }


    public <T> T shallowClone( T object ) throws CloningException {
        if( object == null ) return null;
        try {
            return cloneObject( object, null );
        } catch( InvalidAccessException e ) {
            throw new CloningException( "error on cloning object : {}", object, e );
        }
    }

    public <T> T cloneObject( T object, Map valueReference ) throws InvalidAccessException {

        if( object == null || object instanceof Cloner ) return null;
        if( object instanceof Enum ) return object;
        if( ignorableChecker.isIgnorable(object) ) return null;
        if( immutableChecker.isImmutable(object) ) return object;

        if( valueReference != null && valueReference.containsKey(object) ) {
            return (T) valueReference.get( object );
        }

        DeepCloner cloner = fastCloners.getCloner( object );
        if( cloner != null ) {
            Object copiedValue = cloner.clone( object, this, valueReference );
            if( valueReference != null ) {
                valueReference.put( object, copiedValue );
            }
            return (T) copiedValue;
        }

        return cloneBean( object, valueReference );

    }

    private <T> T cloneBean( T bean, Map valueReference ) throws InvalidAccessException {

        if( bean == null ) return null;

        Class<?> klass = bean.getClass();

        T newBean = (T) Classes.createInstance( klass );

        if( valueReference != null ) {
            valueReference.put( bean, newBean );
        }

        for( Field field : reflector.getFields(klass) ) {
            if( isExclusive(field) ) continue;
            if( ignorableChecker.isIgnorable(field) ) continue;

            Object originalValue = reflector.getFieldValue( bean, field );

            if( field.isSynthetic() || immutableChecker.isImmutable(field) || immutableChecker.isImmutable(originalValue) ) {
                reflector.setField( newBean, field, originalValue );
            } else {
                if( valueReference == null ) {
                    reflector.setField( newBean, field, originalValue );
                } else {
                    Object copiedValue = cloneObject( originalValue, valueReference );
                    reflector.setField( newBean, field, copiedValue );
                }
            }
        }

        return newBean;

    }

    public <T, E extends T> void copy( T source, E target ) throws IllegalArgumentException {

        if( source == null ) throw new IllegalArgumentException( "source can not be null" );
        if( target == null ) throw new IllegalArgumentException( "target can not be null" );

        Class sourceClass = source.getClass();
        Class targetClass = target.getClass();

        if( sourceClass.isArray() ) {
            if( ! targetClass.isArray() ) {
                throw new IllegalArgumentException( String.format("can not copy from Array(%s) to Non-Array(%s)", sourceClass, targetClass) );
            }
            int length = Math.min( Array.getLength(source), Array.getLength(target) );
            for( int i = 0; i < length; i++ ) {
                Object val = Array.get( source, i );
                Array.set( target, i, val );
            }
            return;
        }

        Set<Field> sourceFields = reflector.getFields( sourceClass );
        Set<Field> targetFields = reflector.getFields( targetClass );

        for( Field field : sourceFields ) {
            if( isExclusive(field) ) continue;
            if( ! targetFields.contains(field) ) continue;
            Object value = reflector.getFieldValue( source, field );
            reflector.setField( target, field, value );
        }

    }

    private boolean isAnonymousClass( final Field field ) {
        return "this$0".equals(field.getName());
    }

    public ImmutableChecker getImmutableChecker() {
        return immutableChecker;
    }

    private boolean isExclusive( Field field ) {
        if( Modifier.isStatic(field.getModifiers()) ) return true;
        if( isAnonymousClass(field) ) return true;
        if( field.isEnumConstant() ) return true;
        return false;
    }

}
