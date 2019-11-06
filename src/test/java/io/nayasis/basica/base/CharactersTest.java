package io.nayasis.basica.base;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertTrue( Characters.isJapanese('か') );
        Assert.assertTrue( Characters.isJapanese('フ') );
        Assert.assertTrue( Characters.isJapanese('ﾌ') );
        Assert.assertTrue( ! Characters.isJapanese('！') );
        Assert.assertTrue( Characters.isJapanese('。') );
        Assert.assertTrue( ! Characters.isJapanese('A') );
        Assert.assertTrue( ! Characters.isJapanese('정') );
        Assert.assertTrue( ! Characters.isJapanese('ㄱ') );
        Assert.assertTrue( ! Characters.isJapanese('鄭') );
    }

    @Test
    public void isChinese() {
    }

    @Test
    public void isCJK() {
    }

}