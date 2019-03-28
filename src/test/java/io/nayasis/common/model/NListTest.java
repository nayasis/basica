package io.nayasis.common.model;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class NListTest {

    @Test
    public void print() {

        NList list = new NList();

        list.add( "key", "controller" );
        list.add( "val", "io.nayasis.common.model.NMapTest.printString()" );

        log.debug( "\n" + list.toString( false, false ) );

    }

}