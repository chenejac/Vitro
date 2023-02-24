/* $This file is distributed under the terms of the license in LICENSE$ */

package edu.cornell.mannlib.vitro.webapp.i18n.selection;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.cornell.mannlib.vitro.webapp.controller.VitroRequest;
import edu.cornell.mannlib.vitro.webapp.controller.freemarker.UrlBuilder;
import edu.cornell.mannlib.vitro.webapp.utils.dataGetter.DataGetter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Get the data for the selectable Locales, so the Freemarker template can
 * create a row of flag images that will select the desired locale.
 * <p>
 * If there are no selectable Locales in runtime.properties, we return an empty
 * map. (selectLocale?? will return false)
 * <p>
 * If the Locale has been forced by runtime.properties, we do the same.
 * <p>
 * If there are selectable Locales, the returned map will contain a structure
 * like this:
 *
 * <pre>
 * {selectLocale={
 *   selectLocaleUrl = [the URL for the form action to select a Locale]
 *   locales={         [a list of maps]
 *       {               [a map for each Locale]
 *         code =          [the code for the Locale, e.g. "en_US"]
 *         label =         [the alt text for the Locale, e.g. "Spanish (Spain)"]
 *         country =       [the country for the Locale, e.g. "United States"]
 *         institution =       [the abbreviation for institution, e.g. "UQAM"]
 *         selected =      [true, if this locale is currently selected]
 *       }
 *     }
 *   }
 * }
 * </pre>
 */
public class LocaleSelectionDataGetter implements DataGetter {
    private static final Log log = LogFactory
        .getLog(LocaleSelectionDataGetter.class);
    private static final char PRIVATE_USE_SUBTAG = 'x';
    private final VitroRequest vreq;

    public LocaleSelectionDataGetter(VitroRequest vreq) {
        this.vreq = vreq;
    }

    @Override
    public Map<String, Object> getData(Map<String, Object> valueMap) {
        List<Locale> selectables = SelectedLocale.getSelectableLocales(vreq);
        if (selectables.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("selectLocaleUrl", UrlBuilder.getUrl("/selectLocale"));
        result.put("locales", buildLocalesList(selectables));

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("selectLocale", result);
        log.debug("Sending these values: " + bodyMap);
        return bodyMap;
    }

    private List<Map<String, Object>> buildLocalesList(List<Locale> selectables) {
        Locale currentLocale = SelectedLocale.getCurrentLocale(vreq);
        // The next couple of lines check whether there are locales in the list with the same root.
        // If yes, the institution abbreviation (private tag) will be displayed in UI.
        // For instance, if there are fr_CA_x_uqam and fr_CA in a VIVO instance runtime.properties,
        // the institutional abbreviation (UQAM) will be displayed next to locale name
        // in the dropdown menu for selection of a UI language.
        boolean includeAbbreviation = false;
        Set<String> setOfLocalesBase = new HashSet<>();
        for (final Locale locale : selectables) {
            setOfLocalesBase.add(locale.stripExtensions().toLanguageTag());
        }
        if (setOfLocalesBase.size() < selectables.size()) {
            includeAbbreviation = true;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Locale locale : selectables) {
            try {
                list.add(buildLocaleMap(locale, currentLocale, includeAbbreviation));
            } catch (FileNotFoundException e) {
                log.warn("Can't show the Locale selector for '" + locale
                    + "': " + e);
            }
        }
        return list;
    }

    private Map<String, Object> buildLocaleMap(Locale locale,
                                               Locale currentLocale, boolean includeAbbreviation)
        throws FileNotFoundException {
        Map<String, Object> map = new HashMap<>();

        map.put("code", locale.toLanguageTag().replace('-', '_'));
        map.put("label", locale.getDisplayLanguage(locale));
        map.put("country", locale.getDisplayCountry(locale));
        if (includeAbbreviation) {
            map.put("institution", Optional
                .ofNullable(locale.getExtension(LocaleSelectionDataGetter.PRIVATE_USE_SUBTAG))
                .orElse("").toUpperCase());
        }
        map.put("selected", currentLocale.equals(locale));
        return map;
    }

}
