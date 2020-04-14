package io.nayasis.basica.base.format;

import io.nayasis.basica.base.Characters;
import io.nayasis.basica.base.Strings;
import io.nayasis.basica.base.Types;
import io.nayasis.basica.base.format.function.Replacer;
import io.nayasis.basica.reflection.Reflector;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * String formatter
 */
public class Formatter {

    private static final Replacer bracketCompressor = text -> text.replaceAll( "\\{\\{", "{" ).replaceAll( "\\}\\}", "}" );

    public static final ExtractPattern PATTERN_BASIC  = new ExtractPattern( "\\{([^\\s\\{\\}]*?)\\}"    ).replacer(bracketCompressor).escapeChar('{');
    public static final ExtractPattern PATTERN_SHARP  = new ExtractPattern( "#\\{([^\\s\\{\\}]*?)\\}"   ).replacer(bracketCompressor);
    public static final ExtractPattern PATTERN_DOLLAR = new ExtractPattern( "\\$\\{([^\\s\\{\\}]*?)\\}" ).replacer(bracketCompressor);

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

        Matcher matcher = pattern.pattern().matcher( source );

        StringBuilder sb = new StringBuilder();

        int cursor = 0;
        int index  = 0;

        while( matcher.find() ) {

            String prefix = source.substring( cursor, matcher.start() );

            if( pattern.isEscapable(prefix) ) {
                continue;
            }

            sb.append( pattern.replacer().replace(prefix) );

            Key    key   = new Key( matcher.group(1), index );
            String value = binder.bind( key.name(), key.format(), parameter );

            sb.append( value );

            index++;
            cursor = matcher.end();

            if( koreanModification ) {
                if( modifyKorean(value, cursor, sb, source) ) {
                    cursor++;
                }
            }

        }

        // add remains
        sb.append( pattern.replacer().replace(source.substring(cursor)) );

        return sb.toString();

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

            Object  val   = param.get( key );
            boolean exist = param.containsKey( key );

            if( userFormat.isEmpty() ) {
                if( val == null ) {
                    return exist ? null : "";
                } else {
                    return val.toString();
                }
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
