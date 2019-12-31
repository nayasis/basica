package io.nayasis.basica.model;

import io.nayasis.basica.base.Strings;
import io.nayasis.basica.exception.unchecked.UncheckedIOException;
import io.nayasis.basica.file.Files;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@NoArgsConstructor
public class NProperties extends Properties {

    public NProperties( Properties defaults ) {
        super( defaults );
    }

    public NProperties( String resourcePath ) {
        load( resourcePath );
    }

    public NProperties( URL url ) {
        load( url );
    }

    public NProperties load( String filePath ) throws UncheckedIOException {
        return loadProperties( Files.toStream(filePath) );
    }

    public NProperties load( URL url ) throws UncheckedIOException {
        return loadProperties( Files.getResourceAsStream(url) );
    }

    public NProperties load( File file ) throws UncheckedIOException {
        return loadProperties( Files.toStream(file) );
    }

    public NProperties load( Path path ) throws UncheckedIOException {
        return loadProperties( Files.toStream(path) );
    }

    private NProperties loadProperties( InputStream inputStream ) throws UncheckedIOException {
        if( inputStream == null ) return this;
        try {
            String charset = Files.getCharset( inputStream );
            load( new BufferedReader( new InputStreamReader( inputStream, charset ) ) );
            return this;
        } catch ( IOException e ) {
            throw new UncheckedIOException( e );
        } finally {
            try { inputStream.close(); } catch ( IOException e ) {}
        }
    }

    public String get( String key ) {
        return super.getProperty( key );
    }

    public String getOrDefault( String key, String defaultValue ) {
        return super.getProperty( key, defaultValue );
    }

    public NProperties set( String key, Object value ) {
        super.setProperty( key, Strings.nvl(value) );
        return this;
    }

    public Map<String,String> toMap() {
        Map<String,String> map = new HashMap<>();
        this.forEach( ( key, val ) -> map.put(
            key == null ? null : key.toString(),
            val == null ? null : val.toString()
        ));
        return map;
    }

}