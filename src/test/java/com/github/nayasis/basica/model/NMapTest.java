package com.github.nayasis.basica.model;

import com.github.nayasis.basica.expression.Expression;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class NMapTest {

    @Test
    public void printString() {

        NMap<String,String> map = new NMap();

        map.put( "controller", "com.github.nayasis.model.NMapTest.printString()" );

        log.debug( "\n" + map.toString( false, false ) );

    }

    @Test
    public void byExpression() {

        NMap map = data();

        Assertions.assertTrue(  map.containsKey( Expression.of( "list[0]['tag']" ) ) );
        Assertions.assertTrue(  map.containsKey( Expression.of( "list[0].tag" ) ) );
        Assertions.assertFalse( map.containsKey( Expression.of( "list[0].tag_1" ) ) );

        Assertions.assertEquals( "게임,아케이드,도망가기,탈출하기,달리기,달리기게임,스테이지,점프,점프게임,관리", map.get( Expression.of( "list[0]['tag']" ) ) );
        Assertions.assertEquals( "게임,아케이드,도망가기,탈출하기,달리기,달리기게임,스테이지,점프,점프게임,관리", map.get( Expression.of( "list[0].tag" ) ) );
        Assertions.assertEquals( null, map.get( Expression.of( "list[0].tag_1" ) ) );

        Assertions.assertEquals( "merong", map.getOrDefault( Expression.of( "list[0].tag_1" ), "merong" ) );

    }

    @Test
    public void jsonToNmap() {
        log.debug( "{}", data() );
    }

    private NMap data() {
        return new NMap( "{\n" +
            "    \"list\": [\n" +
            "        {\n" +
            "            \"no\": 1,\n" +
            "            \"item\": \"D000644350\",\n" +
            "            \"tag\": \"게임,아케이드,도망가기,탈출하기,달리기,달리기게임,스테이지,점프,점프게임,관리\",\n" +
            "            \"adult\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"no\": 2,\n" +
            "            \"item\": \"D000655474\",\n" +
            "            \"tag\": \"게임,카드,카드게임,겜블,고포류,화투,여행,고스톱\",\n" +
            "            \"adult\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"no\": 3,\n" +
            "            \"item\": \"D000647651\",\n" +
            "            \"tag\": \"게임,rpg,학습,학습게임,탐험,위한,파이프\",\n" +
            "            \"adult\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"no\": 4,\n" +
            "            \"item\": \"D000656772\",\n" +
            "            \"tag\": \"게임,캐주얼,퍼즐,숫자,숫자게임,연애,숫자퍼즐,사랑,스도쿠\",\n" +
            "            \"adult\": false\n" +
            "        },\n" +
            "        {\n" +
            "            \"no\": 5,\n" +
            "            \"item\": \"D000656731\",\n" +
            "            \"tag\": \"시뮬레이션,연애,오브,연애시뮬레이션,킹,킹오브\",\n" +
            "            \"adult\": false\n" +
            "        }\n" +
            "    ]\n" +
            "}" );
    }

}