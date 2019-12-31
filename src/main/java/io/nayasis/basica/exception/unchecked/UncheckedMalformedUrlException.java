package io.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #UncheckedMalformedUrlException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedMalformedUrlException extends BaseRuntimeException {

    private static final long serialVersionUID = -186619276641466999L;

    public UncheckedMalformedUrlException() {
        super();
    }

    public UncheckedMalformedUrlException( Throwable rootCause ) {
        super( rootCause );
    }

    public UncheckedMalformedUrlException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public UncheckedMalformedUrlException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
