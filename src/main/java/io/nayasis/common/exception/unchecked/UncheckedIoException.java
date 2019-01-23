package io.nayasis.common.exception.unchecked;


/**
 * Runtime {@link #UncheckedIoException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedIoException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public UncheckedIoException() {
        super();
    }

    public UncheckedIoException( Throwable rootCause ) {
        super( rootCause );
    }

    public UncheckedIoException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public UncheckedIoException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
