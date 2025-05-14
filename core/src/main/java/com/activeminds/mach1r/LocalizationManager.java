package com.activeminds.mach1r;

import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.Gdx;

import java.util.Locale;
import java.util.MissingResourceException;

public class LocalizationManager {
    private I18NBundle bundle;

    public void loadDefaultLocale() {
        Locale locale = Locale.getDefault(); // o puedes forzar uno, ej: new Locale("es");
        bundle = I18NBundle.createBundle(Gdx.files.internal("strings"), locale);
    }

    public void loadLanguage(int lang) {
        Locale locale;
        if(lang == 0)
            locale = new Locale("en");
        else if(lang == 1)
            locale = new Locale("fr");
        else if(lang == 2)
            locale = new Locale("es");
        else if(lang == 3)
            locale = new Locale("de");
        else if(lang == 4)
            locale = new Locale("it");
        else
            locale = Locale.getDefault();

        bundle = I18NBundle.createBundle(Gdx.files.internal("strings"), locale);
    }

    public String get(String key) {
        String text;
        try
        {
            text = bundle.get(key);
        }
        catch(MissingResourceException e)
        {
            text = key+"_missing_text";
        }
        return text;
    }

    // Cambiar idioma manualmente
    public void setLocale(Locale locale) {
        bundle = I18NBundle.createBundle(Gdx.files.internal("strings"), locale);
    }
}
