package io.nayasis.common.clone.fastcloner.implement;

import io.nayasis.common.base.Classes;
import io.nayasis.common.clone.Cloner;
import io.nayasis.common.clone.fastcloner.interfaces.DeepCloner;

import java.util.Calendar;
import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class CalanderCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        Calendar source = (Calendar) object;
        Calendar target = Classes.createInstance( source.getClass() );

        target.setTimeInMillis( source.getTimeInMillis() );
        target.setTimeZone( source.getTimeZone() );

        return source;

    }
}
