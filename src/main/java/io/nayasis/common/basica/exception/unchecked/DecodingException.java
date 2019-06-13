package io.nayasis.common.basica.exception.unchecked;


/**
 * Runtime {@link #DecodingException}
 *
 * @author nayasis@gmail.com
 *
 */
public class DecodingException extends BaseRuntimeException {

    private static final long serialVersionUID = 701170958504771499L;

    public DecodingException() {
        super();
    }

    public DecodingException( Throwable rootCause ) {
        super( rootCause );
    }

    public DecodingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public DecodingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
