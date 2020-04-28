package io.nayasis.basica.model;

import io.nayasis.basica.base.Characters;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class NListPrinterTest {

    @Test
    public void print() {

        // set CJK font width from 1 to 2 (if you use CJK font in console)
        Characters.fullwidth( 2 );

        NList list = new NList();

        list.addData( "key", "controller" );
        list.addData( "val", "io.nayasis.model.NMapTest.printString()" );
        
        list.setAlias( "key", "이것은 KEY" );
        list.setAlias( "val", "これは VALUE" );

        log.debug( "\n{}", list );
        log.debug( "\n{}", list.toString( false, false ) );

        Assertions.assertEquals("+------------+-----------------------------------------+\n" +
            "| key        | val                                     |\n" +
            "+------------+-----------------------------------------+\n" +
            "| 이것은 KEY | これは VALUE                            |\n" +
            "+------------+-----------------------------------------+\n" +
            "| controller | io.nayasis.model.NMapTest.printString() |\n" +
            "+------------+-----------------------------------------+", list.toString() );

        Assertions.assertEquals( "+------------+-----------------------------------------+\n" +
            "| controller | io.nayasis.model.NMapTest.printString() |\n" +
            "+------------+-----------------------------------------+", list.toString(false,false) );

    }

    @Test
    public void printNoData() {
        NList list = new NList();
        log.debug( "\n{}", list );
        Assertions.assertEquals( "+---------+\n" +
            "| NO DATA |\n" +
            "+---------+", list.toString() );
    }

    @Test
    public void printNoDataWithHeader() {
        NList list = new NList();
        list.addKey( "name" );
        log.debug( "\n{}", list );
        Assertions.assertEquals( "+---------+\n" +
            "| name    |\n" +
            "+---------+\n" +
            "| NO DATA |\n" +
            "+---------+", list.toString() );
    }

    @Test
    public void printEmptyMap() {

        NList list = new NList();

        list.addData( "key", new HashMap<>() );
        list.addData( "key", new ArrayList<>() );

        log.debug( "\n{}", list );

        Assertions.assertEquals( "+-----+\n" +
            "| key |\n" +
            "+-----+\n" +
            "| {}  |\n" +
            "| []  |\n" +
            "+-----+", list.toString() );

    }

}