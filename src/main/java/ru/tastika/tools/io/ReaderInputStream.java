package ru.tastika.tools.io;


import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;


/**
 * User: hobal
 * Date: 23.04.2005
 * Time: 0:15:23
 */
public class ReaderInputStream extends InputStream {


    private Reader reader;


    public ReaderInputStream(Reader reader) {
        super();
        this.reader = reader;
    }


    public int read() throws IOException {
        return reader.read();
    }

}
