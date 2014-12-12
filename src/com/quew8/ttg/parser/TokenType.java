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

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author Quew8
 */
public enum TokenType implements TokenSequenceMatcher {
    END_OF_SEQUENCE("", false, true),
    EMPTY_SPACE("[\\s\\n]+", true, false),
    CONST_0("0", false, false),
    CONST_1("1", false, false),
    WORD("[a-zA-Z_][\\w_]*", false, false),
    OPEN_BRACKET("\\(", false, false), 
    CLOSE_BRACKET("\\)", false, false), 
    OPEN_SQ_BRACKET("\\[", false, false), 
    CLOSE_SQ_BRACKET("\\]", false, false), 
    OR("\\+", false, false), 
    AND("\\.", false, false),
    XOR("\\^", false, false),
    NOR("\\~\\+", false, false), 
    NAND("\\~\\.", false, false), 
    XNOR("\\~\\^", false, false),
    NOT("\\~", false, false),
    COMMA(",", false, false),
    EQUALS("=", false, false);

    private TokenType(String regex, boolean discard, boolean special) {
        this.pattern = Pattern.compile(regex);
        this.discard = discard;
        this.special = special;
    }
    
    private static TokenType[] regularValues;
    private final Pattern pattern;
    private final boolean discard;
    private final boolean special;

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isDiscard() {
        return discard;
    }

    public boolean isSpecial() {
        return special;
    }

    @Override
    public int match(MatchResult result, TokenSequence sequence, int offset) {
        if(sequence.get(offset).getType() == this) {
            return 1;
        } else {
            return -1;
        }
    }
    
    public static TokenType[] getRegularValues() {
        if(regularValues == null) {
            ArrayList<TokenType> vals = new ArrayList<TokenType>();
            for(TokenType tt: TokenType.values()) {
                if(!tt.isSpecial()) {
                    vals.add(tt);
                }
            }
            regularValues = vals.toArray(new TokenType[vals.size()]);
        }
        return regularValues;
    }
    
}
