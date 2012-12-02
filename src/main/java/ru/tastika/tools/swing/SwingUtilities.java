package ru.tastika.tools.swing;


import javax.swing.*;
import java.awt.*;


/**
 *
 * @author hobal
 */
public class SwingUtilities {


    public static final Font SERIF_PLAIN_FONT = new Font("Serif", Font.PLAIN, 10);
    public static final Font SANS_SERIF_PLAIN_FONT = new Font("SansSerif", Font.PLAIN, 10);


    public static void setFontForAll(Font font, Component[] comps) {
        Component[] temp;
        for (Component component : comps) {
            component.setFont(font);
            if (component instanceof Container) {
                temp = ((Container) component).getComponents();
                if (temp.length > 0) {
                    setFontForAll(font, temp);
                }
            }
        }
    }


    public static void setBackgroundForAllPanels(Color color, Component[] comps) {
        Component[] temp;
        for (Component component : comps) {
            if (component instanceof JPanel || component instanceof JRootPane || component instanceof JLayeredPane || component instanceof JMenuItem) {
                component.setBackground(color);
            }
            if (component instanceof JMenu) {
                temp = ((JMenu) component).getMenuComponents();
                if (temp.length > 0) {
                    setBackgroundForAllPanels(color, temp);
                }
            }
            else if (component instanceof Container) {
                temp = ((Container) component).getComponents();
                if (temp.length > 0) {
                    setBackgroundForAllPanels(color, temp);
                }
            }
        }
    }


    /**
     * Center a Window, Frame, JFrame, Dialog, etc.
     * After packing a Frame or Dialog, centre it on the screen.
     */
    public static void center(Window w, boolean centerOfParent) {
        centre(w, centerOfParent);
    }


    /**
     * Centre a Window, Frame, JFrame, Dialog, etc.
     * After packing a Frame or Dialog, centre it on the screen or parent component.
     */
    public static void centre(Window w, boolean centerOfParent) {
        int parentX = 0;
        int parentY = 0;
        Dimension us = w.getSize();
        Dimension them;
        Container parent = w.getParent();
        if (centerOfParent && parent != null) {
            Point location = parent.getLocation();
            parentX = location.x;
            parentY = location.y;
            them = parent.getSize();
            if (parentX < 0 || parentY < 0 || them.width < us.width || them.height < us.height) {
                parentX = 0;
                parentY = 0;
                them = Toolkit.getDefaultToolkit().getScreenSize();
            }
        }
        else {
            them = Toolkit.getDefaultToolkit().getScreenSize();
        }
        int newX = parentX + (them.width - us.width) / 2;
        int newY = parentY + (them.height - us.height) / 2;
        w.setLocation(newX, newY);
    }


    /**
     * Center a Window, Frame, JFrame, Dialog, etc.,
     * but do it the American Spelling Way :-)
     */
    public static void center(Window w) {
        SwingUtilities.centre(w, true);
    }


    /**
     * Center relatively to the parent
     * @param w
     * @param parent
     */
    public static void center(Window w, Window parent) {
        Dimension us = w.getSize(),
                them = parent.getSize();
        int newX = parent.getX() + (them.width - us.width) / 2;
        int newY = parent.getY() + (them.height - us.height) / 2;
        w.setLocation(newX, newY);
    }

}
