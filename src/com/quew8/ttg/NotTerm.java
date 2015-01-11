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
public class NotTerm extends Term {
    private final Term val;
    private String s;

    public NotTerm(Term val) {
        this.val = val;
    }

    @Override
    public void addAllVariables(ArrayList<String> vars) {
        val.addAllVariables(vars);
    }

    @Override
    public void initialize() {
        super.initialize();
        val.initialize();
        s = "~(" + val.getString() + ")";
    }

    @Override
    public void evaluateStep(HashMap<String, Boolean> state) {
        if(val.isEvald()) {
            setValue(!val.getValue());
            s = toStringFill(getValue(), s.length());
        } else {
            val.evaluateStep(state);
            s = "~(" + val.getString() + ")";
        }
    }

    @Override
    public String getString() {
        return s;
    }
    
    @Override
    public String toString() {
        return "NOT(" + val + ")";
    }
}
