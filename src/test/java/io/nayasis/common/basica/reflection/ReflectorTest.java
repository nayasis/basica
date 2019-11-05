package io.nayasis.common.basica.reflection;

import io.nayasis.common.basica.model.NDate;
import io.nayasis.common.basica.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
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
}