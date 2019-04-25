package io.nayasis.common.basica.exception.unchecked;


/**
 * Runtime {@link #SyntaxException}
 *
 * @author nayasis@gmail.com
 *
 */
public class SyntaxException extends BaseRuntimeException {

    private static final long serialVersionUID = -4074629747093113307L;

    public SyntaxException() {
        super();
    }

    public SyntaxException( Throwable rootCause ) {
        super( rootCause );
    }

    public SyntaxException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public SyntaxException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
