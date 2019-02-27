package io.nayasis.common.model;

import io.nayasis.common.file.Files;
import io.nayasis.common.reflection.Reflector;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

@Slf4j
public class MessagesTest {

    @Test
    public void loadSingleFile() {

        String path = Files.getRootPath() + "/message/message.en.prop";

        Messages.load( path );

//        Assert.assertEquals( "세션이 종료되었습니다.", Messages.get("err.session.expired") );
        Assert.assertEquals( "Session is expired.", Messages.get("err.session.expired") );

    }

    @Test
    public void loadMulti() {

        String path = Files.getRootPath() + "/message/**.prop";

        Messages.load( path );

        log.debug( Messages.get("err.session.expired") );

//        Assert.assertEquals( "세션이 종료되었습니다.", Messages.get("err.session.expired") );
//        Assert.assertEquals( "Session is expired.", Messages.get("err.session.expired") );


    }

}