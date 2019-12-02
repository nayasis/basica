package io.nayasis.basica.reflection.core;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.model.NDate;
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
import java.util.List;

@Slf4j
public class KryoClonerTest {

    private KryoCloner cloner = new KryoCloner();

    @Test
    public void encode() {

        User user = new User().name("nayasis").age(40);

        String encoded = cloner.encodeToString( user );

        log.debug( "{}", encoded );

        Assert.assertEquals( "AVABbmF5YXNp8w==", encoded );

        User clone = cloner.decodeFromString( encoded, User.class );

        Assert.assertEquals( user.age(), clone.age() );
        Assert.assertEquals( user.name(), clone.name() );

    }

    @Test
    public void cloneTest() {

        User user  = new User().name("nayasis").age(40);
        User clone = cloner.cloneDeep( user );

        Assert.assertEquals( user.age(), clone.age() );
        Assert.assertEquals( user.name(), clone.name() );

        clone.name( "changed" );
        clone.age( 50 );

        Assert.assertNotEquals( user.name(), clone.name() );
        Assert.assertNotEquals( user.age(), clone.age() );

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
    private BigDecimal balance;
    private LocalDateTime regDt;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent=true)
class Person {

    private String name;
    private NDate birth;
    private List<Person> children = new ArrayList<>();

    public int hashCode() {
        return (int)(name.hashCode() + birth.toTime() + children.size() );
    }

    public String toString() {
        return Strings.format( "{ name : {}, birth : {}, children count : {} }", name, birth, children.size() );
    }

}