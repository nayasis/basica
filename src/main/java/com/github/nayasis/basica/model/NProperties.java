package com.github.nayasis.basica.model;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.exception.unchecked.UncheckedIOException;
import com.github.nayasis.basica.file.Files;
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
        loadProperties( Files.toInputStream(resourcePath), Files.detectCharset(resourcePath) );
    }

    public NProperties( URL url ) {
        loadProperties( Files.toInputStream(url), Files.detectCharset(url) );
    }

    public NProperties( File file ) {
        loadProperties( Files.toInputStream(file), Files.detectCharset(file) );
    }

    public NProperties( Path path ) {
        loadProperties( Files.toInputStream(path), Files.detectCharset(path) );
    }

    private NProperties loadProperties( InputStream inputStream, String charset ) throws UncheckedIOException {
        if( inputStream == null ) return this;
        try {
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