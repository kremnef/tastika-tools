package ru.tastika.tools.string;

import java.awt.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;

/**
 * проверка на русский
 * User: osa
 * Date: 16.11.2007
 * Time: 13:34:38
 * To change this template use File | Settings | File Templates.
 */
public class HTMLEscapeUtils {


    private static HashMap<String, String> htmlEntities;
    

    static {
        htmlEntities = new HashMap<String, String>();
        //        htmlEntities.put("&lt;", "<");
        //        htmlEntities.put("&gt;", ">");
        htmlEntities.put("&amp;", "&");
        htmlEntities.put("&quot;", "\"");
        htmlEntities.put("&nbsp;", " ");
        htmlEntities.put("&copy;", "\u00a9");
        htmlEntities.put("&reg;", "\u00ae");
        htmlEntities.put("&euro;", "\u20a0");
        htmlEntities.put("&#39;", "'");
    }

    public static String unescapeHTML(String source, int start) {
        int i, j;

        i = source.indexOf("&", start);
        if (i > -1) {
            j = source.indexOf(";", i);
            if (j > i) {
                String entityToLookFor = source.substring(i, j + 1);
                String value = htmlEntities.get(entityToLookFor);
                if (value == null) {
                    String tryNumber = source.substring(i + 2, j);
                    try {
                        int entity = Integer.parseInt(tryNumber);
                        char charEntity = (char) entity;
                        value = "" + charEntity;
                    }
                    catch (NumberFormatException nfe) {
                        value = null;
                    }

                }
                if (value != null) {
                    source = new StringBuffer().append(source.substring(0, i)).append(value).append(source.substring(j + 1)).toString();
                    return unescapeHTML(source, start + 1);// recursive call;
                }

            }

        }
        return source;
    }

    public static String safeUnescapeHTML(String source) {
        return _safeUnescapeHTML(new StringBuilder(source), 0).toString();
    }
    
    
    private static StringBuilder _safeUnescapeHTML(StringBuilder source, int start){
        int i, j;

        while( (i = source.indexOf("&", start)) > -1){
//            if (i > -1) {
                j = source.indexOf(";", i);
                if (j > i) {
                    String entityToLookFor = source.substring(i, j + 1);
                    String value = (String) htmlEntities.get(entityToLookFor);
                    if (value == null) {
                        String tryNumber = source.substring(i + 2, j);
                        try {
                            int entity = Integer.parseInt(tryNumber);
                            char charEntity = (char) entity;
                            value = "" + charEntity;
                        } catch (NumberFormatException nfe) {
                            value = null;
                        }

                    }
                    if (value != null) {
                        source.replace(i, j + 1, value);
                    }
                }
                start = i + 1;
//        }
        }
        return source;
    }
    
    
    public static String unescapeUnicodeChars(String source, int start) {
        int i, j;

        i = source.indexOf("\\u", start);
        if (i > -1) {
            j = i + 6;
            if (j < source.length()) {
                String value = null;

                String tryNumber = source.substring(i + 2, j);
                try {
                    int entity = Integer.parseInt(tryNumber);
                    char charEntity = (char) entity;
                    value = "" + charEntity;
                } catch (NumberFormatException nfe) {
                    value = null;
                }


                if (value != null) {
                    source = new StringBuffer().append(source.substring(0, i)).append(value).append(source.substring(j + 1)).toString();
                    return unescapeUnicodeChars(source, start + 1);// recursive call
                }

            }

        }
        return source;
    }

    public static String escapeHTML(String aText) {
        final StringBuilder result = new StringBuilder();
        final StringCharacterIterator iterator = new StringCharacterIterator(aText);
        char character = iterator.current();
        while (character != CharacterIterator.DONE) {
            if (character == '<') {
                result.append("&lt;");
            } else if (character == '>') {
                result.append("&gt;");
            } else if (character == '&') {
                result.append("&amp;");
            } else if (character == '\"') {
                result.append("&quot;");
            } else if (character == '\'') {
                result.append("&#039;");
            } else if (character == '(') {
                result.append("&#040;");
            } else if (character == ')') {
                result.append("&#041;");
            } else if (character == '#') {
                result.append("&#035;");
            } else if (character == '%') {
                result.append("&#037;");
            } else if (character == ';') {
                result.append("&#059;");
            } else if (character == '+') {
                result.append("&#043;");
            } else if (character == '-') {
                result.append("&#045;");
            } else {
                //the char is not a special one
                //add it to the result as is
                result.append(character);
            }
            character = iterator.next();
        }
        return result.toString();
    }

    public static String getHTMLColor(Color c) {
        StringBuilder result = new StringBuilder("#");
        if (c.getRed() < 0x10) {
            result.append("0");
        }
        result.append(Integer.toHexString(c.getRed()));
        if (c.getGreen() < 0x10) {
            result.append("0");
        }
        result.append(Integer.toHexString(c.getGreen()));
        if (c.getBlue() < 0x10) {
            result.append("0");
        }
        result.append(Integer.toHexString(c.getBlue()));
        return result.toString().toUpperCase();

    //        String colorR = "0" + Integer.toHexString(c.getRed());
    //        colorR = colorR.substring(colorR.length() - 2);
    //        String colorG = "0" + Integer.toHexString(c.getGreen());
    //        colorG = colorG.substring(colorG.length() - 2);
    //        String colorB = "0" + Integer.toHexString(c.getBlue());
    //        colorB = colorB.substring(colorB.length() - 2);
    //        String html_color = "#" + colorR + colorG + colorB;
    //        return html_color;
    }
}  


