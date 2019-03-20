package io.nayasis.common.cipher;

import io.nayasis.common.base.Strings;
import io.nayasis.common.cipher.vo.KeyPair;
import io.nayasis.common.exception.unchecked.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * Encrypter with RSA algorithm
 *
 * @author nayasis@gmail.com
 * @since 2018-05-30
 */
public class RsaEncrypter {

    public KeyPair generateKey() throws NoSuchAlgorithmException {

        KeyPair keyPair = new KeyPair();

        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator;

        try {
            keyPairGenerator = KeyPairGenerator.getInstance( "RSA" );
            keyPairGenerator.initialize(512, secureRandom );
            keyPair.setKeyPair( keyPairGenerator.genKeyPair() );
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException( e );
        }

        return keyPair;

    }

    public String encrypt( String value, PublicKey publicKey ) {
        try {
            byte[] encrypted = getCipher(publicKey).doFinal( getBytes(value) );
            return Base64.encodeBase64String( encrypted );
        } catch( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException( e );
        }
    }

    public String encrypt( String value, String publicKey ) {
        KeyPair keyPair = new KeyPair();
        keyPair.setPublicKey( publicKey );
        return encrypt( value, keyPair.getPublicKey() );
    }

    public String decrypt( String value, PrivateKey privateKey ) {
        byte[] bytes = Base64.decodeBase64( getBytes( value ) );
        try {
            byte[] decrypted = getCipher(privateKey).doFinal( bytes );
            return new String( decrypted );
        } catch( IllegalBlockSizeException | BadPaddingException e ) {
            throw new RuntimeException( e );
        }
    }

    public String decrypt( String value, String privateKey ) {
        KeyPair keyPair = new KeyPair();
        keyPair.setPrivateKey( privateKey );
        return decrypt( value, keyPair.getPrivateKey() );
    }

    private byte[] getBytes( String value ) {
        return Strings.nvl(value).getBytes();
    }

    private Cipher getCipher( Key key ) throws NoSuchAlgorithmException {
        try {
            Cipher cipher = Cipher.getInstance( "RSA" );
            if( key instanceof PublicKey ) {
                cipher.init(Cipher.ENCRYPT_MODE, key );
            } else if( key instanceof PrivateKey ) {
                cipher.init(Cipher.DECRYPT_MODE, key );
            }
            return cipher;
        } catch( java.security.NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e ) {
            throw new NoSuchAlgorithmException( e );
        }
    }

}
