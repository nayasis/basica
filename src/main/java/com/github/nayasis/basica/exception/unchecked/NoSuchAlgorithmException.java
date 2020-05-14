package com.github.nayasis.basica.exception.unchecked;


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

    public NoSuchAlgorithmException( Throwable cause ) {
        super( cause );
    }

    public NoSuchAlgorithmException( String message, Object... param ) {
        super( message, param );
    }

    public NoSuchAlgorithmException( Throwable cause, String message, Object... param ) {
    	super( cause, message, param );
    }

}
