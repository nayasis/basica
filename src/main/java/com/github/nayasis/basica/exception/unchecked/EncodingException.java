package com.github.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #EncodingException}
 *
 * @author nayasis@gmail.com
 *
 */
public class EncodingException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public EncodingException() {
        super();
    }

    public EncodingException( Throwable rootCause ) {
        super( rootCause );
    }
    
    public EncodingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public EncodingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
