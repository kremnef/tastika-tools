package ru.tastika.swing.modalframe;


import java.util.EventObject;


/**
 *
 * @author hobal
 */
public class WaitWindowEvent extends EventObject {


    public static final int ACTION_WINDOW_SHOWN = 1;
    public static final int ACTION_WINDOW_CLOSED = 2;
    public static final int ACTION_STATUS_TEXT_CHANGED = 3;
    public static final int ACTION_CANCELED = 4;
    private int waitWindowAction;


    public WaitWindowEvent(Object source, int action) {
        super(source);
        waitWindowAction = action;
    }


    /**
     * @return the exerciseAction
     */
    public int getWaitWindowAction() {
        return waitWindowAction;
    }


    /**
     * @param fileTreeNodeAction the fileTreeNodeAction to set
     */
    public void setWaitWindowAction(int waitWindowAction) {
        this.waitWindowAction = waitWindowAction;
    }
    
}
