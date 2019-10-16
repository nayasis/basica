package io.nayasis.common.basica.reflection;

import io.nayasis.common.basica.model.NDate;
import io.nayasis.common.basica.validation.Validator;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class ReflectorTest {

    @Test
    public void cloneTest() {

        User user  = new User().name("nayasis").age(40);
        User clone = Reflector.clone(user);

        Assert.assertEquals( user.name(), clone.name() );
        Assert.assertEquals( user.age(), clone.age() );

    }

    @Test
    public void copy() {

        User user = new User().name("nayasis").age(40);
        Account account = new Account();

        Reflector.copy( user, account );

        Assert.assertEquals( "nayasis", account.name() );
        Assert.assertEquals( 40, account.age().intValue() );
        Assert.assertEquals( null, account.address() );
        Assert.assertEquals( null, account.balance() );

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