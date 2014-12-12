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
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Quew8
 */
public abstract class Term {
    private boolean b;
    private boolean evald = false;
    
    protected void setValue(boolean b) {
        this.b = b;
        this.evald = true;
    }
    
    protected boolean isEvald() {
        return evald;
    }
    
    public boolean getValue() {
        if(!isEvald()) {
            throw new RuntimeException();
        }
        return b;
    }
    
    public boolean evaluate(HashMap<String, Boolean> state) {
        if(TruthTableGenerator.DEBUG)
            System.out.println("Evaluating " + Arrays.toString(state.keySet().toArray()) + " :: " + Arrays.toString(state.values().toArray()));
        int i = 0;
        initialize();
        while(!isEvald()) {
            if(TruthTableGenerator.DEBUG)
                System.out.println("Evaluation Step: " + (i++));
            evaluateStep(state);
        }
        return getValue();
    }
    
    public void initialize() {
        evald = false;
    }
    
    public abstract void evaluateStep(HashMap<String, Boolean> state);
    public abstract String getString();
    public abstract void addAllVariables(ArrayList<String> vars);
    
    public static String toStringFill(boolean b, int length) {
        int n = length - 1;
        if(n == 0) {
            return toString(b);
        } else {
            int pre;
            if(n % 2 == 0) {
                pre = n / 2;
            } else {
                pre = (n - 1) / 2;
            }
            return nEmpties(pre) + toString(b) + nEmpties(n - pre);
        }
    }
    
    public static String toString(boolean b) {
        return b ? "1" : "0";
    }
    
    public static String nEmpties(int n) {
        return nChars(n, ' ');
    }
    
    public static String nChars(int n, char c) {
        String s = "";
        for(int i = 0; i < n; i++) {
            s += c;
        }
        return s;
    }
}
