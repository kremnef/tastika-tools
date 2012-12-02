package ru.tastika.swing.modalframe;


import java.util.EventListener;


/**
 *
 * @author hobal
 */
public interface WaitWindowListener extends EventListener {


    public void windowShown(WaitWindowEvent e);


    public void windowClosed(WaitWindowEvent e);


    public void statusTextChanged(WaitWindowEvent e);


    public void canceled(WaitWindowEvent e);
}
