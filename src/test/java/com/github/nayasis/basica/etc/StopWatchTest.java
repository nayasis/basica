package com.github.nayasis.basica.etc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class StopWatchTest {

    @Test
    public void testSimple() throws InterruptedException {

        StopWatch watch = new StopWatch().simple();

        sleep( 1 );

        log.debug( "{}", watch );
        log.debug( "{} ms", watch.elapsedMilliSeconds() );

        watch.start( "2nd phase" );

        sleep( 2 );

        log.debug( "{}", watch );
        log.debug( "{} ms", watch.elapsedMilliSeconds() );

        watch.stop();

    }

    @Test
    public void test() throws InterruptedException {

        StopWatch watch = new StopWatch();

        watch.start( "1st phase" );

        sleep( 1 );

        watch.start( "2nd phase" );

        log.debug( "\n{}", watch );
        log.debug( "{} ms", watch.elapsedMilliSeconds() );

        sleep( 2 );

        watch.stop();

        log.debug( "\n{}", watch );

        Assertions.assertThrows( IllegalStateException.class, () -> {
            log.debug( "{} ms", watch.elapsedMilliSeconds() );
        });

    }

    private void sleep( double sec ) throws InterruptedException {
        Thread.sleep( (long)(sec * 1_000) );
    }

}