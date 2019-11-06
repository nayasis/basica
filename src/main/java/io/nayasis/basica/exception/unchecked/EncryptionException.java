package io.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #EncryptionException}
 *
 * @author nayasis@gmail.com
 *
 */
public class EncryptionException extends BaseRuntimeException {

    private static final long serialVersionUID = 1608977233573328783L;

    public EncryptionException() {
        super();
    }

    public EncryptionException( Throwable rootCause ) {
        super( rootCause );
    }

    public EncryptionException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public EncryptionException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
