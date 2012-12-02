package ru.tastika.swing.modalframe;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import ru.tastika.swing.modalframe.modal.test.JModalWindow;
import ru.tastika.tools.swing.SwingUtilities;
import ru.tastika.tools.util.Utilities;


/**
 *
 * @author hobal
 */
public class WaitWindow extends JModalWindow {


    private EventListenerList listenerList = new EventListenerList();
    private WaitWindowEvent waitWindowEvent;

    private JLabel statusLabel;
    private JLabel logoLabel;
    private String statusText;
    private boolean dialogVisible;
    private Thread updateStatusTextStringThread;
    private String[] suffixesToUpdateMessage = new String[]{
        "",
        ".",
        "..",
        "...",
        "....",
        ".....",
        "......",
        ".......",
        "........",
        "........."
    };
    private Icon[] frames;
    private static final int FRAME_UPDATE_TIME_MILLIS = 100;
    private String title;
    private Icon initialLogo;
    private boolean showCancelButton;
    private boolean cancelButtonUpdated;
    private long timeBeforeShowCancelButton;
    private long startTimeBeforeShowCancelButton;
    private JPanel cancelButtonPanel;

    
    public WaitWindow(Window owner, String title, Icon initialLogo) {
        super(owner, true);
        this.title = title;
        this.initialLogo = initialLogo;
        initComponents();
    }


    private void initComponents() {

        this.statusText = "";
        JPanel logoPanel = new JPanel(new BorderLayout(5, 5));
        logoPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        logoLabel = new JLabel(initialLogo);
        logoLabel.setPreferredSize(new Dimension(41, 38));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setPreferredSize(new Dimension(240, 41));
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 21));

        JLabel dotsLabel = new JLabel("......................................................................................................................................................");
        dotsLabel.setFont(new Font("Arial", Font.PLAIN, 8));

        logoPanel.add(logoLabel, BorderLayout.WEST);
        logoPanel.add(titleLabel, BorderLayout.CENTER);
        logoPanel.add(dotsLabel, BorderLayout.SOUTH);

        JPanel statusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabelPanel.setPreferredSize(new Dimension(240, 28));
        statusLabelPanel.setBorder(new EmptyBorder(0, 10, 10, 10));

        Font tahomaText13 = SwingUtilities.SANS_SERIF_PLAIN_FONT.deriveFont(13f);
        statusLabel = new JLabel("");
        statusLabel.setFont(tahomaText13);
        statusLabelPanel.add(statusLabel);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabelPanel, BorderLayout.CENTER);
        
        cancelButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        cancelButtonPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {


            public void actionPerformed(ActionEvent e) {
                fireCanceled();
                setVisible(false);
            }
        });
        cancelButton.setBorderPainted(false);
        cancelButton.setBackground(new Color(0xEE, 0xEE, 0xEE));
        cancelButtonPanel.add(cancelButton);

        statusPanel.add(cancelButtonPanel, BorderLayout.EAST);

        updateCancelButtonPanel();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(logoPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        pack();
        SwingUtilities.setFontForAll(tahomaText13, cancelButtonPanel.getComponents());
        SwingUtilities.center(this);
    }


    public void setFrames(Icon[] frames) {
        this.frames = frames;
    }


    public void setStatusText(String statusText) {
        if (statusText == null) {
            statusText = "";
        }
        this.statusText = statusText;
        setStatusText(statusText, "");
        fireStatusTextChanged();
    }


    private void setStatusText(String statusText, String suffix) {
        statusLabel.setText(statusText + suffix);
    }


    public void setVisible(boolean visible) {
        this.dialogVisible = visible;
        if (dialogVisible && (updateStatusTextStringThread == null || !updateStatusTextStringThread.isAlive())) {
            updateStatusTextStringThread = new Thread(new Runnable() {

                public void run() {
                    int suffixNumber = 0;
                    int framesNumber = 0;
                    while (dialogVisible) {
                        if (frames != null) {
                            logoLabel.setIcon(frames[framesNumber]);
                            framesNumber++;
                            framesNumber = framesNumber % frames.length;
                        }
                        setStatusText(statusText, suffixesToUpdateMessage[suffixNumber]);
                        suffixNumber++;
                        suffixNumber = suffixNumber % suffixesToUpdateMessage.length;

                        if (!cancelButtonUpdated && startTimeBeforeShowCancelButton + timeBeforeShowCancelButton < System.currentTimeMillis()) {
                            updateCancelButtonPanel();
                        }

                        try {
                            Thread.sleep(FRAME_UPDATE_TIME_MILLIS);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            updateStatusTextStringThread.start();
        }
        super.setVisible(visible);
        if (visible) {
            fireWindowShown();
        }
        else {
            fireWindowClosed();
        }
    }


    public static void main(String[] args) {
        WaitWindow window = new WaitWindow(null, "ColligoNet Site Designer", null);
        window.setSecondsBeforeShowCancelButton(5);
        window.setShowCancelButton(true);
        window.setVisible(true);
        for (int i = 0; i < 20; i++) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            window.setStatusText(String.valueOf(i));
        }
    }


    /**
     * @return the showCancelButton
     */
    public boolean isShowCancelButton() {
        return showCancelButton;
    }


    /**
     * @param showCancelButton the showCancelButton to set
     */
    public void setShowCancelButton(boolean showCancelButton) {
        this.showCancelButton = showCancelButton;
        if (showCancelButton) {
            startTimeBeforeShowCancelButton = System.currentTimeMillis();
            cancelButtonUpdated = false;
        }
        else {
            updateCancelButtonPanel();
        }
    }


    private void updateCancelButtonPanel() {
        if (cancelButtonPanel.isVisible() != showCancelButton) {
            cancelButtonPanel.setVisible(showCancelButton);
            pack();
        }
        cancelButtonUpdated = true;
    }


    public void addWaitWindowListener(WaitWindowListener listener) {
        listenerList.add(WaitWindowListener.class, listener);
    }


    public WaitWindowListener[] getWaitWindowListeners() {
        return listenerList.getListeners(WaitWindowListener.class);
    }


    public void removeWaitWindowListener(WaitWindowListener listener) {
        listenerList.remove(WaitWindowListener.class, listener);
    }


    protected void fireWindowShown() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WaitWindowListener.class) {
                // Lazily create the event:
                if (waitWindowEvent == null) {
                    waitWindowEvent = new WaitWindowEvent(this, WaitWindowEvent.ACTION_WINDOW_SHOWN);
                }
                else {
                    waitWindowEvent.setWaitWindowAction(WaitWindowEvent.ACTION_WINDOW_SHOWN);
                }
                ((WaitWindowListener) listeners[i + 1]).windowShown(waitWindowEvent);
            }
        }
    }


    protected void fireWindowClosed() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WaitWindowListener.class) {
                // Lazily create the event:
                if (waitWindowEvent == null) {
                    waitWindowEvent = new WaitWindowEvent(this, WaitWindowEvent.ACTION_WINDOW_CLOSED);
                }
                else {
                    waitWindowEvent.setWaitWindowAction(WaitWindowEvent.ACTION_WINDOW_CLOSED);
                }
                ((WaitWindowListener) listeners[i + 1]).windowClosed(waitWindowEvent);
            }
        }
    }


    protected void fireStatusTextChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WaitWindowListener.class) {
                // Lazily create the event:
                if (waitWindowEvent == null) {
                    waitWindowEvent = new WaitWindowEvent(this, WaitWindowEvent.ACTION_STATUS_TEXT_CHANGED);
                }
                else {
                    waitWindowEvent.setWaitWindowAction(WaitWindowEvent.ACTION_STATUS_TEXT_CHANGED);
                }
                ((WaitWindowListener) listeners[i + 1]).statusTextChanged(waitWindowEvent);
            }
        }
    }


    protected void fireCanceled() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WaitWindowListener.class) {
                // Lazily create the event:
                if (waitWindowEvent == null) {
                    waitWindowEvent = new WaitWindowEvent(this, WaitWindowEvent.ACTION_CANCELED);
                }
                else {
                    waitWindowEvent.setWaitWindowAction(WaitWindowEvent.ACTION_CANCELED);
                }
                ((WaitWindowListener) listeners[i + 1]).canceled(waitWindowEvent);
            }
        }
    }


    /**
     * @return the secondsBeforeShowCancelButton
     */
    public int getSecondsBeforeShowCancelButton() {
        return (int) (timeBeforeShowCancelButton / 1000);
    }


    /**
     * @param secondsBeforeShowCancelButton the secondsBeforeShowCancelButton to set
     */
    public void setSecondsBeforeShowCancelButton(int secondsBeforeShowCancelButton) {
        this.timeBeforeShowCancelButton = (long) secondsBeforeShowCancelButton * 1000l;
    }

}
