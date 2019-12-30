package io.nayasis.basica.exception;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class CallerTest {

    @Test
    public void main() {
        log.debug( "{}", caller1(0) );
        log.debug( "{}", caller1(1) );
        log.debug( "{}", caller1(2) );
        log.debug( "{}", caller1(3) );
    }

    private Caller caller1( int depth ) {
        return caller2( depth );
    }

    private Caller caller2( int depth ) {
        return new Caller(depth );
    }

}