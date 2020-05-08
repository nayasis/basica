package com.github.nayasis.basica.base;

import lombok.Data;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
@ExtensionMethod({String.class, StringExtension.class})
public class StringExtensionTest {

    @Test
    public void main() {

        Assertions.assertEquals( "a", "{}".fmt("a") );

        String temp = null;
        Assertions.assertEquals( "", temp.fmt("a") );
        Assertions.assertEquals( "", temp.toLowerCase() );

        Assertions.assertEquals( "camelCase", "camel_case".toCamel() );
        Assertions.assertEquals( "camel_case", "camelCase".toSnake() );

        String json = "{'a':1, 'b':2, 'c':'abcd'}";

        Assertions.assertEquals( 1, json.toMap().get("a") );
        Assertions.assertEquals( "abcd", json.toMap().get("c") );
        Assertions.assertEquals( "abcd", json.toBean(Bean.class).getC() );

        Bean param = json.toBean(Bean.class);

        Assertions.assertEquals( "1 is 2 or abcd", "{a} is {b} or {c}".fmt(json.toMap()) );

    }

}

@Data
class Bean {
    private int    a;
    private int    b;
    private String c;
}
