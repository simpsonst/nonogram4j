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

package uk.ac.lancs.nonogram.solver;

import java.util.HashMap;
import java.util.Map;
import uk.ac.lancs.nonogram.line.Cache;

/**
 * Implements a cache for per-algorithm line state.
 * 
 * @author simpsons
 */
public final class SimpleCache implements Cache {
    private final Map<Key<?>, Object> entries = new HashMap<>();

    /**
     * Get a cached value.
     * 
     * @param key the key that indexes the require value
     * 
     * @return the required value, or null if no value has already been
     * indexed under that key
     */
    @Override
    public <T> T get(Key<? extends T> key) {
        return key.type.cast(entries.get(key));
    }

    /**
     * Cache a value.
     * 
     * @param key the key under which to index the value
     * 
     * @param value the value to be cached
     */
    @Override
    public <T> void set(Key<? super T> key, T value) {
        entries.put(key, value);
    }

    @Override
    public SimpleCache clone() {
        SimpleCache other = new SimpleCache();
        for (Map.Entry<Key<?>, Object> entry : entries.entrySet()) {
            @SuppressWarnings("unchecked")
            Key<Object> key = (Key<Object>) entry.getKey();
            Object value = key.cloner.clone(entry.getValue());
            other.entries.put(key, value);
        }
        return other;
    }
}
