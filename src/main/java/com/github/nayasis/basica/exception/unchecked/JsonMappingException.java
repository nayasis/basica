package com.github.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #JsonMappingException}
 *
 * @author nayasis@gmail.com
 *
 */
public class JsonMappingException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    public JsonMappingException() {
        super();
    }

    public JsonMappingException( Throwable rootCause ) {
        super( rootCause );
    }

    public JsonMappingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public JsonMappingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
