package com.github.nayasis.basica.expression;

import com.github.nayasis.basica.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * MVEL expression wrapper
 */
@Slf4j
public class Expression {

    private String       expRaw;
    private byte[]       expZip;
    private Serializable compiled;

    /**
     * constructor
     *
     * @param expression    MVEL expression language
     */
    public Expression( String expression ) {

        expression = Strings.trim( expression );

        byte[] zip = zip( expression );

        if( zip.length < expression.getBytes().length ) {
            expZip = zip;
        } else {
            expRaw = expression;
        }

        compiled = ExpressionCore.compile( expression );

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
        return expZip == null ? expRaw : unzip( expZip );
    }

    private byte[] zip( String value ) {
        try(
            ByteArrayOutputStream out  = new ByteArrayOutputStream();
            GZIPOutputStream      gzip = new GZIPOutputStream( out )
        ) {
            gzip.write( value.getBytes() );
            gzip.close();
            return out.toByteArray();
        } catch( IOException e ) {
            return null;
        }
    }

    private String unzip( byte[] bytes ) {
        try(
            ByteArrayInputStream input  = new ByteArrayInputStream( bytes );
            GZIPInputStream      gzip   = new GZIPInputStream( input );
            BufferedReader       reader = new BufferedReader( new InputStreamReader(gzip) )
        ) {
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach( line -> sb.append( line ) );
            return sb.toString();
        } catch( IOException e ) {
            return null;
        }
    }

}
