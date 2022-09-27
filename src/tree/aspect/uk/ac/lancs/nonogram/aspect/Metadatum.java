// -*- c-basic-offset: 4; indent-tabs-mode: nil -*-

/*
 * Copyright (c) 2011,2022, Lancaster University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the
 *    distribution.
 * 
 *  * Neither the name of the copyright holder nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package uk.ac.lancs.nonogram.aspect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Holds locale-specific strings.
 * 
 * @author simpsons
 */
public class Metadatum {
    private final Map<Locale, String> values;

    /**
     * Create a package of locale-specific strings.
     * 
     * @param values the values for specific locales; can be
     * {@code null}
     */
    public Metadatum(Map<? extends Locale, ? extends String> values) {
        this.values =
            values == null ? Collections.emptyMap() : Map.copyOf(values);
    }

    /**
     * Convert separate maps of metadata values into an integrated map.
     * 
     * @param defaults a map of default values
     * 
     * @param localeSpecifics a map of values sub-indexed by locale
     * 
     * @return the integrated map
     */
    public static Map<String, Metadatum>
        map(Map<? extends String,
                ? extends Map<? extends Locale,
                              ? extends String>> localeSpecifics) {
        return localeSpecifics.entrySet().stream().collect(Collectors
            .toMap(Map.Entry::getKey, e -> new Metadatum(e.getValue())));
    }

    /**
     * Get the most appropriate value for given language preferences.
     * 
     * @param priority the language preferences
     * 
     * @return the best value for the preferences; or {@code null} if
     * none are suitable
     */
    public String get(List<Locale.LanguageRange> priority) {
        Locale bestKey = Locale.lookup(priority, values.keySet());
        return values.get(bestKey);
    }

    /**
     * Get the most appropriate value for given language preferences.
     * 
     * @param priority the language preferences
     * 
     * @return the best value for the preferences; or the default value
     * if there is no matching value
     */
    public Map.Entry<Locale, String>
        getPair(List<Locale.LanguageRange> priority) {
        Locale bestKey = Locale.lookup(priority, values.keySet());
        String value = values.get(bestKey);
        if (value == null) return null;
        return Map.entry(bestKey, value);
    }

    /**
     * Get the value for a specific locale.
     * 
     * @param locale the requested locale
     * 
     * @return the corresponding value; or {@code null} if not defined
     */
    public String get(Locale locale) {
        return values.get(locale);
    }

    /**
     * Get a set view of the locales with defined values. Modifications
     * to this set will modify this object's state.
     * 
     * @return the set of defined locales
     */
    public Collection<Locale> getLocales() {
        return values.keySet();
    }
}
