package com.github.nayasis.basica.file.handler;

import lombok.Cleanup;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import com.github.nayasis.basica.file.Files;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Zip File Handler
 *
 * @author 1002159
 * @since 2015-11-02
 */
public class ZipFileHandler {

    private void decompress( ArchiveInputStream input, File outputDirectory ) {

        ArchiveEntry entry ;

        try ( ArchiveInputStream zis = input ) {
            while ( (entry = zis.getNextEntry()) != null ) {

                File target = new File ( outputDirectory, entry.getName() );
                target.getParentFile().mkdirs();

                if ( entry.isDirectory() ){
                    target.mkdirs();
                } else {

                    int     length = 0;
                    byte [] buffer = new byte[ 1024 * 8 ];

                    @Cleanup BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(target) );
                    while ( (length = zis.read(buffer)) >= 0 ){
                        bos.write(buffer, 0, length);
                    }

                }
            }
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }

    }

    public void unzip( File target, File directory, Charset charset ) {
        try {
            ArchiveInputStream stream = new ZipArchiveInputStream(
                new FileInputStream(target), charset.name(), true );
            decompress( stream, directory );
        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

    private void compress( File target, ArchiveOutputStream outputStream ) {

        List<File> files = new ArrayList<>();

        String basePath;

        if( target.isDirectory() ) {
            basePath = target.getPath();
            for( Path path : Files.findFile( target.getPath(), -1, "**.*" ) ) {
                files.add( path.toFile() );
            }
        } else {
            basePath = target.getParent();
            files.add( target );
        }

        FileInputStream     fis = null;
        ArchiveOutputStream zos = outputStream;

        try {

            for( File file : files ) {

                String       name  = Files.relativePath( basePath, file.getPath() );
                ArchiveEntry entry = zos.createArchiveEntry( file, name );

                zos.putArchiveEntry( entry );

                int length;
                byte[] buf = new byte[ 1024 * 8 ];

                fis = new FileInputStream( file );

                while ( (length = fis.read(buf, 0, buf.length)) >= 0 ){
                    zos.write(buf, 0, length);
                }

                fis.close();
                zos.closeArchiveEntry();

            }

        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        } finally {
            if( fis != null ) try { fis.close(); } catch( IOException e ) {}
            if( zos != null ) try { zos.closeArchiveEntry(); } catch( IOException e ) {}
        }

    }

    public void zip( File source, File zipfile, Charset charset ) {
        try {

            ZipArchiveOutputStream stream = new ZipArchiveOutputStream( zipfile );
            stream.setEncoding( charset.name() );
            stream.setCreateUnicodeExtraFields( ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS );
            compress( source, stream );

        } catch( IOException e ) {
            throw new UncheckedIOException( e );
        }
    }

}
