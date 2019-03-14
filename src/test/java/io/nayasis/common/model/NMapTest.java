package io.nayasis.common.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class NMapTest {

    @Test
    public void printString() {

        NMap map = new NMap();

        map.put( "controller", "io.nayasis.common.model.NMapTest.printString()" );

        log.debug( "\n" + map.toString( false, false ) );

    }


}