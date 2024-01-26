package com.example.demo;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vaadin.flow.i18n.DefaultI18NProvider;
import com.vaadin.flow.i18n.I18NUtil;

@Component
public class WatI18nProvider extends DefaultI18NProvider {

    public WatI18nProvider(List<Locale> providedLocales) {
        super(providedLocales);
        
    }
     @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        if (key == null) {
            getLogger().warn("Got lang request for key with null value!");
            return "";
        }

        final ResourceBundle bundle;

        if (key.startsWith("guide")) {
            bundle = getGuideResourceBundle(locale);
        } else {
            bundle = getBundle(locale); 
        }

        if (bundle == null) {
            return key;
        }

        String value;
        try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            getLogger().debug("Missing resource for key " + key, e);
            return "!" + locale.getLanguage() + ": " + key;
        }
        if (params.length > 0) {
            value = new MessageFormat(value, locale).format(params);
        }
        return value;
    }

    private ResourceBundle getBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(BUNDLE_PREFIX, locale,
                    getClassLoader());
        } catch (final MissingResourceException e) {
            getLogger().warn("Missing resource bundle for " + BUNDLE_PREFIX
                    + " and locale " + locale.getDisplayName(), e);
        }
        return null;
    }
    private ResourceBundle getGuideResourceBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(BUNDLE_FOLDER + "."
            + "guide", locale,
                    getClassLoader());
        } catch (final MissingResourceException e) {
            getLogger().warn("Missing resource bundle for " + BUNDLE_FOLDER + "."
            + BUNDLE_FILENAME
                    + " and locale " + locale.getDisplayName(), e);
        }
        return null;
    }

    static Logger getLogger() {
        return LoggerFactory.getLogger(DefaultI18NProvider.class);
    }
    protected static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    
}
