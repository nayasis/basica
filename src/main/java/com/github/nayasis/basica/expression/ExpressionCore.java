package com.github.nayasis.basica.expression;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.reflection.Reflector;
import com.github.nayasis.basica.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.io.Serializable;

/**
 * MVEL expression core
 */
@Slf4j
public class ExpressionCore {

    private static ParserContext ctx = new ParserContext();

    static {
        ctx.addImport( Strings.class   );
        ctx.addImport( Validator.class );
        ctx.addImport( Reflector.class );
        try {
            ctx.addImport( "nvl", Validator.class.getMethod("nvl",Object.class,Object.class,Object[].class) );
            ctx.addImport( "nvl", Strings.class.getMethod("nvl",Object.class) );
        } catch ( NoSuchMethodException e ) {
            log.error( e.getMessage(), e );
        }
    }

    /**
     * get parser context
     *
     * @return parser context
     */
    public static ParserContext ctx() {
        return ctx;
    }

    /**
     * compile expression
     *
     * @param expression    MVEL expression
     * @return compiled code
     * @throws CompileException if compile error occurs.
     */
    public static Serializable compile( String expression ) throws CompileException {
        return MVEL.compileExpression( expression, ctx );
    }

    /**
     * run compiled expression
     *
     * @param expression    compiled expression
     * @param param         parameter
     * @param <T>           return type
     * @return execution result
     */
    public static <T> T run( Serializable expression, Object param ) {
        Object val = MVEL.executeExpression( expression, param );
        return (val == null) ? null : (T) val;
    }

    /**
     * run compiled expression
     *
     * @param expression    compiled expression
     * @param <T>           return type
     * @return execution result
     */
    public static <T> T run( Serializable expression ) {
        return run( expression, null );
    }

}