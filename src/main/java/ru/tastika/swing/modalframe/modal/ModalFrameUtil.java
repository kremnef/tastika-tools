package ru.tastika.swing.modalframe.modal;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * Created by IntelliJ IDEA.
 * User: Alexandr
 * Date: 21.11.2005
 * Time: 14:29:29
 */
public class ModalFrameUtil {

    static class EventPump implements InvocationHandler {
        Frame frame;


        public EventPump(Frame frame) {
            this.frame = frame;
        }


        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return frame.isShowing() ? Boolean.TRUE : Boolean.FALSE;
        }


        // When the reflection calls in this method has to be
        // replaced once Sun provides a public API to pump events.
        public void start() throws Exception {
            Class clazz = Class.forName("java.awt.Conditional");
            Object conditional = Proxy.newProxyInstance(
                    clazz.getClassLoader(),
                    new Class[] { clazz },
                    this);

            Method pumpMethod = Class.forName("java.awt.EventDispatchThread").getDeclaredMethod("pumpEvents", new Class[] { clazz });
            pumpMethod.setAccessible(true);
            pumpMethod.invoke(Thread.currentThread(), new Object[] { conditional });
        }
    }


    // Show the given frame as modal to the specified owner.
    // NOTE: this method returns only after the modal frame is closed.
    public static void showAsModal(final Frame frame, final Frame owner) {
        frame.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                owner.setEnabled(false);
            }


            public void windowClosed(WindowEvent e) {
                owner.setEnabled(true);
                owner.removeWindowListener(this);
                owner.toFront();
            }
        });


        owner.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                if (frame.isShowing()) {
                    frame.setExtendedState(JFrame.NORMAL);
                    frame.toFront();
                }
                else {
                    owner.removeWindowListener(this);
                }
            }
        });


        frame.setVisible(true);

        try {
            new EventPump(frame).start();
        }
        catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }
}