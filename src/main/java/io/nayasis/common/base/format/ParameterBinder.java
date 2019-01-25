package io.nayasis.common.base.format;

public interface ParameterBinder<T> {

    String bind( String key, String format, T parameter );

}
