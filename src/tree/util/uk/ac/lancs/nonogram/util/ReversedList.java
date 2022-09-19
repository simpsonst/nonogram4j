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

package uk.ac.lancs.nonogram.util;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @resume A reversed view of a list
 * 
 * @author simpsons
 * 
 * @param <E> the list's element type
 */
public final class ReversedList<E> extends AbstractList<E> {
    private final List<E> base;

    /**
     * Create a reversed view of a list.
     * 
     * @param base the base list
     */
    public ReversedList(List<E> base) {
        this.base = base;
    }

    private int reversedIndex(int i) {
        return size() - 1 - i;
    }

    /**
     * Replace the element at a given position.
     * 
     * @param index the position of the element to replace
     * 
     * @param element the replacing element
     * 
     * @return the replaced element
     */
    @Override
    public E set(int index, E element) {
        return base.set(reversedIndex(index), element);
    }

    /**
     * Get an iterator over all elements in proper sequence.
     * 
     * @return an iterator over all elements
     */
    @Override
    public Iterator<E> iterator() {
        return listIterator();
    }

    /**
     * Get an iterator over all elements in proper sequence, starting at
     * a given position.
     * 
     * @param index the initial position
     * 
     * @return an iterator over all elements at the given position
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ReversedListIterator(base.listIterator(size() - index));
    }

    /**
     * Get a view of a portion of the list.
     * 
     * @param fromIndex the index of the first element to appear in the
     * new view
     * 
     * @param toIndex the index of the element immediately after the
     * last one to appear in the new view
     * 
     * @return a view of the specified portion of the list
     */
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new ReversedList<>(base.subList(size() - toIndex,
                                               size() - fromIndex));
    }

    /**
     * Determine whether the list has no elements.
     * 
     * @return {@code true} if the list has no elements, or
     * {@code false} otherwise
     */
    @Override
    public boolean isEmpty() {
        return base.isEmpty();
    }

    /**
     * Determine whether the list contains a given element.
     * 
     * @param o the element to search for
     * 
     * @return {@code true} if the element is found in the list
     */
    @Override
    public boolean contains(Object o) {
        return base.contains(o);
    }

    /**
     * Determine whether the list contains all elements of another
     * collection.
     * 
     * @param c the collection of other elements to search for
     * 
     * @return {@code true} if this list contains all the specified
     * elements
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return base.containsAll(c);
    }

    /**
     * Get the element at a given position.
     * 
     * @param index the index of the element
     * 
     * @return the element at the specified position
     */
    @Override
    public E get(int index) {
        return base.get(size() - 1 - index);
    }

    /**
     * Get the number of elements in the list.
     * 
     * @return the list's size
     */
    @Override
    public int size() {
        return base.size();
    }

    private class ReversedListIterator implements ListIterator<E> {
        private final ListIterator<E> base;

        public ReversedListIterator(ListIterator<E> base) {
            this.base = base;
        }

        @Override
        public boolean hasNext() {
            return base.hasPrevious();
        }

        @Override
        public E next() {
            return base.previous();
        }

        @Override
        public boolean hasPrevious() {
            return base.hasNext();
        }

        @Override
        public E previous() {
            return base.next();
        }

        @Override
        public int nextIndex() {
            return base.previousIndex();
        }

        @Override
        public int previousIndex() {
            return base.nextIndex();
        }

        @Override
        public void remove() {
            base.remove();
        }

        @Override
        public void set(E e) {
            base.set(e);
        }

        @Override
        public void add(E e) {
            base.add(e);
        }
    }
}
