package org.touzin8.jchip8;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Chip8 extends Application {
    int height = 32;
    int width = 64;

    @Override
    public void start(Stage stage) throws IOException {
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