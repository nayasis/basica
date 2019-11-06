package io.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #UncheckedIOException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedIOException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public UncheckedIOException() {
        super();
    }

    public UncheckedIOException( Throwable rootCause ) {
        super( rootCause );
    }

    public UncheckedIOException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public UncheckedIOException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
