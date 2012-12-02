package ru.tastika.resources;


import java.util.Locale;


/**
 * If the Locale changes this Event will fired by the
 * Translator to all registered LocalChangeListeners.
 */

public class LocaleChangeEvent {


    /**
     * Represents the old Locale or null
     */
    protected Locale oldLocale;

    /**
     * Represents the new Locale
     */
    protected Locale newLocale;


    /**
     * Creates a new Locale Change Event with the old and the
     * new Locale.
     */
    public LocaleChangeEvent(Locale oldLocale, Locale newLocale) {
        this.oldLocale = oldLocale;
        this.newLocale = newLocale;
    }


    /**
     * Returns the old Locale
     */
    public Locale getOldLocale() {
        return this.oldLocale;
    }


    /**
     * Returns the new Locale
     */
    public Locale getNewLocale() {
        return this.newLocale;
    }

}
