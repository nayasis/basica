package com.github.nayasis.basica.expression;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.model.NMap;
import com.github.nayasis.basica.reflection.Reflector;
import com.github.nayasis.basica.validation.Validator;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

import java.io.Serializable;
import java.util.Map;

@Slf4j
public class MvelTest {

    @Test
    public void test() {

        Person p = param();

        Map context = new NMap( p );

        VariableResolverFactory functionFactory = new MapVariableResolverFactory( context );

        String expression = " name == 'nayasis' && age == 40 && address == empty";

        Boolean result = (Boolean) MVEL.eval( expression, functionFactory );

        log.debug( "result : {}", result );


    }

    @Test
    public void simple() {

        String expression = " name == 'nayasis' && age == 40 && address == empty";

        Serializable compiled = MVEL.compileExpression( expression );

        Object o = MVEL.executeExpression( compiled, param() );

        log.debug( "result : {}", o );

    }

    @Test
    public void contains() {

        String expression = " ['nayasis','jake'].contains(name) ";

        Serializable compiled = MVEL.compileExpression( expression );

        Object o = MVEL.executeExpression( compiled, param() );

        log.debug( "result : {}", o );

    }

    @Test
    public void like() {

        String expression = " name.matches('.+?sis$') ";

        Serializable compiled = MVEL.compileExpression( expression );

        Object o = MVEL.executeExpression( compiled, param() );

        log.debug( "result : {}", o );

    }

    @Test
    public void nvl() throws NoSuchMethodException {

        ParserContext ctx = new ParserContext();
        ctx.addImport( Strings.class   );
        ctx.addImport( Validator.class );
        ctx.addImport( Reflector.class );
//        ctx.addPackageImport( Strings.class.getPackage().getName() );
//        ctx.addPackageImport( Validator.class.getPackage().getName() );
//        ctx.addPackageImport( Reflector.class.getPackage().getName() );
//        ctx.addImport( "Validator", Validator.class );
        ctx.addImport( "nvl", Validator.class.getMethod("nvl",Object.class,Object.class,Object[].class) );
        ctx.addImport( "nvl", Strings.class.getMethod("nvl",Object.class) );

        Serializable expression = MVEL.compileExpression( " Strings.nvl(address,'default address') ", ctx );
        Object o = MVEL.executeExpression( expression, param() );

        log.debug( "result : [{}]", o );

        expression = MVEL.compileExpression( " nvl(address,'default address') ", ctx );
        o = MVEL.executeExpression( expression, param() );
        log.debug( "result : [{}]", o );

        expression = MVEL.compileExpression( " nvl(address) ", ctx );
        o = MVEL.executeExpression( expression, param() );
        log.debug( "result : [{}]", o );

    }

    @Test
    public void typecast() {

        Serializable expression = MVEL.compileExpression( " 1 == '1' " );
        Object o = MVEL.executeExpression( expression, param() );

        log.debug( "result : [{}]", o );

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
    }

}
