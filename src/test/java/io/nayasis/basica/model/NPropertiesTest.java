package io.nayasis.basica.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class NPropertiesTest {

    @Test
    public void load() {

        NProperties properties = new NProperties( "/model/test.properties" );

        log.debug( properties.toString() );

        assertEquals( "SELECT * FROM MY_TABLE", properties.get("sql") );
        assertEquals( "merong", properties.get("granada") );

    }

}