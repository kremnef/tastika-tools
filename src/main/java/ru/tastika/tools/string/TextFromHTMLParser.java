package ru.tastika.tools.string;


import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.StringReader;


/**
 * User: osa
 * Date: 08.04.2008
 * Time: 11:08:01
 */

public class TextFromHTMLParser extends HTMLEditorKit.ParserCallback {


    private StringBuilder result;


    public void handleText(char[] data, int pos) {
        if (result.length() > 0) {
            result.append(System.getProperty("line.separator"));
        }
        result.append(data);
    }


    public String getResult() {
        return result.toString();
    }


    public String parseHTML(String HTML) throws IOException {
        result = new StringBuilder();
        StringReader reader = new StringReader(HTML);
        ParserDelegator parser = new ParserDelegator();
        parser.parse(reader, this, true);
        return result.toString();
    }
}
