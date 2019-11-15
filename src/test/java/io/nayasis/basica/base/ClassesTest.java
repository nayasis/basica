package io.nayasis.basica.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class ClassesTest {

    @Test
    public void isResourceExisted() {

        Assert.assertTrue( Classes.isResourceExisted( "/xml/Deformed.xml" ) );
        Assert.assertFalse( Classes.isResourceExisted( "/xml/Deformed.xm" ) );

    }

}