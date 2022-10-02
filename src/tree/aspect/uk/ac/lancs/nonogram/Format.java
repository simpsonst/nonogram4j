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

package uk.ac.lancs.nonogram;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Locale;

/**
 * Converts between puzzles and text formats.
 * 
 * @author simpsons
 */
public interface Format {
    /**
     * Write a puzzle to a character stream.
     * 
     * @param puzzle the puzzle to write
     * 
     * @param out the stream to write to
     * 
     * @throws IOException if an I/O error occurs
     */
    void write(Puzzle puzzle, Writer out) throws IOException;

    /**
     * Read a puzzle from a character stream.
     * 
     * @param in the stream to read from
     * 
     * @param defaultLocale the locale to assume for unspecified
     * meta-data
     * 
     * @return the parsed puzzle
     * 
     * @throws IOException if an I/O error occurs
     * 
     * @throws IllegalArgumentException if puzzle is incorrectly
     * formatted
     */
    Puzzle read(Reader in, Locale defaultLocale) throws IOException;
}
