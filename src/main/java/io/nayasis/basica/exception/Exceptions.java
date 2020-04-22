package io.nayasis.basica.exception;

import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static io.nayasis.basica.validation.Validator.isFound;
import static io.nayasis.basica.validation.Validator.isNotEmpty;

/**
 * Throwable Utility
 */
@UtilityClass
@Slf4j
public class Exceptions {

    private Exceptions instance = new Exceptions();

    private String filter;

    /**
     * get filter
     *
     * @return regular expression to exclude stack-traces
     */
    public String getFilter() {
        return filter;
    }

    /**
     * set filter to exclude
     *
     * @param filter    regular expression to exclude stack-traces
     * @return  Throwables
     */
    public Exceptions setFilter( String filter ) {
        Exceptions.filter = filter;
        return instance;
    }

    /**
     * filter stack traces in exception
     *
     * @param exception exception to exclude stack-traces by filter
     * @return  exception having stack-traces filtered.
     */
    public Throwable filter( Throwable exception ) {

        if( exception == null ) return null;

        Throwable clone = new Throwable( exception.getMessage() );

        List<StackTraceElement> list = new ArrayList<>();

        for( StackTraceElement e : exception.getStackTrace() ) {
            if( isNotEmpty(filter) && isFound(e.toString(),filter) ) continue;
            list.add( e );
        }
        clone.setStackTrace( list.toArray( new StackTraceElement[] {} ) );

        if( exception.getCause() != null ) {
            Throwable cause = filter( exception.getCause() );
            clone.initCause( cause );
        }

        return clone;

    }

    /**
     * get root cause of given exception.
     *
     * @param exception exception to inspect.
     * @return  innermost exception or null if not exist.
     */
    public Throwable getRootCause( Throwable exception ) {

        if (exception == null) return null;

        Throwable root  = null;
        Throwable cause = exception.getCause();

        while( cause != null && cause != root ) {
            root  = cause;
            cause = cause.getCause();
        }
        return root;

    }

    /**
     * convert to string
     *
     * @param exception
     * @return
     */
    public String toString( Throwable exception ) {
        if( exception == null ) return "";
        ThrowableProxy proxy = new ThrowableProxy( filter(exception) );
        proxy.calculatePackagingData();
        return ThrowableProxyUtil.asString( proxy );
    }

}
