package io.nayasis.common.model;

import io.nayasis.common.base.Strings;
import io.nayasis.common.exception.unchecked.UncheckedIOException;
import io.nayasis.common.file.Files;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class NProperties extends Properties {

    public NProperties() {}

    public NProperties( Properties defaults ) {
        super( defaults );
    }

    public NProperties( String resourcePath ) {
        load( resourcePath );
    }

    public NProperties load( String resourcePath ) throws UncheckedIOException {
        BufferedInputStream inputStream = new BufferedInputStream( Files.getResource( resourcePath ) );
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
