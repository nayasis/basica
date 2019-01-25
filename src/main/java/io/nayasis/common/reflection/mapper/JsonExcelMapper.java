package io.nayasis.common.reflection.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.nayasis.common.reflection.deserializer.SimpleDateDeserializer;
import io.nayasis.common.reflection.serializer.simple.SimpleDateSerializer;

import java.util.Date;


public class JsonExcelMapper extends NObjectMapper {

	public JsonExcelMapper() {
		super();
		setCustomDeserializer();
	}

	@Override
	protected void setCustomDeserializer() {

		SimpleModule module = new SimpleModule( "DateSerializer" );

		module.addSerializer(   Date.class,  new SimpleDateSerializer()   );
		module.addDeserializer( Date.class,  new SimpleDateDeserializer() );

		registerModule( module );

	}

}
