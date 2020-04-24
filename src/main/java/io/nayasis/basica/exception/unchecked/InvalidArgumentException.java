package io.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #InvalidArgumentException}
 *
 * @author nayasis@gmail.com
 *
 */
public class InvalidArgumentException extends BaseRuntimeException {

    private static final long serialVersionUID = -6514253502390899131L;

    public InvalidArgumentException() {
        super();
    }

    public InvalidArgumentException( Throwable cause ) {
        super( cause );
    }

    public InvalidArgumentException( String message, Object... param ) {
        super( message, param );
    }

    public InvalidArgumentException( Throwable cause, String message, Object... param ) {
    	super( cause, message, param );
    }

}