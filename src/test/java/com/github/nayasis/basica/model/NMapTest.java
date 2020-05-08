package com.github.nayasis.basica.model;

import com.github.nayasis.basica.model.NMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class NMapTest {

    @Test
    @SuppressWarnings({ "unsafe", "unchecked" })
    public void printString() {

        NMap map = new NMap();

        map.put( "controller", "com.github.nayasis.model.NMapTest.printString()" );

        log.debug( "\n" + map.toString( false, false ) );

    }


}