package com.github.nayasis.basica.exception.unchecked;

import com.github.nayasis.basica.model.Messages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BaseRuntimeExceptionTest {

    @Test
    public void errorMessage() {

        Messages.put( "merong", "NO MERONG : {}" );

        BaseRuntimeException e = new BaseRuntimeException( "merong", 1 );

        Assertions.assertEquals( e.getMessage(), "NO MERONG : 1" );

    }

}