package io.nayasis.basica.model;

import io.nayasis.basica.base.Characters;
import io.nayasis.basica.base.Strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Printer to show as grid table
 *
 */
public class NListPrinter {

    private static final int    LIMIT_CNT           = 500;
    private static final int    MAX_COLUMN_LENGTH   = 255;
    private static final String NEW_LINE            = "-------------------------------------------------------------";
    public static final String MESSAGE_NO_DATA = "NO DATA";

    private NList nlist;

    public NListPrinter( NList nlist ) {
        this.nlist = nlist;
    }

    /**
     * convert too String
     *
     * @param header    if true, print header
     * @param all       if true, print all row
     * @return grid data contents
     */
    public String toString( boolean header, boolean all ) {
        nlist.refreshKey();
        return toPrintable( nlist, header, header, all ? Integer.MAX_VALUE : LIMIT_CNT ).toString();
    }

    private Printable toPrintable( NList data, boolean includeHeader, boolean includeAlias, int count ) {

        Printable p = new Printable();

        for( Object key : data.keySet() ) {

            int width = 0;

            if( includeHeader ) {
                String header = toDisplayString( key );
                p.header.put( key, header );
                width = Math.max( width, header.length() );
            }

            if( includeAlias && data.hasAlias() ) {
                String alias = toDisplayString( data.getAlias( key ) );
                p.alias.put( key, alias );
                width = Math.max( width, alias.length() );
            }

            p.width.put( key, width );

        }

        for( int i = 0, iCnt = Math.min(count,data.size()); i < iCnt; i++ ) {

            NMap row = data.getRow( i );
            Map<Object,String> map = new HashMap<>();

            for( Object key : data.keySet() ) {

                String txt = toDisplayString( row.get(key) );
                map.put( key, txt );

                int width = Math.max( p.width.get(key), txt.length() );
                p.width.put( key, width );

            }

            p.body.add( map );

        }

        return p;

    }

    private String toDisplayString( Object val ) {

        if( val == null ) return "";
        if( val instanceof Map ) {
            if( ((Map) val ).isEmpty() ) return "{{}}";
        }

        String txt = val.toString().replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r");

        if( txt.length() > MAX_COLUMN_LENGTH ) {
            txt = txt.substring( 0, MAX_COLUMN_LENGTH );
        }

        if( ! Characters.isFontWidthModified() ) return txt;

        double count = 0; StringBuilder sb = new StringBuilder();

        for( int i = 0, iCnt = txt.length(); i < iCnt; i++ ) {
            char c = txt.charAt( i );
            count += Characters.getFontWidth( c );
            if( count > MAX_COLUMN_LENGTH ) {
                return sb.toString();
            }
            sb.append( c );
        }

        return sb.toString();

    }

    private class Printable {

        List<Map<Object,String>> body   = new ArrayList<>();
        Map<Object,Integer>      width  = new LinkedHashMap<>();
        Map<Object,String>       header = new HashMap<>();
        Map<Object,String>       alias  = new HashMap<>();

        public String getNewLine() {

            StringBuffer sb = new StringBuffer();
            sb.append( '+' );

            if( width.isEmpty() ) {

                for( int i = 0, iCnt = MESSAGE_NO_DATA.length() + 2; i < iCnt; i++ ) {
                    sb.append( '-' );
                }

                sb.append( '+' );

            } else {
                for( Object key : width.keySet() ) {
                    for( int i = 0, iCnt = width.get(key) ; i <= iCnt; i++ ) {
                        sb.append( '-' );
                    }
                    sb.append( '+' );
                }
            }


            sb.append( '\n' );

            return sb.toString();

        }

        public Set<Object> keySet() {
            return width.keySet();
        }


        public String toString() {

            StringBuilder sb = new StringBuilder();

            String newline = getNewLine();

            if( ! header.isEmpty() ) {

                sb.append( newline );

                for( Object key : keySet() ) {
                    sb.append( "| " );
                    sb.append( Strings.dprpad( header.get(key), width.get(key), ' ' ) );
                }
                sb.append( "|\n" );

            }

            if( ! alias.isEmpty() ) {

                sb.append( newline );

                for( Object key : keySet() ) {
                    sb.append( "| " );
                    sb.append( Strings.dprpad( alias.get(key), width.get(key), ' ' ) );
                }
                sb.append( "|\n" );

            }

            sb.append( newline );

            if( body.isEmpty() ) {
                sb.append( "| " );
                sb.append( Strings.dprpad( MESSAGE_NO_DATA, newline.length() - 4, ' ' ) );
                sb.append( "|\n" );
            } else {
                for( Map<Object,String> row : body ) {
                    for( Object key : keySet() ) {
                        sb.append( "| " );
                        sb.append( Strings.dprpad( row.get(key), width.get(key), ' ' ) );
                    }
                    sb.append( "|\n" );
                }
            }

            sb.append( newline );

            return sb.toString();


        }

    }

}
