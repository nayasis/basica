package io.nayasis.common.reflection.serializer.simple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.nayasis.common.model.NDate;

import java.io.IOException;

public class SimpleNDateSerializer extends JsonSerializer<NDate> {

    @Override
    public void serialize( final NDate value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( value.toDate(), generator );
    }

}
