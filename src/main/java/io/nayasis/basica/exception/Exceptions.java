package io.nayasis.basica.exception;

import io.nayasis.basica.exception.helper.ProxyThrowables;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.PrintWriter;
import java.io.StringWriter;
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

    private boolean hasLogback = true;

    /**
     * get filter
     *
     * @return regular expression to exclude stacktrace.
     */
    public String getFilter() {
        return filter;
    }

    /**
     * set filter excluding stacktrace.
     *
     * @param regexp    regular expression excluding stacktrace.
     * @return  Throwables
     */
    public Exceptions setFilter( String regexp ) {
        Exceptions.filter = regexp;
        return instance;
    }

    /**
     * filter stacktrace in exception
     *
     * @param exception exception to exclude stacktrace
     * @return  exception excluding stacktrace by filter regular expression.
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

        if( hasLogback ) {
            try {
                return new ProxyThrowables().toString( exception );
            } catch ( Throwable e ) {
                hasLogback = false;
            }
        }

        StringWriter writer = new StringWriter();
        exception.printStackTrace( new PrintWriter(writer) );
        return writer.toString();

    }

}
