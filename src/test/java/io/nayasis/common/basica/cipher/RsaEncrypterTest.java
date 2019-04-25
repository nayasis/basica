package io.nayasis.common.basica.cipher;

import io.nayasis.common.basica.base.Strings;
import io.nayasis.common.basica.cipher.vo.KeyPair;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class RsaEncrypterTest {

    @Test
    public void test() {
        checkValidation( "Hello RSA world !" );
    }

    @Test
    public void longText() {
        checkValidation( "basica 정화수 鄭柱虎 merong GTA VI ファイナルファンタジー 1234567890 maxbyte 53 is possible to encrypt." );
    }

    private void printLine() {
        System.out.println( Strings.lpad("", 60, '-' ) );
    }

    private void print( KeyPair keyPair ) {
        log.debug( "public key  : {}", keyPair.getTextPublicKey() );
        log.debug( "private key : {}", keyPair.getTextPublicKey() );
    }

    private void checkValidation( String plainText ) {

        RsaEncrypter encryptor = new RsaEncrypter();

        KeyPair keyPair = encryptor.generateKey();

        print( keyPair );

        printLine();

        String encrypt = encryptor.encrypt( plainText, keyPair.getPublicKey() );
        String decrypt = encryptor.decrypt( encrypt, keyPair.getPrivateKey() );

        log.debug( encrypt );
        log.debug( decrypt );

        assertEquals( plainText, decrypt );

    }

}