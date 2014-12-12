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
package com.quew8.ttg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author Quew8
 */
public class Expression extends Term {
    public static final LogicFunc[] PRECEDENCE = {
        LogicFunc.XOR,
        LogicFunc.XNOR,
        LogicFunc.AND,
        LogicFunc.NAND,
        LogicFunc.OR,
        LogicFunc.NOR
    };
    private boolean inBrackets = false;
    private final ArrayList<Term> values = new ArrayList<>();
    private final ArrayList<LogicFunc> operators = new ArrayList<>();
    private final LinkedList<StepTerm> iterValues = new LinkedList<StepTerm>();
    private final LinkedList<LogicFunc> iterFuncs = new LinkedList<LogicFunc>();
    private int iterPLevel;
    private int iterStep;
    private StringBuilder s;
    
    public Expression(Term val, boolean inBrackets) {
        addSimplified(val);
        this.inBrackets = inBrackets;
    }
    
    public void setInBrackets(boolean inBrackets) {
        this.inBrackets = inBrackets;
    }
    
    public void setValue(Term ref) {
        values.clear();
        operators.clear();
        addSimplified(ref);
    }
    
    public void add(LogicFunc operator, Term ref) {
        operators.add(operator);
        addSimplified(ref);
    }
    
    private void addSimplified(Term val) {
        if(val instanceof Expression) {
            Expression e = (Expression) val;
            if(!e.inBrackets) {
                values.add(e.values.get(0));
                for(int i = 0; i < e.operators.size(); i++) {
                    operators.add(e.operators.get(i));
                    values.add(e.values.get(i+1));
                }
                return;
            }
        }
        values.add(val);
    }

    @Override
    public void addAllVariables(ArrayList<String> vars) {
        for(Term val: values) {
            val.addAllVariables(vars);
        }
    }
    
    @Override
    public void initialize() {
        super.initialize();
        for(Term t: values) {
            t.initialize();
        }
        s = new StringBuilder("(");
        iterValues.clear();
        iterFuncs.clear();
        for(int i = 0; i < operators.size(); i++) {
            String s2 = values.get(i).getString();
            StepTerm t = new StepTerm();
            t.startIndex = s.length();
            t.endIndex = s.length() + s2.length();
            s.append(s2);
            s.append(" ").append(operators.get(i).name()).append(" ");
            iterValues.add(t);
            iterFuncs.add(operators.get(i));
        }
        String s2 = values.get(values.size() - 1).getString();
        StepTerm t = new StepTerm();
        t.startIndex = s.length();
        t.endIndex = s.length() + s2.length();
        iterValues.add(t);
        s.append(s2).append(")");
        iterPLevel = 0;
        iterStep = 0;
    }
    
    @Override
    public void evaluateStep(HashMap<String, Boolean> state) {
        if(iterValues.size() == values.size()) {
            for(int i = 0; i < values.size(); i++) {
                if(!values.get(i).isEvald()) {
                    values.get(i).evaluateStep(state);
                    if(values.get(i).isEvald()) {
                        iterValues.get(i).b = values.get(i).getValue();
                        s.replace(
                                iterValues.get(i).startIndex, 
                                iterValues.get(i).endIndex, 
                                toStringFill(
                                        iterValues.get(i).b, 
                                        iterValues.get(i).endIndex - iterValues.get(i).startIndex
                                )
                        );
                    } else {
                        s.replace(
                                iterValues.get(i).startIndex, 
                                iterValues.get(i).endIndex, 
                                values.get(i).getString()
                        );
                    }
                    return;
                }
            }
        }
        if(iterValues.size() == 1) {
            setValue(iterValues.get(0).b);
        }
        for(; iterPLevel < PRECEDENCE.length; iterPLevel++) {
            for(iterStep = 0; iterStep < iterFuncs.size(); iterStep++) {
                if(iterFuncs.get(iterStep) == PRECEDENCE[iterPLevel]) {
                    switch(iterFuncs.get(iterStep)) {
                        case OR: iterValues.get(iterStep+1).b |= iterValues.get(iterStep).b; break;
                        case AND: iterValues.get(iterStep+1).b &= iterValues.get(iterStep).b; break;
                        case XOR: iterValues.get(iterStep+1).b ^= iterValues.get(iterStep).b; break;
                        case NOR: iterValues.get(iterStep+1).b = !(iterValues.get(iterStep).b | iterValues.get(iterStep+1).b); break;
                        case NAND: iterValues.get(iterStep+1).b = !(iterValues.get(iterStep).b & iterValues.get(iterStep+1).b); break;
                        case XNOR: iterValues.get(iterStep+1).b = !(iterValues.get(iterStep).b ^ iterValues.get(iterStep+1).b); break;
                        default: throw new RuntimeException("Unrecognized Operator: " + operators.get(iterStep));
                    }
                    iterValues.get(iterStep+1).startIndex = iterValues.get(iterStep).startIndex;
                    s.replace(
                            iterValues.get(iterStep+1).startIndex, 
                            iterValues.get(iterStep+1).endIndex, 
                            toStringFill(
                                    iterValues.get(iterStep+1).b, 
                                    iterValues.get(iterStep+1).endIndex - iterValues.get(iterStep+1).startIndex
                            )
                    );
                    iterValues.remove(iterStep);
                    iterFuncs.remove(iterStep);
                    iterStep--;
                    return;
                }
            }
        }
        if(iterValues.size() != 1) {
            throw new RuntimeException("");
        }
        setValue(iterValues.get(0).b);
    }
    
    @Override
    public String getString() {
        return s.toString();
    }
    
    @Override
    public String toString() {
        if(values.size() == 1) {
            return values.get(0).toString();
        }
        String ls = "( " + values.get(0);
        for(int i = 0; i < operators.size(); i++) {
            ls += " " + operators.get(i).name();
            ls += " " + values.get(i + 1);
        }
        return ls + " )";
    }
    
    private static class StepTerm {
        boolean b;
        int startIndex;
        int endIndex;

        @Override
        public String toString() {
            return "StepTerm{" + "b=" + b + '}';
        } 
    }
}
