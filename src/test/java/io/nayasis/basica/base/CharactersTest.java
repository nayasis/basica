package io.nayasis.basica.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharactersTest {

    @Test
    public void setFullwidth() {

    }

    @Test
    public void getFullwidth() {
    }

    @Test
    public void resolveKorean() {
    }

    @Test
    public void hasHangulJongsung() {
    }

    @Test
    public void isHalfWidth() {
    }

    @Test
    public void getFontWidth() {
    }

    @Test
    public void isKorean() {
    }

    @Test
    public void isJapanese() {
        Assertions.assertTrue( Characters.isJapanese('か') );
        Assertions.assertTrue( Characters.isJapanese('フ') );
        Assertions.assertTrue( Characters.isJapanese('ﾌ') );
        Assertions.assertTrue( ! Characters.isJapanese('！') );
        Assertions.assertTrue( Characters.isJapanese('。') );
        Assertions.assertTrue( ! Characters.isJapanese('A') );
        Assertions.assertTrue( ! Characters.isJapanese('정') );
        Assertions.assertTrue( ! Characters.isJapanese('ㄱ') );
        Assertions.assertTrue( ! Characters.isJapanese('鄭') );
    }

    @Test
    public void isChinese() {
    }

    @Test
    public void isCJK() {
    }

}