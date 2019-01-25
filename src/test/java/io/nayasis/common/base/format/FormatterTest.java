package io.nayasis.common.base.format;

import io.nayasis.common.model.NMap;
import lombok.ToString;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.nayasis.common.base.format.Formatter.PATTERN_DOLLAR;
import static io.nayasis.common.base.format.Formatter.PATTERN_SHARP;

public class FormatterTest {

    Formatter formatter = new Formatter();

    @Test
    public void bindParamViaDollar() {

        NMap parameter = new NMap( "{'name':'abc', 'age':2}" );

        Result r1 = bind( PATTERN_DOLLAR,"PRE ${age} POST", parameter );
        Result r2 = bind( PATTERN_DOLLAR,"PRE \\${age} POST", parameter );
        Result r3 = bind( PATTERN_DOLLAR,"${name} PRE ${age} POST", parameter );
        Result r4 = bind( PATTERN_DOLLAR,"${name} PRE ${age:%3d} POST", parameter );

        Assert.assertEquals( "PRE ? POST",      r1.string );
        Assert.assertEquals( "PRE ${age} POST", r2.string );
        Assert.assertEquals( "? PRE ? POST",    r3.string );
        Assert.assertEquals( "? PRE ? POST",    r4.string );

        Assert.assertEquals( "[2]",      r1.params.toString() );
        Assert.assertEquals( "[]",       r2.params.toString() );
        Assert.assertEquals( "[abc, 2]", r3.params.toString() );
        Assert.assertEquals( "[abc, 2]", r4.params.toString() );

    }

    @Test
    public void bindParamViaSharp() {

        NMap parameter = new NMap( "{'name':'abc', 'age':2}" );

        Result r1 = bind( PATTERN_SHARP,"PRE #{age} POST", parameter );
        Result r2 = bind( PATTERN_SHARP,"PRE \\#{age} POST", parameter );
        Result r3 = bind( PATTERN_SHARP,"#{name} PRE #{age} POST", parameter );
        Result r4 = bind( PATTERN_SHARP,"#{name} PRE #{age:%3d} POST", parameter );

        Assert.assertEquals( "PRE ? POST",      r1.string );
        Assert.assertEquals( "PRE #{age} POST", r2.string );
        Assert.assertEquals( "? PRE ? POST",    r3.string );
        Assert.assertEquals( "? PRE ? POST",    r4.string );

        Assert.assertEquals( "[2]",      r1.params.toString() );
        Assert.assertEquals( "[]",       r2.params.toString() );
        Assert.assertEquals( "[abc, 2]", r3.params.toString() );
        Assert.assertEquals( "[abc, 2]", r4.params.toString() );

    }


    private Result bind( ExtractPattern pattern, String format, Map parameter ) {

        Result res = new Result();

        res.string = formatter.bindParam( pattern, format, parameter, ( key, userFormat, param ) -> {

            Object val = param.get( key );

            res.params.add( val );

            return "?";

        }, false );

        return res;

    }

    @ToString
    private static class Result {
        public String       string;
        public List<Object> params = new ArrayList<>();
    }

}