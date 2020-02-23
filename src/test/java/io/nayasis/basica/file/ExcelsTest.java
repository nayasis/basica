package io.nayasis.basica.file;

import io.nayasis.basica.base.Classes;
import io.nayasis.basica.model.NList;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class ExcelsTest {

    @Test
    void readFrom() {

        NList sheet = Excels.readFrom(Classes.getResourceAsStream("/file/option.xlsx"), "run");

        log.debug( "\n{}", sheet );

        Assertions.assertEquals( sheet.keySize(), 12 );
        Assertions.assertEquals( sheet.getRow(0).get("0"), "item" );
        Assertions.assertEquals( sheet.getRow(1).get("1"), "core" );
        Assertions.assertEquals( sheet.getRow(5).get("2"), "label" );

    }
}