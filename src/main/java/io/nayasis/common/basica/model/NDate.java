package io.nayasis.common.basica.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.nayasis.common.basica.base.Strings;
import io.nayasis.common.basica.exception.unchecked.ParseException;
import io.nayasis.common.basica.reflection.deserializer.NDateDeserializer;
import io.nayasis.common.basica.reflection.serializer.simple.SimpleNDateSerializer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static java.util.Calendar.MILLISECOND;

/**
 * NDate
 *
 * @author nayasis@gmail.com
 * @since 2013-03-04
 */
@JsonSerialize( using = SimpleNDateSerializer.class )
@JsonDeserialize( using = NDateDeserializer.class )
public class NDate implements Serializable {

	public static final NDate MIN = new NDate("0000-01-01");
	public static final NDate MAX = new NDate("9999-12-31 23:59:59.999");

    private Calendar currentTime = Calendar.getInstance();

    public static final String FULL_FORMAT     = "yyyyMMddHHmmssSSS";
    public static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static final String DEFAULT_FORMAT  = "yyyy-MM-dd HH:mm:ss";
    private static final Set<String> ISO_8601_COMPATIBLE_FORMATS = new LinkedHashSet<>(
        Arrays.asList( "yyyyMMdd'T'HHmmssZ", "yyyyMMdd'T'HHmmssSSSZ", "yyyyMMdd'T'HHmmssSSZ", "yyyyMMdd'T'HHmmssSZ" )
    );

    private static final Set<String> ALL_FORMATS = new LinkedHashSet<>();
    static {
        ALL_FORMATS.add( DEFAULT_FORMAT  );
        ALL_FORMATS.add( FULL_FORMAT     );
        ALL_FORMATS.add( ISO_8601_FORMAT );
        ALL_FORMATS.addAll( ISO_8601_COMPATIBLE_FORMATS );
    }

    /**
     * constructor with current date
     */
    public NDate() {}

    /**
     * constructor
     *
     * @param date initial date
     */
    public NDate( Date date ) {
        setDate( (Date) date.clone() );
    }

    /**
     * constructor
     *
     * @param date initial date
     */
    public NDate( LocalDate date ) {
        setDate( Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
    }

    /**
     * constructor
     *
     * @param date initial date
     */
    public NDate( LocalDateTime date ) {
        setDate( date );
    }

    /**
     * constructor
     *
     * @param date initial date
     */
    public NDate( Calendar date ) {
        setDate( date );
    }

    /**
     * constructor
     *
     * @param date initial date
     */
    public NDate( NDate date ) {
        setDate( date );
    }

    /**
     * constructor
     *
     * @param date initial date
     */
    public NDate( long date ) {
        setDate( date );
    }

    /**
     * construct in sequence [year, month, day, hour, minute, second, milli-second]
     *
     * <pre>
     * NDate date01 = new NDate( "2012.01" );
     * NDate date02 = new NDate( "2012.01.02" );
     * NDate date03 = new NDate( "2012-01-02" );
     * NDate date04 = new NDate( "2012-01-02 13:20" );
     * NDate date05 = new NDate( "2012-01-02 13:20:42" );
     * </pre>
     *
     * @param date date
     * @throws ParseException fail in parsing date with defined format
     */
    public NDate( String date ) throws ParseException {
        setDate( date );
    }

    /**
     * construct date in specific format
     *
     * <pre>
     * NDate date01 = new NDate( "01/22/1977", "MM/DD/YYYY" );
     * NDate date02 = new NDate( "23:42", "HH:MI" );
     * NDate date03 = new NDate( "23:42 01/22", "HH:MI MM/DD" );
     * </pre>
     *
     * @param date      date
     * @param format    date format [YYYY:year, MM:month, DD:day, HH:hour, MI:minute, SS:second, FFF:milli-second ]
     * @throws ParseException fail in parsing date with defined format
     */
    public NDate( String date, String format ) throws ParseException {
        setDate( date, format );
    }

    /**
     * set current date to now
     *
     * @return self instance
     */
    public NDate setNow() {
        return setDate( new Date() );
    }

    /**
     * set date in sequence [year, month, day, hour, minute, second, milli-second]
     *
     * <pre>
     * NDate date = new NDate();
     *
     * date.setDate( "2011.12.24" );
     * date.setDate( "2011-12-24" );
     * date.setDate( "2011.12.24 12:20" );
     * date.setDate( "2011.12.24 13:20:45" );
     * </pre>
     *
     * @param date date string
     * @return self instance
     * @throws ParseException fail in parsing date with defined format
     */
    public NDate setDate( String date ) throws ParseException {
        return setDate( date, null );
    }

    /**
     * set date in specific format.
     *
     * <pre>
     * NDate date = new NDate();
     *
     * date.setDate( "2011-12-24 23:10:45", "YYYY-MM-DD HH:MI:SS" );
     * </pre>
     *
     * @param date      date
     * @param format    date format [YYYY:year, MM:month, DD:day, HH:hour, MI:minute, SS:second, FFF:milli-second ]
     * @return self instance
     * @throws ParseException fail in parsing date with defined format
     */
    public NDate setDate( String date, String format ) throws ParseException {

        if( Strings.isEmpty(date) ) {
            setDate( new Date() );
            return this;
        }

        String pattern = toDateFormat( format, true );
        String value   = toDateDigit( date );

        int length = Math.min( pattern.length(), value.length() );

        pattern = pattern.substring( 0, length ).replaceAll( "T", "'T'" );
        value   = value.substring( 0, length );

        try {
            parse( value, pattern );
            return this;
        } catch( ParseException e ) {
            for( String isoFormat : ISO_8601_COMPATIBLE_FORMATS ) {
                try {
                    return setDate( date, isoFormat );
                } catch( ParseException isoError ) {}
            }
            throw e;
        }

    }

    private String strip( String format ) {
        return format.replaceAll( "[^yMdTHmsSZ]", "" );
    }

    private String toDateDigit( String value ) {
        return value.replaceAll( "[^0-9T\\+]", "" );
    }

    private void parse( String val, String pattern ) {
        SimpleDateFormat sdf = new SimpleDateFormat( pattern );
        try {
            currentTime.setTime( sdf.parse(val) );
        } catch( java.text.ParseException e ) {
            throw new ParseException( e, e.getMessage() );
        }
    }

    /**
     * set date
     *
     * @param date date
     * @return self instance
     */
    public NDate setDate( Date date ) {
        this.currentTime.setTime( date ); return this;
    }

    /**
     * set date
     *
     * @param date      date
     * @param zoneId    zone id
     * @return  self instance
     */
    public NDate setDate( LocalDateTime date, ZoneId zoneId ) {
        Date realDate = Date.from( date.atZone(zoneId).toInstant() );
        setDate( realDate );
        return this;
    }

    /**
     * set date
     *
     * @param date date
     * @return self instance
     */
    public NDate setDate( LocalDateTime date ) {
        return setDate( date, ZoneId.systemDefault() );
    }

    /**
     * set date
     *
     * @param date date
     * @return self instance
     */
    public NDate setDate( LocalDate date ) {
        return setDate( date, ZoneId.systemDefault() );
    }

    /**
     * set date
     *
     * @param date      date
     * @param zoneId    zone id
     * @return  self instance
     */
    public NDate setDate( LocalDate date, ZoneId zoneId ) {
        Date realDate = Date.from( date.atStartOfDay(zoneId).toInstant() );
        setDate( realDate );
        return this;
    }

    /**
     * set date
     *
     * @param date the milliseconds since January 1, 1970, 00:00:00 GMT.
     * @return self instance
     */
    public NDate setDate( long date ) {
    	this.currentTime.setTime( new Date(date) );
        return this;
    }

    /**
     * set date
     *
     * @param date date
     * @return self instance
     */
    public NDate setDate( Calendar date ) {
        this.currentTime = (Calendar) date.clone();
        return this;
    }

    /**
     * set date
     *
     * @param date date
     * @return self instance
     */
    public NDate setDate( NDate date ) {
        this.currentTime = (Calendar) date.toCalendar().clone();
        return this;
    }

    /**
     * convert to date
     *
     * @return Date instance
     */
    public Date toDate() {
        return this.currentTime.getTime();
    }

    /**
     * convert to calendar.
     *
     * @return Calendar instance
     */
    public Calendar toCalendar() {
        return this.currentTime;
    }

    public LocalTime toLocalTime( ZoneId zoneId ) {
        return toZonedDateTime( zoneId ).toLocalTime();
    }

    public LocalTime toLocalTime() {
        return toLocalTime( ZoneId.systemDefault() );
    }

    public LocalDateTime toLocalDateTime( ZoneId zoneId ) {
        return toZonedDateTime( zoneId ).toLocalDateTime();
    }

    public LocalDateTime toLocalDateTime() {
        return toLocalDateTime( ZoneId.systemDefault() );
    }

    public LocalDate toLocalDate( ZoneId zone ) {
        return toZonedDateTime( zone ).toLocalDate();
    }

    public LocalDate toLocalDate() {
        return toLocalDate( ZoneId.systemDefault() );
    }

    public ZonedDateTime toZonedDateTime( ZoneId zone ) {
        return toDate().toInstant().atZone( zone );
    }

    public ZonedDateTime toZonedDateTime() {
        return toZonedDateTime( ZoneId.systemDefault() );
    }

    /**
     * get time in milli-seconds
     *
     * @return milli-seconds time value
     */
    public long toTime() {
        return this.currentTime.getTimeInMillis();
    }

    @Override
    public String toString() {
        return toString( DEFAULT_FORMAT );
    }

    /**
     * convert to String in specific format
     *
     * @param format date format [YYYY:year, MM:month, DD:day, HH:hour, MI:minute, SS:second, FFF:milli-second ]
     * @return 포맷에 맞는 날짜 문자열
     */
    public String toString( String format ) {
        SimpleDateFormat sdf = new SimpleDateFormat( toDateFormat(format,false) );
        return sdf.format( toDate() );
    }

    /**
     * convert format to SimpleDateFormat
     *
     * @param format    date format [YYYY:year, MM:month, DD:day, HH:hour, MI:minute, SS:second, FFF:milli-second ]
     * @param doStrip   strip non date-digit ( remove all except 'yyyyMMddTHHmmssSSSZZZ' )
     * @return SimpleDateFormat
     */
    private String toDateFormat( String format, boolean doStrip ) {

        if( Strings.isEmpty(format) ) return FULL_FORMAT;

        if( ALL_FORMATS.contains(format) ) return format;

        format = format
        	.replaceAll( "YYYY", "yyyy" )
            .replaceAll( "([^D])DD([^D]|$)", "$1dd$2" )
            .replaceAll( "MI",     "mm"   )
            .replaceAll( "([^S])SS([^S]|$)", "$1ss$2" )
            .replaceAll( "F",    "S"    );

        if( doStrip ) format = strip( format );

        return format;

    }

    /**
     * truncate date's hour/minute/second/millisecond and remain it's date related properties only
     *
     * @return truncated date ( 00:00:00 )
     */
    public NDate truncate() {
        return setHour( 0 ).setMinute( 0 ).setSecond( 0 ).setMillisecond( 0 );
    }

    /**
     * get year
     *
     * @return year
     */
    public int getYear() {
        return currentTime.get( Calendar.YEAR );
    }

    /**
     * get month
     *
     * @return month ( 1 - 12 )
     */
    public int getMonth() {
        return currentTime.get( Calendar.MONTH ) + 1;
    }

    /**
     * get day
     *
     * @return day
     */
    public int getDay() {
        return currentTime.get( Calendar.DATE );
    }

    /**
     * get week day
     *
     * @return week day ( 1:sunday, 2:monday, 3:thuesday, 4:wednesday, 5:thursday, 6:friday, 7:saturdays )
     */
    public int getWeekDay() {
        return currentTime.get( Calendar.DAY_OF_WEEK );
    }

    /**
     * get hours
     *
     * @return hours ( 0 - 24 )
     */
    public int getHour() {
        return currentTime.get( Calendar.HOUR_OF_DAY );
    }

    /**
     * get miniutes
     *
     * @return minutes (0-59)
     */
    public int getMinute() {
        return currentTime.get( Calendar.MINUTE );
    }

    /**
     * get seconds
     *
     * @return seconds (0-59)
     */
    public int getSecond() {
        return currentTime.get( Calendar.SECOND );
    }

    /**
     * get milli-seconds
     *
     * @return milli-seconds
     */
    public int getMillisecond() {
        return currentTime.get( MILLISECOND );
    }

    /**
     * add or subtract year
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addYear( int value ) {
        currentTime.add( Calendar.YEAR, value );
        return this;
    }

    /**
     * set year
     *
     * @param value value to set
     * @return  self instance
     */
    public NDate setYear( int value ) {
        currentTime.set( Calendar.YEAR, value );
        return this;
    }

    /**
     * adds or subtracts month
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addMonth( int value ) {
        currentTime.add( Calendar.MONTH, value );
        return this;
    }

    /**
     * set month
     *
     * @param value value to set
     * @return  self instance
     */
    public NDate setMonth( int value ) {
        currentTime.set( Calendar.MONTH, value );
        return this;
    }

    /**
     * adds or subtracts day
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addDay( int value ) {
        currentTime.add( Calendar.DATE, value );
        return this;
    }

    /**
     * set day
     *
     * @param value value to set
     * @return self instance
     */
    public NDate setDay( int value ) {
        currentTime.set( Calendar.DATE, value );
        return this;
    }

    /**
     * adds or subtracts hour
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addHour( int value ) {
        currentTime.add( Calendar.HOUR_OF_DAY, value );
        return this;
    }

    /**
     * set hour (24-hour clock)
     *
     * @param value value to set
     * @return self instance
     */
    public NDate setHour( int value ) {
        currentTime.set( Calendar.HOUR_OF_DAY, value );
        return this;
    }

    /**
     * adds or subtracts minute
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addMinute( int value ) {
        currentTime.add( Calendar.MINUTE, value );
        return this;
    }

    /**
     * set minute
     *
     * @param value value to set
     * @return self instance
     */
    public NDate setMinute( int value ) {
        currentTime.set( Calendar.MINUTE, value );
        return this;
    }

    /**
     * adds or subtracts second
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addSecond( int value ) {
        currentTime.add( Calendar.SECOND, value );
        return this;
    }

    /**
     * set second
     *
     * @param value value to set
     * @return  self instance
     */
    public NDate setSecond( int value ) {
        currentTime.set( Calendar.SECOND, value );
        return this;
    }

    /**
     * adds or subtracts mili-second
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addMillisecond( int value ) {
        currentTime.add( MILLISECOND, value );
        return this;
    }

    /**
     * set mili-second
     *
     * @param value value to add or subtract
     * @return  self instance
     */
    public NDate setMillisecond( int value ) {
        currentTime.set( MILLISECOND, value );
        return this;
    }


    /**
     * get beginning of month date from current date.
     *
     * <pre>
     * NDate date = new NDate( "2012.02.29 13:21:41" );
     *
     * System.out.println( date.beginningOfMonth() ); → '2012.02.01 00:00:00'
     * </pre>
     *
     * @return new NDate to be setted with beginning of month date
     */
    public NDate beginningOfMonth() {

        Calendar newDate = Calendar.getInstance();
        newDate.set( getYear(), getMonth() - 1, 1, 0, 0, 0 );
        newDate.set( MILLISECOND, 0 );

        return new NDate( newDate );

    }

    /**
     * get end of month date from current date.
     *
     * <pre>
     * NDate date = new NDate( "2012.02.29 13:21:41" );
     *
     * System.out.println( date.endOfMonth() ); → '2012.02.29 23:59:59.999'
     * </pre>
     *
     * @return new NDate to be setted with end of month date
     *
     */
    public NDate endOfMonth() {

        Calendar newDate = Calendar.getInstance();

        newDate.set( getYear(), getMonth(), 1, 0, 0, 0 );
        newDate.set( MILLISECOND, 0 );
        newDate.add( MILLISECOND, -1 );

        return new NDate( newDate );

    }

    /**
     * get days difference between them
     *
     * @param date date to compare
     * @return days difference
     */
    public int betweenDays( NDate date ) {

        Calendar c1 = new NDate( toString("YYYYMMDD" ) ).toCalendar();
        Calendar c2 = new NDate( date.toString("YYYYMMDD" ) ).toCalendar();

        long diff = difference( c1, c2 );

        return (int) ( diff / 86400000 ); // 24 * 60 * 60 * 1000

    }

    /**
     * get hours difference between them
     *
     * @param date date to compare
     * @return hours difference
     */
    public int betweenHours( NDate date ) {

        Calendar c1 = new NDate( toString("YYYYMMDDHHMISS" ) ).toCalendar();
        Calendar c2 = new NDate( date.toString("YYYYMMDDHHMISS" ) ).toCalendar();

        long diff = difference( c1, c2 );

        return (int) ( diff / 3600000 ); // 60 * 60 * 1000

    }

    /**
     * get times difference between them
     *
     * @param date date to compare
     * @return times difference
     */
    public long betweenTimes( NDate date ) {
        return difference( this.currentTime, date.currentTime );
    }

    /**
     * compare NDate
     *
     * @param   date    date to compare
     * @return  compared result (-1: lesser, 0: equal, 1: later)
     */
    public int compareTo( NDate date ) {

        long srcTime = toCalendar().getTimeInMillis();
        long trgTime = date.toCalendar().getTimeInMillis();
        return ( srcTime < trgTime ? -1 : (srcTime == trgTime ? 0 : 1) );

    }

    /**
     * compare to be greater than other
     *
     * @param date date to compare
     * @return true if it is greater than other.
     */
    public boolean greaterThan( NDate date ) {
        return compareTo( date ) > 0 ;
    }

    /**
     * compare to be equal or greater than other
     *
     * @param date date to compare
     * @return true if it is equal or greater than other.
     */
    public boolean greaterThanOrEqual( NDate date ) {
        return compareTo( date ) >= 0;
    }

    /**
     * compare to be less than other
     *
     * @param date date to compare
     * @return true if it is less than other.
     */
    public boolean lessThan( NDate date ) {
        return compareTo( date ) < 0;
    }

    /**
     * compare to be equal or less than other
     *
     * @param date date to compare
     * @return true if it is equal or less than other.
     */
    public boolean lessThanOrEqual( NDate date ) {
        return compareTo( date ) <= 0;
    }

    /**
     * get difference milli-seconds between Calendar instances.
     *
     * @param c1 first
     * @param c2 second
     * @return difference (unit:milli-second)
     */
    private long difference( Calendar c1, Calendar c2 ) {

        long time1 = c1.getTimeInMillis();
        long time2 = c2.getTimeInMillis();

        return Math.abs( time1 - time2 );

    }

    @Override
    public NDate clone() {
        return new NDate( toDate() );
    }

    @Override
    public boolean equals( Object object ) {

        if( object == null ) return false;
        if( this == object ) return true;

        if( object instanceof NDate ) {
            NDate nDate = (NDate) object;
            return currentTime.equals( nDate.currentTime );
        } else if( object instanceof Date ) {
            return toDate().equals( object );
        } else if( object instanceof Calendar ) {
            return currentTime.equals( object );
        }

        return false;

    }

}