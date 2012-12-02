package ru.tastika.swing.modalframe.modal.test;


import ru.tastika.swing.modalframe.modal.utility.Utils;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Vector;


/**
 * <p>Title: JModalWindow</p>
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
 * <p><b>JModalWindow</b> is based on the following ideas:</p>
 * <p/>
 * <ol>
 * <li>from Sandip Chitale found at <a href="http://www.jguru.com/jguru/faq/">jGuru FAQ</a>
 * to disable the <code>modalOwner</code> (but this results in flickering windows because of
 * the <code>peer.disable()</code> call.</li>
 * <li>from <a href="dsyrstad@vscorp.com">Dan Syrstad</a> to make a JFrame "busy".</li>
 * <li>from <a href="msmits@quobell.nl">Maks Smits</a> to blur the <code>modalOwner</code>.</li>
 * </ol>
 * <p/>
 * <p>Because the placement of the window could block the view on a window below it,
 * I added the possiblity to drag a window.</p>
 * @author Jene Jasper
 * @version 1.1
 */
public class JModalWindow extends JWindow implements InputBlocker {


    private Window modalToWindow;
    private Vector<Window> blockingWindows;
    private boolean notifiedModalToWindow;
    private Component returnFocus;
    private JPanel contentPanel;
    private static Window sharedOwner;
    private Point priorDragLocation;

    /**
     * <p>Distance from border which activates drag cursor.</p>
     */
    private static int DRAG_BORDER_DISTANCE = 1;

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


    /**
     * <p>Default constructor for modal window.</p>
     */
    public JModalWindow() {
        this(true);
    }


    /**
     * <p>Constructor for modal window.</p>
     * @param modal indication if windows should be modal.
     */
    public JModalWindow(boolean modal) {
        this((Window) null, modal);
    }


    /**
     * <p>Constructor for modal window.</p>
     * @param owner related window which could be blocked by this one.
     */
    public JModalWindow(Window owner) {
        this(owner, true);
    }


    /**
     * <p>Constructor for modal window.</p>
     * @param owner related window which could be blocked by this one.
     * @param modal indication if windows should be modal.
     */
    public JModalWindow(Window owner, boolean modal) {
        this(owner, null, modal);
    }


    /**
     * <p>Constructor for modal window.</p>
     * @param owner       related window which could be blocked by this one.
     * @param returnFocus component in owner which should regain focus after closing this window.
     */
    public JModalWindow(Window owner, Component returnFocus) {
        this(owner, returnFocus, true);
    }


    /**
     * <p>Constructor for modal window.</p>
     * @param owner       related window which could be blocked by this one.
     * @param returnFocus component in owner which should regain focus after closing this window.
     * @param modal       indication if windows should be modal.
     */
    public JModalWindow(Window owner, Component returnFocus, boolean modal) {
        super(owner == null ? getSharedOwnerFrame() : owner);

        this.returnFocus = returnFocus;

        contentPanel = new JPanel();

        contentPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

        contentPanel.setLayout(new BorderLayout() {
            public void addLayoutComponent(Component comp, Object constraints) {
                if (constraints == null) {
                    constraints = BorderLayout.CENTER;
                }

                super.addLayoutComponent(comp, constraints);
            }
        });

        setContentPane(contentPanel);

        if (modal) {
            modalToWindow = owner;
        }

        synchronized (JModalWindow.this) {
            notifiedModalToWindow = true;
            blockingWindows = new Vector<Window>();
        }

        enableEvents(WindowEvent.WINDOW_EVENT_MASK | ComponentEvent.MOUSE_MOTION_EVENT_MASK);

        initBusyPanel();
    }


    /**
     * <p>Default owner for windows that don't have a physical owner.</p>
     * @return default owner.
     */
    private static Window getSharedOwnerFrame() {
        JWindow jw;

        if (sharedOwner == null) {
            jw = new JWindow();
            sharedOwner = jw.getOwner();
        }

        return sharedOwner;
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
     * <p>Determine if this window is blocked.</p>
     * @return indication if this window is blocked.
     */
    public boolean isBusy() {
        return (blockingWindows.size() > 0);
    }


    /**
     * <p>Should be called by blocking modal window/frame children.</p>
     * <p/>
     * <p>The counter is used to make it possible to open several modal window/frame children
     * from this window.</p>
     * <p/>
     * <p>Note:</p>
     * <p/>
     * <ul>
     * <li>Setting the frame cursor <b>and</b> the glass pane cursor in this order works around
     * the Win32 problem where you have to move the mouse 1 pixel to get the Cursor to change.
     * <li>Force glass pane to get focus so that we consume <code>KeyEvents</code>.
     * </ul>
     * @param busy           indication if a modal window/frame is opened or closed and this window is blocked
     *                       or unblocked.
     * @param blockingWindow child window that blocks owner.
     */
    public void setBusy(boolean busy, Window blockingWindow) {
        if (busy) {
            BUSY_CURSOR = Utils.getBusyCursor();

            if (blockingWindows.size() == 0) {
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
            }
        }
    }


    /**
     * <p>Place the window in the center of the screen.</p>
     */
    public void centerOfScreen() {
        Utils.centerOfScreen(this);
    }


    /**
     * <p>Try to center the window in the owner window/frame.</p>
     */
    public void centerOfOwner() {
        Utils.centerOfOwner(this, getOwner());
    }


    /**
     * <p>Try to position the window relative to the specified component.</p>
     * @param child component where the window should be positioned just below.
     */
    public void relativeToOwnerChild(Component child) {
        Utils.relativeToOwnerChild(this, getOwner(), child);
    }


    /**
     * <p>Apparently this window is closed and thus owner is no longer blocked by this one.</p>
     */
    private void restoreOwner() {
        synchronized (JModalWindow.this) {
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
     * <p>Determine whether the window should be dragged or the drag cursor should be turned on or off.</p>
     * @param mouseEvent the event that triggered this method call.
     */
    protected void processMouseMotionEvent(MouseEvent mouseEvent) {
        switch (mouseEvent.getID()) {
            case MouseEvent.MOUSE_MOVED:
                checkDragZone(mouseEvent);
                break;
            case MouseEvent.MOUSE_DRAGGED:
                dragWindow(mouseEvent);
                break;
        }

        super.processMouseMotionEvent(mouseEvent);
    }


    /**
     * <p>Determine based on the mouse position if the drag cursor should be turned on or off.</p>
     * @param mouseEvent the event that triggered this method call.
     */
    private void checkDragZone(MouseEvent mouseEvent) {
        priorDragLocation = mouseEvent.getPoint();

        if ((mouseEvent.getX() < DRAG_BORDER_DISTANCE) || (mouseEvent.getX() >= getWidth() - DRAG_BORDER_DISTANCE)) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
        else
        if ((mouseEvent.getY() < DRAG_BORDER_DISTANCE) || (mouseEvent.getY() >= getHeight() - DRAG_BORDER_DISTANCE)) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        }
        else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }


    /**
     * <p>Determine based on the mouse position the new window location.</p>
     * <p/>
     * <p>Note: It is no longer possible to drag the window off the screen.</p>
     * @param mouseEvent the event that triggered this method call.
     */
    private void dragWindow(MouseEvent mouseEvent) {
        int x, y, dx, dy;

        if (this.getCursor().equals(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR))) {
            if (priorDragLocation == null) {
                priorDragLocation = mouseEvent.getPoint();
            }
            else {
                dx = mouseEvent.getX() - priorDragLocation.x;
                dy = mouseEvent.getY() - priorDragLocation.y;

                x = getX() + dx;
                y = getY() + dy;

                if ((x != getX()) || (y != getY())) {
                    Utils.keepWindowOnScreen(this, x, y);
                }

                priorDragLocation = null;
            }
        }
    }


    /**
     * <p>Monitor window events and trigger appropriate actions.</p>
     * @param windowEvent the event that triggered this method call.
     */
    protected void processWindowEvent(WindowEvent windowEvent) {
        switch (windowEvent.getID()) {
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
     * <p>Try to dispose the window. If the window is currently blocked it is not allowed to.</p>
     * @param windowEvent the event that triggered this method call.
     */
    private void tryToDispose(WindowEvent windowEvent) {
        if (isBusy()) {
            return;
        }
        else {
            dispose();
        }

        super.processWindowEvent(windowEvent);
    }


    /**
     * <p>Close the window and unblock the owner.</p>
     * @param windowEvent the event that triggered this method call.
     */
    private void close(WindowEvent windowEvent) {
        restoreOwner();
        release();

        super.processWindowEvent(windowEvent);
    }


    /**
     * <p>Option to make another thread wait for this window to close.</p>
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
     * <p>Notify threads that are waiting for this window to close, that the window is closing.</p>
     */
    synchronized final public void release() {
        notifyAll();
    }


    /**
     * <p>Paint a border around the window.</p>
     * @param gfx window <code>Graphics</code>.
     */
    public void paint(Graphics gfx) {
        gfx.draw3DRect(0, 0, getWidth(), getHeight(), false);

        super.paint(gfx);
    }


    /**
     * <p>Set the window background color.</p>
     * @param color background color.
     */
    public void setBackground(Color color) {
        super.setBackground(color);
        this.contentPanel.setBackground(color);
    }


    /**
     * <p>Set the window foreground color.</p>
     * @param color foreground color.
     */
    public void setForeground(Color color) {
        super.setForeground(color);
        this.contentPanel.setForeground(color);
    }


    /**
     * <p>Handle blocking of owner when this window is shown.</p>
     * <p/>
     * <p><b>Note:</b>Unlike JDialog calling this method doesn't halt the calling thread.
     * To create the same effect, call the method {@link #wait_for_close() wait_for_close}
     * afterwards.</p>
     */
    public void setVisible(boolean visible) {
        if (visible) {
            synchronized (JModalWindow.this) {
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
     * <p>Disable closing of window when child window/frame has this window blocked.</p>
     * <p/>
     * <p><b>Note:</b>Unlike JDialog calling this method doesn't halt the calling thread.
     * To create the same effect, call the method {@link #wait_for_close() wait_for_close}
     * afterwards.</p>
     *
     * @param visible new window visibility.
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
}
