package io.nayasis.common.basica.model;

import io.nayasis.common.basica.base.Strings;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Printer to show as grid table
 *
 */
public class NListPrinter {

    private static final int     LIMIT_CNT_toString = 5_000;
    private static final String  NEW_LINE           = "-------------------------------------------------------------";

    private NList nlist = null;

    public NListPrinter( NList nlist ) {
        this.nlist = nlist;
    }

    /**
     * Print NList data
     *
     * @param printHeader if true, print header
     * @param printAllRow if true, print all row
     * @return grid data contents
     */
    public String toString( boolean printHeader, boolean printAllRow ) {

        nlist.refreshKey();

        StringBuilder sb = new StringBuilder();

        if( nlist.keySize() == 0 ) {

            sb.append(NEW_LINE).append( "\n" )
              .append("    NO DATA\n")
              .append(NEW_LINE).append( "\n" );

            return sb.toString();

        }

        Map<Object, Integer> columnWidthList = getColumnWidth();

        String newLine = getNewLine( columnWidthList );

        if( printHeader ) {
            sb.append( newLine );
            printKey( sb, columnWidthList );
            printAlias( sb, columnWidthList );
        }

        sb.append( newLine );
        printData( sb, columnWidthList, printAllRow );
        sb.append( newLine );

        return sb.toString();

    }

    private void printKey( StringBuilder writer, Map<Object, Integer> columnWidthList ) {
        for( Object key : nlist.header.keySet() ) {
            writer.append( "| " );
            writer.append( Strings.displayRpad(key, columnWidthList.get(key), ' ') );
        }
        writer.append( "|\n" );
    }

    /**
     * 별칭 출력
     *
     * @param writer
     * @param columnWidthList
     */
    private void printAlias( StringBuilder writer, Map<Object, Integer> columnWidthList ) {

        // alias 출력여부 체크
        boolean printable = false;

        for( Object key : nlist.header.keySet() ) {
            if( nlist.alias.containsKey(key) ) {
                printable = true;
                break;
            }
        }

        if( ! printable ) return;

        // alias 출력
        for( Object key : nlist.header.keySet() ) {

            writer.append( "| " );

            if( nlist.alias.containsKey(key) ) {
                writer.append( Strings.displayRpad(String.format( "(%s)", nlist.alias.get(key) ), columnWidthList.get(key), ' ') );

            } else {
                writer.append( Strings.displayRpad("", columnWidthList.get(key), ' ') );
            }

        }

        writer.append( "|\n" );

    }



    private void printData( StringBuilder writer, Map<Object, Integer> columnWidthList, boolean printAllRow ) {

        int printCnt = printAllRow ? nlist.size() : Math.min(nlist.size(), LIMIT_CNT_toString);

        for( int i = 0; i < printCnt; i++ ) {

            for( Object key : nlist.header.keySet() ) {
                writer.append("| ");
                writer.append( Strings.displayRpad(getValue(i, key), columnWidthList.get(key), ' ') );
            }

            writer.append( "|\n" );
        }


        if( printCnt < nlist.size() ) {

            String newLine = getNewLine( columnWidthList );
            int    innerLineLength = newLine.length() - 4;

            writer.append( newLine );
            writer.append( "| " ).append( Strings.displayRpad( String.format("Omit [%d] cnt", nlist.size() - printCnt), innerLineLength, ' ') ).append( "|\n" );
            writer.append( "| " ).append( Strings.displayRpad("If you want to see all,",      innerLineLength, ' ') ).append( "|\n" );
            writer.append( "| " ).append( Strings.displayRpad("Use toDebugString() instead.", innerLineLength, ' ') ).append( "|\n" );

        }

    }

    private Object getValue(int i, Object key) {

        Object val = nlist.get(key, i);

        if( val instanceof Map ) {
            if( ((Map) val ).isEmpty() ) return "{-}";
        }

        return nlist.get(key, i);
    }

    private String getNewLine( Map<Object, Integer> columnWidthList ) {

        StringBuffer sb = new StringBuffer();

        for( Object key : columnWidthList.keySet() ) {
            for( int i = 0, iCnt = columnWidthList.get(key) ; i < iCnt; i++ ) {
                sb.append( '-' );
            }
            sb.append( "--" );
        }

        sb.append( "-\n" );

        return sb.toString();

    }

    private Map<Object, Integer> getColumnWidth() {

        Map<Object, Integer> list = new LinkedHashMap<Object, Integer>();

        for( Object key : nlist.keySet() ) {

            int columnWidth = 0;

            columnWidth = Math.max( columnWidth, Strings.getDisplayLength( key ) );
            columnWidth = Math.max( columnWidth, Strings.getDisplayLength( nlist.alias.get( key ) ) ) + 2; // (%s)

            for( NMap row : nlist.body ) {
                columnWidth = Math.max(  columnWidth, Strings.getDisplayLength( row.get( key ) ) );
            }

            list.put( key, columnWidth );

        }

        return list;

    }

}