package ru.tastika.swing.modalframe.modal.utility;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;


/**
 * <p>Title: Some support methods and some alternatives for swing methods</p>
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
 * @version 1.1
 */
public class Utils {


    private static HashMap<String, ImageIcon> imageCache = new HashMap<String, ImageIcon>();
    private static Utils instance;

    private static int[] polygonXSign = new int[] { 8, 23, 31, 31, 23, 8, 0, 0 };
    private static int[] polygonYSign = new int[] { 0, 0, 8, 23, 31, 31, 23, 8 };
    private static int[] polygonXSignBorder = new int[] { 8, 23, 30, 30, 23, 8, 1, 1 };
    private static int[] polygonYSignBorder = new int[] { 1, 1, 8, 23, 30, 30, 23, 8 };
    private static int[] polygonXSignS = new int[] { 4, 5, 7, 8, 8, 4, 4, 5, 7, 8 };
    private static int[] polygonYSignS = new int[] { 21, 22, 22, 21, 17, 13, 9, 8, 8, 9 };
    private static int[] polygonXSignT = new int[] { 10, 14, 12, 12 };
    private static int[] polygonYSignT = new int[] { 8, 8, 8, 22 };
    private static int[] polygonXSignO = new int[] { 16, 17, 19, 20, 20, 19, 17, 16, 16 };
    private static int[] polygonYSignO = new int[] { 9, 8, 8, 9, 21, 22, 22, 21, 9 };
    private static int[] polygonXSignP = new int[] { 22, 22, 25, 26, 26, 25, 22 };
    private static int[] polygonYSignP = new int[] { 22, 8, 8, 9, 14, 15, 15 };
    private static int[] polygonXBorderS = new int[] { 3, 5, 7, 9, 9, 5, 5, 7, 8, 9, 7, 5, 3, 3, 7, 7, 5, 4, 3 };
    private static int[] polygonYBorderS = new int[] { 21, 23, 23, 21, 17, 13, 9, 9, 10, 9, 7, 7, 9, 13, 17, 21, 21, 20, 21 };
    private static int[] polygonXBorderT = new int[] { 9, 15, 15, 13, 13, 11, 11, 9, 9 };
    private static int[] polygonYBorderT = new int[] { 7, 7, 9, 9, 23, 23, 9, 9, 7 };
    private static int[] polygonXOuterBorderO = new int[] { 15, 17, 19, 21, 21, 19, 17, 15, 15 };
    private static int[] polygonYOuterBorderO = new int[] { 9, 7, 7, 9, 21, 23, 23, 21, 9 };
    private static int[] polygonXInnerBorderO = new int[] { 17, 19, 19, 17, 17 };
    private static int[] polygonYInnerBorderO = new int[] { 9, 9, 21, 21, 9 };
    private static int[] polygonXOuterBorderP = new int[] { 21, 21, 25, 27, 27, 25, 23, 23, 21 };
    private static int[] polygonYOuterBorderP = new int[] { 23, 7, 7, 9, 14, 16, 16, 23, 23 };
    private static int[] polygonXInnerBorderP = new int[] { 23, 25, 25, 23, 23 };
    private static int[] polygonYInnerBorderP = new int[] { 9, 9, 14, 14, 9 };

    private static Color DARK_RED = new Color(173, 57, 57);
    private static Color MEDIUM_RED = new Color(198, 115, 115);
    private static Color LIGHT_RED = new Color(214, 156, 156);
    private static Color WHITE = Color.white;

    /**
     * Safety margin for window positioning in case of operating system toolbars and so on.
     */
    private final static int SCREEN_SAFETY_MARGIN = 50;


    /**
     * <p>Private default constructor to create the notion of a singleton.</p>
     */
    private Utils() {
    }


    /**
     * <p>Retrieval of the <code>Utils</code> singleton instance.</p>
     * @return instance of <code>Utils</code> object.
     */
    public static Utils getInstance() {
        if (instance == null) {
            instance = new Utils();
        }

        return instance;
    }


    /**
     * <p>A simple minded look and feel change: ask each node in the tree to updateUI()
     * - that is, to initialize its UI property with the current look and feel.
     * (Copy of swing version)</p>
     * @param cmp component which UI must be updated.
     */
    public static void updateComponentTreeUI(Component cmp) {
        updateComponentTreeUI0(cmp);

        cmp.invalidate();
        cmp.validate();
        cmp.repaint();
    }


    /**
     * <p>A simple minded look and feel change: ask each node in the tree to updateUI()
     * - that is, to initialize its UI property with the current look and feel.
     * (Copy of swing version)</p>
     * <p/>
     * <p>Note: With one minor change, that the component itself is changed last to make
     * changes to children immediately visible instead of requiring a repaint of the window or frame
     * triggered by the user.</p>
     * @param cmp component which UI must be updated.
     */
    private static void updateComponentTreeUI0(Component cmp) {
        Component[] children = null;

        if (cmp instanceof JMenu) {
            children = ((JMenu) cmp).getMenuComponents();
        }
        else if (cmp instanceof Container) {
            children = ((Container) cmp).getComponents();
        }

        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                updateComponentTreeUI0(children[i]);
            }
        }

        if (cmp instanceof JComponent) {
            ((JComponent) cmp).updateUI();
        }
    }


    /**
     * <p>Create a cursor for blocked modal component.
     * The default one which is a STOP sign.
     * Alternatively you could set a String with the resource location for
     * the default busy icon filed under the key <b>"swingx.busy.cursor"</b> in the UIManager.</p>
     * @return busy cursor.
     */
    public static Cursor getBusyCursor() {
        Image cursor = null;
        String optionalCursorIcon = UIManager.getString("swingx.busy.cursor");

        if (optionalCursorIcon == null) {
            cursor = getStopImage();
        }
        else {
            cursor = getIcon(optionalCursorIcon).getImage();
        }

        Cursor busyCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "BUSY");

        if (busyCursor.getType() != Cursor.CUSTOM_CURSOR) {
            busyCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
        }

        return busyCursor;
    }


    /**
     * <p>Create a default image in case the specified image couldn't be found.</p>
     * @return default image.
     */
    public static Image getMissingImage() {
        int size = 16;
        Image rv = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics g = rv.getGraphics();

        g.setColor(Color.red);
        g.fillRect(0, 0, size, size);
        g.setColor(Color.white);

        g.drawLine(2, 2, size - 3, size - 3);
        g.drawLine(3, 2, size - 3, size - 4);
        g.drawLine(2, 3, size - 4, size - 3);

        g.drawLine(2, size - 3, size - 3, 2);
        g.drawLine(2, size - 4, size - 4, 2);
        g.drawLine(3, size - 3, size - 3, 3);

        return rv;
    }


    /**
     * <p>Create a STOP sign image.</p>
     * @return STOP sign image.
     */
    public static Image getStopImage() {
        int size = 32;
        Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics gfx = img.getGraphics();

        gfx.setColor(DARK_RED);
        gfx.fillPolygon(polygonXSign, polygonYSign, polygonXSign.length);

        gfx.setColor(LIGHT_RED);
        gfx.drawPolygon(polygonXSign, polygonYSign, polygonXSign.length);

        gfx.setColor(WHITE);
        gfx.drawPolygon(polygonXSignBorder, polygonYSignBorder, polygonXSignBorder.length);
        gfx.drawPolyline(polygonXSignS, polygonYSignS, polygonXSignS.length);
        gfx.drawPolyline(polygonXSignT, polygonYSignT, polygonXSignT.length);
        gfx.drawPolyline(polygonXSignO, polygonYSignO, polygonXSignO.length);
        gfx.drawPolyline(polygonXSignP, polygonYSignP, polygonXSignP.length);

        gfx.setColor(MEDIUM_RED);
        gfx.drawPolyline(polygonXBorderS, polygonYBorderS, polygonXBorderS.length);
        gfx.drawPolyline(polygonXBorderT, polygonYBorderT, polygonXBorderT.length);
        gfx.drawPolyline(polygonXOuterBorderO, polygonYOuterBorderO, polygonXOuterBorderO.length);
        gfx.drawPolyline(polygonXInnerBorderO, polygonYInnerBorderO, polygonXInnerBorderO.length);
        gfx.drawPolyline(polygonXOuterBorderP, polygonYOuterBorderP, polygonXOuterBorderP.length);
        gfx.drawPolyline(polygonXInnerBorderP, polygonYInnerBorderP, polygonXInnerBorderP.length);

        return img;
    }


    /**
     * <p>Retrieve an icon based on the name.</p>
     * @param name name of icon to locate.
     * @return icon for name.
     */
    public static ImageIcon getIcon(String name) {
        return getIcon(name, new ImageIcon(getMissingImage()));
    }


    /**
     * <p>Retrieve an icon based on the name.
     * If the icon couldn't be located use the specified default icon.</p>
     * @param name        name of icon to locate.
     * @param defaultIcon default icon if named one couldn't be found.
     * @return icon for name.
     */
    public static ImageIcon getIcon(String name, ImageIcon defaultIcon) {
        ImageIcon icon;
        URL url;

        try {
            icon = (ImageIcon) imageCache.get(name);

            if (icon != null) {
                return icon;
            }

            url = getInstance().getClass().getClassLoader().getResource(name);

            if (url == null) {
                return defaultIcon;
            }

            icon = new ImageIcon(url);
            imageCache.put(name, icon);

            return icon;
        }
        catch (NullPointerException npe) {
            System.out.println("Error retrieving icon:" + name);

            return defaultIcon;
        }
    }


    /**
     * <p>Place the window in the center of the screen.</p>
     * @param win window that must be positioned on screen.
     */
    public static void centerOfScreen(Window win) {
        Toolkit tk = win.getToolkit();
        Dimension dim = tk.getScreenSize();

        win.setLocation(dim.width / 2 - win.getWidth() / 2, dim.height / 2 - win.getHeight() / 2);
    }


    /**
     * <p>Try to center the window in the owner window.</p>
     * @param win   window that must be positioned on screen.
     * @param owner window that must be used as virtual screen.
     */
    public static void centerOfOwner(Window win, Window owner) {
        double x, y, w, h;
        Dimension scrSize, winSize, ownerSize;
        Point p;

        scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        winSize = win.getSize();
        ownerSize = owner.getSize();

        x = (ownerSize.getWidth() - winSize.getWidth()) / 2;
        y = (ownerSize.getHeight() - winSize.getHeight()) / 2;

        p = owner.getLocation();

        x += p.getX();
        y += p.getY();

        if (x + winSize.getWidth() > scrSize.getWidth() - SCREEN_SAFETY_MARGIN) {
            x = scrSize.getWidth() - winSize.getWidth() - SCREEN_SAFETY_MARGIN;
        }

        if (x < 0) {
            x = 0;
        }

        if (y + winSize.getHeight() > scrSize.getHeight() - SCREEN_SAFETY_MARGIN) {
            y = scrSize.getHeight() - winSize.getHeight() - SCREEN_SAFETY_MARGIN;
        }

        if (y < 0) {
            y = 0;
        }

        win.setLocation((int) x, (int) y);
    }


    /**
     * <p>Try to position the window relative to the specified component.</p>
     * @param win   window that must be positioned on screen.
     * @param owner window that must be used as virtual screen.
     * @param child component where the window should be positioned just below.
     */
    public static void relativeToOwnerChild(Window win, Window owner, Component child) {
        double x, y, w, h, maxX;
        Dimension scrSize, winSize, childSize;
        Point p;
        Component parent;

        scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        winSize = win.getSize();
        p = child.getLocation();
        childSize = child.getSize();

        x = SCREEN_SAFETY_MARGIN;
        maxX = p.getX() + childSize.getWidth();
        y = p.getY() + childSize.getHeight();
        parent = child.getParent();

        while ((parent != null) && (parent != owner)) {
            p = parent.getLocation();
            maxX += p.getX();
            y += p.getY();
            parent = parent.getParent();
        }

        p = owner.getLocation();

        x += p.getX();
        y += p.getY();

        if (x + winSize.getWidth() < maxX) {
            x = maxX - winSize.getWidth();
        }

        if (x + winSize.getWidth() > scrSize.getWidth() - SCREEN_SAFETY_MARGIN) {
            x = scrSize.getWidth() - winSize.getWidth() - SCREEN_SAFETY_MARGIN;
        }

        if (x < 0) {
            x = 0;
        }

        if (y + winSize.getHeight() > scrSize.getHeight() - SCREEN_SAFETY_MARGIN) {
            y = scrSize.getHeight() - winSize.getHeight() - SCREEN_SAFETY_MARGIN;
        }

        if (y < 0) {
            y = 0;
        }

        win.setLocation((int) x, (int) y);
    }


    /**
     * <p>Check if new location keeps the window on the screen otherwise adjust position and place window.</p>
     * @param win window that must be repositioned on screen.
     * @param x   new x location for window.
     * @param y   new y location for window.
     */
    public static void keepWindowOnScreen(Window win, int x, int y) {
        Dimension scrSize, winSize;
        Component parent;

        scrSize = Toolkit.getDefaultToolkit().getScreenSize();
        winSize = win.getSize();

        if (x + SCREEN_SAFETY_MARGIN > scrSize.getWidth()) {
            x = (int) scrSize.getWidth() - SCREEN_SAFETY_MARGIN;
        }

        if (x + (int) winSize.getWidth() - SCREEN_SAFETY_MARGIN < 0) {
            x = SCREEN_SAFETY_MARGIN - (int) winSize.getWidth();
        }

        if (y + SCREEN_SAFETY_MARGIN > scrSize.getHeight()) {
            y = (int) scrSize.getHeight() - SCREEN_SAFETY_MARGIN;
        }

        if (y + (int) winSize.getHeight() - SCREEN_SAFETY_MARGIN < 0) {
            y = SCREEN_SAFETY_MARGIN - (int) winSize.getHeight();
        }

        win.setLocation(x, y);
    }
}
