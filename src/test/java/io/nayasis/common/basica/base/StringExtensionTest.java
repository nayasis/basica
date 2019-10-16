package io.nayasis.common.basica.base;

import lombok.Data;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

@Slf4j
@ExtensionMethod({String.class, StringExtension.class})
public class StringExtensionTest {

    @Test
    public void main() {

        Assert.assertEquals( "a", "{}".fmt("a") );

        String temp = null;
        Assert.assertEquals( "", temp.fmt("a") );
        Assert.assertEquals( "", temp.toLowerCase() );

        Assert.assertEquals( "camelCase", "camel_case".toCamel() );
        Assert.assertEquals( "camel_case", "camelCase".toSnake() );

        String json = "{'a':1, 'b':2, 'c':'abcd'}";

        Assert.assertEquals( 1, json.toMap().get("a") );
        Assert.assertEquals( "abcd", json.toMap().get("c") );
        Assert.assertEquals( "abcd", json.toBean(Bean.class).getC() );

        Bean param = json.toBean(Bean.class);

        Assert.assertEquals( "1 is 2 or abcd", "{a} is {b} or {c}".fmt(json.toMap()) );

    }

}

@Data
class Bean {
    private int    a;
    private int    b;
    private String c;
}
