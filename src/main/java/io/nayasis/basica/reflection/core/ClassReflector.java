package io.nayasis.basica.reflection.core;

import io.nayasis.basica.validation.Validator;
import io.nayasis.basica.base.Classes;
import io.nayasis.basica.base.Strings;
import io.nayasis.basica.cache.implement.LruCache;
import io.nayasis.basica.exception.unchecked.UncheckedIllegalAccessException;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Class reflector
 *
 * @author nayasis@gmail.com
 * @since 2017-03-24
 */
@UtilityClass
public class ClassReflector {

    private final LruCache<Class,Set<Field>>       CACHE_FIELD       = new LruCache<>(256 );
    private final LruCache<Class,Set<Method>>      CACHE_METHOD      = new LruCache<>(256 );
    private final LruCache<Class,Set<Constructor>> CACHE_CONSTRUCTOR = new LruCache<>(256 );

    /**
     * set value to target instance's field.
     *
     * @param target    target instance
     * @param field     target field
     * @param value     value to modify
     */
    public void setValue( Object target, Field field, Object value ) {
        execute( target, field, () -> {
            if( Modifier.isStatic(field.getModifiers()) ){
                field.set( null, value );
            } else {
                field.set( target, value );
            }
        });
    }

    /**
     * get value from target instance's field
     *
     * @param target    target instance
     * @param field     target field
     * @param <T>
     * @return  field's value
     */
    public <T> T getValue( Object target, Field field ) throws UncheckedIllegalAccessException {

        final Object[] val = new Object[1];

        execute( target, field, () -> {
            if( Modifier.isStatic(field.getModifiers()) ){
                val[0] = field.get( null );
            } else {
                val[0] = field.get( target );
            }
        });

        return val[0] == null ? null : (T) val[0];

    }

    /**
     * get value from target instance's field
     *
     * @param target    target instance
     * @param field     target field
     * @param <T>
     * @return  field's value
     */
    public <T> T getValue( Object target, String field ) throws UncheckedIllegalAccessException {

        Set<Field> fields = getFields( target, field );

        if( fields.isEmpty() ) {
            throw new UncheckedIllegalAccessException( Strings.format("There is no field({}) in class({})", field, target.getClass()) );
        } else if( fields.size() > 1 ) {
            List<String> fieldNames = fields.stream().map( f -> f.getName() ).collect( Collectors.toList() );
            throw new UncheckedIllegalAccessException( Strings.format("Specify field name more precisely. (searchName: {}, field: {}, class: {})", field, fieldNames, target.getClass()) );
        }

        Field targetField = fields.iterator().next();

        return getValue( target, targetField );

    }

    private void execute( Object target, Field field, Runner runner ) throws UncheckedIllegalAccessException {

        if( target == null || field == null ) return;

        boolean inaccessible = ! field.isAccessible();

        if( inaccessible )
            field.setAccessible( true );

        try {
            runner.run();
        } catch( IllegalAccessException e ) {
            throw new UncheckedIllegalAccessException( e );
        } finally {
            if( inaccessible ) {
                field.setAccessible( false );
            }
        }

    }

    private interface Runner {
        void run() throws IllegalAccessException;
    }

    /**
     * Get fields in object
     *
     * @param object    object to extract fields
     * @return fields
     */
    public Set<Field> getFields( Object object ) {
        if( object == null ) return new HashSet<>();
        return getFields( object.getClass() );
    }

    /**
     * check if field is static or not.
     *
     * @param field field
     * @return true if field is static.
     */
    public boolean isStatic( Field field ) {
        return Modifier.isStatic( field.getModifiers() );
    }

    /**
     * Get fields in Class.
     *
     * @param klass    class to extract fields
     * @return fields
     */
    public Set<Field> getFields( Class klass ) {

        if( klass == null ) return new HashSet<>();

        if( CACHE_FIELD.contains(klass) )
            return CACHE_FIELD.get( klass );

        Set<Field> fields = new HashSet<>();

        addAll( fields, klass.getDeclaredFields() );

        Class<?> parent = klass;
        while ( (parent = parent.getSuperclass()) != Object.class && parent != null ) {
            addAll( fields, parent.getDeclaredFields() );
        }

        CACHE_FIELD.putIfAbsent( klass, fields );

        return fields;

    }

    private void addAll( Set<Field> set, Field[] fields ) {
        for ( Field field : fields ) {
            set.add(field);
        }
    }

    /**
     * Get fields in object.
     *
     * @param target    object to extract fields
     * @param regex     regexp to filter fields by name.
     * @return
     */
    public Set<Field> getFields( Object target, String regex ) {
        if( target == null ) return new HashSet<>();
        return getFields( target.getClass(), regex );
    }

    /**
     * Get fields in Class.
     *
     * @param klass class to extract fields
     * @param regex regexp to filter fields by name.
     * @return
     */
    public Set<Field> getFields( Class klass, String regex ) {
        if( Strings.isEmpty(regex) ) return new HashSet<>();
        Set<Field> result = new HashSet<>();
        for( Field field : getFields(klass) ) {
            if( field.getName().matches(regex) ) {
                result.add( field );
            }
        }
        return result;
    }

    /**
     * get methods in object
     *
     * @param object    object to extract methods.
     * @return methods
     */
    public Set<Method> getMethods( Object object ) {
        if( object == null ) return new HashSet<>();
        return getMethods( object.getClass() );
    }

    /**
     * get methods in Class
     *
     * @param klass     class to extract methods
     * @return methods
     */
    public Set<Method> getMethods( Class klass ) {

        if( klass == null ) return new HashSet<>();

        if( CACHE_METHOD.contains(klass) )
            return CACHE_METHOD.get( klass );

        Set<Method> methods = new HashSet<>();

        Classes.findParents(klass).forEach( parent -> {
            methods.addAll( getMethods(parent) );
        });

        for( Method method : klass.getDeclaredMethods() ) {
            methods.add( method );
        }

        CACHE_METHOD.putIfAbsent( klass, methods );

        return methods;

    }

    /**
     * get methods in object
     *
     * @param object    object to extract methods.
     * @param regex     regexp to filter methods by name.
     * @return methods
     */
    public Set<Method> getMethods( Object object, String regex ) {
        if( object == null ) return new HashSet<>();
        return getMethods( object.getClass(), regex );
    }

    /**
     * get methods in Class
     *
     * @param klass     class to extract methods
     * @param regex     regexp to filter methods by name.
     * @return methods
     */
    public Set<Method> getMethods( Class klass, String regex ) {
        if( Validator.isEmpty(regex) ) return new HashSet<>();
        Set<Method> result = new LinkedHashSet<>();
        for( Method method : getMethods(klass) ) {
            if( method.getName().matches(regex) ) {
                result.add( method );
            }
        }
        return result;
    }

    /**
     * get constructors in object.
     *
     * @param object    object to extract constructors.
     * @return constructors
     */
    public Set<Constructor> getConstructors( Object object ) {
        if( object == null ) return new HashSet<>();
        return getConstructors( object.getClass() );
    }

    /**
     * get constructors
     *
     * @param klass class to extract constructors.
     * @return constructors.
     */
    public Set<Constructor> getConstructors( Class klass ) {

        if( klass == null ) return new HashSet<>();

        if( CACHE_CONSTRUCTOR.contains(klass) )
            return CACHE_CONSTRUCTOR.get( klass );

        Set<Constructor> constructors = new HashSet<>();
        for( Constructor constructor : klass.getDeclaredConstructors() ) {
            constructors.add( constructor );
        }
        CACHE_CONSTRUCTOR.putIfAbsent( klass, constructors );
        return constructors;

    }

}