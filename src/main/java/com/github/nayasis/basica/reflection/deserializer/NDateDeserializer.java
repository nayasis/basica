package com.github.nayasis.basica.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.nayasis.basica.exception.unchecked.ParseException;
import com.github.nayasis.basica.model.NDate;

import java.io.IOException;

import static com.github.nayasis.basica.model.NDate.ISO_8601_FORMAT;

public class NDateDeserializer extends JsonDeserializer<NDate> {

	@Override
    public NDate deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		try {
			long value = jp.getLongValue();
			return new NDate( value );
		} catch( JsonParseException e ) {
			String string = jp.getValueAsString();
			try {
				return new NDate( string, ISO_8601_FORMAT );
			} catch ( ParseException ex ) {
				return new NDate( string );
			}
		}
    }

}
