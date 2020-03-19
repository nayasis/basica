package io.nayasis.basica.exception.unchecked;

import io.nayasis.basica.base.Strings;

import java.io.Serializable;


/**
 * Base RuntimeException
 *
 * @author nayasis@gmail.com
 *
 */
public class BaseRuntimeException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -7789633699277626783L;

    /**
     * Error Code
     */
    private String errorCode = "";

    public BaseRuntimeException() {
        super();
    }

    /**
     * Constructor
     *
     * @param rootCause root cause
     */
    public BaseRuntimeException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructor
     *
     * @param format      error message format
     * @param parameters  parameters binding with '{}' phrase
     */
    public BaseRuntimeException( String format, Object... parameters ) {
        super( Strings.format(format, parameters) );
    }

    /**
     * Constructor
     *
     * @param rootCause   root cause
     * @param format      error message format
     * @param parameters  parameters binding with '{}' phrase
     */
    public BaseRuntimeException( Throwable rootCause, String format, Object... parameters ) {
        super( Strings.format(format, parameters), rootCause );
    }

    /**
     * get error code
     *
     * @return String error code
     */
    public String errorCode() {
    	return errorCode;
    }

    /**
     * set error code
     *
     * @param errorCode error code
     */
    public void errorCode( Object errorCode ) {
        this.errorCode = Strings.nvl( errorCode );
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if( ! Strings.isEmpty(errorCode) ) {
            sb.append( '[' ).append( errorCode ).append( ']' );
        }
        sb.append( super.getMessage() );
        return sb.toString();
    }

}
