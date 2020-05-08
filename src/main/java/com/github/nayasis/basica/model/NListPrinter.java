package com.github.nayasis.basica.model;

import com.github.nayasis.basica.base.Characters;
import com.github.nayasis.basica.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Printer to show as grid table
 *
 */
@Slf4j
public class NListPrinter {

    private NList list;

    private int showCount      = 500;
    private int maxColumnWidth = 255;

    /**
     * constructor
     *
     * @param list  data to print
     */
    public NListPrinter( NList list ) {
        this.list = list;
    }

    /**
     * get row counts to print
     *
     * @return  row counts
     */
    public int showCounts() {
        return showCount;
    }

    /**
     * set row counts to print
     *
     * @param showCount counts to print (default: 500)
     * @return self
     */
    public NListPrinter showCounts( int showCount ) {
        if( showCount > 0 )
            this.showCount = showCount;
        return this;
    }

    /**
     * get max column width to print
     * @return max column width
     */
    public int maxColumnWidth() {
        return maxColumnWidth;
    }

    /**
     * set max column width to print
     *
     * @param maxColumnWidth    width (default: 255)
     * @return
     */
    public NListPrinter maxColumnWidth( int maxColumnWidth ) {
        if( maxColumnWidth > 0 )
            this.maxColumnWidth = maxColumnWidth;
        return this;
    }

    /**
     * convert to string
     *
     * @param header    if true, print header
     * @param alias     if true, print alias
     * @return grid data contents
     */
    public String toString( boolean header, boolean alias ) {
        return toPrintable( list, header, alias ).toString();
    }

    private Printable toPrintable( NList data, boolean includeHeader, boolean includeAlias ) {

        Printable p = new Printable();

        if( data.body.isEmpty() && ! data.keySet().isEmpty() ) {
            data = data.clone();
            data.addData( data.getKey( 0), Printable.MESSAGE_NO_DATA );
        }

        for( Object key : data.keySet() ) {

            int width = 0;

            if( includeHeader ) {
                String header = toDisplayString( key );
                p.header.put( key, header );
                width = Math.max( width, Strings.getDisplayLength(header) );
            }

            if( includeAlias && data.hasAlias() ) {
                String alias = toDisplayString( data.getAlias( key ) );
                p.alias.put( key, alias );
                width = Math.max( width, Strings.getDisplayLength(alias) );
            }

            p.width.put( key, width );

        }

        for( int i = 0, iCnt = Math.min( showCounts(),data.size()); i < iCnt; i++ ) {

            NMap row = data.getRow( i );
            Map<Object,String> map = new HashMap<>();

            for( Object key : data.keySet() ) {

                String txt = toDisplayString( row.get(key) );
                map.put( key, txt );

                int width = Math.max( p.width.get(key), Strings.getDisplayLength(txt) );
                p.width.put( key, width );

            }

            p.body.add( map );

        }

        return p;

    }

    private String toDisplayString( Object val ) {

        String txt = toString( val );

        if( txt.length() > maxColumnWidth() ) {
            txt = txt.substring( 0, maxColumnWidth() );
        }

        if( ! Characters.isFontWidthModified() ) return txt;

        double count = 0; StringBuilder sb = new StringBuilder();

        for( int i = 0, iCnt = txt.length(); i < iCnt; i++ ) {
            char c = txt.charAt( i );
            count += Characters.getFontWidth( c );
            if( count > maxColumnWidth() ) {
                return sb.toString();
            }
            sb.append( c );
        }

        return sb.toString();

    }

    private String toString( Object val ) {
        if( val == null ) return "";
        if( val instanceof Map ) {
            if( ((Map) val ).isEmpty() ) return "{}";
        } else if( val instanceof Collection ) {
            if( ((Collection)val).isEmpty() ) return "[]";
        }
        return val.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");
    }

    private class Printable {

        private static final String MESSAGE_NO_DATA = "NO DATA";

        List<Map<Object,String>> body   = new ArrayList<>();
        Map<Object,Integer>      width  = new LinkedHashMap<>();
        Map<Object,String>       header = new HashMap<>();
        Map<Object,String>       alias  = new HashMap<>();

        public String getNewLine() {

            StringBuffer sb = new StringBuffer();
            sb.append( '+' );

            if( width.isEmpty() ) {
                sb.append( Strings.line('-', MESSAGE_NO_DATA.length() + 2 ) ).append( '+' );
            } else {
                for( Object key : width.keySet() ) {
                    sb.append( Strings.line('-', width.get(key) + 2 ) ).append( '+' );
                }
            }

            return sb.toString();

        }

        public Set<Object> keySet() {
            return width.keySet();
        }

        public String toString() {

            StringBuilder sb = new StringBuilder();

            String newline = getNewLine();

            if( ! header.isEmpty() ) {
                sb.append( newline ).append( '\n' );
                for ( Object key : keySet() ) {
                    sb.append( wrap( header.get(key), width.get(key) ) );
                }
                sb.append( "|\n" );

            }

            if( ! alias.isEmpty() ) {

                sb.append( newline ).append( '\n' );

                for( Object key : keySet() ) {
                    sb.append( wrap( alias.get(key), width.get(key) ) );
                }
                sb.append( "|\n" );

            }

            sb.append( newline ).append( '\n' );

            if( body.isEmpty() ) {
                sb.append( wrap(MESSAGE_NO_DATA) ).append( "|\n" );
            } else {
                for( Map<Object,String> row : body ) {
                    for( Object key : keySet() ) {
                        sb.append( wrap( row.get(key), width.get(key) ) );
                    }
                    sb.append( "|\n" );
                }
            }

            sb.append( newline );

            return sb.toString();

        }

        private StringBuilder wrap( String value ) {
            return wrap( value, value.length() );
        }

        private StringBuilder wrap( String value, int length ) {
            StringBuilder sb = new StringBuilder();
            sb.append( '|' ).append( ' ' );
            sb.append( Strings.dprpad( value, length, ' ' ) );
            sb.append( ' ' );
            return sb;
        }

    }

}
