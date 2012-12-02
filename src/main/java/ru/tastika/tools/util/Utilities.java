package ru.tastika.tools.util;


import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * User: Alexandr
 * Date: 19.12.2004
 * Time: 23:51:45
 */
public class Utilities {


    private static final String[] STRING = new String[] { };


    public static char[] byteToCharArray(byte[] bytes) {
        char[] chars = new char[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            chars[i] = (char) bytes[i];
        }
        return chars;
    }


    public static byte[] charToByteArray(char[] chars) {
        byte[] bytes = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }


    public static Color getNegativeColor(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return new Color(255 - r, 255 - g, 255 - b);
    }


    /**
     * Take the given string and chop it up into a series
     * of strings on whitespace boundries.  This is useful
     * for trying to get an array of strings out of the
     * resource file.
     */
    public static String[] tokenize(String input) {
        if (input == null) {
            return STRING;
        }
        StringTokenizer t = new StringTokenizer(input);
        String cmd[];

        cmd = new String[t.countTokens()];
        int i = 0;
        while (t.hasMoreTokens()) {
            cmd[i] = t.nextToken();
            i++;
        }

        return cmd;
    }


    public static String correctSlashes(String url) {
        return url.trim().replaceAll("\\\\", "/");
    }


    public static String addSlashesToCSSName(String cssName) {
        return cssName.replaceAll("([\\*\\.\\-])", "\\\\$1");
    }


    public static String addPatternSlashes(String content) {
        return content.replaceAll("(\\p{Punct})", "\\\\$1");
    }


    public static String addTrailingSlash(String url) {
        String url2 = url.trim();
        if (!url2.endsWith("/")) {
            url2 = url2 + "/";
        }
        return url2;
    }


    public static String removeTrailingSlash(String url) {
        String url2 = url.trim();
        if (url2.endsWith("/")) {
            url2 = url2.substring(0, url2.length() - 1);
        }
        return url2;
    }


    public static boolean parseStringToBoolean(String value) {
        return "1".equals(value) || "true".equalsIgnoreCase(value);
    }


    public static int parseStringToInteger(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }


    public static int parseCharToInteger(char value) {
        try {
            return Integer.parseInt(String.valueOf(value));
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }


    public static int parseBooleanToInteger(boolean value) {
        return value ? 1 : 0;
    }


    public static short parseStringToShort(String value) {
        if (value == null) {
            return 0;
        }
        try {
            return Short.parseShort(value);
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }


    public static Date parseStringToDate(String value, String pattern) {
        if (value == null) {
            return new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(value);
        }
        catch (ParseException e) {
            return new Date();
        }
    }


    public static String formatDateToString(Date date, String pattern) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }


    public static float parseStringToFloat(String value) {
        if (value == null) {
            return 0f;
        }
        try {
            return Float.parseFloat(value);
        }
        catch (NumberFormatException nfe) {
            return 0f;
        }
    }


    public static long parseStringToLong(String value) {
        if (value == null) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        }
        catch (NumberFormatException nfe) {
            return 0L;
        }
    }


    public static double parseStringToDouble(String value) {
        if (value == null) {
            return 0d;
        }
        try {
            return Double.parseDouble(value);
        }
        catch (NumberFormatException nfe) {
            return 0d;
        }
    }


    private static String hexEncode(byte[] aInput) {
        StringBuffer result = new StringBuffer();
        char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        for (byte b : aInput) {
            result.append(digits[(b & 0xf0) >> 4]);
            result.append(digits[b & 0x0f]);
        }
        return result.toString();
    }


    public static String getUID() {
        String uid;
        try {
            SecureRandom prng = SecureRandom.getInstance("SHA1PRNG");

            /* generate a random number */
            String randomNum = Integer.toString(prng.nextInt());

            /* get its digest */
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] result = sha.digest(randomNum.getBytes());

            uid = hexEncode(result);
        }
        catch (NoSuchAlgorithmException e) {
            uid = String.valueOf(System.currentTimeMillis());
        }
        return uid;
    }


    public static String getNumberStringWithLeadingZeroes(int number, int maxLengthWithZeroes) {
        String numberString = String.valueOf(number);
        if (numberString.length() < maxLengthWithZeroes) {
            StringBuilder prefix = new StringBuilder();
            for (int i = 0; i < maxLengthWithZeroes - numberString.length(); i++) {
                prefix.append('0');
            }
            return prefix.append(numberString).toString();
        }
        return numberString;
    }


    public static String implode(Collection collection, String glue) {
        return implode(collection.toArray(), glue);
    }


    public static String implode(Object[] objects, String glue) {
        return implode(objects, glue, 0, objects.length);
    }


    public static String implode(Collection collection, String glue, int start, int length) {
        return implode(collection.toArray(), glue, start, length);
    }


    public static String implode(Object[] objects, String glue, int start, int length) {
        StringBuilder str = new StringBuilder();
        int end = start + length;
        for (int i = start; i < end && i < objects.length; i++) {
            str.append(String.valueOf(objects[i])).append(glue);
        }
        return str.length() > 0 ? str.substring(0, str.length() - glue.length()) : "";
    }


    public static boolean hasNonLatinSymbols(String str) {
        Pattern latinSymbolsPattern = Pattern.compile("^[A-Za-z0-9\\s\\!\\@\\#\\$\\%\\^\\&\\(\\)_\\+\\-\\=\\~\\`\\'\\.,]*$");
        Matcher matcher = latinSymbolsPattern.matcher(str);
        boolean hasOnlyLatins = matcher.find();
        return !hasOnlyLatins;
    }


    public static String getFileContentAsText(String resource) {
        StringBuilder content = new StringBuilder();
        String line;
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(resource);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            bufferedReader.close();
            if (inputStream != null) {
                inputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }


    public static TreeSet<Object> getKeysForValue(Map map, Object value) {
        TreeSet<Object> foundKeys = new TreeSet<Object>();
        for (Object key : map.keySet()) {
            Object valueInMap = map.get(key);
            if (valueInMap != null && valueInMap.equals(value)) {
                foundKeys.add(key);
            }
        }
        return foundKeys;
    }


    public static boolean containsIgnoreCase(Collection<String> strings, String str) {
        for (String s : strings) {
            if (s.equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

}
