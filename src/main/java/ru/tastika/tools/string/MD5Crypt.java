/**
 * FreeBSD-compatible md5-style password crypt,
 * based on crypt-md5.c by Poul-Henning Kamp, which was distributed
 * with the following notice:
 *
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <phk@login.dknet.dk> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Poul-Henning Kamp
 * ----------------------------------------------------------------------------
 *
 * @author Nick Johnson <freebsd@spatula.net>
 * @version 1.0
 */

package ru.tastika.tools.string;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Class containing static methods for encrypting passwords
 * in FreeBSD md5 style.
 * @author Nick Johnson <freebsd@spatula.net>
 * @version 1.0
 */

public class MD5Crypt {


    private static final String MAGIC = "$1$";
    private static final String ITOA64 = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static MessageDigest md5;


    private MD5Crypt() {
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    public void update(byte[] bytes) {
        md5.update(bytes);
    }


    public void update(byte[] bytes, int offset, int len) {
        md5.update(bytes, offset, len);
    }


    public static MD5Crypt getInstance() {
        return new MD5Crypt();
    }


    private static String cryptTo64(int value, int length) {
        StringBuilder output = new StringBuilder();
        while (--length >= 0) {
            output.append(ITOA64.substring(value & 0x3f, (value & 0x3f) + 1));
            value >>= 6;
        }
        return (output.toString());
    }


    /**
     * Encrypts a password using FreeBSD-style md5-based encryption
     * @param password The cleartext password to be encrypted
     * @param salt     The salt used to add some entropy to the encryption
     * @return The encrypted password, or an empty string on error
     * @throws java.security.NoSuchAlgorithmException
     *          if java.security
     *          does not support MD5
     */

    public static String crypt(String password, String salt) throws java.security.NoSuchAlgorithmException {

        /* First get the salt into a proper format.  It can be no more than
           * 8 characters, and if it starts with the magic string, it should
           * be skipped.
       */
        if (salt.startsWith(MAGIC)) {
            salt = salt.substring(MAGIC.length());
        }

        int saltEnd = salt.indexOf('$');
        if (saltEnd != -1) {
            salt = salt.substring(0, saltEnd);
        }

        if (salt.length() > 8) {
            salt = salt.substring(0, 8);
        }

        /* now we have a properly formatted salt */

        MessageDigest md5First = MessageDigest.getInstance("MD5");
        MessageDigest md5Second = MessageDigest.getInstance("MD5");

        /* First we update one MD5 with the password, magic string, and salt */
        md5First.update(password.getBytes());
        md5First.update(MAGIC.getBytes());
        md5First.update(salt.getBytes());

        /* Now start a second MD5 with the password, salt, and password again */
        md5Second.update(password.getBytes());
        md5Second.update(salt.getBytes());
        md5Second.update(password.getBytes());

        byte[] md5SecondDigest = md5Second.digest();

        int md5Size = md5SecondDigest.length;
        int pwLength = password.length();

        /* Update the first MD5 a few times starting at the first
           * character of the second MD5 digest using the smaller
          * of the MD5 length or password length as the number of
       * bytes to use in the update.
       */
        for (int i = pwLength; i > 0; i -= md5Size) {
            md5First.update(md5SecondDigest, 0, i > md5Size ? md5Size : i);
        }

        /* the FreeBSD code does a memset to 0 on "final" (md5_2_digest) here
       * which may be a bug, since it references "final" again if the
       * conditional below is true, meaning it always is equal to 0
       */

        md5Second.reset();

        /* Again, update the first MD5 a few times, this time
        * using either 0 (see above) or the first byte of the
        * password, depending on the lowest order bit's value
       */
        byte[] pwBytes = password.getBytes();
        for (int i = pwLength; i > 0; i >>= 1) {
            if ((i & 1) == 1) {
                md5First.update((byte) 0);
            }
            else {
                md5First.update(pwBytes[0]);
            }
        }

        /* Set up the output string. It'll look something like
       * $1$salt$ to begin with
       */
        StringBuffer output = new StringBuffer(MAGIC);
        output.append(salt);
        output.append("$");

        byte[] md5FirstDigest = md5First.digest();

        /* According to the original source, this bit of madness
       * is introduced to slow things down.  It also further
       * mutates the result.
       */
        byte[] saltBytes = salt.getBytes();
        for (int i = 0; i < 1000; i++) {
            md5Second.reset();
            if ((i & 1) == 1) {
                md5Second.update(pwBytes);
            }
            else {
                md5Second.update(md5FirstDigest);
            }
            if (i % 3 != 0) {
                md5Second.update(saltBytes);
            }
            if (i % 7 != 0) {
                md5Second.update(pwBytes);
            }
            if ((i & 1) != 0) {
                md5Second.update(md5FirstDigest);
            }
            else {
                md5Second.update(pwBytes);
            }
            md5FirstDigest = md5Second.digest();
        }

        /* Reorder the bytes in the digest and convert them to base64 */
        int value;
        value = ((md5FirstDigest[0] & 0xff) << 16) | ((md5FirstDigest[6] & 0xff) << 8) | (md5FirstDigest[12] & 0xff);
        output.append(cryptTo64(value, 4));
        value = ((md5FirstDigest[1] & 0xff) << 16) | ((md5FirstDigest[7] & 0xff) << 8) | (md5FirstDigest[13] & 0xff);
        output.append(cryptTo64(value, 4));
        value = ((md5FirstDigest[2] & 0xff) << 16) | ((md5FirstDigest[8] & 0xff) << 8) | (md5FirstDigest[14] & 0xff);
        output.append(cryptTo64(value, 4));
        value = ((md5FirstDigest[3] & 0xff) << 16) | ((md5FirstDigest[9] & 0xff) << 8) | (md5FirstDigest[15] & 0xff);
        output.append(cryptTo64(value, 4));
        value = ((md5FirstDigest[4] & 0xff) << 16) | ((md5FirstDigest[10] & 0xff) << 8) | (md5FirstDigest[5] & 0xff);
        output.append(cryptTo64(value, 4));
        value = md5FirstDigest[11] & 0xff;
        output.append(cryptTo64(value, 2));

        /* Drop some hints to the GC */
        md5First = null;
        md5Second = null;
        md5FirstDigest = null;
        md5SecondDigest = null;
        pwBytes = null;
        saltBytes = null;
        return output.toString();
    }


    public static String md5ToString16(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(str.getBytes());
        byte[] bytes = md5.digest();
        return UnicodeFormatter.bytesToHexString(bytes);
    }


    public static String md5ToString16(byte[] bytes) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(bytes);
        byte[] returnBytes = md5.digest();
        return UnicodeFormatter.bytesToHexString(returnBytes);
    }


    public String md5ToString16() {
        byte[] returnBytes = md5.digest();
        return UnicodeFormatter.bytesToHexString(returnBytes);
    }


    public static String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(str.getBytes());
        return new String(md5.digest());
    }


    public static String md5(byte[] bytes) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(bytes);
        return new String(md5.digest());
    }


    public static String localAuthorityHash(String login, String password) throws NoSuchAlgorithmException {
        return crypt(login, md5ToString16(password));
    }

}
