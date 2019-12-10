package io.nayasis.basica.model;

import io.nayasis.basica.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class NPropertiesTest {

    @Test
    public void load() {

        String path = Files.getRootPath() + "/model/test.properties";

        NProperties properties = new NProperties( path );

        log.debug( properties.toString() );

        assertEquals( "SELECT * FROM MY_TABLE", properties.get("sql") );
        assertEquals( "merong", properties.get("granada") );

    }


}