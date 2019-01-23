package io.nayasis.common.reflection.serializer.simple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.nayasis.common.model.NList;

import java.io.IOException;

public class SimpleNListSerializer extends JsonSerializer<NList> {

    @Override
    public void serialize(final NList value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( value.toList(), generator );
    }

}
