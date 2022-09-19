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

package uk.ac.lancs.nonogram.line;

/**
 * Holds per-algorithm state between calls to a line solver.
 * 
 * @author simpsons
 */
public interface Cache {
    /**
     * Indexes a line-solver cache while recording a type.
     * 
     * @author simpsons
     * 
     * @param <T> the type of value stored by a line solver in a cache
     */
    final class Key<T> {
        /**
         * @resume The type of values indexed by this key
         */
        public final Class<T> type;

        /**
         * @resume The object used to clone items of this type
         */
        public final Cloner<T> cloner;

        /**
         * Create a new cache key for a given type.
         * 
         * @param type the type of value that the key indexes
         */
        public Key(Class<T> type, Cloner<T> cloner) {
            this.type = type;
            this.cloner = cloner;
        }
    }

    /**
     * Clones objects stored in a cache.
     * 
     * @author simpsons
     * 
     * @param <T> the type of value stored by a line solver in a cache
     */
    interface Cloner<T> {
        /**
         * Clone an object.
         * 
         * @param obj the object to be cloned
         * 
         * @return a clone of the object
         */
        T clone(T obj);
    }

    /**
     * Get a cached value.
     * 
     * @param key the key that indexes the require value
     * 
     * @return the required value, or null if no value has already been
     * indexed under that key
     */
    <T> T get(Key<? extends T> key);

    /**
     * Cache a value.
     * 
     * @param key the key under which to index the value
     * 
     * @param value the value to be cached
     */
    <T> void set(Key<? super T> key, T value);

    /**
     * Clone this cache. This goes through all keys, and clones the
     * targeted object.
     * 
     * @return a new cache with identical state to this one
     */
    Cache clone();
}
