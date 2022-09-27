/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.i18n;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.dao.WebappDaoFactory;
import edu.cornell.mannlib.vitro.webapp.i18n.selection.SelectedLocale;
import edu.cornell.mannlib.vitro.webapp.modelaccess.ModelAccess;
import edu.cornell.mannlib.vitro.webapp.utils.developer.DeveloperSettings;
import edu.cornell.mannlib.vitro.webapp.utils.developer.Key;

/**
 * Provides access to a bundle of text strings, based on the name of the bundle,
 * the Locale of the requesting browser, and the current theme directory.
 *
 * If the bundle name is not specified, the default name of "all" is used.
 *
 * If a requested bundle is not found, no error is thrown. Instead, an empty
 * bundle is returned that produces error message strings when asked for text.
 */
public class I18n {
	private static final Log log = LogFactory.getLog(I18n.class);

	public static final String DEFAULT_BUNDLE_NAME = "all";

	/**
	 * If this attribute is present on the request, then the cache has already
	 * been cleared.
	 */
	private static final String ATTRIBUTE_CACHE_CLEARED = I18n.class.getName()
			+ "-cacheCleared";

	/**
	 * This is where the work gets done. Not declared final, so it can be
	 * modified in unit tests.
	 */
	private static I18n instance;

	private final ServletContext ctx;



	protected I18n(ServletContext ctx) {
		this.ctx = ctx;
	}

	// ----------------------------------------------------------------------
	// Static methods
	// ----------------------------------------------------------------------

	/**
	 * This setup method must be called before I18n static methods can be used.
	 * It is currently called from LocaleSelectionSetup.contextInitialized, which
	 * should ensure it is called when the context is initialized.
	 */
	public static void setup(ServletContext ctx) {
		I18n.instance = new I18n(ctx);
	}

	/**
	 * A convenience method to get a bundle and format the text.
	 */
	public static String text(String bundleName, HttpServletRequest req,
			String key, Object... parameters) {
		return bundle(bundleName, req).text(key, parameters);
	}

	/**
	 * A convenience method to get the default bundle and format the text.
	 */
	public static String text(HttpServletRequest req, String key,
			Object... parameters) {
		return bundle(req).text(key, parameters);
	}

	/**
	 * Get a request I18nBundle by this name.
	 */
	public static I18nBundle bundle(String bundleName, HttpServletRequest req) {
		return instance.getBundle(bundleName, req);
	}

	/**
	 * Get the default request I18nBundle.
	 */
	public static I18nBundle bundle(HttpServletRequest req) {
		return instance.getBundle(DEFAULT_BUNDLE_NAME, req);
	}

	/**
	 * Get the default context I18nBundle for preferred locales.
	 */
	public static I18nBundle bundle(List<Locale> preferredLocales) {
		return instance.getBundle(DEFAULT_BUNDLE_NAME, preferredLocales);
	}

	// ----------------------------------------------------------------------
	// The instance
	// ----------------------------------------------------------------------

	/** Holds the current theme directory, as far as we know. */
	private AtomicReference<String> themeDirectory = new AtomicReference<String>(
			"");

	/**
	 * Get an I18nBundle by this name. The request provides the preferred
	 * Locale, the application directory, the theme directory and the
	 * development mode flag.
	 *
	 * If the request indicates that the system is in development mode, then the
	 * cache is cleared on each request.
	 *
	 * If the theme directory has changed, the cache is cleared.
	 *
	 * Declared 'protected' so it can be overridden in unit tests.
	 */
	protected I18nBundle getBundle(String bundleName, HttpServletRequest req) {
		log.debug("Getting request bundle '" + bundleName + "'");

		checkDevelopmentMode(req);
		checkForChangeInThemeDirectory(req);

		Locale locale = req.getLocale();

		return getBundle(bundleName, locale);
	}

	/**
	 * Get an I18nBundle by this name. The context provides the selectable
	 * Locale, the application directory, the theme directory and the
	 * development mode flag. Choosing matching locale from context by
	 * provided preferred locales.
	 *
	 * If the context indicates that the system is in development mode, then the
	 * cache is cleared on each request.
	 *
	 * If the theme directory has changed, the cache is cleared.
	 *
	 * Declared 'protected' so it can be overridden in unit tests.
	 */
	protected I18nBundle getBundle(String bundleName, List<Locale> preferredLocales) {
		log.debug("Getting context bundle '" + bundleName + "'");

		checkDevelopmentMode();
		checkForChangeInThemeDirectory(ctx);

		Locale locale = SelectedLocale.getPreferredLocale(ctx, preferredLocales);

		return getBundle(bundleName, locale);
	}

	/**
	 * Get an I18nBundle by this name, context, and locale.
	 */
	private I18nBundle getBundle(String bundleName, Locale locale) {
		I18nLogger i18nLogger = new I18nLogger();
		try {
			String dir = themeDirectory.get();
			ResourceBundle.Control control = new ThemeBasedControl(ctx, dir);
			ResourceBundle rb = ResourceBundle.getBundle(bundleName,
					locale, control);

			return new I18nBundle(bundleName, rb, i18nLogger);
		} catch (MissingResourceException e) {
			log.warn("Didn't find text bundle '" + bundleName + "'");

			return NullI18nBundle.getInstance(bundleName, i18nLogger);
		} catch (Exception e) {
			log.error("Failed to create text bundle '" + bundleName + "'", e);

			return NullI18nBundle.getInstance(bundleName, i18nLogger);
		}
	}

	/**
	 * If we are in development mode, clear the cache on each request.
	 */
	private void checkDevelopmentMode(HttpServletRequest req) {
		if (DeveloperSettings.getInstance().getBoolean(Key.I18N_DEFEAT_CACHE)) {
			log.debug("In development mode - clearing the cache.");
			clearCacheOnRequest(req);
		}
	}

	/**
	 * If we are in development mode, clear the cache.
	 */
	private void checkDevelopmentMode() {
		if (DeveloperSettings.getInstance().getBoolean(Key.I18N_DEFEAT_CACHE)) {
			log.debug("In development mode - clearing the cache.");
			ResourceBundle.clearCache();
		}
	}

	/**
	 * If the theme directory has changed from before, clear the cache of all
	 * ResourceBundles.
	 */
	private void checkForChangeInThemeDirectory(HttpServletRequest req) {
		String currentDir = new VitroRequest(req).getAppBean().getThemeDir();
		String previousDir = themeDirectory.getAndSet(currentDir);
		if (!currentDir.equals(previousDir)) {
			log.debug("Theme directory changed from '" + previousDir + "' to '"
					+ currentDir + "' - clearing the cache.");
			clearCacheOnRequest(req);
		}
	}

	/**
	 * If we have a complete model access and the theme directory has changed
	 * from before, clear the cache of all ResourceBundles.
	 */
	private void checkForChangeInThemeDirectory(ServletContext ctx) {
		WebappDaoFactory wdf = ModelAccess.on(ctx)
			.getWebappDaoFactory();
		// Only applicable if context has a complete model access
		if (Objects.nonNull(wdf)
				&& Objects.nonNull(wdf.getApplicationDao())
				&& Objects.nonNull(wdf.getApplicationDao().getApplicationBean())) {
			String currentDir = wdf
				.getApplicationDao()
				.getApplicationBean()
				.getThemeDir();
			String previousDir = themeDirectory.getAndSet(currentDir);
			if (!currentDir.equals(previousDir)) {
				log.debug("Theme directory changed from '" + previousDir + "' to '"
						+ currentDir + "' - clearing the cache.");
				ResourceBundle.clearCache();
			}
		}
	}

	/** Only clear the cache one time per request. */
	private void clearCacheOnRequest(HttpServletRequest req) {
		if (req.getAttribute(ATTRIBUTE_CACHE_CLEARED) != null) {
			log.debug("Cache was already cleared on this request.");
		} else {
			ResourceBundle.clearCache();
			log.debug("Cache cleared.");
			req.setAttribute(ATTRIBUTE_CACHE_CLEARED, Boolean.TRUE);
		}
	}

	// ----------------------------------------------------------------------
	// Control classes for instantiating ResourceBundles
	// ----------------------------------------------------------------------

	/**
	 * Instead of looking in the classpath, look in the theme i18n directory and
	 * the application i18n directory.
	 */
	static class ThemeBasedControl extends ResourceBundle.Control {

		private final ServletContext ctx;
		private final String themeDirectory;


		public ThemeBasedControl(ServletContext ctx, String themeDirectory) {
			this.ctx = ctx;
			this.themeDirectory = themeDirectory;
		}


		/**
		 * Don't look for classes to satisfy the request, just property files.
		 */
		@Override
		public List<String> getFormats(String baseName) {
			return FORMAT_PROPERTIES;
		}

		/**
		 * Don't look in the class path, look in the current servlet context, in
		 * the bundle directory under the theme directory and in the bundle
		 * directory under the application directory.
		 */
		@Override
		public ResourceBundle newBundle(String baseName, Locale locale,
				String format, ClassLoader loader, boolean reload)
				throws IllegalAccessException, InstantiationException,
				IOException {
			checkArguments(baseName, locale, format);

			log.debug("Creating bundle for '" + baseName + "', " + locale
					+ ", '" + format + "', " + reload);

			String bundleName = toBundleName(baseName, locale);
			if (bundleName == null) {
				throw new NullPointerException("bundleName may not be null.");
			}

			return VitroResourceBundle.getBundle(bundleName, ctx, locale, themeDirectory, this);
		}

		/**
		 * When creating the chain of acceptable Locales, include approximate
		 * matches before giving up and using the root Locale.
		 *
		 * Check the list of supported Locales to see if any have the same
		 * language but different region. If we find any, sort them and insert
		 * them into the usual result list, just before the root Locale.
		 */
		@Override
		public List<Locale> getCandidateLocales(String baseName, Locale locale) {
			// Find the list of Locales that would normally be returned.
			List<Locale> usualList = super
					.getCandidateLocales(baseName, locale);

			// If our "selectable locales" include no approximate matches that
			// are not already in the list, we're done.
			SortedSet<Locale> approximateMatches = findApproximateMatches(locale);
			approximateMatches.removeAll(usualList);
			if ((! usualList.contains(locale)) && (approximateMatches.contains(locale))){
				usualList.add(0, locale);
				approximateMatches.remove(locale);
			}
			if (approximateMatches.isEmpty()) {
				return usualList;
			}

			// Otherwise, insert those approximate matches into the list just
			// before the ROOT locale.
			List<Locale> mergedList = new LinkedList<>(usualList);
			int rootLocaleHere = mergedList.indexOf(Locale.ROOT);
			if (rootLocaleHere == -1) {
				mergedList.addAll(approximateMatches);
			} else {
				mergedList.addAll(rootLocaleHere, approximateMatches);
			}

			return mergedList;
		}

		private SortedSet<Locale> findApproximateMatches(Locale locale) {
			SortedSet<Locale> set = new TreeSet<>(new LocaleComparator());

			for (Locale l : SelectedLocale.getSelectableLocales(ctx)) {
				if (locale.getLanguage().equals(l.getLanguage())) {
					set.add(l);
				}
			}

			return set;
		}

		/**
		 * The documentation for ResourceBundle.Control.newBundle() says I
		 * should throw these exceptions.
		 */
		private void checkArguments(String baseName, Locale locale,
				String format) {
			if (baseName == null) {
				throw new NullPointerException("baseName may not be null.");
			}
			if (locale == null) {
				throw new NullPointerException("locale may not be null.");
			}
			if (format == null) {
				throw new NullPointerException("format may not be null.");
			}
			if (!FORMAT_DEFAULT.contains(format)) {
				throw new IllegalArgumentException(
						"format must be one of these: " + FORMAT_DEFAULT);
			}
		}

	}

	private static class LocaleComparator implements Comparator<Locale> {
		@Override
		public int compare(Locale o1, Locale o2) {
			int c = o1.getLanguage().compareTo(o2.getLanguage());
			if (c == 0) {
				c = o1.getCountry().compareTo(o2.getCountry());
				if (c == 0) {
					c = o1.getVariant().compareTo(o2.getVariant());
				}
			}
			return c;
		}
	}
}
