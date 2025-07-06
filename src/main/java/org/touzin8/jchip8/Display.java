/*
* MIT License

Copyright (c) 2025 8touzine

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

* */
package org.touzin8.jchip8;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

public class Display implements DisplayPort {


    GraphicsContext gc;
    private int _height = 32;
    private int _width = 64;
    private int _pixelSze_x;
    private int _pixelSze_y;
    private Stage stage;
    private FileChooser chooser = new FileChooser();
    private Core core;
    private String color;



    //int[][] frameBuffer = new int[_width][_height];
    @FXML
    ComboBox comboColor;

    @FXML
    Button breakDev;

    @FXML
    private Canvas convaZER;

    @FXML
    private ListView listopcodes;
    @FXML
    private ListView listRegistery;

    private boolean devMode = false;

    ObservableList<String> registerZ = FXCollections.observableArrayList();

    @FXML
    private void initialize(){
        _pixelSze_x = (int)(convaZER.getWidth() / _width);//64
        _pixelSze_y = (int)(convaZER.getHeight() / _height);//32
        gc = convaZER.getGraphicsContext2D();
        gc.setFill(Color.rgb(75,25,50));
        //gc.fillRect(75, 75, 100, 100);
        gc.fillRect(75, 75, _pixelSze_x, _pixelSze_y);
        initCombo();
        //draw = new Drawer(_height, _width, _pixelSze);
        color = "blue";
        initRegister();
    }
    private void initRegister(){
        registerZ.setAll("V0: ", "V1: ","V2: ",
                "V3: ", "V4: ", "V5: ", "V6: ",
                "V7: ", "V8: ", "V9: ",
                "VA: ", "VB: ", "VC: ",
                "VD: ", "VE: ", "VF: ");
        listRegistery.setItems(registerZ);

    }

    private void initCombo(){
        comboColor.getItems().addAll("pink", "blue", "black", "green");
        comboColor.setOnAction(event -> {
            color = comboColor.getValue().toString();
        });
    }


    public void setCore(Core coreZER){
        core = coreZER;
    }

    @Override
    public void draw(int[][] framebuffer) {
        System.out.println("draw");
        for(var y = 0;y < _height;y++ ){
            for(var x = 0; x < _width;x++){
                if(framebuffer[x][y] == 1){
                    //x la taille du pixel pour la reso
                    gc.setFill(Theme.getDark(color));
                    gc.fillRect(x * _pixelSze_x, y * _pixelSze_y, _pixelSze_x, _pixelSze_y);
                }else{
                    gc.setFill(Theme.getLight(color));
                    gc.fillRect(x * _pixelSze_x, y * _pixelSze_y, _pixelSze_x, _pixelSze_y);
                }
            }
        }
    }

    @Override
    public void logOpcode(int opcodes, int pc){
        if(devMode) {
            String entry = (Integer.toHexString(opcodes) + "  " + pc).toString();
            if (listopcodes.getItems().size() > 10) {
                listopcodes.getItems().removeFirst();
            }
            listopcodes.getItems().add(entry);
        }
    }

    public void devTool(){
        listopcodes.setVisible(!listopcodes.isVisible());
        breakDev.setVisible(!breakDev.isVisible());
        listRegistery.setVisible(!listRegistery.isVisible());
        devMode = !devMode;
    }

    public void closeWindow(ActionEvent event){
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void restartApp(ActionEvent event){
        Chip8.resart();
    }

    public void chooseRom(ActionEvent event){
        configureChoose(chooser);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        File rom = chooser.showOpenDialog(stage);
        if(rom == null){
            return;
        }
        listopcodes.getItems().removeAll();
        listRegistery.getItems().removeAll();
        core.loadSelectedRom(rom);

    }

    public void flowMan(ActionEvent event){
        Button clicked = (Button) event.getSource();
        if(Objects.equals(clicked.getText().toString(), "Break")){
            pauseFlow();
            clicked.setText("Resume");
        }else if(Objects.equals(clicked.getText(), "Resume")){
            resumeFlow();
            clicked.setText("Break");
        }
    }

    private void pauseFlow(){
        core.pauseLoop();
    }
    public void resumeFlow(){
        core.resumeLoop();
    }

    private void configureChoose(FileChooser choos){
        choos.setTitle("Choose ROM");
        choos.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ch8", "*.ch8")
        );
    }

    @Override
    public void logRegistery(byte[] V){
        if (devMode) {
            for(var i= 0; i < V.length; i++){
                registerZ. set(i, "V(" + i + "): " + (V[i] & 0xFF));
            }
        }
        //ObservableList<String> registerZ = FXCollections.observableArrayList();
    }



}

