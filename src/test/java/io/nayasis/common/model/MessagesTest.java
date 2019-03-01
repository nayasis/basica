package io.nayasis.common.model;

import io.nayasis.common.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

@Slf4j
public class MessagesTest {

    @Test
    public void loadSingleFile() {

        String path = Files.getRootPath() + "/message/message.en.prop";

        Messages.load( path );

        Assert.assertEquals( "Session is expired.", Messages.get("err.session.expired") );
        Assert.assertEquals( "notExistCode", Messages.get("notExistCode") );

    }

    @Test
    public void loadMulti() {

        String path = Files.getRootPath() + "/message/**.prop";

        Messages.load( path );

        Assert.assertEquals( "Session is expired.", Messages.get( Locale.ENGLISH,"err.session.expired") );
        Assert.assertEquals( "Session is expired.", Messages.get( Locale.UK,"err.session.expired") );
        Assert.assertEquals( "세션이 종료되었습니다.", Messages.get( Locale.KOREAN,"err.session.expired") );

    }

}