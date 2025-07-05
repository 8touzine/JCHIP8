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
    private int _pixelSze_x;
    private int _pixelSze_y;
    //int[][] frameBuffer = new int[_width][_height];

    @FXML
    private Canvas convaZER;

    @FXML
    private void initialize(){
        _pixelSze_x = (int)(convaZER.getWidth() / _width);//64
        _pixelSze_y = (int)(convaZER.getHeight() / _height);//32
        gc = convaZER.getGraphicsContext2D();
        gc.setFill(Color.rgb(75,25,50));
        //gc.fillRect(75, 75, 100, 100);
        gc.fillRect(75, 75, _pixelSze_x, _pixelSze_y);
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
        System.out.println("draw");
        for(var y = 0;y < _height;y++ ){
            for(var x = 0; x < _width;x++){
                if(framebuffer[x][y] == 1){
                    //x la taille du pixel pour la reso
                    gc.setFill(Color.rgb(75,25,50));
                    gc.fillRect(x * _pixelSze_x, y * _pixelSze_y, _pixelSze_x, _pixelSze_y);
                }else{
                    gc.setFill(Color.rgb(175,125,150));
                    gc.fillRect(x * _pixelSze_x, y * _pixelSze_y, _pixelSze_x, _pixelSze_y);
                }
            }
        }
    }
}