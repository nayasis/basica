package io.nayasis.common.reflection;

import io.nayasis.common.validation.Validator;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.*;

@Slf4j
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

    @Test
    public void toJsonJava8Date() {

        Account jake = Account.builder()
            .name("jake")
            .age( 9 )
            .regDt(LocalDateTime.now())
            .address("Jeongja, Sung-Nam si")
            .balance(BigDecimal.ZERO)
            .build();

        String json = Reflector.toJson(jake);

        Map map = Reflector.toMapFrom(json);

        String regDt = (String) map.get("regDt");

        Assert.assertTrue(Validator.isMatched(regDt, "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"));

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
    private String        name;
    private Integer       age;
    private String        address;
    private BigDecimal    balance;
    private LocalDateTime regDt;
}