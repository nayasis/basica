package io.nayasis.common.exception.unchecked;


/**
 * Runtime {@link #ParseException}
 *
 * @author nayasis@gmail.com
 *
 */
public class ParseException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public ParseException() {
        super();
    }

    public ParseException( Throwable rootCause ) {
        super( rootCause );
    }

    public ParseException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public ParseException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
