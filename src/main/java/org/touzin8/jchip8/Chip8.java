package org.touzin8.jchip8;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Chip8 extends Application {
    private int frameRate;
    private DisplayPort display = new Display();
    private Memory memzer = new Memory();

    @Override
    public void start(Stage stage) throws IOException {


        //keyboard

        Core chip8core = new Core(memzer, display);
        chip8core.load("roms/Space Invaders [David Winter].ch8");
        chip8core.mainLoop();


        FXMLLoader fxmlLoader = new FXMLLoader(Chip8.class.getResource("chip8.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 400);
        String style = getClass().getResource("darkpink.css").toExternalForm();

        scene.getStylesheets().add(style);
        stage.setTitle("JCHIP8");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}