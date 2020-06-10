package com.github.nayasis.basica.etc;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class StopWatchTest {

    @Test
    public void test() {

        StopWatch watch = new StopWatch();

        watch.start();
        watch.stop();

        log.debug( "{} ns", watch.elapsedNanoSeconds() );

    }

}