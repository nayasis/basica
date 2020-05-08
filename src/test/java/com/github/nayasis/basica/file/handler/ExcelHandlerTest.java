package com.github.nayasis.basica.file.handler;

import com.github.nayasis.basica.base.Classes;
import com.github.nayasis.basica.file.Files;
import com.github.nayasis.basica.file.handler.ExcelHandler;
import com.github.nayasis.basica.model.NList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ExcelHandlerTest {

    @Test
    public void single() {

        String file = Files.getUserHome() + "/excel-test.xlsx";

        log.debug( "test file path : [{}]", file );

        try {

            Files.delete( file );

            ExcelHandler handler = new ExcelHandler( file );
            handler.write( getTestData() );
            NList sheet = handler.read();
            log.debug( "\n{}", sheet );

            assertEquals( 2, sheet.size() );
            assertEquals( "[name, age, city]", sheet.keySet().toString() );
            assertEquals( 45, sheet.getRow( 0).get( "age" ) );
            assertEquals( "jake", sheet.getRow( 1).get( "name" ) );

        } finally {
            Files.delete( file );
        }

    }

    @Test
    public void multi() {

        String file = Files.getUserHome() + "/excel-multi-test.xlsx";

        log.debug( "test file path : [{}]", file );

        try {

            Files.delete( file );

            ExcelHandler handler = new ExcelHandler( file );
            handler.writeAll( getTestDataMap() );
            Map<String,NList> sheets = handler.readAll();
            log.debug( "\n{}", sheets );

            assertEquals( 3, sheets.size() );

            for( NList sheet : sheets.values() ) {
                assertEquals( 2, sheet.size() );
                assertEquals( "[name, age, city]", sheet.keySet().toString() );
                assertEquals( 45, sheet.getRow( 0).get( "age" ) );
                assertEquals( "jake", sheet.getRow( 1).get( "name" ) );
            }

        } finally {
            Files.delete( file );
        }

    }


    @Test
    public void read() {

        ExcelHandler handler = new ExcelHandler( Classes.getResourceStream( "/file/option.xlsx" ) );

        NList sheet = handler.read();

        log.debug( "\n{}", sheet );

        assertEquals( sheet.keySize(), 34 );
        assertEquals( sheet.getRow(0).get("0"), "item"  );
        assertEquals( sheet.getRow(1).get("1"), "core"  );
        assertEquals( sheet.getRow(5).get("2"), "label" );

    }

    private NList getTestData() {

        NList sheet = new NList();

        sheet.addData( "name", "nayasis" ).addData( "age", 45 ).addData( "city", "seoul" );
        sheet.addData( "name", "jake" ).addData( "age", 9 ).addData( "city", "sungnam" );

        return sheet;

    }

    private Map<String,NList> getTestDataMap() {

        Map<String,NList> map = new LinkedHashMap<>();

        map.put( "sheet-A", getTestData() );
        map.put( "sheet-B", getTestData() );
        map.put( "sheet-C", getTestData() );

        return map;

    }

}