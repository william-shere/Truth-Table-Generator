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

/**
 *
 * @author Quew8
 */
public class Variable extends Term {
    private final String varName;
    private String s;
    
    public Variable(String varName) {
        this.varName = varName;
        this.s = varName;
    }

    @Override
    public void addAllVariables(ArrayList<String> vars) {
        if(!vars.contains(varName)) {
            vars.add(varName);
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        s = varName;
    }

    @Override
    public void evaluateStep(HashMap<String, Boolean> state) {
        setValue(state.get(varName));
        s = toStringFill(getValue(), s.length());
    }

    @Override
    public String getString() {
        return s;
    }
    
    @Override
    public String toString() {
        return "Var(" + s + ")";
    }
}
