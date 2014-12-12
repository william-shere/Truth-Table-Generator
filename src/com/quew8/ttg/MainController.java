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

import com.quew8.ttg.parser.Parser;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;

/**
 *
 * @author Quew8
 */
public class MainController implements Initializable {
    public static final int[] H_K_MAP_INDICES_2 = {0, 2};
    public static final int[] V_K_MAP_INDICES_2 = {0, 1};
    public static final int[] H_K_MAP_INDICES_3 = {0, 2, 6, 4};
    public static final int[] V_K_MAP_INDICES_3 = {0, 1};
    public static final int[] H_K_MAP_INDICES_4 = {0, 4, 12, 8};
    public static final int[] V_K_MAP_INDICES_4 = {0, 1, 3, 2};
    @FXML
    private TextField funcField;
    @FXML
    private TextArea outputArea;
    @FXML
    private Button genButton;
    @FXML
    private ToggleButton kMapToggle;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        funcField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                generate();
            }
        });
        genButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                generate();
            }
        });
    }
    
    private void generate() {
        if(kMapToggle.isSelected()) {
            generateKMap();
        } else {
            generateTT();
        }
    }
    
    private void generateTT() {
        try {
            String s = "";
            Request r = Parser.parse(funcField.getText());
            String[] vars = r.getOrder().get();
            HashMap<String, Boolean> state = new HashMap<>();
            for(String var: vars) {
                s += var + " ";
            }
            s += "|";
            for(Function func: r.getFuncs()) {
                s += " " + func.getVarName();
            }
            s += "\n" + nChars(s.length(), '-') + "\n";
            for(int i = 0; i < Math.pow(2, vars.length); i++) {
                for(int j = 0; j < vars.length; j++) {
                    boolean b = (i & ( 1 << ( vars.length - 1 - j ) )) != 0;
                    state.put(vars[j], b);
                    s += toStringFill(b, vars[j]) + " ";
                }
                s += "|";
                for(Function func: r.getFuncs()) {
                    s += " " + toStringFill(func.getExp().evaluate(state), func.getVarName());
                }
                s += "\n";
            }
            outputArea.setText(s);
        } catch(RuntimeException ex) {
            outputArea.setText(ex.getMessage());
            throw ex;
        }
    }
    
    private void generateKMap() {
        try {
            String s = "";
            Request r = Parser.parse(funcField.getText());
            if(r.getFuncs().length != 1) {
                throw new RuntimeException("K Map can only display one function at a time");
            }
            String[] vars = r.getOrder().get();
            String[] hVars, vVars;
            int[] hIndices, vIndices;
            if(vars.length == 2) {
                hVars = new String[]{vars[0]};
                vVars = new String[]{vars[1]};
                hIndices = H_K_MAP_INDICES_2;
                vIndices = V_K_MAP_INDICES_2;
            } else if(vars.length == 3) {
                hVars = new String[]{vars[0], vars[1]};
                vVars = new String[]{vars[2]};
                hIndices = H_K_MAP_INDICES_3;
                vIndices = V_K_MAP_INDICES_3;
            } else if(vars.length == 4) {
                hVars = new String[]{vars[0], vars[1]};
                vVars = new String[]{vars[2], vars[3]};
                hIndices = H_K_MAP_INDICES_4;
                vIndices = V_K_MAP_INDICES_4;
            } else {
                throw new RuntimeException("Unsupported K-Map Size: " + vars.length);
            }
            String vHeader = "";
            for(String vVar: vVars) {
                vHeader += vVar + " ";
            }
            String hHeader = nEmpties(vHeader.length());
            String hExtra = "";
            for(String hVar: hVars) {
                hHeader += hVar + " ";
                hExtra += nEmpties(hVar.length() + 1);
            }
            vHeader += nEmpties(hHeader.length() - vHeader.length());
            hHeader += "| ";
            vHeader += "| ";
            for(int i = 0; i < Math.pow(2, hVars.length); i++) {
                for(int j = 0; j < hVars.length; j++) {
                    hHeader += toString( ( hIndices[i] & ( 1 << ( vars.length - 1 - j ) ) ) != 0);
                }
                hHeader += " ";
            }
            s += hHeader + "\n";
            s += vHeader + "\n";
            s += nChars(hHeader.length(), '-') + "\n";
            HashMap<String, Boolean> state = new HashMap<>();
            for(int i = 0; i < Math.pow(2, vVars.length); i++) {
                for(int j = 0; j < vVars.length; j++) {
                    s += toStringFill( ( vIndices[i] & (1 << (vVars.length - 1 - j)) ) != 0, vVars[j]) + " ";
                }
                s += hExtra + "| ";
                for(int j = 0; j < Math.pow(2, hVars.length); j++) {
                    int nth = vIndices[i] + hIndices[j];
                    for(int k = 0; k < vars.length; k++) {
                        boolean b = ( nth & ( 1 << ( vars.length - 1 - k ) )) != 0;
                        state.put(vars[k], b);
                    }
                    s += toString(r.getFuncs()[0].getExp().evaluate(state)) + nEmpties(hVars.length);
                }
                s += "\n";
            }
            outputArea.setText(s);
        } catch(RuntimeException ex) {
            outputArea.setText(ex.getMessage());
            throw ex;
        }
    }
    
    private static String toStringFill(boolean b, String var) {
        int n = var.length() - 1;
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
    
    private static String toString(boolean b) {
        return b ? "1" : "0";
    }
    
    private static String nEmpties(int n) {
        return nChars(n, ' ');
    }
    
    private static String nChars(int n, char c) {
        String s = "";
        for(int i = 0; i < n; i++) {
            s += c;
        }
        return s;
    }
}
