package ru.tastika.tools.string;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;


/**
 * Created by IntelliJ IDEA.
 * User: Alexandr
 * Date: 23.12.2005
 * Time: 13:40:39
 */
public class DesEncrypter {

    /* 8-byte Salt */
    private static byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    /* Iteration count */
    private static int iterationCount = 19;


    public static String encrypt(String str, String keyPhrase) {
        try {
            /* Encode the string into bytes using utf-8 */
            byte[] utf8 = str.getBytes("UTF8");

            /* Create the key */
            KeySpec keySpec = new PBEKeySpec(keyPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            Cipher ecipher = Cipher.getInstance(key.getAlgorithm());

            /* Prepare the parameter to the ciphers */
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            /* Create the cipher */
            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            /* Encrypt */
            byte[] enc = ecipher.doFinal(utf8);

            /* Encode bytes to base64 to get a string */
            return new sun.misc.BASE64Encoder().encode(enc);
        }
        catch (Exception e) {
            return null;
        }
        //catch (UnsupportedEncodingException e) {
        //    return null;
        //}
        //catch (BadPaddingException e) {
        //    return null;
        //}
        //catch (NoSuchAlgorithmException e) {
        //    return null;
        //}
        //catch (IllegalBlockSizeException e) {
        //    return null;
        //}
        //catch (NoSuchPaddingException e) {
        //    return null;
        //}
        //catch (InvalidKeySpecException e) {
        //    return null;
        //}
        //catch (InvalidAlgorithmParameterException e) {
        //    return null;
        //}
        //catch (InvalidKeyException e) {
        //    return null;
        //}
    }


    public static String decrypt(String str, String keyPhrase) {
        try {
            /* Create the key */
            KeySpec keySpec = new PBEKeySpec(keyPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            Cipher dcipher = Cipher.getInstance(key.getAlgorithm());

            /* Prepare the parameter to the ciphers */
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            /* Create the ciphers */
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            /* Decode base64 to get bytes */
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            /* Decrypt */
            byte[] utf8 = dcipher.doFinal(dec);

            /* Decode using utf-8 */
            return new String(utf8, "UTF8");
        }
        catch (Exception e) {
            return null;
        }
        //catch (InvalidKeySpecException e) {
        //    return null;
        //}
        //catch (NoSuchAlgorithmException e) {
        //    return null;
        //}
        //catch (InvalidAlgorithmParameterException e) {
        //    return null;
        //}
        //catch (InvalidKeyException e) {
        //    return null;
        //}
        //catch (NoSuchPaddingException e) {
        //    return null;
        //}
        //catch (BadPaddingException e) {
        //    return null;
        //}
        //catch (IOException e) {
        //    return null;
        //}
        //catch (IllegalBlockSizeException e) {
        //    return null;
        //}
    }


    public static void main(String[] args) {
        String pass = "";
        String key = "";

        String eStr = DesEncrypter.encrypt(pass, key);
        System.out.println("encrypted = " + eStr);
        String dStr = DesEncrypter.decrypt(eStr, key);
        System.out.println("decrypted = " + dStr);
    }
}
