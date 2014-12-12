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

/**
 *
 * @author Quew8
 */
public class ComplexToken {
    private final Token[] tokens;

    public ComplexToken(Token[] tokens) {
        this.tokens = tokens;
    }
    
    public Token get(int i) {
        return tokens[i];
    }
    
    public int getNTokens() {
        return tokens.length;
    }
}
