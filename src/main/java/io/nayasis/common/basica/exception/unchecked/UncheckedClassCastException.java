package io.nayasis.common.basica.exception.unchecked;


/**
 * Runtime {@link #UncheckedClassCastException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedClassCastException extends BaseRuntimeException {

    private static final long serialVersionUID = 2358838095582158749L;

    /**
     * Constructor
     */
    public UncheckedClassCastException() {
        super();
    }

    /**
     * Constructor
     *
     * @param rootCause root cause
     */
    public UncheckedClassCastException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructor
     *
     * @param format        error message format
     * @param messageParam  parameters binding with '{}' phrase
     */
    public UncheckedClassCastException( String format, Object... messageParam ) {
        super( format, messageParam );
    }

    /**
     * Constructor
     *
     * @param rootCause     root cause
     * @param format        error message format
     * @param messageParam  parameters binding with '{}' phrase
     */
    public UncheckedClassCastException( Throwable rootCause, String format, Object... messageParam ) {
    	super( rootCause, format, messageParam );
    }

}
