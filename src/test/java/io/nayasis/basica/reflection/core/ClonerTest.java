package io.nayasis.basica.reflection.core;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.model.NDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
public class ClonerTest {

    @Test
    public void encode() {

        User user = new User().name("nayasis").age(40);

        String encoded = Cloner.encodeToString( user );

        log.debug( "{}", encoded );

        assertEquals( "AVABbmF5YXNp8w==", encoded );

        User clone = Cloner.decodeFromString( encoded, User.class );

        assertEquals( user.age(), clone.age() );
        assertEquals( user.name(), clone.name() );

    }

    @Test
    public void encodeFromStrings() {

        User user = new User().name("nayasis").age(40);

        String encoded = Strings.encode( user );

        log.debug( "{}", encoded );

        User clone = Strings.decode( encoded, User.class );

        assertEquals( user.age(), clone.age() );
        assertEquals( user.name(), clone.name() );

    }

    @Test
    public void cloneTest() {

        User user  = new User().name("nayasis").age(40);
        User clone = Cloner.cloneDeep( user );

        assertEquals( user.age(), clone.age() );
        assertEquals( user.name(), clone.name() );

        clone.name( "changed" );
        clone.age( 50 );

        assertNotEquals( user.name(), clone.name() );
        assertNotEquals( user.age(), clone.age() );

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