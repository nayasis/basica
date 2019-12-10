package io.nayasis.basica.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ClassesTest {

    @Test
    public void isResourceExisted() {

        Assertions.assertTrue( Classes.isResourceExisted( "/xml/Deformed.xml" ) );
        Assertions.assertFalse( Classes.isResourceExisted( "/xml/Deformed.xm" ) );

    }

}