package io.nayasis.basica.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class NListTest {

    @Test
    public void print() {

        NList list = new NList();

        list.add( "key", "controller" );
        list.add( "val", "io.nayasis.model.NMapTest.printString()" );

        log.debug( "\n" + list.toString( false, false ) );

    }

}