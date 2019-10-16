package io.nayasis.common.basica.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.nayasis.common.basica.exception.unchecked.ParseException;
import io.nayasis.common.basica.model.NDate;

import java.io.IOException;
import java.util.Date;

import static io.nayasis.common.basica.model.NDate.ISO_8601_FORMAT;

public class SimpleDateDeserializer extends JsonDeserializer<Date> {

	@Override
    public Date deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		try {
			long value = jp.getLongValue();
			return new Date( value );
		} catch( JsonParseException e ) {
			String string = jp.getValueAsString();
			try {
				return new NDate( string, ISO_8601_FORMAT ).toDate();
			} catch ( ParseException ex ) {
				return new NDate( string ).toDate();
			}
		}
    }

}
