package org.touzin8.jchip8;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.File;

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
        memzer.loadFontset();
        memzer.loadROM(romPath);

    }

    public void loadSelectedRom(File rom){
        memzer.loadROM(rom);
    }

    public void mainLoop(){
        //60 par sec
        Duration frameDuration = Duration.millis(1000.0 / 60); //64Hz
       timeline = new Timeline(new KeyFrame(frameDuration, event -> {
           if(memzer.isWaitingKey()){
                System.out.println("awaiting key");
               return;
           } //ne rien faire si il attend une touche
            for(int i = 0;i<10;i++){//sinon trop lent
                opcode =  memzer.fetchOpcode();
                display.logOpcode(opcode, memzer.getPC());
                decodeOpcode(opcode);
            }

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

        //todo voici le decode qui se fera dans le core
      System.out.println("pc: " + memzer.getPC() +" opcode: " + Integer.toHexString(opcode));
      //System.out.println("pc: " + memzer.getPC());
        switch (opcode & 0xF000) {
            case 0x0000:
                // y a 2 opcode qui commencnt avec 0x0 -> 0x00E0 et 0x00EE
                switch (opcode & 0x000F){
                    case 0x0000:
                        //clear screen
                       memzer.clearFrame();
                       System.out.println("cleeaaar");
                        //display.clear(memzer.getFrameBuffer());
                       // memzer.setDrawFlag(true);
                        memzer.setPC(memzer.getPC() + 2);
                        break;
                    case 0x000E:
                        memzer.returnFromSubroutine();
                        break;
                    default:
                        System.out.println("Unknown opcode: " + opcode);
                        memzer.setPC(memzer.getPC() + 2);
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
                    case 0x000E:
                        System.out.println("Unknown opcode:" + opcode);
                        memzer.setPC(memzer.getPC() + 2);
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
                    default:
                        System.out.println("Unknown opcode: " + opcode);
                        memzer.setPC(memzer.getPC() + 2);
                        break;
                }
                break;
            default:
                System.out.println("Unknown opcode: " + opcode);
                memzer.setPC(memzer.getPC() + 2);
                break;
            //todo: documenter en commentaire les opcodes
        }

    }


}
