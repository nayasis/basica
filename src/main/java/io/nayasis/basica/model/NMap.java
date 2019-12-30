package io.nayasis.basica.model;

import com.jayway.jsonpath.JsonPath;
import io.nayasis.basica.reflection.Reflector;

import java.util.LinkedHashMap;
import java.util.Map;

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
    public NMap clone() {
        return Reflector.clone( this );
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
            result.add( "key", key );
            Object val = get( key );
            if( showType ) {
                result.add( "type", val == null ? null : val.getClass().getTypeName() );
            }
            result.add( "val", val );
        }

        return result.toString( showHeader, true );

    }

    /**
     * Get value by json path
     *
     * @param jsonPath json path
     * @see <a href="https://github.com/jayway/JsonPath">json path example</a>
     * @return value(s) extracted by json path
     */
    public <T> T getByJsonPath( String jsonPath ) {
        Object val = null;
        if( containsKey( jsonPath ) ) {
            val = get( jsonPath );
        } else {
            try {
                val = JsonPath.read( this, jsonPath );
            } catch ( Exception e ) {}
        }
        return val == null ? null : (T) val;
    }

    /**
     * rebuild key to JsonPath. <br><br>
     *
     * Map can contains POJO and then JsonPath could not working on it.
     * it change all POJO value to Map.
     *
     * @return self instance
     */
    public NMap rebuildKeyToJsonPath() {
        return new JsonPathMapper().toJsonPath( this );
    }


}
