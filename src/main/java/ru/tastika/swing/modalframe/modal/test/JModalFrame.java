package ru.tastika.swing.modalframe.modal.test;


import ru.tastika.swing.modalframe.modal.utility.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;


/**
 * <p>Title: JModalFrame</p>
 * <p>Copyright: Copyright (c) 2001-2003</p>
 * <p/>
 * <p>This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.</p>
 * <p/>
 * <p>This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.</p>
 * <p/>
 * <p>You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA</p>
 * <p/>
 * <hr>
 * <p/>
 * <p><b>JModalFrame</b> is based on the following ideas:</p>
 * <p/>
 * <ol>
 * <li>from Sandip Chitale found at <a href="http://www.jguru.com/jguru/faq/">jGuru FAQ</a>
 * to disable the <code>modalOwner</code> (but this results in flickering windows because of
 * the <code>peer.disable()</code> call.</li>
 * <li>from <a href="dsyrstad@vscorp.com">Dan Syrstad</a> to make a JFrame "busy".</li>
 * <li>from <a href="msmits@quobell.nl">Maks Smits</a> to blur the <code>modalOwner</code>.</li>
 * </ol>
 * <p/>
 * <p>The frame uses the default icon image, which may vary with platform.
 * As usual you could set the icon with the standard JFrame method <code>setIconImage</code>.
 * Alternatively you could set a String with the resource location for
 * the default JModalFrame icon filed under the key <b>"swingx.frame.icon"</b> in the UIManager.</p>
 * @author Jene Jasper
 * @version 1.1
 */
public class JModalFrame extends JFrame implements InputBlocker {


    private Window modalToWindow;
    private Vector<Window> blockingWindows;
    private boolean notifiedModalToWindow;
    private Component returnFocus;

    /**
     * <p>Cursor that indicates that the window is blocked.</p>
     */
    private static Cursor BUSY_CURSOR = Utils.getBusyCursor();

    /**
     * <p>Refinement of raster. Only change this if blurring is too slow.</p>
     */
    private static int BLUR_STEP = 2;

    /**
     * <p>Style of rastering. Only change this if blurring is too slow.</p>
     */
    private static int BLUR_STYLE = JBusyPanel.BLUR_STYLE_RASTER;

    private JPanel busyPanel;
    private Color blurColor;

    private Cursor oldCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private boolean wasResizable = true;
    private int minWidth,
            minHeight;


    /**
     * <p>Default constructor for modal frame.</p>
     */
    public JModalFrame() {
        this(true);
    }


    /**
     * <p>Default constructor for modal frame.</p>
     * @param modal indication if windows should be modal.
     */
    public JModalFrame(boolean modal) {
        this("", modal);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param title frame title.
     */
    public JModalFrame(String title) {
        this(title, true);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param title frame title.
     * @param modal indication if windows should be modal.
     */
    public JModalFrame(String title, boolean modal) {
        this(null, title, modal);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner related window which could be blocked by this one.
     */
    public JModalFrame(Window owner) {
        this(owner, true);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner related window which could be blocked by this one.
     * @param modal indication if windows should be modal.
     */
    public JModalFrame(Window owner, boolean modal) {
        this(owner, null, "", modal);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner       related window which could be blocked by this one.
     * @param returnFocus component in owner which should regain focus after closing this frame.
     */
    public JModalFrame(Window owner, Component returnFocus) {
        this(owner, returnFocus, true);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner       related window which could be blocked by this one.
     * @param returnFocus component in owner which should regain focus after closing this frame.
     * @param modal       indication if windows should be modal.
     */
    public JModalFrame(Window owner, Component returnFocus, boolean modal) {
        this(owner, returnFocus, "", modal);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner related window which could be blocked by this one.
     * @param title frame title.
     */
    public JModalFrame(Window owner, String title) {
        this(owner, title, true);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner related window which could be blocked by this one.
     * @param title frame title.
     * @param modal indication if windows should be modal.
     */
    public JModalFrame(Window owner, String title, boolean modal) {
        this(owner, null, title, modal);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner       related window which could be blocked by this one.
     * @param returnFocus component in owner which should regain focus after closing this frame.
     * @param title       frame title.
     */
    public JModalFrame(Window owner, Component returnFocus, String title) {
        this(owner, returnFocus, title, true);
    }


    /**
     * <p>Constructor for modal frame.</p>
     * @param owner       related window which could be blocked by this one.
     * @param returnFocus component in owner which should regain focus after closing this frame.
     * @param title       frame title.
     * @param modal       indication if windows should be modal.
     */
    public JModalFrame(Window owner, Component returnFocus, String title, boolean modal) {
        super(title);

        if (modal) {
            modalToWindow = owner;
        }

        synchronized (JModalFrame.this) {
            notifiedModalToWindow = true;
            blockingWindows = new Vector<Window>();
        }

        this.returnFocus = returnFocus;

        enableEvents(WindowEvent.WINDOW_EVENT_MASK);

        String optionalFrameIcon = UIManager.getString("swingx.frame.icon");

        if (optionalFrameIcon != null) {
            setIconImage(Utils.getIcon(optionalFrameIcon).getImage());
        }

        this.addComponentListener(new MinimumFrameSizeAdapter());

        initBusyPanel();
    }


    /**
     * <p>Initialize blurring glass pane.</p>
     */
    private void initBusyPanel() {
        blurColor = getContentPane().getBackground().darker();
        busyPanel = new JBusyPanel(BLUR_STEP, blurColor, BLUR_STYLE);

        this.setGlassPane(busyPanel);
    }


    /**
     * <p>Determine if this frame is blocked.</p>
     * @return indication if this frame is blocked.
     */
    public boolean isBusy() {
        return (blockingWindows.size() > 0);
    }


    /**
     * <p>Should be called by blocking modal window/frame children.</p>
     * <p/>
     * <p>The counter is used to make it possible to open several modal window/frame children
     * from this frame.</p>
     * <p/>
     * <p>Note:</p>
     * <p/>
     * <ul>
     * <li>Setting the frame cursor <b>and</b> the glass pane cursor in this order works around
     * the Win32 problem where you have to move the mouse 1 pixel to get the Cursor to change.
     * <li>Force glass pane to get focus so that we consume <code>KeyEvents</code>.
     * </ul>
     * @param busy           indication if a modal window/frame is opened or closed and this frame is blocked
     *                       or unblocked.
     * @param blockingWindow child window that blocks owner.
     */
    public void setBusy(boolean busy, Window blockingWindow) {
        if (busy) {
            BUSY_CURSOR = Utils.getBusyCursor();

            if (blockingWindows.size() == 0) {
                wasResizable = isResizable();
                setResizable(false);
                oldCursor = this.getCursor();
                this.setCursor(BUSY_CURSOR);
            }

            this.getGlassPane().setVisible(true);

            if (!blockingWindows.contains(blockingWindow)) {
                blockingWindows.add(blockingWindow);
            }

            busyPanel.grabFocus();
            this.getGlassPane().setCursor(BUSY_CURSOR);
        }
        else {
            blockingWindows.remove(blockingWindow);

            if (blockingWindows.size() == 0) {
                this.getGlassPane().setCursor(oldCursor);
                this.getGlassPane().setVisible(false);

                this.requestFocus();
                this.setCursor(oldCursor);
                this.setResizable(wasResizable);
            }
        }
    }


    /**
     * <p>Place the frame in the center of the screen.</p>
     */
    public void centerOfScreen() {
        Utils.centerOfScreen(this);
    }


    /**
     * <p>Try to center the frame in the owner window/frame.</p>
     */
    public void centerOfOwner() {
        Utils.centerOfOwner(this, getOwner());
    }


    /**
     * <p>Try to position the frame relative to the specified component.</p>
     * @param child component where the frame should be positioned just below.
     */
    public void relativeToOwnerChild(Component child) {
        Utils.relativeToOwnerChild(this, getOwner(), child);
    }


    /**
     * <p>Set a minimal frame resize.</p>
     * @param dim minimal frame size.
     */
    public void setMinSize(Dimension dim) {
        setMinSize(dim.width, dim.height);
    }


    /**
     * <p>Set a minimal frame resize.</p>
     * @param width  minimal frame width.
     * @param height minimal frame height.
     */
    public void setMinSize(int width, int height) {
        minWidth = width;
        minHeight = height;
    }


    /**
     * <p>Apparently this frame is closed and thus owner is no longer blocked by this one.</p>
     */
    private void restoreOwner() {
        synchronized (JModalFrame.this) {
            if ((modalToWindow != null) && !notifiedModalToWindow) {
                if (modalToWindow instanceof InputBlocker) {
                    ((InputBlocker) modalToWindow).setBusy(false, this);
                }
                else {
                    modalToWindow.setEnabled(true);
                }

                modalToWindow.toFront();

                notifiedModalToWindow = true;
            }

            if (returnFocus != null) {
                Window owner = SwingUtilities.windowForComponent(returnFocus);
                boolean stillBusy;

                if (owner instanceof InputBlocker) {
                    stillBusy = ((InputBlocker) owner).isBusy();
                }
                else {
                    stillBusy = !owner.isEnabled();
                }

                if (!stillBusy) {
                    returnFocus.requestFocus();
                }
            }
        }
    }


    /**
     * <p>Overrule <code>DefaultCloseOperation</code> when frame is blocked.</p>
     * @return the default close operation.
     */
    public int getDefaultCloseOperation() {
        if (isBusy()) {
            return JFrame.DO_NOTHING_ON_CLOSE;
        }
        else {
            return super.getDefaultCloseOperation();
        }
    }


    /**
     * <p>Monitor window events and trigger appropriate actions.</p>
     * @param windowEvent the event that triggered this method call.
     */
    protected void processWindowEvent(WindowEvent windowEvent) {
        switch (windowEvent.getID()) {
            case WindowEvent.WINDOW_ICONIFIED:
                checkIconifyAllowed(windowEvent);
                break;
            case WindowEvent.WINDOW_ACTIVATED:
                checkForBlockingWindows(windowEvent);
                break;
            case WindowEvent.WINDOW_CLOSING:
                tryToDispose(windowEvent);
                break;
            case WindowEvent.WINDOW_CLOSED:
                close(windowEvent);
                break;
            default:
                super.processWindowEvent(windowEvent);
                break;
        }
    }


    /**
     * <p>Check if frame may be iconified. If the frame is currently blocked it is not allowed to.</p>
     * @param windowEvent the event that triggered this method call.
     */
    private void checkIconifyAllowed(WindowEvent windowEvent) {
        if (isBusy()) {
            setState(NORMAL);

            return;
        }

        super.processWindowEvent(windowEvent);
    }


    /**
     * <p>Check if frame may be activated. Otherwise active the blocking windows.</p>
     * @param windowEvent the event that triggered this method call.
     */
    private void checkForBlockingWindows(WindowEvent windowEvent) {
        super.processWindowEvent(windowEvent);

        if (isBusy()) {
            Window blockingWindow;
            Iterator iterator = blockingWindows.iterator();

            while (iterator.hasNext()) {
                blockingWindow = (Window) iterator.next();

                if (blockingWindow instanceof Frame) {
                    ((Frame) blockingWindow).setState(NORMAL);
                }

                blockingWindow.toFront();
            }
        }
    }


    /**
     * <p>Try to dispose the frame. If the frame is currently blocked it is not allowed to.</p>
     * @param windowEvent the event that triggered this method call.
     */
    private void tryToDispose(WindowEvent windowEvent) {
        if (isBusy()) {
            return;
        }

        close(windowEvent);
    }


    /**
     * <p>Close the frame and unblock the owner.</p>
     * @param windowEvent the event that triggered this method call.
     */
    private void close(WindowEvent windowEvent) {
        restoreOwner();
        release();

        super.processWindowEvent(windowEvent);
    }


    /**
     * <p>Option to make another thread wait for this frame to close.</p>
     */
    synchronized final public void wait_for_close() {
        try {
            wait();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }


    /**
     * <p>Notify threads that are waiting for this frame to close, that the frame is closing.</p>
     */
    synchronized final public void release() {
        notifyAll();
    }


    /**
     * <p>Handle blocking of owner when this frame is shown.</p>
     * <p/>
     * <p><b>Note:</b>Unlike JDialog calling this method doesn't halt the calling thread.
     * To create the same effect, call the method {@link #wait_for_close() wait_for_close}
     * afterwards.</p>
     */
    public void setVisible(boolean visible) {
        if (visible) {
            synchronized (JModalFrame.this) {
                if ((modalToWindow != null) && notifiedModalToWindow) {
                    if (modalToWindow instanceof InputBlocker) {
                        ((InputBlocker) modalToWindow).setBusy(true, this);
                    }
                    else {
                        modalToWindow.setEnabled(false);
                    }

                    notifiedModalToWindow = false;
                }
            }
        }
        else {
            if (isBusy()) {
                setVisible(true);

                return;
            }

            restoreOwner();
            release();
        }
        super.setVisible(visible);
    }

    /**
     * <p>Disable closing of frame when child window/frame has this frame blocked.</p>
     * <p/>
     * <p><b>Note:</b>Unlike JDialog calling this method doesn't halt the calling thread.
     * To create the same effect, call the method {@link #wait_for_close() wait_for_close}
     * afterwards.</p>
     *
     * @param visible new frame visibility.
     */
//    public void setVisible(boolean visible) {
//        if (!visible) {
//            if (isBusy()) {
//                setVisible(true);
//
//                return;
//            }
//
//            restoreOwner();
//            release();
//        }
//
//        super.setVisible(visible);
//    }


    /**
     * <p>Title: MinimumFrameSizeAdapter inner class of JModalFrame to monitor minimum frame resizing.</p>
     * <p>Description: Enhancements for javax.swing</p>
     * <p>Copyright: Copyright (c) 2001-2003</p>
     * <p/>
     * <p>This library is free software; you can redistribute it and/or
     * modify it under the terms of the GNU Lesser General Public
     * License as published by the Free Software Foundation; either
     * version 2.1 of the License, or (at your option) any later version.</p>
     * <p/>
     * <p>This library is distributed in the hope that it will be useful,
     * but WITHOUT ANY WARRANTY; without even the implied warranty of
     * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
     * Lesser General Public License for more details.</p>
     * <p/>
     * <p>You should have received a copy of the GNU Lesser General Public
     * License along with this library; if not, write to the Free Software
     * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA</p>
     * @author Jene Jasper
     * @version 1.0
     */
    private class MinimumFrameSizeAdapter extends ComponentAdapter {
        /**
         * <p>In case the frame is resized make sure that isn't resized smaller than the minimum
         * width and/or height.</p>
         * @param componentEvent the event that triggered this method call.
         */
        public void componentResized(ComponentEvent componentEvent) {
            int width, height;

            width = getWidth();
            height = getHeight();

            if ((minWidth > 0) && (width < minWidth)) {
                width = minWidth;
            }

            if ((minHeight > 0) && (height < minHeight)) {
                height = minHeight;
            }

            if ((width != getWidth()) || (height != getHeight())) {
                setSize(width, height);
            }
        }
    }
}
