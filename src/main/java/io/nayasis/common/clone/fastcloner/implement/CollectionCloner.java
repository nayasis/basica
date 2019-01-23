package io.nayasis.common.clone.fastcloner.implement;

import io.nayasis.common.base.Classes;
import io.nayasis.common.clone.Cloner;
import io.nayasis.common.clone.fastcloner.interfaces.DeepCloner;
import io.nayasis.common.exception.unchecked.UncheckedClassCastException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Set cloner
 *
 * @author nayasis@gmail.com
 * @since 2017-03-28
 */
public class CollectionCloner implements DeepCloner {
    @Override
    public Object clone( Object object, Cloner cloner, Map valueReference ) {

        Collection source = (Collection) object;
        Collection target;
        try {
            target = Classes.createInstance( source.getClass() );
        } catch( UncheckedClassCastException e ) {
            target = new ArrayList();
        }

        for( Object val : source ) {
            target.add( cloner.cloneObject(val, valueReference) );
        }

        return target;

    }
}
