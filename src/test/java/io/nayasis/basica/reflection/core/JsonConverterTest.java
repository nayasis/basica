package io.nayasis.basica.reflection.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonConverterTest {

    @Test
    public void deepClone() {

        User a = new User( "nayasis", 42 );
        User b = new User( "jake", 9 );


    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class User {
        private String name;
        private int    age;
    }

}