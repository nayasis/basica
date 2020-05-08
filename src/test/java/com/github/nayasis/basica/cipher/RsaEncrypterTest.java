package com.github.nayasis.basica.cipher;

import com.github.nayasis.basica.base.Strings;
import com.github.nayasis.basica.cipher.vo.KeyPair;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class RsaEncrypterTest {

    @Test
    public void test() {
        checkValidation( "Hello RSA world !" );
    }

    @Test
    public void longText() {
        checkValidation( "basica 한글 鄭柱虎 merong GTA VI ファイナルファンタジー 1234567890 max byte 53 is possible to encrypt." );
    }

    private void printLine() {
        System.out.println( Strings.lpad("", 60, '-' ) );
    }

    private void print( KeyPair keyPair ) {
        log.debug( "public key  : {}", keyPair.getTextPublicKey() );
        log.debug( "private key : {}", keyPair.getTextPublicKey() );
    }

    private void checkValidation( String plainText ) {

        RsaEncrypter encrypter = new RsaEncrypter();

        KeyPair keyPair = encrypter.generateKey();

        print( keyPair );

        printLine();

        String encrypt = encrypter.encrypt( plainText, keyPair.getPublicKey() );
        String decrypt = encrypter.decrypt( encrypt, keyPair.getPrivateKey() );

        log.debug( encrypt );
        log.debug( decrypt );

        assertEquals( plainText, decrypt );

    }

}