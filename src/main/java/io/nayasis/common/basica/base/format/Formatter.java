package io.nayasis.common.basica.base.format;

import io.nayasis.common.basica.base.Characters;
import io.nayasis.common.basica.base.Strings;
import io.nayasis.common.basica.base.Types;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * String formatter
 */
public class Formatter {

    public static final ExtractPattern PATTERN_BASIC  = new ExtractPattern( "(^|[^\\\\])\\{(|.+?[^\\\\])\\}",    2, "\\\\(\\{|\\})",     "$1" );
    public static final ExtractPattern PATTERN_SHARP  = new ExtractPattern( "(^|[^\\\\])#\\{(|.+?[^\\\\])\\}",   2, "\\\\(#|\\{|\\})",   "$1" );
    public static final ExtractPattern PATTERN_DOLLAR = new ExtractPattern( "(^|[^\\\\])\\$\\{(|.+?[^\\\\])\\}", 2, "\\\\(\\$|\\{|\\})", "$1" );
    public static final String         FORMAT_INDEX   = "_{{%d}}";

    /**
     * return formatted string binding parameters
     *
     * @param format                format string
     * @param parameter             binding parameter
     * @param binder                binder containing binding logic
     * @param <T>
     * @return formatter string
     */
    public <T> String bindParam( ExtractPattern pattern, Object format, T parameter, ParameterBinder<T> binder ) {
        return bindParam( pattern, format, parameter, binder, false );
    }

    /**
     * return formatted string binding parameters
     *
     * @param format                format string
     * @param parameter             binding parameter
     * @param binder                binder containing binding logic
     * @param koreanModification    flag whether modify korean JOSA characters
     * @param <T>
     * @return formatter string
     */
    public <T> String bindParam( ExtractPattern pattern, Object format, T parameter, ParameterBinder<T> binder, boolean koreanModification ) {

        String source = Strings.nvl( format );

        if( source.isEmpty() ) return source;

        Matcher matcher = pattern.getPattern().matcher( source );

        StringBuilder sb = new StringBuilder();

        int cursor = 0;
        int index  = 0;

        while( matcher.find() ) {

            Key key = new Key( matcher.group(pattern.getTargetGroup()), index );

            sb.append( removeEscapeParamTag(pattern,source.substring(cursor, matcher.start())) );

            for( int i = 1; i < pattern.getTargetGroup(); i++ ) {
                sb.append( matcher.group(i) );
            }

            String val = binder.bind( key.getName(), key.getFormat(), parameter );

            sb.append( val );

            for( int i = pattern.getTargetGroup() + 1; i <= matcher.groupCount(); i++ ) {
                sb.append( matcher.group(i) );
            }

            index++;

            cursor = matcher.end();

            if( koreanModification ) {
                if( modifyKorean(val, cursor, sb, source) ) {
                    cursor++;
                    continue;
                }
            }

        }

        sb.append( removeEscapeParamTag(pattern,source.substring(cursor)) );

        return sb.toString();

    }

    private String removeEscapeParamTag( ExtractPattern pattern, String val ) {
        return pattern.getRecoveryPattern().matcher( val ).replaceAll( pattern.getRecoveryReplacer() );
    }

    private boolean modifyKorean( String val, int cursor, StringBuilder buffer, String source ) {

        if( Strings.isEmpty(val) || cursor >= source.length() ) return false;

        boolean hasJongsong = Characters.hasHangulJongsung( val.charAt( val.length() - 1 ) );

        if( hasJongsong ) {
            char josa = source.charAt( cursor );
            switch ( josa ) {
                case '은' : case '는' :
                    buffer.append( hasJongsong ? '은' : '는' ); return true;
                case '이' : case '가' :
                    buffer.append( hasJongsong ? '이' : '가' ); return true;
                case '을' : case '를' :
                    buffer.append( hasJongsong ? '을' : '를' ); return true;
            }
        }

        return false;

    }

    public String format( Object format, Object... parameter ) {

        if( parameter.length == 0 ) return Strings.nvl( format );

        Map params;

        if( parameter.length == 1 && Types.isMap(parameter[0]) ) {
            params = (Map) parameter[0];
        } else {
            params = new HashMap();
            int index = 0;
            for( Object param : parameter ) {
                params.put( String.format(FORMAT_INDEX, index++), param );
            }
        }

        return bindParam( PATTERN_BASIC, format, params, (key, userFormat, param) -> {

            Object val = param.get( key );

            if( userFormat.isEmpty() ) {
                return val == null ? null : val.toString();
            } else {
                return String.format( userFormat, val );
            }

        }, true );

    }

}
