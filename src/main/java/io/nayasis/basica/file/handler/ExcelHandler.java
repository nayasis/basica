package io.nayasis.basica.file.handler;

import io.nayasis.basica.exception.unchecked.InvalidArgumentException;
import io.nayasis.basica.file.Files;
import io.nayasis.basica.file.handler.implement.ApachePoiReader;
import io.nayasis.basica.file.handler.implement.ApachePoiWriter;
import io.nayasis.basica.model.NList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Excel file handler
 *
 * @author nayasis@gmail.com
 * @since 2020-04-27
 */
@Slf4j
@NoArgsConstructor
public class ExcelHandler {

    private static final String DEFAULT_SHEET_NAME = "Sheet1";
    private static final String ERROR_MESSAGE      = "There is no dependency to Apache POI [org.apache.poi.poi-ooxml:4.1.2]";

    private Object resource;

    @Getter @Setter @Accessors(fluent=true)
    private boolean useHeader = true;

    @Getter @Setter @Accessors(fluent=true)
    private String type;

    /**
     * constructor
     *
     * @param stream    inputstream for reading
     */
    public ExcelHandler( InputStream stream ) {
        set( stream );
    }

    /**
     * constructor
     *
     * @param stream    outputstream for writing
     */
    public ExcelHandler( OutputStream stream ) {
        set( stream );
    }

    /**
     * constructor
     *
     * @param file  excel file
     */
    public ExcelHandler( File file ) {
        set( file );
    }

    /**
     * constructor
     *
     * @param file  excel file
     */
    public ExcelHandler( String file ) {
        set( file );
    }

    /**
     * constructor
     *
     * @param file  excel file
     */
    public ExcelHandler( Path file ) {
        set( file );
    }

    /**
     * set resource to handle excel.
     * <p>resource will be closed when read done.
     *
     * @param stream    inputstream
     * @return  self instance
     */
    public ExcelHandler set( InputStream stream ) {
        resource = stream;
        return this;
    }

    /**
     * set resource to handle excel.
     * <p>resource will be closed when write done.
     *
     * @param stream    outputstream
     * @return  self instance
     */
    public ExcelHandler set( OutputStream stream ) {
        resource = stream;
        return this;
    }

    /**
     * set resource to handle file
     *
     * @param file  excel file
     * @return  self instance
     */
    public ExcelHandler set( File file ) {
        type( Files.getExtension(file) );
        resource = Files.normalizeSeparator( file.getPath() );
        return this;
    }

    /**
     * set resource to handle file
     *
     * @param file  excel file
     * @return  self instance
     */
    public ExcelHandler set( String file ) {
        type( Files.getExtension(file) );
        resource = Files.normalizeSeparator( file );
        return this;
    }

    /**
     * set resource to handle file
     *
     * @param file  excel file
     * @return  self instance
     */
    public ExcelHandler set( Path file ) {
        type( Files.getExtension(file) );
        resource = Files.normalizeSeparator( file );
        return this;
    }

    private InputStream inputStream() {
        if( resource == null )
            throw new InvalidArgumentException( "resource is not assigned." );
        if( resource instanceof InputStream )
            return (InputStream) resource;
        if( resource instanceof OutputStream )
            throw new InvalidArgumentException( "resource(InputStream) can not be read." );
        return Files.toInputStream( (String) resource );
    }

    private OutputStream outputStream() {
        if( resource == null )
            throw new InvalidArgumentException( "resource is not assigned." );
        if( resource instanceof OutputStream )
            return (OutputStream) resource;
        if( resource instanceof InputStream )
            throw new InvalidArgumentException( "resource(OutputStream) can not be read." );
        return Files.toOutputStream( (String) resource );
    }

    private ApachePoiWriter writer() {
        try {
            return new ApachePoiWriter();
        } catch ( Throwable e ) {
            throw new LinkageError( ERROR_MESSAGE, e );
        }
    }

    private ApachePoiReader reader() {
        try {
            return new ApachePoiReader();
        } catch ( Throwable e ) {
            throw new LinkageError( ERROR_MESSAGE, e );
        }
    }

    /**
     * return all data
     *
     * @return  key is sheet name, value is grid data.
     */
    public Map<String,NList> readAll() {
        return reader().readAll( inputStream(), useHeader() );
    }

    /**
     * read data from first sheet
     *
     * @return  grid data
     */
    public NList read() {
        return reader().readSheet( inputStream(), useHeader() );
    }

    /**
     * read data from first sheet
     *
     * @param type  generic type of return value.
     * @param <T>   expected class to return
     * @return  grid data
     */
    public <T> List<T> read( Class<T> type ) {
        return  reader().readSheet( inputStream(), true ).toList( type );
    }

    /**
     * read data
     *
     * @param sheetName sheet name to retrieve data
     * @return  grid data
     */
    public NList read( String sheetName ) {
        return reader().readSheet( inputStream(), sheetName, useHeader() );
    }

    /**
     * read data from first sheet
     *
     * @param sheetName sheet name to retrieve data
     * @param type      generic type of return value.
     * @param <T>       expected class to return
     * @return  grid data
     */
    public <T> List<T> read( String sheetName, Class<T> type ) {
        return  reader().readSheet( inputStream(), sheetName, true ).toList( type );
    }

    /**
     * write all data
     *
     * @param sheets    key is sheet name, value is grid data.
     */
    public void writeAll( Map<String,?> sheets ) {
        writer().write( outputStream(), sheets, type(), useHeader() );
    }

    /**
     * write data
     *
     * @param sheet     grid data
     * @param sheetName sheet name
     */
    public void write( Collection<?> sheet, String sheetName ) {
        writer().writeSheet( outputStream(), sheet, sheetName, type(), useHeader() );
    }

    /**
     * write data
     *
     * @param sheet grid data
     */
    public void write( Collection<?> sheet ) {
        write( sheet, DEFAULT_SHEET_NAME );
    }

    /**
     * write data
     *
     * @param sheet     grid data
     * @param sheetName sheet name
     */
    public void write( NList sheet, String sheetName ) {
        writer().writeSheet( outputStream(), sheet, sheetName, type(), useHeader() );
    }

    /**
     * write data
     *
     * @param sheet grid data
     */
    public void write( NList sheet ) {
        write( sheet, DEFAULT_SHEET_NAME );
    }

}