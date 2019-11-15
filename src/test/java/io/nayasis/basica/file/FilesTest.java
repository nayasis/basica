package io.nayasis.basica.file;

import io.nayasis.basica.base.Classes;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
public class FilesTest {

    @Test
    public void readFromResource() throws MalformedURLException {

        URL path = Classes.getResource( "/xml/Deformed.xml" );

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        Assert.assertFalse( txt1.isEmpty() );
        Assert.assertFalse( txt2.isEmpty() );
        Assert.assertEquals( txt1, txt2 );

    }

    @Test
    public void readFromFile() {

        String path = Files.getRootPath() + "/xml/Grammer.xml";

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        Assert.assertFalse( txt1.isEmpty() );
        Assert.assertFalse( txt2.isEmpty() );
        Assert.assertEquals( txt1, txt2 );

    }


}