package org.touzin8.jchip8;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Chip8 extends Application {
    private int frameRate;
    private Memory memzer = new Memory();
    private static Stage stageZer;
    private static final Map<KeyCode, Integer> KEY_MAP = Map.ofEntries(
            Map.entry(KeyCode.DIGIT1, 0x1),   // 1
            Map.entry(KeyCode.DIGIT2, 0x2),   // 2
            Map.entry(KeyCode.DIGIT3, 0x3),   // 3
            Map.entry(KeyCode.DIGIT4, 0xC),   // 4 (à droite de 3)
            Map.entry(KeyCode.A, 0x4),         // A (au lieu de Q en QWERTY)
            Map.entry(KeyCode.Z, 0x5),         // Z (au lieu de W)
            Map.entry(KeyCode.E, 0x6),         // E (idem)
            Map.entry(KeyCode.R, 0xD),         // R (idem)
            Map.entry(KeyCode.Q, 0x7),         // Q (au lieu de A)
            Map.entry(KeyCode.S, 0x8),         // S
            Map.entry(KeyCode.D, 0x9),         // D
            Map.entry(KeyCode.F, 0xE),         // F
            Map.entry(KeyCode.W, 0xA),         // W (au lieu de Z)
            Map.entry(KeyCode.X, 0x0),         // X
            Map.entry(KeyCode.C, 0xB),         // C
            Map.entry(KeyCode.V, 0xF)          // V

    );

    @Override
    public void start(Stage stage) throws IOException {


        //keyboard

        System.out.println(
                " .----------------.  .----------------.  .----------------.  .----------------.   .----------------. \n" +
                        "| .--------------. || .--------------. || .--------------. || .--------------. | | .--------------. |\n" +
                        "| |     ______   | || |  ____  ____  | || |     _____    | || |   ______     | | | |     ____     | |\n" +
                        "| |   .' ___  |  | || | |_   ||   _| | || |    |_   _|   | || |  |_   __ \\   | | | |   .' __ '.   | |\n" +
                        "| |  / .'   \\_| | || |   | |__| |   | || |      | |     | || |    | |__) |  | | | |   | (__) |   | |\n" +
                        "| |  | |         | || |   |  __  |   | || |      | |     | || |    |  ___/   | | | |   .`____'.   | |\n" +
                        "| |  \\ `.___.'\\| || |  _| |  | |_  | || |     _| |_    | || |   _| |_      | | | |  | (____) |  | |\n" +
                        "| |   `._____.'  | || | |____||____| | || |    |_____|   | || |  |_____|     | | | |  `.______.'  | |\n" +
                        "| |              | || |              | || |              | || |              | | | |              | |\n" +
                        "| '--------------' || '--------------' || '--------------' || '--------------' | | '--------------' |\n" +
                        " '----------------'  '----------------'  '----------------'  '----------------'   '----------------' ");
        System.out.println("chip8 emulator by 8touzin on github");

        System.out.println("initiat FXML");
        FXMLLoader fxmlLoader = new FXMLLoader(Chip8.class.getResource("chip8.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 610);
        scene.setOnKeyPressed(event -> {
            Integer keyIndex = KEY_MAP.get(event.getCode());
            if(keyIndex != null){
                System.out.println("keypressed: " + keyIndex);
                memzer.setKey(keyIndex, true);
            }
        });
        System.out.println("initiate controller FXML");
        Display display = fxmlLoader.getController();

        scene.setOnKeyReleased(event -> {
            Integer keyIndex = KEY_MAP.get(event.getCode());
            if(keyIndex != null){
                System.out.println("keyreleased: " + keyIndex);
                memzer.setKey(keyIndex, false);
            }
        });
        String style = getClass().getResource("darkpink.css").toExternalForm();

        scene.getStylesheets().add(style);
       // stage.setTitle("JCHIP8");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(false);
        stage.setScene(scene);
        stageZer = stage;
        stage.show();

        Core chip8core = new Core(memzer, display);
        display.setCore(chip8core);
        //chip8core.load("roms/danm8ku.ch8");
        chip8core.load("roms/Space Invaders [David Winter].ch8");
       // chip8core.load("roms/Cave.ch8");
        chip8core.mainLoop();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void resart(){
        Platform.runLater(()->{
            stageZer.close();

            try {
                new Chip8().start(new Stage());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


}