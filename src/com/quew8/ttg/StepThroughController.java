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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author Quew8
 */
public class StepThroughController implements Initializable {
    @FXML
    private TextField funcField;
    @FXML
    private Button genButton;
    @FXML
    private Button stepButton;
    @FXML
    private TextField outputField;
    @FXML
    private TextField varField;
    @FXML
    private TableView varTable;
    private Term terms;
    private HashMap<String, Boolean> state;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        genButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    Request r = Parser.parse(funcField.getText());
                    if(r.getFuncs().length != 1) {
                        throw new RuntimeException("Step through function can only have one function");
                    }
                    String[] vars = r.getOrder().get();
                    state = new HashMap<String, Boolean>();
                    int n = Integer.parseInt(varField.getText().trim());
                    for(int i = 0; i < vars.length; i++) {
                        state.put(vars[i], (n & (1 << (vars.length-1-i))) != 0);
                    }
                    terms = r.getFuncs()[0].getExp();
                    terms.initialize();
                    outputField.setText(terms.getString());
                } catch(RuntimeException ex) {
                    outputField.setText(ex.getMessage());
                    throw ex;
                }
            }
            
        });
        stepButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                terms.evaluateStep(state);
                outputField.setText(terms.getString());
            }
            
        });
    }    
    
}
