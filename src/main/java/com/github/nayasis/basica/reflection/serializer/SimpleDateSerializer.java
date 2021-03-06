package com.github.nayasis.basica.reflection.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.nayasis.basica.model.NDate;

import java.io.IOException;
import java.util.Date;

import static com.github.nayasis.basica.model.NDate.DEFAULT_FORMAT;

public class SimpleDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize( final Date value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( new NDate(value).toString(DEFAULT_FORMAT), generator );
    }

}
