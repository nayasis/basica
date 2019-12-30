package io.nayasis.basica.cipher;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class EncrypterTest {

    private String originMessage = "한글입니다. password 漢文 ファイナル";

    private Encrypter encrypter = new Encrypter();

    @Test
    public void basic() {
        String secretKey = "비밀키입니다.";
        checkValidWorking( secretKey );
    }

    @Test
    public void nullTest() {
        checkValidWorking( null );
    }

    @Test
    public void generateKey() {
        String key = encrypter.generateKey();
        log.debug( key );
        checkValidWorking( key );
    }

    private void checkValidWorking( String secretKey ) {

        String encrypted = encrypter.encrypt( originMessage, secretKey );
        String decrypted = encrypter.decrypt( encrypted, secretKey );

        log.debug( "encrypted : {}", encrypted );
        log.debug( "decrypted : {}", decrypted );

        assertEquals( decrypted, originMessage );

    }

}