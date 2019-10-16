package io.nayasis.common.basica.model;

import io.nayasis.common.basica.base.Strings;
import io.nayasis.common.basica.exception.unchecked.ParseException;
import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;

@UtilityClass
public class NDateParser {

    private final String FULL_FORMAT = "yyyyMMddHHmmssSSS";

    public Date toDate( String date, String format ) throws ParseException {

        try {
            if( Strings.isNotEmpty(format) )
                return parse( date, format );
        } catch ( Exception e ) {}

        format = toDateFormat( format );
        date   = toDigit( date );

        StringBuilder pattern = new StringBuilder();
        StringBuilder value   = new StringBuilder();

        int length = Math.min( format.length(), date.replace("+","").length() );

        for( int i=0, j=0; i < length; i++, j++ ) {

            char f = format.charAt(i);

            try {
                if( f == 'Z' ) {
                    for( int k = 0; k < 5; k++ )
                        value.append( date.charAt(j++) );
                } else {
                    value.append( date.charAt(j) );
                }
            } catch ( IndexOutOfBoundsException e ) {
                throw new ParseException(e);
            }

            pattern.append( f );

        }

        return parse( value.toString(), pattern.toString() );

    }

    private Date parse( String value, String pattern ) throws ParseException {
        try {
            return new SimpleDateFormat( pattern ).parse( value );
        } catch ( java.text.ParseException e ) {
            throw new ParseException(e);
        }
    }

    /**
     * convert date format style from DBMS to JAVA
     * @param format
     * @return Java style format
     */
    private String toDateFormat( String format ) {

        if( Strings.isEmpty(format) ) return FULL_FORMAT;

        format = format
            .replaceAll( "'.*?'", " " ) // remove user text
            .replaceAll( "YYYY", "yyyy" )
            .replaceAll( "(^|[^D])DD([^D]|$)", "$1dd$2" )
            .replaceAll( "MI",     "mm"   )
            .replaceAll( "(^|[^S])SS([^S]|$)", "$1ss$2" )
            .replaceAll( "(^|[^F])FFF([^F]|$)", "$1SSS$2" )
            ;

        return format.replaceAll( "[^yMdHmsSZ]", "" );

    }

    private String toDigit( String date ) {
        return date.replaceAll( "[^0-9\\+]", "" );
    }

}
