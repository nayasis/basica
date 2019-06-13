package io.nayasis.common.basica.base.format;

import io.nayasis.common.basica.base.Strings;

import static io.nayasis.common.basica.base.format.Formatter.FORMAT_INDEX;

public class Key {

    private String name   = "";
    private String format = "";

    public Key( String info, int index ) {

        if( ! info.isEmpty() ) {
            String[] infos = info.split( ":" );
            name = infos[ 0 ];
            if( infos.length >= 2 ) {
                format = infos[ 1 ];
            }
        }

        if( Strings.isEmpty(name) ) {
            name = String.format( FORMAT_INDEX, index );
        }

    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat( String format ) {
        this.format = format;
    }

}
