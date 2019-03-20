package io.nayasis.common.cipher;

import io.nayasis.common.base.Strings;
import io.nayasis.common.cipher.vo.KeyPair;
import io.nayasis.common.model.NMap;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

@Slf4j
public class RsaEncrypterTest {

    @Test
    public void test() {

        RsaEncrypter encryptor = new RsaEncrypter();

        KeyPair keyPair = encryptor.generateKey();

        print( keyPair );

        printLine();

        String plainText = "Hello RSA world !";

        String encrypt = encryptor.encrypt( plainText, keyPair.getPublicKey() );
        String decrypt = encryptor.decrypt( encrypt, keyPair.getPrivateKey() );

        log.debug( encrypt );
        log.debug( decrypt );

        assertEquals( plainText, decrypt );

    }

    private void printLine() {
        System.out.println( Strings.lpad("", 60, '-' ) );
    }

    private void print( KeyPair keyPair ) {
        log.debug( "public key  : {}", keyPair.getEncodedPublicKey() );
        log.debug( "private key : {}", keyPair.getEncodedPublicKey() );
    }

}