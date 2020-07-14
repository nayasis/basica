package com.github.nayasis.basica.expression;

import com.github.nayasis.basica.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * MVEL expression wrapper
 */
@Slf4j
public class Expression {

    private String       raw;
    private Serializable compiled;

    public static Expression of( String expression ) {
        return new Expression( expression );
    }

    /**
     * constructor
     *
     * @param expression    MVEL expression language
     * @see <a href="http://mvel.documentnode.com/#basic-syntax">MVEL language guide</a>
     */
    public Expression( String expression ) {
        expression = Strings.trim( expression );
        raw        = expression;
        compiled   = ExpressionCore.compile( expression );
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
        return raw;
    }

}