package io.nayasis.common.basica.model;

import io.nayasis.common.basica.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class NPropertiesTest {

    @Test
    public void load() {

        String path = Files.getRootPath() + "/model/test.properties";

        NProperties properties = new NProperties( path );

        log.debug( properties.toString() );

        Assert.assertEquals( "SELECT * FROM MY_TABLE", properties.get("sql") );
        Assert.assertEquals( "merong", properties.get("granada") );

    }


}