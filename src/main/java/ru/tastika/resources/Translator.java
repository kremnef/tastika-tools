package ru.tastika.resources;


import java.text.MessageFormat;
import java.util.*;


/**
 * Contains ResourceBundle objects.
 * The first (deepest) bundle is the Graphpad bundle.
 * If a user wants to use Graphpad as a framework
 * he can push his own bundle with the <tt>pushBundle</tt>
 * method. Requests will procedure with the following
 * logic: The translator asks the highest bundle
 * for a localized text string. If the bundle has
 * no entry for the specified key. The next bundle will
 * ask for the key. If the deepest bundle hasn't a
 * value for the key the key will return.
 */

public class Translator {


    /**
     * Container for the registered LocaleChangeListener.
     * @see LocaleChangeListener
     */
    protected static Vector<LocaleChangeListener> listeners = new Vector<LocaleChangeListener>();

    /**
     * The translator creates outputs on the
     * System.err if a resource wasn't found and
     * this boolean is <tt>true</tt>
     */
    protected static boolean logNotFoundResources = false;

    /**
     * Contains ResourceBundle objects.
     * The first bundle is the Graphpad bundle.
     * If a user wants to use Graphpad as a framework
     * he can push his own bundle.
     */
    protected static Stack<ResourceBundle> bundles = new Stack<ResourceBundle>();

    /**
     * Contains ResourceBundle names
     * The first bundlename is the Graphpad bundle.
     * If a user wants to use Graphpad as a framework
     * he can push his own bundle.
     */
    protected static Stack<String> bundleNames = new Stack<String>();

    /**
     * Resouce Bundle with proper names.
     */
    protected static DefaultResourceBundle defaultBundle = new DefaultResourceBundle();


    /**
     * Returns the resouce bundle with the proper names.
     */
    public static DefaultResourceBundle getDefaultResourceBundle() {
        return defaultBundle;
    }


    // перенести в использующие классы, e.g. Tunnel
//    /** pushes the default bundle for graphpad
//     *  to the stack
//     *  @see #pushBundle
//     */
//    static {
//        pushBundle("ru.taskforce.dlb.resources.dlb");
//        pushBundle("ru.taskforce.dlb.resources.tunnelcode");
//        pushBundle("ru.taskforce.dlb.resources.anchors");
//    }


    /**
     * Returns the localized String for the key. If
     * the String wasn't found the method will return
     * the Key but not null.
     * @param sKey Key for the localized String.
     */
    public static String getString(String sKey) {
        return getString(bundles.size() - 1, sKey);
    }


    /**
     * Returns the localized String for the key. If
     * the String wasn't found the method will return
     * null.
     * @param bundleIndex The bundle index for the request.
     *                    The method requests the bundle
     *                    at the specified position and
     *                    at all deeper positions.
     * @param sKey        Key for the localized String.
     */
    public static String getString(int bundleIndex, String sKey) {
        if (bundleIndex < bundles.size() && bundleIndex >= 0) {
            ResourceBundle bundle = (ResourceBundle) bundles.get(bundleIndex);
            try {
                return bundle.getString(sKey);
            }
            catch (MissingResourceException mrex) {
                return getString(bundleIndex - 1, sKey);
            }
        }
        else {
            try {
                return defaultBundle.getString(sKey);
            }
            catch (MissingResourceException mrex) {
                if (logNotFoundResources) {
                    System.err.println("Resource for the following key not found:" + sKey);
                }
                return null;
            }
        }
    }


    /**
     * Returns the localized String for the key. If
     * the String wasn't found the method will return
     * the key but not null.
     * @param sKey   Key for the localized String.
     * @param values Object array for placeholders.
     * @see MessageFormat#format(String, Object...)
     */
    public static String getString(String sKey, Object[] values) {
        return getString(bundles.size() - 1, sKey, values);
    }


    /**
     * Returns the localized String for the key. If
     * the String wasn't found the method will return
     * an empty String but not null and will log
     * on the System.err the key String,
     * if logNotFoundResources is true.
     * @param sKey    Key for the localized String.
     * @param sKey    if true and the key wasn't found a message
     *                will print on the System.err.
     * @param oValues Object array for placeholders.
     * @see MessageFormat#format(String, Object...) 
     */
    public static String getString(int bundleIndex,
                                   String sKey,
                                   Object[] oValues) {

        if (bundleIndex < bundles.size() && bundleIndex >= 0) {
            ResourceBundle bundle = (ResourceBundle) bundles.get(bundleIndex);
            try {
                return MessageFormat.format(bundle.getString(sKey), oValues);
            }
            catch (MissingResourceException mrex) {
                return getString(bundleIndex - 1, sKey, oValues);
            }
        }
        else {
            try {
                return defaultBundle.getString(sKey);
            }
            catch (MissingResourceException mrex) {
                if (logNotFoundResources) {
                    System.err.println("Resource for the following key not found:" + sKey);
                }
                return null;
            }
        }
    }


    /**
     * Adds a locale change listener to this Translator. If
     * the local changes, this
     * translator will fire locale change events.
     */
    public static void addLocaleChangeListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }


    /**
     * Removes the registered Listener
     */
    public static void removeLocaleChangeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }


    /**
     * Returns the current locale
     */
    public static Locale getLocale() {
        return Locale.getDefault();
    }


    /**
     * Sets the new locale and fires events
     * to the locale change listener.
     */
    public static void setLocale(Locale locale) {
        // change the locale
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(locale);

        // reload the bundles
        reloadBundles();

        // update listeners
        Vector cpyListeners;
        synchronized (listeners) {
            cpyListeners = (Vector) listeners.clone();
        }

        Enumeration oEnum = cpyListeners.elements();
        while (oEnum.hasMoreElements()) {
            LocaleChangeListener listener =
                    (LocaleChangeListener) oEnum.nextElement();
            listener.localeChanged(new LocaleChangeEvent(oldLocale, locale));
        }
    }


    /**
     * Reloads the bundles at the stack by using the default locale.
     */
    public static void reloadBundles() {
        // update the proper bundles
        defaultBundle.requeryDefaultNames();

        // update the bundles at the stack
        int i = 0;
        for (Iterator<String> iterator = bundleNames.iterator(); iterator.hasNext(); i++) {
            ResourceBundle resourcebundle =
                    PropertyResourceBundle.getBundle(iterator.next());
            bundles.set(i, resourcebundle);
        }
    }


    /**
     * Pushes the specified bundle on the stack.
     * An example for a bundle file name is 'org.jgraph.pad.resources.Graphpad'.
     * Don't add the suffix of the filename, the language or country code.
     * @see PropertyResourceBundle#getBundle
     */
    public static void pushBundle(String filename) {
        ResourceBundle resourcebundle = PropertyResourceBundle.getBundle(filename);
        bundles.push(resourcebundle);
        bundleNames.push(filename);
    }


    /**
     * Pops the highest bundle on the stack
     */
    public static void popBundle() {
        bundles.pop();
        bundleNames.pop();
    }


    /**
     * removes the specified bundle
     */
    public static void removeBundle(int index) {
        bundles.remove(index);
        bundleNames.remove(index);
    }


    /**
     * Returns the logNotFoundResources.
     * @return boolean
     */
    public static boolean isLogNotFoundResources() {
        return logNotFoundResources;
    }


    /**
     * Sets the logNotFoundResources.
     * @param logNotFoundResources The logNotFoundResources to set
     */
    public static void setLogNotFoundResources(boolean logNotFoundResources) {
        Translator.logNotFoundResources = logNotFoundResources;
    }
}
