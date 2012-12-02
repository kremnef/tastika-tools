package ru.tastika.tools.file;


import java.beans.ExceptionListener;


/**
 * User: hobal
 * Date: 28.01.2005
 * Time: 22:21:10
 */
public class XMLDecoderExceptionListener implements ExceptionListener {


    private Exception exception;
    private boolean exceptionThrown = false;


    public XMLDecoderExceptionListener() {
        super();
    }


    public void exceptionThrown(Exception e) {
        exception = e;
        exceptionThrown = true;
    }


    public Exception getException() {
        return exception;
    }


    public boolean isExceptionThrown() {
        return exceptionThrown;
    }
}
