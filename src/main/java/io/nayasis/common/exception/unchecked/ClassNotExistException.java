package io.nayasis.common.exception.unchecked;


/**
 * Runtime Exception for ClassNotFound
 *
 * @author nayasis@gmail.com
 *
 */
public class ClassNotExistException extends BaseRuntimeException {

    private static final long serialVersionUID = 2358838095582158749L;

    /**
     * Constructor
     */
    public ClassNotExistException() {
        super();
    }

    /**
     * Constructor
     *
     * @param rootCause root cause
     */
    public ClassNotExistException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructor
     *
     * @param format        error message format
     * @param messageParam  parameters binding with '{}' phrase
     */
    public ClassNotExistException( String format, Object... messageParam ) {
        super( format, messageParam );
    }

    /**
     * Constructor
     *
     * @param rootCause     root cause
     * @param format        error message format
     * @param messageParam  parameters binding with '{}' phrase
     */
    public ClassNotExistException( Throwable rootCause, String format, Object... messageParam ) {
    	super( rootCause, format, messageParam );
    }

}
