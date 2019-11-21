package io.nayasis.basica.reflection.core;

import com.google.gson.Gson;
import io.nayasis.basica.etc.StopWatch;
import io.nayasis.basica.reflection.Reflector;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class JsonConverterTest {

    @Test
    public void compareParsingResult() {

        int count = 1_000_000;

        User user = new User( "nayasis", 42, "PanKyo", "OneStore" );

        // initialize
        Gson gson = new Gson();
        Reflector.toJson( user );

        StopWatch watch = new StopWatch();

        watch.start( "gson" );
        for( int i=0; i < count; i++ ) {
            gson.toJson( user );
        }
        String jsonGson = gson.toJson( user );

        watch.start( "jackson" );
        for( int i=0; i < count; i++ ) {
            Reflector.toJson( user );
        }
        String jsonJackson = Reflector.toJson( user );

        watch.stop();

        log.debug( "{}", jsonJackson );
        log.debug( "{}", jsonGson );
        log.debug( ">> performance\n{}", watch );

    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class User {
        private String name;
        private int    age;
        private String address;
        private String company;
    }

}