package io.nayasis.basica.reflection.core;

import io.nayasis.basica.base.Classes;
import io.nayasis.basica.base.Types;
import io.nayasis.basica.reflection.Reflector;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class BeanCopier {

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
