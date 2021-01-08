package com.github.nayasis.basica.expression;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExpressionTest {

    @Test
    public void simple() {
        Assertions.assertEquals( 21, (int) new Expression(" 3 * 7" ).run() );
        Assertions.assertTrue( new Expression( "name == 'nayasis' && age == 40 && address == empty").test(param()) );
        Assertions.assertFalse( new Expression( "name == 'nayasis' && age == 40 && address != empty").test(param()) );
        Assertions.assertTrue( new Expression( "children.get(0).name == 'jake'").test(param()) );
        Assertions.assertTrue( new Expression( "children[0].name == 'jake'").test(param()) );
    }

    @Test
    public void contains() {
        Assertions.assertTrue( Expression.of("['nayasis','jake'].contains(name)").test(param()) );
    }

    @Test
    public void like() {
        Assertions.assertTrue( Expression.of("name.matches('.+?sis$')").test(param()) );
    }

    @Test
    public void nvl() {
        Assertions.assertEquals( "default", new Expression("Strings.nvl(address,'default')").run(param()) );
        Assertions.assertEquals( "default", new Expression("nvl(address,'default')").run(param()) );
        Assertions.assertEquals( "", new Expression("nvl(address)").run(param()) );
    }

    @Test
    public void typecast() {
        Assertions.assertTrue( new Expression( "1 == '1'" ).test() );
        Assertions.assertTrue( new Expression( "1 + (2 * 3) == '7'" ).test() );
        Assertions.assertTrue( new Expression( "1 + 'a' == '1a'" ).test() );
        Assertions.assertFalse( new Expression( "1 + '2' == '3'" ).test() );
        Assertions.assertTrue( new Expression( "1 + (int)'2' == '3'" ).test() );
    }

    @Test
    public void print() {
        Assertions.assertEquals( "1 == '1'"            , new Expression( "1 == '1'"            , true ).toString() );
        Assertions.assertEquals( "1 + (2 * 3) == '7'"  , new Expression( "1 + (2 * 3) == '7'"  , true ).toString() );
        Assertions.assertEquals( "1 + 'a' == '1a'"     , new Expression( "1 + 'a' == '1a'"     , true ).toString() );
        Assertions.assertEquals( "1 + '2' == '3'"      , new Expression( "1 + '2' == '3'"      , true ).toString() );
        Assertions.assertEquals( "1 + (int)'2' == '3'" , new Expression( "1 + (int)'2' == '3'" , true ).toString() );
    }

    private Person param() {
        Person p = new Person().name( "nayasis" ).age( 40 ).job( "engineer" );
        p.children().add( new Person().name("jake").age(10).job("student").address("seoul") );
        return p;
    }

    @Data
    @Accessors(fluent=true)
    public static class Person {
        private String name;
        private int    age;
        private String job;
        private String address;
        private List<Person> children = new ArrayList<>();
    }

}