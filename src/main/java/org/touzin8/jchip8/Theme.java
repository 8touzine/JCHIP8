/*
* MIT License

Copyright (c) 2025 8touzin

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
