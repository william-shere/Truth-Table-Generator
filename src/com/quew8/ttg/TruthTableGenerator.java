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

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.application.Application;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 * @author Quew8
 */
public class TruthTableGenerator extends Application {
    public static final boolean DEBUG = false;
    public static final String VERSION = "1.0";
    private TabPane tabPane;
    
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        tabPane = new TabPane();
        final Stage finStage = stage;
        tabPane.getTabs().addListener(new ListChangeListener<Tab>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends Tab> c) {
                if(c.getList().isEmpty()) {
                    finStage.close();
                }
            }
            
        });
        newTab();
        
        root.getChildren().addAll(createMenuBar(), tabPane);
        Scene scene = new Scene(root);
        stage.setTitle("Truth Table Generator " + VERSION);
        stage.setScene(scene);
        stage.show();
    }

    public void newTab() {
        try {
            final Tab t = new Tab("Function");
            t.setContent((Node) FXMLLoader.load(getClass().getResource("Main.fxml")));
            t.setContextMenu(createTabContextMenu(t));
            tabPane.getTabs().add(t);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public void newStepThroughTab() {
        try {
            final Tab t = new Tab("Step");
            t.setContent((Node) FXMLLoader.load(getClass().getResource("StepThrough.fxml")));
            t.setContextMenu(createTabContextMenu(t));
            tabPane.getTabs().add(t);
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu functions = new Menu("Functions");
        functions.getItems().addAll(getCommonFunctionMenus());
        
        Menu help = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    if(Desktop.isDesktopSupported()) {
                        File tempFile = File.createTempFile("readme", ".txt");
                        try(BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        TruthTableGenerator.class.getResourceAsStream("readme.txt")
                                )
                        )) {
                            try(BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                                String s;
                                while((s = reader.readLine()) != null) {
                                    writer.write(s);
                                    writer.newLine();
                                }
                                Desktop.getDesktop().open(tempFile);
                            }
                        }
                    }
                } catch(IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            
        });
        help.getItems().addAll(about);
        menuBar.getMenus().addAll(functions, help);
        return menuBar;
    }
    
    private ContextMenu createTabContextMenu(final Tab t) {
        ContextMenu menu = new ContextMenu();
        MenuItem delete = new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                tabPane.getTabs().remove(t);
            }
        });
        menu.getItems().addAll(getCommonFunctionMenus());
        menu.getItems().addAll(delete);
        return menu;
    }
    
    private MenuItem[] getCommonFunctionMenus() {
        MenuItem addMenuItem = new MenuItem("Add Function");
        addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                newTab();
            }
        });
        MenuItem addStepThroughMenuItem = new MenuItem("Add Step Through Function");
        addStepThroughMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent evt) {
                newStepThroughTab();
            }
        });
        return new MenuItem[]{addMenuItem, addStepThroughMenuItem};
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
