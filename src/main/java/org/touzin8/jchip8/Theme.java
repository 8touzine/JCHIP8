package org.touzin8.jchip8;

import javafx.scene.paint.Color;

public class Theme{
    private static Color darkpink = Color.rgb(75,25,50);
    private static Color lightpink = Color.rgb(175,125,150);
    private static Color darkblue = Color.rgb(25,29,75);
    private static Color lighblue = Color.rgb(125,129,175);
    private static Color darkblack= Color.rgb(0,0,0);
    private static Color lighwhite = Color.rgb(200,200,200);
    private static Color darkgreen = Color.rgb(25,75,29);
    private static Color lightgreen = Color.rgb(125,175,129);

    public static Color getDark(String color){
        switch (color){
            case "pink" -> {
                return darkpink;
            }
            case "blue" -> {
                return darkblue;
            }
            case "black" -> {
                return darkblack;
            }
            case "green" -> {
                return darkgreen;
            }
        }
        return darkblack;
    }
    public static Color getLight(String color){
        switch (color){
            case "pink" -> {
                return lightpink;
            }
            case "blue" -> {
                return lighblue;
            }
            case "black" -> {
                return lighwhite;
            }
            case "green" -> {
                return lightgreen;
            }
        }
        return darkblack;
    }
}
