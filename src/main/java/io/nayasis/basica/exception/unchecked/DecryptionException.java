package io.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #DecryptionException}
 *
 * @author nayasis@gmail.com
 *
 */
public class DecryptionException extends BaseRuntimeException {

    private static final long serialVersionUID = -1037536492987727415L;

    public DecryptionException() {
        super();
    }

    public DecryptionException( Throwable rootCause ) {
        super( rootCause );
    }

    public DecryptionException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public DecryptionException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
