package org.touzin8.jchip8;

public class Core {
    private final Memory memzer;
    private final DisplayPort display;
    private int X;
    private int Y;
    private int opcode;

    public static final int screen_width = 64;
    public static final int screen_heigth = 32;
    private int[][] frameBuffer = new int[screen_width][screen_heigth];

    public Core(Memory mem, DisplayPort dis){
        memzer = mem;
        display = dis;
    }

    public void cycle(){
        opcode =  memzer.fetchOpcode();
        decodeOpcode();
    }

    /////////
    public void decodeOpcode(){
        //composition du opcode
        //todo: ici c'est une fonction fetch
        ///var opcode = memory[PC] << 8 | memory[PC + 1];

        //checker quel opcode: 0xX000 avec le filtre 0xF000
        X = (opcode & 0x0F00) >> 8;
        Y = (opcode & 0x00F0) >> 4;
        //todo voici le decode qui se fera dans le core
        switch (opcode & 0xF000) {
            case 0x0000:
                // y a 2 opcode qui commencnt avec 0x0 -> 0x00E0 et 0x00EE
                switch (opcode & 0x000F){
                    case 0x0000:
                        //clear screen

                        break;
                    case 0x000E:
                        memzer.returnFromSubroutine();
                        break;
                }
                break;
            case 0x1000:
                memzer.jumpToAddres();
                break;
            case 0x2000:
                memzer.callSubRoutine();
            case 0x3000:
                memzer.skipNext1();
                break;

            case 0x4000:
                memzer.skipNext2();
                break;
            case 0x5000:
                memzer.skipNext3();
                break;
            case 0x6000:
                memzer.setVxToVn();
                break;
            case 0x7000:
                memzer.addNnToVx();
                break;
            case 0x8000:
                memzer.vx8FFFF();
                break;
            case 0x9000:
                memzer.op9FFF();
                break;
            case 0xA000:
                memzer.opAFFF();
                break;
            //TODO: continuer les opcodes BNNN
            case 0xB000:
                memzer.opBFFF();
                break;
            case 0xC000:
                memzer.opCFFF();
            case 0xD000:
                memzer.opDFFF();
            case 0xE000:
                memzer.opEFFF();
                break;
            case 0xF000:
                switch (opcode & 0x00FF){
                    case 0x0007:
                        memzer.opFF07();
                        break;
                    case 0x000A:
                        memzer.opFF0A();
                        break;
                    case 0x001E:
                        memzer.opFF1E();
                        break;
                    case 0x0029:
                        memzer.opFF29();
                        break;
                    case 0x0033:
                        memzer.opFF33();
                        break;
                    case 0x0055:
                        memzer.opFF55();
                        break;
                    case 0x0065:
                        memzer.opFF65();
                        break;
                }
            default:
                System.out.println("Unknown opcode: " + opcode);
                break;
            //todo: documenter en commentaire les opcodes
        }

    }


}
