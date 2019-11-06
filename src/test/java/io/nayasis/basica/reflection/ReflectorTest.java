package io.nayasis.basica.reflection;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.model.NDate;
import io.nayasis.basica.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReflectorTest {

    @Test
    public void cloneTest() {

        User user  = new User().name("nayasis").age(40);
        User clone = Reflector.clone(user);

        Assert.assertEquals( user.name(), clone.name() );
        Assert.assertEquals( user.age(), clone.age() );

        clone.age( 9 );

        Assert.assertEquals( user.age(), 40 );

    }

    @Test
    public void cloneRecursiveTest() {

        Person person = new Person().name("nayasis").birth(new NDate("1977-01-22") );
        person.children().add( person );

        Person clone = Reflector.clone( person );

        log.debug( clone.toString() );

        Assert.assertEquals( person.name(), clone.name() );
        Assert.assertEquals( person.birth().toTime(), clone.birth().toTime() );
        Assert.assertEquals( person.children().size(), clone.children().size() );
        Assert.assertEquals( clone.children().get(0).name(), clone.name() );
        Assert.assertEquals( clone.children().get(0).birth(), clone.birth() );

    }

    @Test
    public void arrayCloneTest() {

        Collection<User> users = Arrays.asList( new User("nayasis",40), new User("jake",40) );
        Collection<User> clone = Reflector.clone( users );

        Assert.assertEquals( users.toString(), clone.toString() );

        Iterator<User> iteratorUsers = users.iterator();
        Iterator<User> iteratorClone = clone.iterator();

        while( iteratorUsers.hasNext() ) {
            User a = iteratorUsers.next();
            User b = iteratorClone.next();
            Assert.assertTrue( a.equals(b) );
        }

    }

    @Test
    public void copy() {

        User user = new User().name("nayasis").age(40);
        Account account = new Account().address( "jongja-dong" ).balance( new BigDecimal(1000) );

        Reflector.copy( user, account );

        Assert.assertEquals( "nayasis", account.name() );
        Assert.assertEquals( 40, account.age().intValue() );
        Assert.assertEquals( "jongja-dong", account.address() );
        Assert.assertEquals( new BigDecimal(1000), account.balance() );

    }

    @Test
    public void copyRecursiveTest() {

        Person person = new Person().name("nayasis").birth(new NDate("1977-01-22") );
        person.children().add( person );

        Person another = new Person();
        Reflector.copy( person, another );

        log.debug( another.toString() );

        Assert.assertEquals( person.name(), another.name() );
        Assert.assertEquals( person.birth().toTime(), another.birth().toTime() );
        Assert.assertEquals( person.children().size(), another.children().size() );
        Assert.assertEquals( another.children().get(0).name(), another.name() );
        Assert.assertEquals( another.children().get(0).birth(), another.birth() );

    }

    @Test
    public void copyPrimitiveArray() {

        int[] source = { 1,2,3,4 };
        BigDecimal[] target = new BigDecimal[ 4 ];

        Reflector.copy( source, target );

        log.debug( Arrays.toString(source) );
        log.debug( Arrays.toString(target) );

        Assert.assertEquals(
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
            Assert.assertEquals( src.name(), trg.name() );
            Assert.assertEquals( src.age(),  trg.age().intValue() );
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
                Assert.assertEquals( src.name(), trg.name() );
                Assert.assertEquals( src.age(),  trg.age().intValue() );
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
            .regDt(LocalDateTime.now())
            .address("Jeongja, Sung-Nam si")
            .balance(BigDecimal.ZERO)
            ;

        String json = Reflector.toJson(jake);

        Map map = Reflector.toMapFrom(json);

        String regDt = (String) map.get("regDt");

        Assert.assertTrue( Validator.isMatched(regDt, "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));

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

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent=true)
class User {
    private String name;
    private int    age;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent=true)
class Account {
    private String        name;
    private Integer       age;
    private String        address;
    private BigDecimal    balance;
    private LocalDateTime regDt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent=true)
class Person {

    private String name;
    private NDate  birth;
    private List<Person> children = new ArrayList<>();

    public int hashCode() {
        return (int)(name.hashCode() + birth.toTime() + children.size() );
    }

    public String toString() {
        return Strings.format( "{ name : {}, birth : {}, children count : {} }", name, birth, children.size() );
    }

}