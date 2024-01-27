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

/**
 * Custom I18n provider.
 */
@Component
public class WatI18nProvider extends DefaultI18NProvider {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 * 
	 * @param providedLocales Locale list
	 */
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
		String baseName;

		if (key.startsWith("guide")) {
			// If the key is for guide page, change resource bundle.
			baseName = BUNDLE_FOLDER + "." + "guide";
		} else {
			// Default rsource bundle.
			baseName = BUNDLE_PREFIX;
		}
		bundle = ResourceBundle.getBundle(baseName, locale, Thread.currentThread().getContextClassLoader());
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

	static Logger getLogger() {
		return LoggerFactory.getLogger(DefaultI18NProvider.class);
	}
}
