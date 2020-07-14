package com.github.nayasis.basica.model;

import com.jayway.jsonpath.JsonPath;
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
     * Get value by json path
     *
     * @param jsonPath json path
     * @param <T> This is the type parameter
     * @see <a href="https://github.com/jayway/JsonPath">json path example</a>
     * @return value(s) extracted by json path
     */
    @SuppressWarnings("unchecked")
    public <T> T getByJsonPath( String jsonPath ) {
        Object val = null;
        if( containsKey( jsonPath ) ) {
            val = get( jsonPath );
        } else {
            try {
                val = JsonPath.read( this, jsonPath );
            } catch ( Exception ignored ) {}
        }
        return val == null ? null : (T) val;
    }

    /**
     * return true if this map contains a mapping for the specified json path.
     *
     * @param jsonPath  json path
     * @see <a href="https://github.com/jayway/JsonPath">json path example</a>
     * @return true if this map contains a maaping for the specified key.
     */
    public boolean containsJsonPath( String jsonPath ) {
        if( containsKey(jsonPath) ) return true;
        try {
            JsonPath.read( this, jsonPath );
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    /**
     * rebuild key for JsonPath. <br><br>
     *
     * This map could contain POJO so JsonPath could not working because JsonPath could work well only in entire Map structure.
     * it change all POJO value to Map.
     *
     * @return self instance
     */
    public NMap buildKeyToJsonPath() {
        return new JsonPathMapper().toJsonPath( this );
    }

}