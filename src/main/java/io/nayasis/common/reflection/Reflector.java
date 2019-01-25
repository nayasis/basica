package io.nayasis.common.reflection;

import com.fasterxml.jackson.core.type.TypeReference;
import io.nayasis.common.base.Classes;
import io.nayasis.common.clone.Cloner;
import io.nayasis.common.exception.unchecked.JsonMappingException;
import io.nayasis.common.exception.unchecked.UncheckedClassCastException;
import io.nayasis.common.model.NList;
import io.nayasis.common.reflection.core.BeanMerger;
import io.nayasis.common.reflection.core.CoreReflector;
import io.nayasis.common.reflection.core.JsonConverter;
import io.nayasis.common.reflection.mapper.Invocator;
import io.nayasis.common.reflection.mapper.NObjectMapper;
import io.nayasis.common.reflection.mapper.MethodInvocator;

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
public class Reflector {

	public final static JsonConverter jsonConverter = new JsonConverter( new NObjectMapper() );

	/**
	 * Creates and returnes a copy of object
	 *
	 * @param object object to clone
	 * @param <T> object's generic type
	 * @return a clone of object
	 */
    @SuppressWarnings( "unchecked" )
    public static <T> T clone( T object ) {
		return clone( object, true );
    }

	/**
	 * Creates and returnes a copy of object
	 *
	 * @param object 	object to clone
	 * @param deepClone if true, clone all elements in object's Map or Collection
	 * @param <T> object's generic type
	 * @return a clone of object
	 */
	@SuppressWarnings( "unchecked" )
	public static <T> T clone( T object, boolean deepClone ) {
		if( deepClone ) {
			return new Cloner().deepClone( object );
		} else {
			return new Cloner().shallowClone( object );
		}
	}

	/**
	 * Copy data in instance
	 *
	 * @param source	bean as source
	 * @param target	bean as target
	 */
    public static void copy( Object source, Object target ) {
		if( source == null || target == null ) return;
		if( Classes.isExtendedBy(target.getClass(),source.getClass()) || Classes.isExtendedBy(source.getClass(),target.getClass()) ) {
    		new Cloner().copy( source, target );
		} else {
			Object newTarget = toBeanFrom( source, target.getClass() );
			new Cloner().copy( newTarget, target );
		}
    }

	/**
	 * Merge data in instance
	 *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
	 * @param <T> target's generic type
	 * @return merged Map
	 */
    public static <T> T merge( Object source, T target ) {
		return merge( source, target, true );
	}

	/**
	 * Merge data in instance
	 *
     * @param source    source containing additional properties to merge in
     * @param target    target object to extend. it will receive the new properties
     * @param skipEmpty if false, skip merging when source value is null. if true, skip merging when source vale is null or empty.
	 * @param <T> source's generic type
	 * @return merged Map
	 */
    public static <T> T merge( Object source, T target, boolean skipEmpty ) {
        return new BeanMerger().merge( source, target, skipEmpty );
    }

	/**
	 * Get inspection result of fields within instance
	 *
	 * @param bean    instance to inspect
	 * @return report of fields' value
	 */
    public static String toString( Object bean ) {
		CoreReflector coreReflector = new CoreReflector();
    	NList result = new NList();
        for( Field field : coreReflector.getFields(bean) ) {
        	if( ! field.isAccessible() ) field.setAccessible( true );
			String typeName = field.getType().getName();
        	result.add( "field", field.getName() );
			result.add( "type", typeName );
        	try {
        		switch( typeName ) {
        			case "[C" :
        				result.add( "value", "[" + new String( (char[]) field.get( bean ) ) + "]" );
        				break;
        			default :
        				result.add( "value", field.get( bean ) );

        		}
        	} catch( IllegalArgumentException | IllegalAccessException e ) {
        		result.add( "value", e.getMessage() );
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
	 * @param ignoreNull	whether or not to ignore null value
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint, boolean sort, boolean ignoreNull ) throws JsonMappingException {
		return jsonConverter.toJson( fromBean, prettyPrint, sort, ignoreNull, null );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @param sort			whether or not to sort key of json
	 * @param ignoreNull	whether or not to ignore null value
	 * @param view	        json view class
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint, boolean sort, boolean ignoreNull, Class view ) throws JsonMappingException {
		return jsonConverter.toJson( fromBean, prettyPrint, sort, ignoreNull, view );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint ) throws JsonMappingException {
		return toJson( fromBean, prettyPrint, false, false );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param prettyPrint	whether or not to make json text pretty
	 * @param view	        json view class
	 * @return json text
	 */
	public static String toJson( Object fromBean, boolean prettyPrint, Class view ) throws JsonMappingException {
		return toJson( fromBean, prettyPrint, false, false, view );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @return json text
	 */
	public static String toJson( Object fromBean ) throws JsonMappingException {
		return toJson( fromBean, false );
	}

	/**
	 * Get json text
	 *
	 * @param fromBean		instance to convert as json data
	 * @param view	        json view class
	 * @return json text
	 */
	public static String toJson( Object fromBean, Class view ) throws JsonMappingException {
		return toJson( fromBean, false, view );
	}

	/**
	 * get json text without null value
	 *
	 * @param fromBean		instance to convert as json
	 * @param prettyPrint	true if you want to see json text with indentation
	 * @return json text
	 */
	public static String toNullIgnoredJson( Object fromBean, boolean prettyPrint ) throws JsonMappingException {
		return toJson( fromBean, prettyPrint, false, true );
	}

	/**
	 * get json text without null value
	 *
	 * @param fromBean	instance to convert as json data
	 * @return json text
	 */
	public static String toNullIgnoredJson( Object fromBean ) throws JsonMappingException {
		return toNullIgnoredJson( fromBean, false );
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
	public static Map<String, Object> toMapWithFlattenKey( Object object ) throws JsonMappingException {
		return jsonConverter.toMapWithFlattenKey( object );
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
	public static Map<String, Object> toMapWithUnflattenKey( Object object ) throws JsonMappingException {
		return jsonConverter.toMapWithUnflattenKey( object );
	}

	/**
	 * check text is valid json type
	 *
	 * @param json	json text
	 * @return valid or not
	 */
	public static boolean isValidJson( String json ) {
		return jsonConverter.isValidJson( json );
	}

	/**
	 * Convert as bean from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @param toClass	class to return
	 * @param <T>		return type
	 * @return	bean filled by object's value
	 */
	public static <T> T toBeanFrom( Object object, Class<T> toClass ) throws JsonMappingException {
		return jsonConverter.toBeanFrom( object, toClass );
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
	public static <T> T toBeanFrom( Object object, TypeReference<T> typeReference ) throws JsonMappingException {
		return jsonConverter.toBeanFrom( object, typeReference );
	}

	/**
	 * convert json to list
	 *
	 * @param json			json text
	 * @param typeClass   	list's generic type
	 * @param <T> generic type
     * @return list
     */
	public static <T> List<T> toListFromJson( String json, Class<T> typeClass ) throws JsonMappingException {
		return jsonConverter.toListFrom( json, typeClass );
	}

	/**
	 * Convert as List
	 *
	 * @param json json text
	 * @return List
	 */
	public static List toListFromJson( String json ) throws JsonMappingException {
		return toListFromJson( json, Object.class );
	}

	/**
	 * Convert as Map from object
	 * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
	 * @return	Map filled by object's value
	 */
	public static Map toMapFrom( Object object ) throws JsonMappingException {
		return jsonConverter.toMapFrom( object );
	}

	/**
	 * Wrap bean with invocation method
	 *
	 * @param beanToWrapProxy	target bean to wrap method
	 * @param interfaces		target interfaces to wrap method
	 * @param methodInvocator	method invocator
	 * @param <T> 				expected class of return
	 * @return	proxy bean to wrap
	 */
    public static <T> T wrapProxy( T beanToWrapProxy, Class<?>[] interfaces, MethodInvocator methodInvocator ) {
    	return (T) Proxy.newProxyInstance( beanToWrapProxy.getClass().getClassLoader(), interfaces, new Invocator<>( beanToWrapProxy, methodInvocator ) );
    }

	/**
	 * Unwrap proxy invocator from bean
	 * @param beanToUnwrapProxy	target bean to unwrap proxy method
	 * @param <T> 				expected class of return
	 * @return	original bean
	 * @throws UncheckedClassCastException if beanToUnwrapProxy is not proxy bean.
	 */
	public static <T> T unwrapProxy( T beanToUnwrapProxy ) {
		if( beanToUnwrapProxy == null || ! Proxy.isProxyClass( beanToUnwrapProxy.getClass() ) ) {
			return beanToUnwrapProxy;
		}
		InvocationHandler invocationHandler = Proxy.getInvocationHandler( beanToUnwrapProxy );
		if( ! (invocationHandler instanceof Invocator) ) {
			throw new UncheckedClassCastException( "Only proxy instance to generated by nayasis.common.reflection.Refector can be unwraped." );
		}
		return (T) ((Invocator<Object>) invocationHandler).getOriginalInstance();
	}

}
