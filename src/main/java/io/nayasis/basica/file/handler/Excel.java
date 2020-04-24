package io.nayasis.basica.file.handler;

import io.nayasis.basica.exception.unchecked.InvalidArgumentException;
import io.nayasis.basica.file.Files;
import io.nayasis.basica.file.handler.implement.ApachePoiWriter;
import io.nayasis.basica.model.NList;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

@Slf4j
public class Excel {

    private Object resource;

    @Getter @Setter @Accessors(fluent=true)
    private boolean useHeader;

    @Getter @Setter @Accessors(fluent=true)
    private boolean xlsx = true;

    public Excel set( InputStream stream ) {
        resource = stream;
        return this;
    }

    public Excel set( OutputStream stream ) {
        resource = stream;
        return this;
    }

    public Excel set( File file ) {
        setXlsx( Files.getExtension(file) );
        resource = Files.normalizeSeparator( file.getPath() );
        return this;
    }

    public Excel set( String file ) {
        setXlsx( Files.getExtension(file) );
        resource = Files.normalizeSeparator( file );
        return this;
    }

    public Excel set( Path file ) {
        setXlsx( Files.getExtension(file) );
        resource = Files.normalizeSeparator( file );
        return this;
    }

    private void setXlsx( String extension ) {
        xlsx = "xlsx".equalsIgnoreCase( extension );
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
        return new ApachePoiWriter();
    }

    private ApachePoiReader reader() {
        return new ApachePoiReader();
    }


    public NList readAll() {
        return null;
    }

}
