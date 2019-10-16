package io.nayasis.common.basica.reflection.serializer.simple;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.nayasis.common.basica.model.NDate;

import java.io.IOException;
import java.util.Date;

import static io.nayasis.common.basica.model.NDate.FULL_FORMAT;

public class SimpleDateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize( final Date value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue( new NDate(value).toString(FULL_FORMAT), generator );
    }

}
