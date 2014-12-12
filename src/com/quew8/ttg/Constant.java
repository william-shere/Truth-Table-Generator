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

import com.quew8.ttg.parser.TokenType;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Quew8
 */
public class Constant extends Term {

    public Constant(boolean constant) {
        setValue(constant);
    }

    @Override
    public void addAllVariables(ArrayList<String> vars) {
        
    }

    @Override
    public void initialize() {
        super.initialize();
    }

    @Override
    public void evaluateStep(HashMap<String, Boolean> state) {
        
    }

    @Override
    public String getString() {
        return toString(getValue());
    }

    public static Term get(TokenType type) {
        switch(type) {
            case CONST_0: return new Constant(false);
            case CONST_1: return new Constant(true);
            default: throw new RuntimeException("Invalid Token Constant: " + type.name());
        }
    }
    
}
