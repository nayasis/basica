package io.nayasis.basica.reflection.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.nayasis.basica.model.NList;

import java.io.IOException;

public class SimpleNListSerializer extends JsonSerializer<NList> {

    @Override
    public void serialize(final NList value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( value.toList(), generator );
    }

}
