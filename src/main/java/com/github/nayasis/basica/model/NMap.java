package com.github.nayasis.basica.model;

import com.github.nayasis.basica.expression.Expression;
import com.github.nayasis.basica.reflection.Reflector;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class NMap<K,V> extends LinkedHashMap<K,V> {

    private static final long serialVersionUID = 4923230488045574545L;

    /**
     * default constructor
     */
    public NMap() {
        super();
    }

    /**
     * constructor
     *
     * @param value	initial value
     */
    @SuppressWarnings("unchecked")
    public NMap( Map value ) {
        super( value );
    }

    /**
     * Constructor
     *
     * @param value if value is String (or StringBuffer or StringBuilder), init map with json parser.
     *              if value is Entity, init map with Bean Parser.
     */
    public NMap( Object value ) {
        bind( value );
    }

    /**
     * bind value from Bean or Map or JSON text
     * @param beanOrMapOrJson	bind value (Bean or Map or JSON text)
     * @return self instance
     */
    @SuppressWarnings("unchecked")
    public NMap bind( Object beanOrMapOrJson ) {
        super.putAll( Reflector.toMapFrom( beanOrMapOrJson ) );
        return this;
    }

    /**
     * Convert data to Json format
     * @param prettyPrint pretty print Y/N
     * @return Json string
     */
    public String toJson( boolean prettyPrint ) {
        return Reflector.toJson( this, prettyPrint );
    }

    /**
     * Convert data to Json format
     *
     * @return Json string
     */
    public String toJson() {
        return toJson( false );
    }

    /**
     * convert data to specific Bean
     *
     * @param klass	Class type to convert
     * @param <T> expected class of return value
     * @return converted bean
     */
    public <T> T toBean( Class<T> klass ) {
        return Reflector.toBeanFrom( this, klass );
    }

    @Override
    public NMap<K,V> clone() {
        return (NMap) super.clone();
    }

    /**
     * get debug string contains key's class type and value
     *
     * @param showHeader	if true, show header
     * @param showType		if true, show key's class type
     * @return debug string
     */
    public String toString( boolean showHeader, boolean showType ) {

        NList result = new NList();

        for( Object key : keySet() ) {
            result.addData( "key", key );
            Object val = get( key );
            if( showType ) {
                result.addData( "type", val == null ? null : val.getClass().getTypeName() );
            }
            result.addData( "val", val );
        }

        return result.toString( showHeader, true );

    }

    /**
     * check if this map could be tested by MVEL.
     * @param expression MVEL test expression
     * @return true if MVEL expression executed successfully.
     */
    public boolean containsKey( Expression expression ) {
        if( expression == null ) return false;
        try {
            expression.run( this );
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    /**
     * get value specified by MVEL
     *
     * @param expression    MVEL expression
     * @return specified value or null
     */
    public V get( Expression expression ) {
        if( expression == null ) return null;
        try {
            return expression.run( this );
        } catch ( Exception e ) {
            return null;
        }
    }

    /**
     * get value specified by MVEL
     *
     * @param expression    MVEL test expression
     * @param defaultValue  substitutive value when return value is null.
     * @return specified value or default value.
     */
    public V getOrDefault( Expression expression, V defaultValue ) {
        V val = get( expression );
        return val == null ? defaultValue : val;
    }

    /**
     * check if this map could be tested by MVEL.
     * @param expression MVEL test expression
     * @return true if MVEL expression executed successfully.
     */
    public boolean containsByPath( String expression ) {
        return containsKey( Expression.of(expression) );
    }

    /**
     * get value specified by MVEL
     *
     * @param expression    MVEL expression
     * @return specified value or null
     */
    public V getByPath( String expression ) {
        return get( Expression.of(expression) );
    }

    /**
     * get value specified by MVEL
     *
     * @param expression    MVEL test expression
     * @param defaultValue  substitutive value when return value is null.
     * @return specified value or default value.
     */
    public V getOrDefaultByPath( String expression, V defaultValue ) {
        return getOrDefault( Expression.of(expression), defaultValue );
    }

}