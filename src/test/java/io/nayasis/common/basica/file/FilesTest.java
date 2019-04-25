package io.nayasis.common.basica.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
public class FilesTest {

    @Test
    public void read() {

        String path = Files.getRootPath() + "/xml/Deformed.xml";

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        Assert.assertEquals( txt1, txt2 );

    }

    @Test
    public void read_2() {

        String path = Files.getRootPath() + "/xml/Grammer.xml";

        String txt1 = Files.readFrom( path, "UTF-8" );
        String txt2 = Files.readFrom( path );

        Assert.assertEquals( txt1, txt2 );

    }


}