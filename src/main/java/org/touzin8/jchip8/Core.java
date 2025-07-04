package org.touzin8.jchip8;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Core {
    private final Memory memzer;
    private final DisplayPort display;
    private int X;
    private int Y;
    private int opcode;
    private Timeline timeline;


    public Core(Memory mem, DisplayPort dis){
        memzer = mem;
        display = dis;
    }

    public void load(String romPath){
        memzer.loadROM(romPath);
        memzer.loadFontset();
    }

    public void mainLoop(){
        //60 par sec
        Duration frameDuration = Duration.millis(1000.0 / 60); //64Hz
       timeline = new Timeline(new KeyFrame(frameDuration, event -> {
            opcode =  memzer.fetchOpcode();
            decodeOpcode(opcode);
            memzer.tickTimers();
            if(memzer.isDrawFlag()){
                display.draw(memzer.getFrameBuffer());
                memzer.setDrawFlag(false);
            }
        }));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    }

    public void stop(){
    timeline.stop();
    }

    /////////
    public void decodeOpcode(int opcode){
        //composition du opcode
        //todo: ici c'est une fonction fetch
        ///var opcode = memory[PC] << 8 | memory[PC + 1];

        //checker quel opcode: 0xX000 avec le filtre 0xF000
//        X = (opcode & 0x0F00) >> 8;
//        Y = (opcode & 0x00F0) >> 4;
        //todo voici le decode qui se fera dans le core
        System.out.println("pc: " + memzer.getPC() + " opcode: " + opcode);
        System.out.println(opcode & 0xF000);
        switch (opcode & 0xF000) {
            case 0x0000:
                // y a 2 opcode qui commencnt avec 0x0 -> 0x00E0 et 0x00EE
                switch (opcode & 0x000F){
                    case 0x0000:
                        //clear screen
                        memzer.clearFrame();
                        display.clear(memzer.getFrameBuffer());
                        memzer.setDrawFlag(true);
                        var newPC = memzer.getPC();
                        memzer.setPC(newPC += 2);
                        break;
                    case 0x000E:
                        memzer.returnFromSubroutine();
                        break;
                }
                break;
            case 0x1000:
                memzer.jumpToAddres(opcode);
                break;
            case 0x2000:
                memzer.callSubRoutine(opcode);
                break;
            case 0x3000:
                memzer.skipNext1(opcode);
                break;
            case 0x4000:
                memzer.skipNext2(opcode);
                break;
            case 0x5000:
                memzer.skipNext3(opcode);
                break;
            case 0x6000:
                memzer.setVxToVn(opcode);
                break;
            case 0x7000:
                memzer.addNnToVx(opcode);
                break;
            case 0x8000:
                memzer.vx8FFFF(opcode);
                break;
            case 0x9000:
                memzer.op9FFF(opcode);
                break;
            case 0xA000:
                memzer.opAFFF(opcode);
                break;
            case 0xB000:
                memzer.opBFFF(opcode);
                break;
            case 0xC000:
                memzer.opCFFF(opcode);
                break;
            case 0xD000:
               memzer.opD000(opcode);
               break;
            case 0xE000:
                memzer.opEFFF(opcode);
                break;
            case 0xF000:
                switch (opcode & 0x00FF){
                    case 0x0007:
                        memzer.opFF07(opcode);
                        break;
                    case 0x0015:
                        memzer.opFF15(opcode);
                        break;
                    case 0x0018:
                        memzer.opFF18(opcode);
                        break;
                    case 0x000A:
                        memzer.opFF0A(opcode);
                        break;
                    case 0x001E:
                        memzer.opFF1E(opcode);
                        break;
                    case 0x0029:
                        memzer.opFF29(opcode);
                        break;
                    case 0x0033:
                        memzer.opFF33(opcode);
                        break;
                    case 0x0055:
                        memzer.opFF55(opcode);
                        break;
                    case 0x0065:
                        memzer.opFF65(opcode);
                        break;
                }
                break;
            default:
                System.out.println("Unknown opcode: " + opcode);
                break;
            //todo: documenter en commentaire les opcodes
        }

    }


}
