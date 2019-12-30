package io.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #CommandLineException}
 *
 * @author nayasis@gmail.com
 *
 */
public class CommandLineException extends BaseRuntimeException {

    private static final long serialVersionUID = -1677096149188759595L;

    public CommandLineException() {
        super();
    }

    public CommandLineException( Throwable rootCause ) {
        super( rootCause );
    }

    public CommandLineException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public CommandLineException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
