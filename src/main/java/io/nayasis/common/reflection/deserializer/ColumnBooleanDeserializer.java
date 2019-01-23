package io.nayasis.common.reflection.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.nybatis.core.util.StringUtil;

import java.io.IOException;

public class ColumnBooleanDeserializer extends JsonDeserializer<Boolean> {

	@Override
    public Boolean deserialize( JsonParser jp, DeserializationContext ctxt ) throws IOException {
		try {
			String val = jp.getValueAsString();
			if( val == null ) return null;
			return StringUtil.toBoolean( val );
		} catch( JsonParseException e ) {
			return jp.getBooleanValue();
		}
    }

}
