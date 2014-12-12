/* 
 * Copyright (C) 2014 William Shere
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.quew8.ttg.parser;

import java.util.Arrays;

/**
 *
 * @author Quew8
 */
class TokenClass implements TokenSequenceMatcher {
    private final TokenSequenceMatcher[] clazz;
    private final LengthModifier mod;

    TokenClass(TokenSequenceMatcher[] clazz, LengthModifier mod) {
        this.clazz = clazz;
        this.mod = mod;
    }

    TokenClass(TokenSequenceMatcher... clazz) {
        this(clazz, LengthModifier.ONE);
    }

    @Override
    public int match(MatchResult result, TokenSequence sequence, int offset) {
        int n = 0;
        int nOffset = offset;
        while(true) {
            int j = test(result, sequence, nOffset);
            if(j != -1) {
                n += j;
                nOffset += j;
            } else {
                break;
            }
            if(!mod.isAcceptMore()) {
                break;
            }
        }
        if(n == 0 && mod.isFailOnNone()) {
            return -1;
        }
        return n;
    }

    private int test(MatchResult result, TokenSequence sequence, int offset) {
        for(int i = 0; i < clazz.length; i++) {
            int j = clazz[i].match(result, sequence, offset);
            if(j != -1) {
                return j;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Class{" + Arrays.toString(clazz) + ", mod=" + mod.name() + '}';
    }
    
}
