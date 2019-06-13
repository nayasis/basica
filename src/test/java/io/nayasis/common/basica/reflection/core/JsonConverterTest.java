package io.nayasis.common.basica.reflection.core;

import io.nayasis.common.basica.reflection.mapper.NObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class JsonConverterTest {

    private JsonConverter jsonConverter = new JsonConverter( new NObjectMapper() );

    @Test
    public void toJson() {

        Person person = new Person("nayasis", 40);

        log.debug( jsonConverter.toJson( person, true ) );

    }

    @Test
    public void toJson1() {
    }

    @Test
    public void toJson2() {
    }

    @Test
    public void toJson3() {
    }

    @Test
    public void toJson4() {
    }

    @Test
    public void toJson5() {
    }

    @Test
    public void toJsonWithoutNull() {
    }

    @Test
    public void toJsonWithoutNull1() {
    }

    @Test
    public void toFlatternMap() {
    }

    @Test
    public void toUnflatternMap() {
    }

    @Test
    public void readTree() {
    }

    @Test
    public void isJson() {
    }

    @Test
    public void toBeanFrom() {
    }

    @Test
    public void toBeanFrom1() {
    }

    @Test
    public void toListFrom() {
    }

    @Test
    public void toCollectionFrom() {
    }

    @Test
    public void toMapFrom() {
    }

    @Test
    public void toMapFrom1() {
    }
}