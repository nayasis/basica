package com.github.nayasis.basica.exception.unchecked;


/**
 * Runtime {@link #ParseException}
 *
 * @author nayasis@gmail.com
 *
 */
public class ParseException extends BaseRuntimeException {

    private static final long serialVersionUID = 2584886845574239228L;

    private Integer lineNumber;
    private Integer columnNumber;

    public ParseException() {
        super();
    }

    public ParseException( Throwable rootCause ) {
        super( rootCause );
    }

    public ParseException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    public ParseException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber( Integer lineNumber ) {
        this.lineNumber = lineNumber;
    }

    public Integer getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber( Integer columnNumber ) {
        this.columnNumber = columnNumber;
    }

}
