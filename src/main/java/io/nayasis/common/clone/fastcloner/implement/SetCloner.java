package io.nayasis.common.clone.fastcloner.implement;

import io.nayasis.common.base.Classes;
import io.nayasis.common.clone.Cloner;
import io.nayasis.common.clone.fastcloner.interfaces.DeepCloner;

import java.util.Map;
import java.util.Set;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class SetCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        Set source = (Set) object;
        Set target = Classes.createInstance( source.getClass() );

        for( Object val : source ) {
            target.add( cloner.cloneObject( val, valueReference ) );
        }

        return target;

    }
}
