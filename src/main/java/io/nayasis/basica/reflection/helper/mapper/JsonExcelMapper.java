package io.nayasis.basica.reflection.helper.mapper;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.nayasis.basica.reflection.deserializer.SimpleDateDeserializer;
import io.nayasis.basica.reflection.serializer.SimpleDateSerializer;

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
