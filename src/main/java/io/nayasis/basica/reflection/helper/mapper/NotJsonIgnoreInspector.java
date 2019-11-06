package io.nayasis.basica.reflection.helper.mapper;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.beans.Transient;

public class NotJsonIgnoreInspector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker( AnnotatedMember m ) {
        Transient annotation = m.getAnnotation( Transient.class );
        if( annotation != null ) {
            return annotation.value();
        }
        return false;
    }

}
