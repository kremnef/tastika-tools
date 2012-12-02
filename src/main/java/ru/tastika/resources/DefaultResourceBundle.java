package ru.tastika.resources;


import java.util.*;


/**
 * Defaultresourcebundle for proper names. (e.g. Locales and
 * Look And Feel names are equal in any language. Therefore
 * this Class contains this proper names.)
 */

public class DefaultResourceBundle extends ResourceBundle {


    /**
     * Hashtable with languageskeys as key and
     * propername as value
     */
    Hashtable<String, String> defaultNames = new Hashtable<String, String>();

    /**
     * A list of registered provider.
     */
    Vector<ProperNameProvider> properNameProvider = new Vector<ProperNameProvider>();


    /**
     * Creates a new Instance an requerys all default names.
     */
    public DefaultResourceBundle() {
        super();
        requeryDefaultNames();
    }


    /**
     * Adds the Propernameprovider and asks him for
     * the proper names.
     */
    public void addProperNameProvider(ProperNameProvider provider) {
        properNameProvider.add(provider);

        Enumeration keys = provider.getKeys();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = provider.getString(key);
            if (key != null && value != null) {
                defaultNames.put(key, value);
            }
        }
    }


    /**
     * removes the propernameprovider
     */
    public void removeProperNameProvider(ProperNameProvider provider) {
        properNameProvider.remove(provider);
        requeryDefaultNames();
    }


    /**
     * Quires the default names. Therefore any registered
     * Propernameprovider is queried.
     */
    public void requeryDefaultNames() {
        defaultNames.clear();

        Locale[] locales = Locale.getAvailableLocales();
        for (int i = 0; i < locales.length; i++) {
            defaultNames.put("Component." + locales[i].toString() + ".Text", locales[i].getDisplayName());
            defaultNames.put("Component." + locales[i].toString() + ".ToolTipText", locales[i].getDisplayName());
            defaultNames.put("Component." + locales[i].toString() + ".Mnemonic", locales[i].getDisplayName());
        }

        // update Values of the ProperNameProviders
        Enumeration oEnum = properNameProvider.elements();
        while (oEnum.hasMoreElements()) {
            ProperNameProvider provider = (ProperNameProvider) oEnum.nextElement();
            Enumeration keys = provider.getKeys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                defaultNames.put(key, provider.getString(key));
            }
        }

    }


    /**
     * merges the keys of any registered ProperNameProvider and returns them.
     */
    public Enumeration<String> getKeys() {
        return defaultNames.elements();
    }


    /**
     * Returns the object for the key or null
     */
    public Object handleGetObject(String key) {
        return defaultNames.get(key);
    }
}
