package ru.tastika.swing.modalframe.modal.test;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * <p>Title: JBusyPanel used to indicate if a modal component is blocked.</p>
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
public class JBusyPanel extends JPanel {
    /**
     * <p>Blur screen with horizontal lines.
     * Use this option if <code>BLUR_STYLE_RASTER</code> is too slow.</p>
     */
    public static int BLUR_STYLE_LINE = 0;

    /**
     * <p>Blur screen with diagonal lines.</p>
     */
    public static int BLUR_STYLE_RASTER = 1;

    private int blurStep;
    private Color blurColor;
    private int blurStyle;


    /**
     * <p>Used to blur a modal component.</p>
     * @param blurStep  size of steps between blurring lines.
     * @param blurColor color used for blurring.
     * @param blurStyle blurring style.
     */
    public JBusyPanel(int blurStep, Color blurColor, int blurStyle) {
        this.blurStep = blurStep;
        this.blurColor = blurColor;
        this.blurStyle = blurStyle;

        this.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                JBusyPanel.this.grabFocus();
            }
        });

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                keyEvent.consume();
            }
        });

        this.setOpaque(false);

        this.setFocusTraversalKeysEnabled(false);
        this.setFocusCycleRoot(true);
    }


    /**
     * <p>Block any focusmanager.</p>
     * @return indication that the <code>JBusyPanel</code> handles the focus events.
     * @deprecated As of 1.4
     */
    public boolean isManagingFocus() {
        return true;
    }


    /**
     * <p>Blur panel with required style.</p>
     * @param gfx panel <code>Graphics</code>.
     */
    public void paint(Graphics gfx) {
        if (blurStyle == BLUR_STYLE_LINE) {
            paintStyleLine(gfx);
        }
        else {
            paintStyleRaster(gfx);
        }
    }


    /**
     * <p>Blur panel with horizontal lines.</p>
     * @param gfx panel <code>Graphics</code>.
     */
    private void paintStyleLine(Graphics gfx) {
        int mid;

        gfx.setColor(blurColor);
        mid = blurStep / 2;

        for (int y = 0; y < getHeight(); y += blurStep) {
            gfx.drawLine(0, y, getWidth(), y);
        }
    }


    /**
     * <p>Blur panel with diagonal crossed lines.</p>
     * @param gfx panel <code>Graphics</code>.
     */
    public void paintStyleRaster(Graphics gfx) {
        int mid, max;

        gfx.setColor(blurColor);
        mid = blurStep / 2;
        max = Math.max(getWidth(), getHeight());

        for (int xy = 0; xy < max; xy += blurStep) {
            gfx.drawLine(0, xy, max, xy + max);
            gfx.drawLine(xy, 0, xy + max, max);
        }
    }
}
