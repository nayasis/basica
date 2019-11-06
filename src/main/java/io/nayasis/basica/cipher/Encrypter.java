package io.nayasis.basica.cipher;

import io.nayasis.basica.validation.Validator;
import io.nayasis.basica.base.Strings;
import io.nayasis.basica.exception.unchecked.DecodingException;
import io.nayasis.basica.exception.unchecked.EncodingException;
import io.nayasis.basica.exception.unchecked.NoSuchAlgorithmException;
import org.springframework.util.Assert;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;

/**
 * Encrypter with AES128 algorithm
 *
 * @author nayasis@gmail.com
 * @since 2016-08-24
 */
public class Encrypter {

    private String algorithm;
    private Key    defaultSecretKey = null;

    /**
     * default constructor
     */
    public Encrypter() {
        this( "AES" );
    }

    /**
     * constructor
     *
     * @param algorithm cipher algorithm
     */
    public Encrypter( String algorithm ) {
        this.algorithm = algorithm;
        getCipher();
    }

    public Key getDefaultSecretKey() {
        return defaultSecretKey;
    }

    public Encrypter setDefaultSecretKey( Key defaultSecretKey ) {
        this.defaultSecretKey = defaultSecretKey;
        return this;
    }

    public Encrypter setDefaultSecretKey( String defaultSecretKey ) {
        this.defaultSecretKey = toSecretKey( defaultSecretKey );
        return this;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * encrypt value
     *
     * @param value         value to encrypt
     * @return encrypted value
     */
    public String encrypt( String value ) {
        Assert.notNull( defaultSecretKey, "default secret key is missing.");
        return encrypt( value, defaultSecretKey );
    }

    /**
     * encrypt value
     *
     * @param value         value to encrypt
     * @param secretKey     secret key
     * @return encrypted value
     */
    public String encrypt( String value, String secretKey ) {
        return encrypt( value, toSecretKey(secretKey) );
    }

    /**
     * encrypt value
     *
     * @param value         value to encrypt
     * @param secretKey     secret key
     * @return encrypted value
     */
    public String encrypt( String value, Key secretKey ) throws EncodingException {

        Cipher cipher = getCipher();

        try {
            cipher.init( Cipher.ENCRYPT_MODE, secretKey );
            byte[] encrypted = cipher.doFinal( Strings.nvl(value).getBytes() );
            return toHex( encrypted );
        } catch( InvalidKeyException | BadPaddingException | IllegalBlockSizeException e ) {
            throw new EncodingException( e );
        }

    }

    /**
     * decrypt value
     *
     * @param value         value to decrypt
     * @return  decrypted value
     */
    public String decrypt( String value ) {
        Assert.notNull( defaultSecretKey, "default secret key is missing.");
        return decrypt( value, defaultSecretKey );
    }

    /**
     * decrypt value
     *
     * @param value         value to decrypt
     * @param secretKey     secret key
     * @return  decrypted value
     */
    public String decrypt( String value, String secretKey ) {
        return decrypt( value, toSecretKey(secretKey) );
    }

    /**
     * decrypt value
     *
     * @param value         value to decrypt
     * @param secretKey     secret key
     * @return  decrypted value
     */
    public String decrypt( String value, Key secretKey ) throws DecodingException {
        Cipher cipher = getCipher();
        try {
            cipher.init( Cipher.DECRYPT_MODE, secretKey );
            byte[] decrypted = cipher.doFinal( toByteArray( value ) );
            return new String( decrypted );
        } catch( InvalidKeyException | BadPaddingException | IllegalBlockSizeException e ) {
            throw new DecodingException( e );
        }
    }

    private Cipher getCipher() throws NoSuchAlgorithmException {
        try {
            return Cipher.getInstance( this.algorithm );
        } catch( java.security.NoSuchAlgorithmException | NoSuchPaddingException e ) {
            throw new NoSuchAlgorithmException( e );
        }
    }


    private Key toSecretKey( String secretKey ) {

        byte[] keyBytes;

        if( isHex(secretKey) ) {
            keyBytes = toByteArray( secretKey );

        } else {

            String key = Strings.rpad( secretKey, 16, '0' );
            try {
                byte[] bytes = key.getBytes( "UTF-8" );
                keyBytes = new byte[ 16 ];
                int len = Math.min( bytes.length, keyBytes.length );
                System.arraycopy(bytes, 0, keyBytes, 0, len );
            } catch( UnsupportedEncodingException e ) {
                throw new EncodingException( e );
            }

        }

        return new SecretKeySpec( keyBytes, this.algorithm );

    }

    /**
     * generate secret key
     *
     * @return secret key
     */
    public Key generateSecretKey() throws NoSuchAlgorithmException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance( this.algorithm );
            SecureRandom random = SecureRandom.getInstance( "SHA1PRNG" );
            keyGenerator.init( 128, random );
            return keyGenerator.generateKey();
        } catch( java.security.NoSuchAlgorithmException e ) {
            throw new NoSuchAlgorithmException( e );
        }
    }

    /**
     * generate secret key
     *
     * @return secret key consist with 16 length HEX code
     */
    public String generateKey() {
        byte[] encoded = generateSecretKey().getEncoded();
        return toHex( encoded );
    }

    private byte[] toByteArray( String hex ) {
        if( Validator.isEmpty(hex) ) return null;
        return DatatypeConverter.parseHexBinary( hex );
    }

    private String toHex( byte byteArray[] ) {
        if( Validator.isEmpty( byteArray ) ) return "";
        StringBuilder sb = new StringBuilder( byteArray.length * 2 );
        for( byte b : byteArray ) {
            sb.append( String.format("%02x", b & 0XFF) );
        }
        return sb.toString();
    }

    private boolean isHex( String value ) {
        if( value == null || value.length() != 32 ) return false;
        return Validator.isMatched( value, "[0-9a-fA-F]{32}" );
    }

}
