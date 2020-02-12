package io.nayasis.basica.base.format;

import io.nayasis.basica.base.Characters;
import io.nayasis.basica.base.Strings;
import io.nayasis.basica.base.Types;
import io.nayasis.basica.reflection.Reflector;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * String formatter
 */
public class Formatter {

    public static final ExtractPattern PATTERN_BASIC  = new ExtractPattern( "(^|[^\\\\])\\{([^\\s]*?)(|[^\\\\])\\}",    new int[]{2,3}, "\\\\(\\{|\\})",     "$1" );
    public static final ExtractPattern PATTERN_SHARP  = new ExtractPattern( "(^|[^\\\\])#\\{([^\\s]*?)(|[^\\\\])\\}",   new int[]{2,3}, "\\\\(#|\\{|\\})",   "$1" );
    public static final ExtractPattern PATTERN_DOLLAR = new ExtractPattern( "(^|[^\\\\])\\$\\{([^\\s]*?)(|[^\\\\])\\}", new int[]{2,3}, "\\\\(\\$|\\{|\\})", "$1" );

    protected static final String FORMAT_INDEX = "_{{%d}}";

    /**
     * return binding parameters in string formatted
     *
     * @param pattern   parameter extracting pattern
     * @param format    format string
     * @param parameter binding parameter
     * @param binder    binder containing binding logic
     * @param <T> This is the type parameter
     * @return formatter string
     */
    public <T> String bindParam( ExtractPattern pattern, Object format, T parameter, ParameterBinder<T> binder ) {
        return bindParam( pattern, format, parameter, binder, false );
    }

    /**
     * return binding parameters in string formatted
     *
     * @param pattern               parameter extracting pattern
     * @param format                format string
     * @param parameter             binding parameter
     * @param binder                binder containing binding logic
     * @param koreanModification    flag whether modify korean JOSA characters
     * @param <T> This is the type parameter
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

            Key key = new Key( getDefinition(matcher,pattern), index );

            sb.append( removeEscapeParamTag(pattern,source.substring(cursor, matcher.start())) );

            String value = null;

            for( int i = 1, iCnt = matcher.groupCount(); i <= iCnt; i++ ) {

                if( pattern.getTargetGroups().contains(i) ) {
                    if( value == null ) {
                        value = binder.bind( key.getName(), key.getFormat(), parameter );
                        sb.append( value );
                    }
                } else {
                    sb.append( matcher.group(i) );
                }

            }

            index++;

            cursor = matcher.end();

            if( koreanModification ) {
                if( modifyKorean(value, cursor, sb, source) ) {
                    cursor++;
                    continue;
                }
            }

        }

        sb.append( removeEscapeParamTag(pattern,source.substring(cursor)) );

        return sb.toString();

    }

    private String getDefinition( Matcher matcher, ExtractPattern pattern ) {
        StringBuilder sb = new StringBuilder();
        for( int i : pattern.getTargetGroups() ) {
            sb.append( matcher.group(i) );
        }
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

        // when null parameter inputted
        if( parameter == null ) {
            parameter = new Object[] { null };
        }

        if( parameter.length == 0 ) return Strings.nvl( format );

        return bindParam( PATTERN_BASIC, format, toParam(parameter), (key, userFormat, param) -> {

            Object val = param.get( key );

            if( userFormat.isEmpty() ) {
                return val == null ? null : val.toString();
            } else {
                return String.format( userFormat, val );
            }

        }, true );

    }

    private Map toParam( Object ... parameters ) {

        Map params = new HashMap();

        if( parameters.length == 1 ) {
            if ( Types.isMap(parameters[0]) ) {
                params.putAll( (Map) parameters[0] );
            } else if ( ! Types.isImmutable(parameters[0]) ) {
                try {
                    params.putAll( Reflector.toMapFrom(parameters[0]) );
                } catch ( Exception e ) {}
            }
        }

        int index = 0;
        for( Object param : parameters ) {
            params.put( String.format(FORMAT_INDEX, index++), param );
        }
        return params;

    }

}
