package com.github.nayasis.basica.expression;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.Serializable;


@Slf4j
public class ExpressionCoreTest {

    @Test
    public void simple() {

        Assertions.assertEquals( 21, (int) run( " 3 * 7" ) );
        Assertions.assertTrue( (boolean) run( "name == 'nayasis' && age == 40 && address == empty", param() ) );
        Assertions.assertFalse( (boolean) run( "name == 'nayasis' && age == 40 && address != empty", param() ) );

    }

    @Test
    public void contains() {
        Assertions.assertTrue( (boolean) run( "['nayasis','jake'].contains(name)", param() ) );
    }

    @Test
    public void like() {
        Assertions.assertTrue( (boolean) run( "name.matches('.+?sis$')", param() ) );
    }

    @Test
    public void nvl() {
        Assertions.assertEquals( "default", run( "Strings.nvl(address,'default')", param() ) );
        Assertions.assertEquals( "default", run( "nvl(address,'default')", param() ) );
        Assertions.assertEquals( "", run( "nvl(address)", param() ) );
    }

    @Test
    public void typecast() {
        Assertions.assertTrue( (boolean) run( "1 == '1'" ) );
        Assertions.assertTrue( (boolean) run( "1 + (2 * 3) == '7'" ) );
        Assertions.assertTrue( (boolean) run( "1 + 'a' == '1a'" ) );
        Assertions.assertFalse( (boolean) run( "1 + '2' == '3'" ) );
        Assertions.assertTrue( (boolean) run( "1 + (int)'2' == '3'" ) );
    }

    private <T> T run( String expression, Object param ) {
        Serializable exp = ExpressionCore.compile( expression );
        return ExpressionCore.run( exp, param );
    }

    private <T> T run( String expression ) {
        return run( expression, null );
    }

    private Person param() {
        return new Person().name( "nayasis" ).age( 40 ).job( "engineer" );
    }

    @Data
    @Accessors(fluent=true)
    public static class Person {
        private String name;
        private int    age;
        private String job;
        private String address;
        private Person child;
    }

}