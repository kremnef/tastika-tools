package ru.tastika.tools.string;


public class UnicodeFormatter {


    static public String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byteToHex(bytes[i]));
        }
        return sb.toString();
    }


    static public String byteToHex(byte b) {
        // Returns hex String representation of byte b
        char hexDigit[] = {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
        return new String(array);
    }


    static public String charToHex(char c) {
        // Returns hex String representation of char c
        byte hi = (byte) (c >>> 8);
        byte lo = (byte) (c & 0xff);
        return byteToHex(hi) + byteToHex(lo);
    }


    static public String rusToUni(String rusText) {
        char[] rusChar = rusText.toCharArray();
        StringBuilder uniText = new StringBuilder();
        for (int i = 0; i < rusChar.length; i++) {
            uniText.append("\\u");
            uniText.append(charToHex(rusChar[i]));
        }
        return uniText.toString();
    }


    static public String uniToRus(String uniText) {
        String rusText = null;
        return rusText;
    }


    public static char[] byteArrayToCharArray(byte[] bytes) {
        int charsSize = (bytes.length + 1) / 2;
        char[] chars = new char[charsSize];
        for (int i = 0; i < charsSize; i++) {
            if (i * 2 + 1 < bytes.length) {
                chars[i] = (char) (((char) bytes[i * 2]) << 8);
                chars[i] += bytes[i * 2 + 1];
            }
            else {
                chars[i] = (char) bytes[i * 2];
            }
        }
        return chars;
    }


    public static byte[] charArrayToByteArray(char[] chars) {
        int byteSize = chars.length * 2;
        byte[] bytes = new byte[byteSize];
        for (int i = 0; i < chars.length; i++) {
            bytes[i * 2] = (byte) (chars[i] >> 8);
            bytes[i * 2 + 1] = (byte) (chars[i] & 0xFF);
        }
        return bytes;
    }


    public static void main(String[] args) {
        showChar(69);
        showChar(85);
        showChar(60);
        showChar(34);
        showChar(8);
        showChar(1);
        showChar(26);
        showChar(17);
    }


    private static void showChar(int i) {
        System.out.println(String.valueOf(i / 33) + " " + String.valueOf(i % 33));
    }

} // class
