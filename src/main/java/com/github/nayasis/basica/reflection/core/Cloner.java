package com.github.nayasis.basica.reflection.core;

import com.github.nayasis.basica.base.Classes;
import com.github.nayasis.basica.base.Types;
import com.github.nayasis.basica.reflection.Reflector;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Slf4j
public class Cloner {

    /**
     * creates and returns a copy of object
     *
     * @param object 	object to clone
     * @param <T> This is the type parameter
     * @return cloned object
     */
    public <T> T clone( T object ) {
        return cloneObject( object, new HashMap<>() );
    }

    private <T> T cloneObject( T object, Map<Object,Object> cloned ) {

        if( object == null ) return null;
        if( cloned.containsKey(object) ) return (T) cloned.get( object );

        Class<?> klass = object.getClass();

        if( Types.isImmutable(klass) ) {
            return object;
        } else if( Types.isEnum(klass) ) {
            return object;
        } else if( Types.isArray(klass) ) {
            return cloneArray( object, cloned );
        }

        Object clone = Classes.createInstance( klass );
        cloned.put( object, clone );

        for( Field field : ClassReflector.getFields(klass) ) {

            int modifiers = field.getModifiers();

            if( Modifier.isStatic(modifiers) ) continue;

            Object val      = ClassReflector.getValue( object, field );
            Object cloneVal = cloneObject( val, cloned );
            ClassReflector.setValue( clone, field, cloneVal );

        }

        return (T) clone;

    }

    private <T> T cloneArray( T object, Map<Object,Object> cloned ) {

        Class<?> klass   = object.getClass();
        Class<?> generic = klass.getComponentType();

        int length = Array.getLength( object );

        Object target = Array.newInstance( generic, length );

        if( length > 0 ) {
            if( Types.isImmutable(generic) ) {
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

    /**
     * copy properties from source to target.
     *
     * @param source  source object
     * @param target  target object
     */
    public void copyProperties( Object source, Object target ) {

        if( source == null || target == null ) return;

        if ( Types.isArray(source) ) {
            copyArray(source, target);
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

    private void copyArray( Object source, Object target ) {

        if ( ! Types.isArray(target) )
            throw new IllegalArgumentException( String.format("can not copy array to non-array class(%s)", target.getClass()) );

        Class srcType = source.getClass().getComponentType();
        Class trgType = target.getClass().getComponentType();

        for (int i = 0, iCnt = Array.getLength(source); i < iCnt; i++ ) {

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
                    copyProperties( srcVal, trgVal );
                }

                Array.set( target, i, trgVal );

            }
        }
    }

    private Object castedSource( Object source, Object target ) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();
        if( Classes.hasExtend(targetClass,sourceClass) || Classes.hasExtend(sourceClass,targetClass) ) {
            return source;
        } else {
            return Reflector.toBeanFrom( source, targetClass );
        }
    }

}