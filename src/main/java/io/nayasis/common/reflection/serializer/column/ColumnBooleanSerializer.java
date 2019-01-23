package io.nayasis.common.reflection.serializer.column;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.nybatis.core.db.constant.NullValue;
import org.nybatis.core.util.StringUtil;

import java.io.IOException;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class ColumnBooleanSerializer extends JsonSerializer<Boolean> {

    @Override
    public void serialize( Boolean value, JsonGenerator generator, SerializerProvider provider ) throws IOException, JsonProcessingException {

        if( isNull(value) ) {
            generator.writeString( NullValue.STRING );
        } else {
            generator.writeString( StringUtil.toYn(value) );
        }

    }

    private boolean isNull( Boolean value ) {
        return value == NullValue.BOOLEAN;
    }

}
