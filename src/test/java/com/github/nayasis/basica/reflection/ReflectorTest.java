package com.github.nayasis.basica.reflection;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.model.NDate;
import com.github.nayasis.basica.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ReflectorTest {

    @Test
    public void cloneTest() {

        User user  = new User().name("nayasis").age(40);
        User clone = Reflector.clone(user);

        assertEquals( user.name(), clone.name() );
        assertEquals( user.age(), clone.age() );

        clone.age( 9 );

        assertEquals( user.age(), 40 );

    }

    @Test
    public void cloneRecursiveTest() {

        Person person = new Person().name("nayasis").birth(new NDate("1977-01-22") );
        person.children().add( person );

        Person clone = Reflector.clone( person );

        log.debug( clone.toString() );

        assertEquals( person.name(), clone.name() );
        assertEquals( person.birth().toTime(), clone.birth().toTime() );
        assertEquals( person.children().size(), clone.children().size() );
        assertEquals( clone.children().get(0).name(), clone.name() );
        assertEquals( clone.children().get(0).birth(), clone.birth() );

    }

    @Test
    public void arrayAsListCloneTest() {

        Collection<User> users = Arrays.asList( new User("nayasis",40), new User("jake",40) );
        Collection<User> clone = Reflector.clone( users );

        assertEquals( users.toString(), clone.toString() );

        Iterator<User> iteratorUsers = users.iterator();
        Iterator<User> iteratorClone = clone.iterator();

        while( iteratorUsers.hasNext() ) {
            User a = iteratorUsers.next();
            User b = iteratorClone.next();
            assertTrue( a.equals(b) );
        }

    }

    @Test
    public void copy() {

        User user = new User().name("nayasis").age(40);
        Account account = new Account().address( "jongja-dong" ).balance( new BigDecimal(1000) );

        Reflector.copy( user, account );

        assertEquals( "nayasis", account.name() );
        assertEquals( 40, account.age().intValue() );
        assertEquals( "jongja-dong", account.address() );
        assertEquals( new BigDecimal(1000), account.balance() );

    }

    @Test
    public void copyRecursiveTest() {

        Person person = new Person().name("nayasis").birth(new NDate("1977-01-22") );
        person.children().add( person );

        Person another = new Person();
        Reflector.copy( person, another );

        log.debug( another.toString() );

        assertEquals( person.name(), another.name() );
        assertEquals( person.birth().toTime(), another.birth().toTime() );
        assertEquals( person.children().size(), another.children().size() );
        assertEquals( another.children().get(0).name(), another.name() );
        assertEquals( another.children().get(0).birth(), another.birth() );

    }

    @Test
    public void toMap() {

        Person p1 = new Person().name("nayasis").birth(new NDate("1977-01-22") );
        Person p2 = new Person().name("jake").birth(new NDate("2011-04-25") );

        Account account = new Account().address("seoul").age(40).balance(new BigDecimal(1_000)).regDt(ZonedDateTime.now());

        p1.children().add( p2 );
        p1.account( account );

        Map map = Reflector.toMapFrom( p1 );

        assertTrue( map.containsKey("name") );
        assertTrue( map.containsKey("birth") );
        assertTrue( map.containsKey("children") );
        assertTrue( map.containsKey("account") );

        Map map2 = Reflector.toFlattenMap( map );

        assertTrue( map2.containsKey("name") );
        assertTrue( map2.containsKey("birth") );
        assertTrue( map2.containsKey("children[0].birth") );
        assertTrue( map2.containsKey("children[0].name") );
        assertTrue( map2.containsKey("account.address") );
        assertTrue( map2.containsKey("account.age") );
        assertTrue( map2.containsKey("account.balance") );

    }

    @Test
    public void toJson() {

        Person  person  = new Person().name("jake").birth(new NDate("2011-04-25") );
        Account account = new Account().address("seoul").age(40).balance(new BigDecimal(1_000)).regDt(person.birth().toZonedDateTime());

        log.debug( "{}", person );
        log.debug( "{}", account );

        log.debug( Reflector.toJson( person ) );
        log.debug( Reflector.toJson( account ) );

        assertEquals( "{\"birth\":\"2011-04-24T15:00:00.000+00:00\",\"children\":[],\"name\":\"jake\"}", Reflector.toJson(person) );
        assertEquals( "{\"address\":\"seoul\",\"age\":40,\"balance\":1000,\"regDt\":\"2011-04-25T00:00:00+09:00\"}", Reflector.toJson(account) );

    }

    @Test
    public void copyPrimitiveArray() {

        int[] source = { 1,2,3,4 };
        BigDecimal[] target = new BigDecimal[ 4 ];

        Reflector.copy( source, target );

        log.debug( Arrays.toString(source) );
        log.debug( Arrays.toString(target) );

        assertEquals(
            Arrays.toString(source),
            Arrays.toString(target)
        );

    }

    @Test
    public void copyArray() {

        User[] source = {
            new User().name("a").age(1),
            new User().name("b").age(2),
            new User().name("c").age(3),
            new User().name("d").age(4),
        };

        Account[] target = new Account[ 4 ];

        Reflector.copy( source, target );

        log.debug( Arrays.toString(source) );
        log.debug( Arrays.toString(target) );

        for( int i=0; i < source.length; i++ ) {
            User    src = source[ i ];
            Account trg = target[ i ];
            assertEquals( src.name(), trg.name() );
            assertEquals( src.age(),  trg.age().intValue() );
        }

    }

    @Test
    public void copyMultiDimensionArray() {

        User[][] source = {
            {
                new User().name("a").age(1),
                new User().name("b").age(2),
                new User().name("c").age(3),
                new User().name("d").age(4)
            },
            {
                new User().name("e").age(5),
                new User().name("f").age(6)
            },
            {
                new User().name("g").age(7),
                new User().name("h").age(8),
                new User().name("i").age(9)
            }
        };

        Account[][] target = new Account[3][];

        Reflector.copy( source, target );

        for( int i=0; i < source.length; i++ ) {

            User[]    childSource = source[ i ];
            Account[] childTarget = target[ i ];

            for( int j=0; j < childSource.length; j++ ) {
                User    src = childSource[j];
                Account trg = childTarget[j];
                assertEquals( src.name(), trg.name() );
                assertEquals( src.age(),  trg.age().intValue() );
            }
        }

        log.debug( Arrays.toString(source) );
        log.debug( Arrays.toString(target) );

    }

    @Test
    public void toJsonJava8Date() {

        Account jake = new Account()
            .name("jake")
            .age( 9 )
            .regDt(ZonedDateTime.now())
            .address("Jeongja, Sung-Nam si")
            .balance(BigDecimal.ZERO)
            ;

        String json = Reflector.toJson(jake);

        Map map = Reflector.toMapFrom(json);

        String regDt = (String) map.get("regDt");

        log.debug( regDt );

        assertTrue( Validator.isMatched(regDt, "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+\\+\\d{2}:\\d{2}"));

    }

    @Test
    public void convertMap() {

        String json = "{ 'name':'nayasis', 'birth':'1977-01-22'  }";

        Map person = Reflector.toMapFrom( json );

        log.debug( person.toString() );

    }

    @Test
    public void convertNDate() {

        String json = "{ 'name':'nayasis', 'birth':'1977-01-22'  }";

        Person person = Reflector.toBeanFrom( json, Person.class );

        log.debug( person.toString() );

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(fluent=true)
    static class User {
        private String name;
        private int    age;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(fluent=true)
    static class Account {
        private String        name;
        private Integer       age;
        private String        address;
        private BigDecimal    balance;
        private ZonedDateTime regDt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(fluent=true)
    static class Person {

        private String       name;
        private NDate        birth;
        private List<Person> children = new ArrayList<>();
        private Account      account;

        public int hashCode() {
            return (int)(name.hashCode() + birth.toTime() + children.size() );
        }

        public String toString() {
            return Strings.format( "{ name : {}, birth : {}, children count : {} }", name, birth, children.size() );
        }

    }

}