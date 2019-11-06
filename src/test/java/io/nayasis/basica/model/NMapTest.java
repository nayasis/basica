package io.nayasis.basica.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class NMapTest {

    @Test
    @SuppressWarnings({ "unsafe", "unchecked" })
    public void printString() {

        NMap map = new NMap();

        map.put( "controller", "io.nayasis.model.NMapTest.printString()" );

        log.debug( "\n" + map.toString( false, false ) );

    }


}