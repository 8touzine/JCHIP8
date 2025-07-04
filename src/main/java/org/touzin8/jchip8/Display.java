package org.touzin8.jchip8;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class Display implements DisplayPort {


    GraphicsContext gc;
    private int _height = 32;
    private int _width = 64;
    private int _pixelSze;
    //int[][] frameBuffer = new int[_width][_height];

    @FXML
    private Canvas convaZER;

    @FXML
    private void initialize(){
        _pixelSze = (int)(convaZER.getWidth() / _width);//64
        gc = convaZER.getGraphicsContext2D();
        gc.setFill(Color.rgb(75,25,50));
        //gc.fillRect(75, 75, 100, 100);
        gc.fillRect(75, 75, _pixelSze, _pixelSze);
        //draw = new Drawer(_height, _width, _pixelSze);
    }
    @Override
    public void clear(int[][] frameBuffer) {
        for(int [] row: frameBuffer){
            Arrays.fill(row, 0);//pour chaque rander grid[x] on remplit tout de 0
        }
        draw(frameBuffer);
    }

    @Override
    public void draw(int[][] framebuffer) {
        for(var y = 0;y < _height;y++ ){
            for(var x = 0; x < _width;x++){
                if(framebuffer[x][y] == 1){
                    //x la taille du pixel pour la reso
                    gc.setFill(Color.rgb(75,25,50));
                    gc.fillRect(x * _pixelSze, y * _pixelSze, _pixelSze, _pixelSze);
                }else{
                    gc.setFill(Color.rgb(175,125,150));
                    gc.fillRect(x * _pixelSze, y * _pixelSze, _pixelSze, _pixelSze);
                }
            }
        }
    }
}