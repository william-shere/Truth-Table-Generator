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

/**
 *
 * @author Quew8
 */
public class Order {
    private final String[] vars;

    public Order(String[] vars) {
        this.vars = vars;
    }
    
    public String[] get() {
        return vars;
    }
    
    public boolean checkOrderWorks(Function[] funcs) {
        ArrayList<String> eVars = new ArrayList<>();
        for(Function f: funcs) {
            f.getExp().addAllVariables(eVars);
        }
        int n = 0;
        for(int i = 0; i < eVars.size(); i++) {
            for(String comp: vars) {
                if(eVars.get(i) != null && eVars.get(i).equals(comp)) {
                    eVars.set(i, null);
                    n++;
                }
            }
        }
        return n == eVars.size();
    }
    
    public static Order createDefaultOrder(Function[] funcs) {
        ArrayList<String> vars = new ArrayList<>();
        for(Function f: funcs) {
            f.getExp().addAllVariables(vars);
        }
        return new Order(vars.toArray(new String[vars.size()]));
    }
}
