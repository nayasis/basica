package io.nayasis.common.basica.exception.unchecked;


/**
 * Runtime {@link #NoSuchAlgorithmException}
 *
 * @author nayasis@gmail.com
 *
 */
public class NoSuchAlgorithmException extends BaseRuntimeException {

    private static final long serialVersionUID = -5852066758758901525L;

    public NoSuchAlgorithmException() {
        super();
    }

    public NoSuchAlgorithmException( Throwable rootCause ) {
        super( rootCause );
    }

    public NoSuchAlgorithmException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public NoSuchAlgorithmException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
