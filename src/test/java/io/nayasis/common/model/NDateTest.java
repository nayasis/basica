package io.nayasis.common.model;

import io.nayasis.common.file.Files;
import io.nayasis.common.reflection.Reflector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.Assert.*;

@Slf4j
public class NDateTest {

    @Test
    public void setDateFromLocalDateTime() {

        LocalDateTime nowLocal = LocalDateTime.now();
        NDate         nowNDate = new NDate( nowLocal );

        Assert.assertEquals( nowLocal.toString(), nowNDate.toString("yyyy-MM-dd'T'HH:mm:ss.SSS") );

    }

    @Test
    public void setDateFromLocalDate() {

        LocalDate nowLocal = LocalDate.now();
        NDate     nowNDate = new NDate( nowLocal );

        Assert.assertEquals( nowLocal.toString(), nowNDate.toString("YYYY-MM-DD") );

    }

    @Test
    public void testSetSecond() throws Exception {
        NDate date = new NDate( "2016-06-02 13:59:21" );
        date.setSecond( 0 );
        assertEquals( "2016-06-02 13:59:00", date.toString() );
    }

    @Test
    public void testIsoConverting() {

        String format = NDate.ISO_8601_FORMAT;

        log.debug( new NDate( "2014-01-01T00:00:00.123+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T00:00:00.12+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T00:00:00.1+0900", format ).toString( format ) );
        log.debug( new NDate( "2014-01-01T00:00:00+0900", format ).toString( format ) );
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

        String testFile = Files.getRootPath() + "/test.serialized.obj";

        SampleVo sampleVo = new SampleVo( "nayasis", new NDate( "1977-01-22" ) );

        String before = sampleVo.toString();

        Files.writeObject( testFile, sampleVo );

        sampleVo = Files.readObject( testFile );

        String after = sampleVo.toString();

        Assert.assertEquals( after, before );

        Files.delete( testFile );

    }

    private static class SampleVo implements Serializable {

        private String name;
        private NDate  birth;

        public SampleVo( String name, NDate birth ) {
            this.name = name;
            this.birth = birth;
        }

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public NDate getBirth() {
            return birth;
        }

        public void setBirth( NDate birth ) {
            this.birth = birth;
        }

        public String toString() {
            return Reflector.toJson( this );
        }

    }

    @Test
    public void test() {

        log.debug( new NDate().toString() );
        log.debug( new NDate().toString("YYYY-MM-DD'T'HH:MI:SS") );

    }

    @Test
    public void truncate(){

        NDate truncate = new NDate().truncate();

        log.debug( truncate.toString("YYYY-MM-DD'T'HH:MI:SS.FFF") );

        Assert.assertEquals( 0, truncate.getHour() );
        Assert.assertEquals( 0, truncate.getMinute() );
        Assert.assertEquals( 0, truncate.getSecond() );
        Assert.assertEquals( 0, truncate.getMillisecond() );

        String truncatedDateString = truncate.toString( "YYYY-MM-DD" );
        NDate ndate = new NDate( truncatedDateString );
        log.debug( truncate.compareTo( ndate ) + "" );

        Assert.assertEquals( 0, truncate.compareTo( ndate ) );

    }

}