package io.nayasis.common.cipher;

import io.nayasis.common.base.Strings;
import io.nayasis.common.cipher.vo.KeyPair;
import io.nayasis.common.exception.unchecked.DecryptionException;
import io.nayasis.common.exception.unchecked.EncryptionException;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Encrypter with RSA algorithm
 *
 * @author nayasis@gmail.com
 * @since 2018-05-30
 */
public class RsaEncrypter {

    private static final int ENCRYPTION_SPLIT_BYTE = 53;
    private static final int DECRIPTION_SPLIT_SIZE = 88;

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

    public String encrypt( String value, PublicKey publicKey ) throws EncryptionException {
        StringBuilder sb = new StringBuilder();
        try {
            Cipher cipher = getCipher( publicKey );
            for( String word : splitByByte(value, ENCRYPTION_SPLIT_BYTE) ) {
                byte[] encrypted = cipher.doFinal( word.getBytes() );
                sb.append( Base64.encodeBase64String( encrypted ) );
            }
            return sb.toString();
        } catch( IllegalBlockSizeException | BadPaddingException e ) {
            throw new EncryptionException( e );
        }
    }

    public String encrypt( String value, String publicKey ) throws EncryptionException {
        KeyPair keyPair = new KeyPair();
        keyPair.setPublicKey( publicKey );
        return encrypt( value, keyPair.getPublicKey() );
    }

    public String decrypt( String value, PrivateKey privateKey ) throws DecryptionException {
        StringBuilder sb = new StringBuilder();
        try {
            Cipher cipher = getCipher( privateKey );
            for( String word : splitBySize(value, DECRIPTION_SPLIT_SIZE) ) {
                byte[] bytes     = Base64.decodeBase64( word.getBytes() );
                byte[] decrypted = cipher.doFinal( bytes );
                sb.append( new String( decrypted ) );
            }
            return sb.toString();
        } catch( IllegalBlockSizeException | BadPaddingException e ) {
            throw new DecryptionException( e );
        }
    }

    public String decrypt( String value, String privateKey ) throws DecryptionException {
        KeyPair keyPair = new KeyPair();
        keyPair.setPrivateKey( privateKey );
        return decrypt( value, keyPair.getPrivateKey() );
    }

    private Cipher getCipher( Key key ) throws NoSuchAlgorithmException {
        try {
            Cipher cipher = Cipher.getInstance( "RSA" );
            if( key instanceof PublicKey ) {
                cipher.init( Cipher.ENCRYPT_MODE, key );
            } else if( key instanceof PrivateKey ) {
                cipher.init( Cipher.DECRYPT_MODE, key );
            }
            return cipher;
        } catch( java.security.NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e ) {
            throw new NoSuchAlgorithmException( e );
        }
    }

    private List<String> splitByByte( String value, int maxBytes ) {

        value = Strings.nvl( value );

        List<String> array = new ArrayList<>();

        int start = 0, end = Math.min( maxBytes, value.length() );

        while( true ) {

            String buffer = value.substring( start, end );

            if( buffer.getBytes().length > maxBytes ) {
                end--;
                continue;
            }

            array.add( buffer );
            start = end;
            end   = Math.min( start + maxBytes, value.length() );

            if( start >= end ) break;

        }

        return array;

    }

    private List<String> splitBySize( String value, int size ) {

        value = Strings.nvl( value );

        List<String> array = new ArrayList<>();

        StringBuilder buffer = new StringBuilder();

        for( char c : value.toCharArray() ) {
            if( buffer.length() == size ) {
                array.add( buffer.toString() );
                buffer = new StringBuilder();
            }
            buffer.append( c );
        }

        if( buffer.length() > 0 ) {
            array.add( buffer.toString() );
        }

        return array;

    }

}
