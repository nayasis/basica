package io.nayasis.basica.model;

import io.nayasis.basica.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

@Slf4j
public class MessagesTest {

    @BeforeEach
    public void initMessagePool() {
        Messages.clear();
    }

    @Test
    public void loadFromFile() {

        String path = Files.getRootPath(this.getClass()) + "/message/message.en.prop";

        Messages.loadFromFile( path );

        Assertions.assertEquals( "Session is expired.", Messages.get("err.session.expired") );
        Assertions.assertEquals( "notExistCode", Messages.get("notExistCode") );

    }

    @Test
    public void loadFromResource() {

        Messages.loadFromResource( "/message/**.prop" );

        Assertions.assertEquals( "Session is expired.", Messages.get( Locale.ENGLISH,"err.session.expired") );
        Assertions.assertEquals( "Session is expired.", Messages.get( Locale.UK,"err.session.expired") );
        Assertions.assertEquals( "세션이 종료되었습니다.", Messages.get( Locale.KOREAN,"err.session.expired") );

    }

}