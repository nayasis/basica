package com.github.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #UncheckedFileNotFoundException}
 *
 * @author nayasis@gmail.com
 *
 */
public class UncheckedFileNotFoundException extends BaseRuntimeException {

    private static final long serialVersionUID = 2310297636395500566L;

    public UncheckedFileNotFoundException() {
        super();
    }

    public UncheckedFileNotFoundException(Throwable rootCause ) {
        super( rootCause );
    }

    public UncheckedFileNotFoundException(String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public UncheckedFileNotFoundException(Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
