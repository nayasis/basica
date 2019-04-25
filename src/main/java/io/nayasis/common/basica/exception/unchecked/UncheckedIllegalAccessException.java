package io.nayasis.common.basica.exception.unchecked;


/**
 * Runtime {@link #UncheckedIllegalAccessException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedIllegalAccessException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public UncheckedIllegalAccessException() {
        super();
    }

    public UncheckedIllegalAccessException(Throwable rootCause ) {
        super( rootCause );
    }

    public UncheckedIllegalAccessException(String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public UncheckedIllegalAccessException(Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
