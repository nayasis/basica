package io.nayasis.common.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.nayasis.common.model.NDate;

import java.io.IOException;
import java.util.Date;

public class SimpleDateDeserializer extends JsonDeserializer<Date> {

	@Override
    public Date deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		try {
			long value = jp.getLongValue();
			return new Date( value );
		} catch( JsonParseException e ) {
			String value = jp.getValueAsString();
			return new NDate( value, NDate.DEFAULT_FORMAT).toDate();
		}
    }

}
