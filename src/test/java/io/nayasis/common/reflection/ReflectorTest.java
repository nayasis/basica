package io.nayasis.common.reflection;

import lombok.*;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ReflectorTest {

    @Test
    public void cloneTest() {

        User user  = User.builder().name("nayasis").age(40).build();
        User clone = Reflector.clone(user);

        Assert.assertEquals( user.getName(), clone.getName() );
        Assert.assertEquals( user.getAge(), clone.getAge() );

    }

    @Test
    public void copy() {

        User user = User.builder().name("nayasis").age(40).build();
        Account account = new Account();

        Reflector.copy( user, account );

        Assert.assertEquals( "nayasis", account.getName() );
        Assert.assertEquals( 40, account.getAge().intValue() );
        Assert.assertEquals( null, account.getAddress() );
        Assert.assertEquals( null, account.getBalance() );

    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
class User {
    private String name;
    private int    age;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
class Account {
    private String     name;
    private Integer    age;
    private String     address;
    private BigDecimal balance;
}