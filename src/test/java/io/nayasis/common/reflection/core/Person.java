package io.nayasis.common.reflection.core;

import io.nayasis.common.model.NDate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Person {

    private String name;
    private int    age;
    private NDate  birthday;
    private String address;

    public Person( String name, int age ) {
        this.name     = name;
        this.age      = age;
        this.birthday = new NDate();
    }

    public void setName( String name ) {
        this.name = name;
    }

    public void setAge( int age ) {
        this.age = age;
    }

    public void setBirthday( NDate birthday ) {
        this.birthday = birthday;
    }

    public void setBirthday( String birthday ) {
        this.birthday.setDate( birthday );
    }

    public void setBirthday( LocalDateTime birthday ) {
        this.birthday.setDate( birthday );
    }

    public NDate getBirthday() {
        return birthday;
    }
}
