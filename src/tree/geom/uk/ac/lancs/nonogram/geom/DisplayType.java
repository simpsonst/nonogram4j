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

package uk.ac.lancs.nonogram.geom;

import java.awt.Component;

/**
 * Identifies distinct types of display.
 * 
 * @author simpsons
 * 
 * @param <W> the type of the display widget
 */
public final class DisplayType<W> {
    /**
     * Get a string representation of this object. This is intended
     * purely for diagnostic purposes.
     * 
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return "DisplayType [key=" + key + ", " + "clazz=" + clazz + "]";
    }

    /**
     * Get this display type's hash code.
     * 
     * @return a hash code based on the key and component type
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    /**
     * Determine whether this display type is equal to another object.
     * 
     * @param obj the object to be compared with
     * 
     * @return {@code true} if the other object is a display type with
     * the same key and component type.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof DisplayType)) return false;
        DisplayType<?> other = (DisplayType<?>) obj;
        if (clazz == null) {
            if (other.clazz != null) return false;
        } else if (!clazz.equals(other.clazz)) return false;
        if (key == null) {
            if (other.key != null) return false;
        } else if (!key.equals(other.key)) return false;
        return true;
    }

    /**
     * @resume The type of the display widget
     */
    public final Class<W> clazz;

    /**
     * @resume The identifying key
     */
    public final String key;

    /**
     * Create a display type.
     * 
     * @param key the identifying key
     * 
     * @param clazz the type of the display widget
     */
    public DisplayType(String key, Class<W> clazz) {
        this.key = key;
        this.clazz = clazz;
    }

    /**
     * @resume The key used to identify AWT displays
     */
    public static final String AWT_KEY = "awt";

    /**
     * @resume The key used to identify Swing displays
     */
    public static final String SWING_KEY = "swing";

    /**
     * @resume The key used to identify null displays
     */
    public static final String NULL_KEY = "null";

    /**
     * Identifies AWT-compatible displays.
     */
    public static final DisplayType<Component> AWT =
        new DisplayType<>(AWT_KEY, Component.class);

    /**
     * Identifies Swing-compatible displays.
     */
    public static final DisplayType<Component> SWING =
        new DisplayType<>(SWING_KEY, Component.class);

    /**
     * Identifies null displays, which ignore all updates.
     */
    public static final DisplayType<Void> VOID =
        new DisplayType<>(NULL_KEY, Void.class);
}
