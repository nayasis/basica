package com.github.nayasis.basica.model;

import com.github.nayasis.basica.file.Files;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.github.nayasis.basica.model.NDate.ISO_8601_FORMAT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class NDateTest {

    @Test
    public void init() {

        assertEquals( "2020-05-01 00:00:00", new NDate("2020-05").toString() );
        assertEquals( "2020-05-01 00:00:00", new NDate("2020-05", "YYYY-MM-DD").toString() );
        assertEquals( "2020-05-01 00:00:00", new NDate("2020-05", "YYYY-MM").toString() );
        assertEquals( "2020-05-01 00:00:00", new NDate("05/2020", "MM/yyyy").toString() );

    }

    @Test
    public void beginningAndEnd() {

        NDate date = new NDate( "2020-05-07" );

        assertEquals( "2020-05-01 00:00:00", date.beginningOfMonth().toString() );
        assertEquals( "2020-05-31 23:59:59", date.endOfMonth().toString() );
        assertEquals( "2020-05-03 00:00:00", date.beginningOfWeek().toString() );
        assertEquals( "2020-05-09 23:59:59", date.endOfWeek().toString() );

    }

    @Test
    public void setDateFromLocalDateTime() {

        LocalDateTime nowLocal = LocalDateTime.now();
        NDate         nowNDate = new NDate( nowLocal );

        String format = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        assertEquals( nowLocal.format( DateTimeFormatter.ofPattern(format)),
            nowNDate.toString(format) );

    }

    @Test
    public void setDateFromLocalDate() {

        LocalDate nowLocal = LocalDate.now();
        NDate     nowNDate = new NDate( nowLocal );

        assertEquals( nowLocal.toString(), nowNDate.toString("YYYY-MM-DD") );

    }

    @Test
    public void testSetSecond() throws Exception {
        NDate date = new NDate( "2016-06-02 13:59:21" );
        date.setSecond( 0 );
        assertEquals( "2016-06-02 13:59:00", date.toString() );
    }

    @Test
    public void testIsoConverting() {

        String format = ISO_8601_FORMAT;

        log.debug( new NDate( "2014-01-01T00:00:00+0900", format ).toString( format ) );

        log.debug( new NDate( "2014-01-01T00:00:00.123+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T00:00:00.12+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T00:00:00.1+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T00:0000+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T000000+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-0101T000000+0900", format ).toString( format ) );
        log.debug( new NDate( "20140101T000000+0900", format ).toString( format ) );

    }

    @Test
    public void testGetBeginningOfMonthDate() throws Exception {

        NDate date = new NDate( "2016-06-02 13:59:21" );

        NDate beginningOfMonthDate = date.beginningOfMonth();
        NDate endOfMonthDate       = date.endOfMonth();

        assertEquals( beginningOfMonthDate.toString(), "2016-06-01 00:00:00" );
        assertEquals( endOfMonthDate.toString(), "2016-06-30 23:59:59" );

        assertEquals( beginningOfMonthDate.getMillisecond(), 0 );
        assertEquals( endOfMonthDate.getMillisecond(), 999 );

    }

    @Test
    public void serializeViaFile() {

        String file = Files.rootPath() + "/test.serialized.obj";

        SampleVo vo = new SampleVo( "nayasis", new NDate( "1977-01-22" ) );

        String before = vo.toString();
        Files.writeObject( file, vo );
        String after = Files.readObject( file ).toString();
        Files.delete( file );

        assertEquals( after, before );

    }

    @Data
    @AllArgsConstructor
    private static class SampleVo implements Serializable {
        private String name;
        private NDate  birth;
    }

    @Test
    public void toStringFormat() {
        NDate date = new NDate( "2019-10-16 16:51:43" );
        assertEquals( "2019-10-16 16:51:43", date.toString() );
        assertEquals( "2019-10-16T16:51:43", date.toString("YYYY-MM-DD'T'HH:MI:SS") );
    }

    @Test
    public void truncate(){

        NDate truncate = new NDate().truncate();

        log.debug( truncate.toString("YYYY-MM-DD'T'HH:MI:SS.FFF") );

        assertEquals( 0, truncate.getHour() );
        assertEquals( 0, truncate.getMinute() );
        assertEquals( 0, truncate.getSecond() );
        assertEquals( 0, truncate.getMillisecond() );

        String truncatedDateString = truncate.toString( "YYYY-MM-DD" );
        NDate ndate = new NDate( truncatedDateString );
        log.debug( truncate.compareTo( ndate ) + "" );

        assertEquals( 0, truncate.compareTo( ndate ) );

    }

    @Test
    public void localDate() {

        NDate a = new NDate("2020-05-14");
        NDate b = new NDate("2020-05-14");

        log.debug( "a : {}, {}", a.toLocalDate(), a.toLocalDate().hashCode() );
        log.debug( "b : {}, {}", b.toLocalDate(), b.toLocalDate().hashCode() );

        assertEquals( a.toLocalDate(), b.toLocalDate() );


    }

}