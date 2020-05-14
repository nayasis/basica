package com.github.nayasis.basica.base.format;

import com.github.nayasis.basica.base.Strings;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent=true)
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
            name = String.format( Formatter.FORMAT_INDEX, index );
        }

    }

}
