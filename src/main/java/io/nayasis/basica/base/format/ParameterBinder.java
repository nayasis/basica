package io.nayasis.basica.base.format;

public interface ParameterBinder<T> {

    String bind( String key, String format, T parameter );

}
