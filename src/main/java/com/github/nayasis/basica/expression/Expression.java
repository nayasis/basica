package com.github.nayasis.basica.expression;

import com.github.nayasis.basica.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.compiler.CompiledExpression;

import java.io.Serializable;

/**
 * MVEL expression wrapper
 */
@Slf4j
public class Expression {

    private String       raw;
    private Serializable compiled;

    /**
     * obtains an instance of {@code Expression}
     *
     * @param expression    MVEL expression language
     * @see <a href="http://mvel.documentnode.com/#basic-syntax">MVEL language guide</a>
     */
    public static Expression of( String expression ) {
        return new Expression( expression );
    }

    /**
     * obtains an instance of {@code Expression}
     *
     * @param expression    MVEL expression language
     * @param preserve      preserve original expression
     * @see <a href="http://mvel.documentnode.com/#basic-syntax">MVEL language guide</a>
     */
    public static Expression of( String expression, boolean preserve ) {
        return new Expression( expression, preserve );
    }

    /**
     * constructor
     *
     * @param expression    MVEL expression language
     * @see <a href="http://mvel.documentnode.com/#basic-syntax">MVEL language guide</a>
     */
    public Expression( String expression ) {
        this( expression, false );
    }

    /**
     * constructor
     *
     * @param expression    MVEL expression language
     * @param preserve      preserve original expression
     * @see <a href="http://mvel.documentnode.com/#basic-syntax">MVEL language guide</a>
     */
    public Expression( String expression, boolean preserve ) {
        expression = Strings.trim( expression );
        compiled   = ExpressionCore.compile( expression );
        if( preserve )
            raw = expression;
    }

    /**
     * run expression
     * @param param  parameter
     * @param <T>    return type
     * @return execution result
     */
    public <T> T run( Object param ) {
        return ExpressionCore.run( compiled, param );
    }

    /**
     * run expression
     * @param <T>    return type
     * @return execution result
     */
    public <T> T run() {
        return ExpressionCore.run( compiled );
    }

    /**
     * test expression
     * @param param  parameter
     * @return execution result
     */
    public boolean test( Object param ) {
        return run( param );
    }

    /**
     * test expression
     * @return execution result
     */
    public boolean test() {
        return run();
    }

    public String toString() {
        if( raw != null ) return raw;
        if( compiled instanceof CompiledExpression ) {
            try {
                return new String( ((CompiledExpression) compiled).getFirstNode().getExpr() );
            } catch ( Exception e ) {
                return compiled.toString();
            }
        } else {
            return compiled.toString();
        }
    }

}