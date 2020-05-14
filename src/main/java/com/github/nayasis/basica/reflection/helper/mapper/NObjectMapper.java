package com.github.nayasis.basica.reflection.helper.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.nayasis.basica.reflection.deserializer.DateDeserializer;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.ALL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;


public class NObjectMapper extends ObjectMapper {

	public NObjectMapper() {
		init();
		setDefaultFilter();
		setCustomDeserializer();
	}

	protected void init() {

		configure( JsonParser.Feature.ALLOW_SINGLE_QUOTES,                true  ); // 문자열 구분기호를 " 뿐만 아니라 ' 도 허용
		configure( SerializationFeature.FAIL_ON_EMPTY_BEANS,              false ); // Bean 이 null 일 경우 허용
		configure( SerializationFeature.WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS, true  ); // char 배열을 문자로 강제 변환하지 않는다.
		configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,     false ); // 대상객체에 매핑할 field가 없을 경우도 허용
		configure( DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,    false );
		configure( MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS,           true  ); // private 변수라도 강제로 매핑
		configure( DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,   true  );
		configure( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,        true  ); // only applied to Map ( not to bean )

//		if( ignoreNull ) {
//			setSerializationInclusion( NON_NULL );
//		}
//
//		if( sort ) {
//			configure( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true );
//			configure( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true );
//		}
//
//		if( notJsonIgnore ) {
//			setAnnotationIntrospector( new NotJsonIgnoreInspector() );
//		}
//
		// java 8 date & time
		configure( SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false ).registerModule( new JavaTimeModule() );

		// only convert by Class' field.
		setVisibility( ALL,   NONE );
		setVisibility( FIELD, ANY  );

	}

	protected void setCustomDeserializer() {
		SimpleModule module = new SimpleModule( "NObjectMapper" );
		module.addDeserializer( Date.class, new DateDeserializer() );
		registerModule( module );
	}

	/**
	 * Prevent error when pojo with @JsonFilter annotation is parsed.
	 */
	private void setDefaultFilter() {
		setFilterProvider( new SimpleFilterProvider().setFailOnUnknownId(false) );
	}

	public NObjectMapper setAnnotationIntrospector( AnnotationIntrospector introspector ) {
		super.setAnnotationIntrospector( introspector );
		return this;
	}

	public NObjectMapper ignoreJsonIgnore( boolean yes ) {
		if( yes ) {
			return setAnnotationIntrospector( new NotJsonIgnoreInspector() );
		} else {
			return setAnnotationIntrospector( null );
		}
	}

	public NObjectMapper ignoreNull( boolean yes ) {
		super.setSerializationInclusion( yes ? NON_NULL : ALWAYS );
		return this;
	}

	public NObjectMapper serializeSortable( boolean yes ) {
		configure( MapperFeature.SORT_PROPERTIES_ALPHABETICALLY,   yes );
		configure( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, yes );
		return this;
	}

}
