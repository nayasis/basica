package io.nayasis.common.basica.exception.unchecked;


/**
 * Runtime {@link #UncheckedClassNotFoundException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedClassNotFoundException extends BaseRuntimeException {

    private static final long serialVersionUID = 2358838095582158749L;

    /**
     * Constructor
     */
    public UncheckedClassNotFoundException() {
        super();
    }

    /**
     * Constructor
     *
     * @param rootCause root cause
     */
    public UncheckedClassNotFoundException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructor
     *
     * @param format        error message format
     * @param messageParam  parameters binding with '{}' phrase
     */
    public UncheckedClassNotFoundException( String format, Object... messageParam ) {
        super( format, messageParam );
    }

    /**
     * Constructor
     *
     * @param rootCause     root cause
     * @param format        error message format
     * @param messageParam  parameters binding with '{}' phrase
     */
    public UncheckedClassNotFoundException( Throwable rootCause, String format, Object... messageParam ) {
    	super( rootCause, format, messageParam );
    }

}
