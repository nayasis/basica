package io.nayasis.common.exception.unchecked;


/**
 * Runtime {@link #CloningException}
 *
 * @author nayasis@gmail.com
 *
 */
public class CloningException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public CloningException() {
        super();
    }

    public CloningException( Throwable rootCause ) {
        super( rootCause );
    }

    public CloningException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public CloningException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
