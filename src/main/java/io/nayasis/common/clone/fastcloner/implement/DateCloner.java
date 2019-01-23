package io.nayasis.common.clone.fastcloner.implement;

import io.nayasis.common.base.Classes;
import io.nayasis.common.clone.Cloner;
import io.nayasis.common.clone.fastcloner.interfaces.DeepCloner;
import io.nayasis.common.model.NDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class DateCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        if( object instanceof Date ) {
            Date source = (Date) object;
            return new Date( source.getTime() );
        } else if( object instanceof NDate ) {
            NDate source = (NDate) object;
            return source.clone();
        } else if( object instanceof LocalDate ) {
            LocalDate source = (LocalDate) object;
            return LocalDate.from( source );
        } else if( object instanceof LocalDateTime ) {
            LocalDateTime source = (LocalDateTime) object;
            return LocalDateTime.from( source );
        }

        throw new IllegalArgumentException( String.format( "Unsupported object to clone. [%s: %s]", Classes.getClass(object), object ) );

    }
}
