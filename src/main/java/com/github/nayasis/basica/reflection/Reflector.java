package com.github.nayasis.basica.reflection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.nayasis.basica.reflection.core.BeanMerger;
import com.github.nayasis.basica.reflection.core.ClassReflector;
import com.github.nayasis.basica.reflection.core.Cloner;
import com.github.nayasis.basica.reflection.core.JsonConverter;
import com.github.nayasis.basica.reflection.helper.invoker.Invoker;
import com.github.nayasis.basica.reflection.helper.invoker.MethodInvoker;
import com.github.nayasis.basica.reflection.helper.mapper.NObjectMapper;
import com.github.nayasis.basica.exception.unchecked.JsonMappingException;
import com.github.nayasis.basica.exception.unchecked.UncheckedClassCastException;
import com.github.nayasis.basica.model.NList;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;

/**
 * Reflection Utility
 *
 * @author nayasis@gmail.com
 *
 */
@UtilityClass
@Slf4j
public class Reflector {

	private JsonConverter mapperNullable         = new JsonConverter( new NObjectMapper() );
	private JsonConverter mapperNotNull          = new JsonConverter( new NObjectMapper().ignoreNull(true) );
	private JsonConverter mapperSortableNullable = new JsonConverter( new NObjectMapper().serializeSortable(true) );
	private JsonConverter mapperSortableNotNull  = new JsonConverter( new NObjectMapper().serializeSortable(true).ignoreNull(true) );

	/**
	 * clone object in deep copy mode.
	 *
	 * @param object object to clone
	 * @param <T> object's generic type
	 * @return a clone of object
	 */
    @SuppressWarnings( "unchecked" )
    public <T> T clone( T object ) {
    	return Cloner.clone( object );
    }

	/**
	 * copy properties.
	 *
	 * @param source  source object
	 * @param target  target object
	 */
    public void copy( Object source, Object target ) {
		if( source == null || target == null ) return;
		Cloner.copyProperties( source, target );
    }

	/**
	 * merge properties
	 *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
	 * @param <T> target's generic type
	 * @return merged Map
	 */
    public <T> T merge( Object source, T target ) {
		return merge( source, target, true );
	}

	/**
	 * Merge properties
	 *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @param skipEmpty if false, skip merging when source value is null. if true, skip merging when source vale is null or empty.
	 * @param <T> source's generic type
	 * @return merged Map
	 */
    public <T> T merge( Object source, T target, boolean skipEmpty ) {
        return new BeanMerger().merge( source, target, skipEmpty );
    }

	/**
	 * Get inspection result of fields within instance
	 *
	 * @param bean    instance to inspect
	 * @return report of fields' value
	 */
    public String toString( Object bean ) {
    	NList result = new NList();
        for( Field field : ClassReflector.getFields(bean) ) {
        	if( ! field.isAccessible() ) field.setAccessible( true );
			String typeName = field.getType().getName();
        	result.addData( "field", field.getName() );
			result.addData( "type", typeName );
        	try {
        		switch( typeName ) {
        			case "[C" :
        				result.addData( "value", "[" + new String( (char[]) field.get( bean ) ) + "]" );
        				break;
        			default :
        				result.addData( "value", field.get( bean ) );

        		}
        	} catch( IllegalArgumentException | IllegalAccessException e ) {
        		result.addData( "value", e.getMessage() );
            }
        }
        return result.toString();
    }

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @param sort			whether or not to sort key of json
	 * @return json text
	 */
	public String toJson( Object fromBean, boolean prettyPrint, boolean sort ) throws JsonMappingException {
		return toJson( fromBean, prettyPrint, sort, true, null );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @param sort			whether or not to sort key of json
	 * @param ignoreNull	ignore null value
	 * @param view	        json view class
	 * @return json text
	 */
	public String toJson( Object fromBean, boolean prettyPrint, boolean sort, boolean ignoreNull, Class view ) throws JsonMappingException {
		JsonConverter converter;
		if( sort ) {
			converter = ignoreNull ? mapperNotNull : mapperNullable;
		} else {
			converter = ignoreNull ? mapperSortableNotNull : mapperSortableNullable;
		}
		return converter.toJson( fromBean, prettyPrint, view );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @return json text
	 */
	public String toJson( Object fromBean, boolean prettyPrint ) throws JsonMappingException {
		return toJson( fromBean, prettyPrint, false );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @param view	        json view class
	 * @return json text
	 */
	public String toJson( Object fromBean, boolean prettyPrint, Class view ) throws JsonMappingException {
		return toJson( fromBean, prettyPrint, false, true, view );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @return json text
	 */
	public String toJson( Object fromBean ) throws JsonMappingException {
		return toJson( fromBean, false );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param view	        json view class
	 * @return json text
	 */
	public String toJson( Object fromBean, Class view ) throws JsonMappingException {
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
	public Map<String, Object> toFlattenMap( Object object ) throws JsonMappingException {
		return mapperNullable.toFlattenMap( object );
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
	public Map<String, Object> toUnflattenMap( Object object ) throws JsonMappingException {
		return mapperNullable.toUnflattenMap( object );
	}

	/**
	 * check text is valid json type
	 *
	 * @param json	json text
	 * @return valid or not
	 */
	public boolean isJson( String json ) {
		return mapperNullable.isJson( json );
	}

	/**
	 * Convert as bean from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @param toClass	class to return
	 * @param <T>		return type
	 * @return	bean filled by object's value
	 */
	public <T> T toBeanFrom( Object object, Class<T> toClass ) throws JsonMappingException {
		return mapperNullable.toBeanFrom( object, toClass );
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
	public <T> T toBeanFrom( Object object, TypeReference<T> typeReference ) throws JsonMappingException {
		return mapperNullable.toBeanFrom( object, typeReference );
	}

	/**
	 * convert json to list
	 *
	 * @param json		json text or collection
	 * @param generic   generic type of List
	 * @param <T> generic type
     * @return list
     */
	public <T> List<T> toListFrom( Object json, Class<T> generic ) throws JsonMappingException {
		return mapperNullable.toListFrom( json, generic );
	}

	/**
	 * Convert as List
	 *
	 * @param json json text or collection
	 * @return List
	 */
	public List toListFrom( Object json ) throws JsonMappingException {
		return toListFrom( json, Object.class );
	}

	/**
	 * Convert as Map from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @return	Map filled by object's value
	 */
	public Map toMapFrom( Object object ) throws JsonMappingException {
		return mapperNullable.toMapFrom( object );
	}

	/**
	 * wrap bean's method in proxy
	 *
	 * @param bean			target bean to wrap method
	 * @param interfaces	bean interfaces to wrap method
	 * @param methodInvoker	method invoker
	 * @param <T> 			expected class of return
	 * @return	proxy bean to wrap
	 */
    public <T> T wrapProxy( T bean, Class<?>[] interfaces, MethodInvoker methodInvoker ) {
    	return (T) Proxy.newProxyInstance( bean.getClass().getClassLoader(), interfaces, new Invoker<>( bean, methodInvoker ) );
    }

	/**
	 * Unwrap proxy method from bean
	 * @param bean	target bean to unwrap proxy method
	 * @param <T> 	expected class of return
	 * @return	original bean
	 * @throws UncheckedClassCastException if bean is not proxy bean.
	 */
	public <T> T unwrapProxy( T bean ) {
		if( bean == null || ! Proxy.isProxyClass( bean.getClass() ) ) {
			return bean;
		}
		InvocationHandler invocationHandler = Proxy.getInvocationHandler( bean );
		if( invocationHandler instanceof Invoker ) {
			return (T) ((Invoker<Object>) invocationHandler).getOriginalInstance();
		} else {
			log.warn( "only bean proxied by [{}.wrapProxy] can be unwrapped.", Reflector.class.getName() );
			return bean;
		}
	}

}
