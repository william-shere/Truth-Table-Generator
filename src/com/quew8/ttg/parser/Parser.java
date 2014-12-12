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

import com.quew8.ttg.Constant;
import com.quew8.ttg.Expression;
import com.quew8.ttg.Function;
import com.quew8.ttg.LogicFunc;
import com.quew8.ttg.NotTerm;
import com.quew8.ttg.Order;
import com.quew8.ttg.Request;
import com.quew8.ttg.Term;
import com.quew8.ttg.TruthTableGenerator;
import com.quew8.ttg.Variable;
import com.quew8.ttg.parser.MatchResult.Snapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;

/**
 *
 * @author Quew8
 */
public class Parser {
    private static final TokenClass
            OPERATOR = new TokenClass(
                    TokenType.OR,
                    TokenType.AND,
                    TokenType.XOR,
                    TokenType.NOR,
                    TokenType.NAND,
                    TokenType.XNOR
            ),
            CONSTANT = new TokenClass(
                    TokenType.CONST_0,
                    TokenType.CONST_1
            );
    
    public static TokenSequence tokenize(String s) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        int i = s.length();
        String remainder = s;
        outerLoop:
        while(i > 0) {
            String test = remainder.substring(0, i);
            for(TokenType tt: TokenType.getRegularValues()) {
                Matcher m = tt.getPattern().matcher(test);
                if(m.matches()) {
                    if(!tt.isDiscard()) {
                        tokens.add(new Token(m.group(), tt));
                    }
                    remainder = remainder.substring(i);
                    i = remainder.length();
                    continue outerLoop;
                }
            }
            i--;
        }
        if(remainder.length() != 0) {
            throw new RuntimeException("Invalid Word: " + remainder);
        }
        return new TokenSequence(tokens.toArray(new Token[tokens.size()]));
    }
    
    public static Request parse(String s) {
        if(TruthTableGenerator.DEBUG) {
            System.out.println("Parsing \"" + s + "\"");
        }
        MatchResult result = new MatchResult(tokenize(s));
        ArrayList<Function> funcs = new ArrayList<>();
        Function f;
        Order o;
        if((f = function(result, funcs.size())) != null) {
            funcs.add(f);
        } else {
            throw new RuntimeException("Invalid Parse");
        }
        while(result.match(TokenType.COMMA)) {
            if((f = function(result, funcs.size())) != null) {
                funcs.add(f);
            } else {
                throw new RuntimeException("Invalid Parse");
            }
        }
        Function[] funcsArray = funcs.toArray(new Function[funcs.size()]);
        if((o = order(result)) == null) {
            o = Order.createDefaultOrder(funcsArray);
        } else if(!o.checkOrderWorks(funcsArray)) {
            throw new RuntimeException("Invalid Order");
        }
        if(TruthTableGenerator.DEBUG) {
            System.out.println(Arrays.toString(funcsArray));
        }
        return new Request(funcsArray, o);
    }
    
    public static Function function(MatchResult result, int nth) {
        Snapshot s = result.takeSnapshot();
        Token t;
        Expression e;
        String name = "Output" + Integer.toString(nth);
        if((t = result.matchToken(TokenType.WORD)) != null) {
            if(result.match(TokenType.EQUALS)) {
                name = t.getText();
            } else {
                result.revert(s);
            }
        }
        if((e = expression(result)) != null) {
            return new Function(name, e);
        }
        result.revert(s);
        return null;
    }
    
    public static Expression expression(MatchResult result) {
        Snapshot s = result.takeSnapshot();
        Term ref;
        LogicFunc op;
        boolean invert = result.match(TokenType.NOT);
        if(result.match(TokenType.OPEN_BRACKET)) { //Open Bracket
            if((ref = expression(result)) != null) { //Expression in bracket
                if(result.match(TokenType.CLOSE_BRACKET)) { //Close Bracket
                    ref = new Expression(ref, true);
                }
            } else { //Opened bracket without closing
                result.revert(s);
                return null;
            }
        } else if((ref = value(result)) == null) { //Else look for value
            result.revert(s);
            return null;
        }
        if(invert) {
            ref = new NotTerm(ref);
        }
        Expression exp = new Expression(ref, false);
        while((op = operator(result)) != null) {
            //System.out.println("EXPRESSION" + expCounter + " OPERATOR");
            Term ref2;
            if((ref2 = expression(result)) != null) {
                //System.out.println("EXPRESSION" + expCounter + " EXPRESSION");
                exp.add(op, ref2);
            } else {
                result.revert(s);
                return null;
            }
        }
        return exp;
    }
    
    public static Order order(MatchResult result) {
        Snapshot s = result.takeSnapshot();
        ArrayList<String> vars = new ArrayList<>();
        Token t;
        if(result.match(TokenType.OPEN_SQ_BRACKET)) {
            while((t = result.matchToken(TokenType.WORD)) != null) {
                vars.add(t.getText());
            }
            if(!vars.isEmpty()) {
                if(result.match(TokenType.CLOSE_SQ_BRACKET)) {
                    return new Order(vars.toArray(new String[vars.size()]));
                }
            }
        }
        result.revert(s);
        return null;
    }
    
    public static LogicFunc operator(MatchResult result) {
        Snapshot s = result.takeSnapshot();
        ComplexToken t;
        if((t = result.matchComplexToken(OPERATOR)) != null) {
            return LogicFunc.get(t.get(0).getType());
        }
        result.revert(s);
        return null;
    }
    
    public static Term value(MatchResult result) {
        Token t;
        ComplexToken ct;
        if((t = result.matchToken(TokenType.WORD)) != null) {
            return new Variable(t.getText());
        } else if((ct = result.matchComplexToken(CONSTANT)) != null) {
            return Constant.get(ct.get(0).getType());
        } else {
            return null;
        }
    }
    
    /*private static TokenGroupSection getGroup(String sequence, LengthModifier modifier, TokenSequenceMatcher... matchers) {
        ArrayList<TokenSequenceMatcher> sections = new ArrayList<TokenSequenceMatcher>();
        int lastSection = 0;
        int lastClass = -1;
        int lastGroup = -1;
        int bracketDepth = 0;
        for(int i = 0; i < sequence.length(); i++) {
            if(sequence.charAt(i) == '[') {
                if(i != lastSection) {
                    sections.add(new TokenSequenceSection(resolve(sequence.substring(lastSection, i), matchers)));
                }
                lastClass = i;
            } else if(sequence.charAt(i) == ']') {
                if(lastClass == -1) {
                    throw new IllegalArgumentException("Malformed Token Sequence: \"" + sequence + "\"");
                }
                int endOfSequence = i;
                LengthModifier mod = LengthModifier.ONE;
                if(
                        i + 1 < sequence.length() && 
                        (
                        sequence.charAt(i+1) == '+' || 
                        sequence.charAt(i+1) == '?' || 
                        sequence.charAt(i+1) == '*'
                        )
                        ) {
                    if(sequence.charAt(i+1) == '+') {
                        mod = LengthModifier.ONE_OR_MORE;
                    } else if(sequence.charAt(i+1) == '*') {
                        mod = LengthModifier.ZERO_OR_MORE;
                    } else if(sequence.charAt(i+1) == '?') {
                        mod = LengthModifier.ONE_OR_ZERO;
                    }
                    i++;
                }
                sections.add(new TokenClassSection(resolve(sequence.substring(lastClass+1, endOfSequence), matchers), mod));
                lastClass = -1;
                lastSection = i + 1;
            } else if(sequence.charAt(i) == '(') {
                if(lastClass != -1) {
                    throw new IllegalArgumentException("Malformed Token Sequence: \"" + sequence + "\"");
                }
                bracketDepth++;
                if(lastGroup == -1) {
                    if(i != lastSection) {
                        sections.add(new TokenSequenceSection(resolve(sequence.substring(lastSection, i), matchers)));
                    }
                    lastGroup = i;
                }
            } else if(sequence.charAt(i) == ')') {
                if(lastGroup == -1) {
                    throw new IllegalArgumentException("Malformed Token Sequence: \"" + sequence + "\"");
                }
                bracketDepth--;
                if(bracketDepth == 0) {
                    int endOfSequence = i;
                    LengthModifier mod = LengthModifier.ONE;
                    if(
                            i + 1 < sequence.length() && 
                            (
                            sequence.charAt(i+1) == '+' || 
                            sequence.charAt(i+1) == '?' || 
                            sequence.charAt(i+1) == '*'
                            )
                            ) {
                        if(sequence.charAt(i+1) == '+') {
                            mod = LengthModifier.ONE_OR_MORE;
                        } else if(sequence.charAt(i+1) == '*') {
                            mod = LengthModifier.ZERO_OR_MORE;
                        } else if(sequence.charAt(i+1) == '?') {
                            mod = LengthModifier.ONE_OR_ZERO;
                        }
                        i++;
                    }
                    sections.add(getGroup(sequence.substring(lastGroup+1, endOfSequence), mod, matchers));
                    lastGroup = -1;
                    lastSection = i + 1;
                }
            }
        }
        if(lastSection < sequence.length()) {
            sections.add(new TokenSequenceSection(resolve(sequence.substring(lastSection), matchers)));
        }
        return new TokenGroupSection(sections.toArray(new TokenSequenceMatcher[sections.size()]), modifier);
    }*/
    
    /*private static TokenGroupSection getGroup(String sequence, TokenSequenceMatcher... matchers) {
        return getGroup(sequence, LengthModifier.ONE, matchers);
    }*/
    
    /*private static TokenSequenceMatcher[] resolve(String s, TokenSequenceMatcher... matchers) {
        TokenSequenceMatcher[] resolved = new TokenSequenceMatcher[s.length()];
        for(int i = 0; i < resolved.length; i++) {
            resolved[i] = matchers[Integer.parseInt(s.substring(i, i+1))];
        }
        return resolved;
    }*/
}
