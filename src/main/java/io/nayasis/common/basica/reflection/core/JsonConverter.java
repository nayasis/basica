package io.nayasis.common.basica.reflection.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.nayasis.common.basica.base.Types;
import io.nayasis.common.basica.validation.Validator;
import io.nayasis.common.basica.exception.unchecked.JsonMappingException;

import java.io.IOException;
import java.util.*;

/**
 * Json Converter
 *
 * - powered by Jackson
 *
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class JsonConverter {

    private ObjectMapper objectMapper;
    private JsonInclude.Value DEFAULT_INCLUSION = JsonInclude.Value.empty();

    public JsonConverter( ObjectMapper mapper ) {
        this.objectMapper = mapper;
    }

    /**
     * get ObjectMapper
     *
     * @return ObjectMapper
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Get json text
     *
     * @param fromBean		instance to convert as json data
     * @param prettyPrint	whether or not to make json text pretty
     * @param view	        json view class
     * @return json text
     */
    public String toJson( Object fromBean, boolean prettyPrint, Class view ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {

        if( fromBean == null ) return null;

        ObjectWriter writer = prettyPrint ? objectMapper.writerWithDefaultPrettyPrinter() : objectMapper.writer();

        if( view != null )
            writer = writer.withView( view );

        try {
            return writer.writeValueAsString( fromBean );
        } catch( IOException e ) {
            throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e );
        }
    }

    /**
     * get json text
     *
     * @param fromBean		instance to convert as json data
     * @param prettyPrint	whether or not to make json text pretty
     * @return json text
     */
    public String toJson( Object fromBean, boolean prettyPrint ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {
        return toJson( fromBean, prettyPrint, null );
    }

    /**
     * Get json text
     *
     * @param fromBean		instance to convert as json data
     * @return json text
     */
    public String toJson( Object fromBean ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {
        return toJson( fromBean, false );
    }

    /**
     * Get json text
     *
     * @param fromBean		instance to convert as json data
     * @param view	        json view class
     * @return json text
     */
    public String toJson( Object fromBean, Class view ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {
        return toJson( fromBean, false, view );
    }

    /**
     * Get map with flatten key
     *
     * <pre>
     * json text or map like this
     *
     * {
     *   "name" : {
     *     "item" : [
     *     	  { "key" : "A", "value" : 1 }
     *     	]
     *   }
     * }
     *
     * will be converted as below.
     *
     * { "name.item[0].key" : "A", "name.item[0].value" : 1 }
     * </pre>
     *
     * @param object	json string, Map or bean
     * @return map with flattern key
     */
    public Map<String, Object> toFlattenMap( Object object ) {
        Map<String, Object> map = new HashMap<>();
        if( Validator.isNull(object) ) return map;
        makeKeyFlatten( "", toMapFrom( object ), map );
        return map;
    }

    private void makeKeyFlatten( String currentPath, Object json, Map result ) {
        if( json instanceof Map ) {
            Map<String, Object> map = (Map) json;
            String prefix = Validator.isEmpty( currentPath ) ? "" : currentPath + ".";
            for( String key : map.keySet() ) {
                makeKeyFlatten( prefix + key, map.get( key ), result );
            }
        } else if( json instanceof List ) {
            List list = (List) json;
            for( int i = 0, iCnt = list.size(); i < iCnt; i++ ) {
                makeKeyFlatten( String.format( "%s[%d]", currentPath, i ), list.get( i ), result );
            }
        } else {
            result.put( currentPath, json );
        }
    }

    /**
     * Get map with unflatten key
     *
     * <pre>
     * json text or map like this
     *
     * { "name.item[0].key" : "A", "name.item[0].value" : 1 }
     *
     * will be converted as below.
     *
     * {
     *   "name" : {
     *     "item" : [
     *     	  { "key" : "A", "value" : 1 }
     *     	]
     *   }
     * }
     * </pre>
     *
     * @param object	json string, Map or bean
     * @return map with flattern key
     */
    public Map<String, Object> toUnflattenMap( Object object ) {

        Map<String, Object> map = new HashMap<>();

        if( Validator.isNull(object) ) return map;

        Map<String, Object> objectMap = toMapFrom( object );

        for( String key : objectMap.keySet() ) {
            makeKeyUnflatten( key, objectMap.get( key ), map );
        }

        return map;

    }

    private void makeKeyUnflatten( String jsonPath, Object value, Map result ) {

        String path  = jsonPath.replaceFirst( "\\[.*\\]", "" ).replaceFirst( "\\..*?$", "" );
        String index = jsonPath.replaceFirst(  "^(" + path + ")\\[(.*?)\\](.*?)$", "$2" );

        if( index.equals( jsonPath ) ) index = "";

        boolean isArray = ! index.isEmpty();

        String currentPath = String.format( "%s%s", path, isArray ? String.format("[%s]", index) : "" );

        boolean isKey = currentPath.equals( jsonPath );

        if( isKey ) {
            if( isArray ) {
                int idx = Validator.nvl( Types.toInt( index ), 0 );
                setValueToListInJson( path, idx, value, result );
            } else {
                result.put( path, value );
            }
        } else {

            if( ! result.containsKey(path) ) {
                result.put( path, isArray ? new ArrayList() : new HashMap() );
            }

            Map newVal;

            if( isArray ) {
                List list = (List) result.get( path );
                int idx = Validator.nvl( Types.toInt( index ), 0 );
                if( list.size() <= idx || list.get(idx) == null ) {
                    setValueToListInJson( path, idx, new HashMap(), result );
                }
                newVal = (Map) list.get( idx );
            } else {
                newVal = (Map) result.get( path );
            }

            String recursivePath = jsonPath.replaceFirst( currentPath.replaceAll( "\\[", "\\\\[" ) + ".", "" );
            makeKeyUnflatten( recursivePath, value, newVal );

        }

    }

    private void setValueToListInJson( String key, int idx, Object value, Map json ) {
        if( ! json.containsKey( key ) ) {
            json.put( key, new ArrayList<>() );
        }
        List list = (List) json.get( key );
        int listSize = list.size();
        if( idx >= listSize ) {
            for( int i = listSize; i <= idx; i++ ) {
                list.add( null );
            }
        }
        list.set( idx, value );
    }

    public JsonNode readTree( String json ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {


        // TODO : read tree structure
/**
 *  JsonNode node = mapper.readTree( json );
 *  Iterator<String> names = node.fieldNames();
 *  while (names.hasNext()) {
 *   String name = (String) names.next();
 *   JsonNodeType type = node.get(name).getNodeType();
 *   System.out.println(name+":"+type); //will print id:STRING
 *  }
 */

        try {
            return objectMapper.readTree( json );
        } catch( IOException e ) {
            throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e );
        }
    }

    private String getContent( String fromJsonString ) {
        return Validator.isEmpty( fromJsonString ) ? "{}" : fromJsonString;
    }

    private String getCollectionLikeContent( String fromJsonString ) {
        return Validator.isEmpty( fromJsonString ) ? "[]" : fromJsonString;
    }

    /**
     * check text is valid json type
     *
     * @param json	json text
     * @return valid or not
     */
    public boolean isJson(String json ) {
        try {
            objectMapper.readTree( json );
            return true;
        } catch( IOException e ) {
            return false;
        }
    }

    /**
     * Convert as bean from object
     * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @param toClass	return type
     * @param <T>		return type
     * @return	bean filled by object's value
     */
    public <T> T toBeanFrom( Object object, Class<T> toClass ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {
        if( Types.isString( object ) ) {
            String json = getContent( object.toString() );
            try {
                return objectMapper.readValue( json, toClass );
            } catch( JsonParseException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( "JsonParseException : {}\n\t- json string :\n{}\n\t- target class : {}", e.getMessage(), json, toClass );
            } catch( IOException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e );
            }
        } else {
            return objectMapper.convertValue( object, toClass );
        }
    }

    /**
     * Convert as bean from object
     * @param object		json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @param typeReference	type to return
     * 	<pre>
     *	  Examples are below.
     *	  	- new TypeReference&lt;List&lt;HashMap&lt;String, Object&gt;&gt;&gt;() {}
     *	    - new TypeReference&lt;List&lt;String&gt;&gt;() {}
     *	    - new TypeReference&lt;List&gt;() {}
     * 	</pre>
     * @param <T>		return type
     * @return	bean filled by object's value
     */
    public <T> T toBeanFrom( Object object, TypeReference<T> typeReference ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {
        if( Types.isString(object) ) {
            String json = getContent( object.toString() );
            try {
                return objectMapper.readValue( json, typeReference );
            } catch( JsonParseException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( "JsonParseException : {}\n\t- json string :\n{}", e.getMessage(), json );
            } catch( IOException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e );
            }
        } else {
            return objectMapper.convertValue( object, typeReference );
        }
    }

    /**
     * convert json to list
     *
     * @param json	        json text or collection
     * @param typeClass   	list's generic type
     * @param <T> generic type
     * @return list
     * @throws io.nayasis.common.basica.exception.unchecked.JsonMappingException  when json parsing error raised
     */
    public <T> List<T> toListFrom( Object json, Class<T> typeClass ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {
        return (List<T>) toCollectionFrom( json, List.class, typeClass );
    }

    /**
     * convert json to collection
     *
     * @param source            json text or collection to be convert
     * @param returnType        return type
     * @param genericType       collection's generic type
     * @param <T>               return class type
     * @return  collection
     * @throws io.nayasis.common.basica.exception.unchecked.JsonMappingException  when json parsing error raised
     */
    public <T> Collection<T> toCollectionFrom( Object source, Class<? extends Collection> returnType, Class<T> genericType ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {

        CollectionType type = getTypeFactory().constructCollectionType( returnType, genericType );

        if( Types.isString(source) ) {
            String json = getCollectionLikeContent( source.toString() );
            try {
                return objectMapper.readValue( json, type );
            } catch( JsonParseException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), json );
            } catch( IOException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e );
            }
        } else {
            return objectMapper.convertValue( source, type );
        }

    }

    /**
     * convert as Map from source
     *
     * @param source        json text, Map or bean to be convert
     * @param returnType    return map type
     * @param keyType       key's type
     * @param valueType     value's type
     * @param <K>           key type of return Map
     * @param <V>           value type of return Map
     * @return converted map
     * @throws io.nayasis.common.basica.exception.unchecked.JsonMappingException  when json parsing error raised
     */
    public <K,V> Map<K,V> toMapFrom( Object source, Class<? extends Map> returnType, Class<K> keyType, Class<V> valueType ) throws io.nayasis.common.basica.exception.unchecked.JsonMappingException {

        if( source == null ) return new HashMap<>();

        MapLikeType type = getTypeFactory().constructMapLikeType( returnType, keyType, valueType );

        if( Types.isString(source) ) {
            String json = getContent( source.toString() );
            try {
                return objectMapper.readValue( json, type );
            } catch( JsonParseException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e, "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), json );
            } catch( IOException e ) {
                throw new io.nayasis.common.basica.exception.unchecked.JsonMappingException( e );
            }
        } else {
            return objectMapper.convertValue( source, type );
        }

    }

    /**
     * Convert as Map from object
     * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @return	Map filled by object's value
     */
    public Map<String, Object> toMapFrom( Object object ) throws JsonMappingException {
        return toMapFrom( object, LinkedHashMap.class, String.class, Object.class );
    }

    private TypeFactory getTypeFactory() {
        return objectMapper.getTypeFactory();
    }

}


