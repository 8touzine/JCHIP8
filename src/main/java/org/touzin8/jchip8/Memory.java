package org.touzin8.jchip8;




import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Getter
@Setter
public class Memory {

    /*
     *
     * CHIP-8 was most commonly implemented on 4K systems, such as the Cosmac VIP and the Telmac 1800.
     * These machines had 4096 (0x1000) memory locations, all of which are 8 bits (a byte) which
     * is where the term CHIP-8 originated.
     * */

    /*
    * The systems memory map:
    0x000-0x1FF - Chip 8 interpreter (contains font set in emu)
    0x050-0x0A0 - Used for the built in 4x5 pixel font set (0-F)
    0x200-0xFFF - Program ROM and work RAM
    * */

    /*
     * The Chip 8 has 35 opcodes which are all two bytes long. To store the current opcode,
     * we need a data type that allows us to store two bytes. An unsigned short has the length
     * of two bytes and therefor fits our needs:
     *
     *
     * char : 2 bytes
     * */

  //  private int opcode;

    //The Chip 8 has 4K memory in total, which we can emulated as
    byte[] memory;

    /*
     * CPU registers: The Chip 8 has 15 8-bit general purpose registers named V0,V1 up to VE.
     * The 16th register is used for the ‘carry flag’. Eight bits is one byte so we
     * can use bytes for this purpose:
     * */
    private byte[] V;

    /*
     * There is an Index register I and a program counter (pc) which can have a value from 0x000 to 0xFFF
     * j'utilise int, c'est pas grave d'utiliser plus de memoire que necessaire
     * */
    private int IR;
    private int PC;

    /*
     * Interupts and hardware registers. The Chip 8 has none, but there are two timer registers that count
     * at 60 Hz. When set above zero they will count down to zero.
     *
     * The system’s buzzer sounds whenever the sound timer reaches zero.
     * */

    private int delay_timer;
    private int sound_timer;


    /*
     * It is important to know that the Chip 8 instruction set has opcodes that allow the program to jump to a
     *  certain address or call a subroutine. While the specification doesn’t mention a stack, you will need to
     * implement one as part of the interpreter yourself. The stack is used to remember the current location before
     * a jump is performed. So anytime you perform a jump or call a subroutine, store the program counter in the stack
     * before proceeding. The system has 16 levels of stack and in order to remember which level of the stack is used,
     * you need to implement a stack pointer (sp).
     *
     * */
    private int[] stack;
    private int sp;

    /*
     * Finally, the Chip 8 has a HEX based keypad (0x0-0xF), you can use an array to store the current state of the key.
     * */
    private byte[] key;
    private byte[] fontset;

    private boolean waitingKey = false;
    private int awaitingRegister;

    //int X;
    //int Y;
    //int NN;

    public static final int screen_width = 64;
    public static final int screen_heigth = 32;
    private int[][] frameBuffer = new int[screen_width][screen_heigth];
    private Display display;


    public Memory(){
        this.memory = new byte[4096];
        this.PC = 0x200;
        this.sp = 0;
        this.IR = 0;
        //this.opcode = 0;
        this.fontset = new Fontset().getFontset();//j'ai sync et j'ai mis le plugin lombok
        this.V = new byte[16];
        this.stack = new int[16];
        this.key = new byte[16];
    }

    public void loadFontset(){
        loadToMemory(fontset, 0);
    }

    public void loadDisplay(Display display){
        this.display = display;
    }



    public void loadROM(String path){
        File readROM = new File(path);
        byte[] buffer;
        try {
            FileInputStream fis = new FileInputStream(readROM);
            buffer = fis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadToMemory(buffer, 0x200);// 0x200-0xFFF - Program ROM and work RAM, 0x200 -> 512
    }

    public void loadToMemory(byte[] collect, int startIndex) {
//        for(var i = startIndex; i < fontset.length; i++){
//            memory[i] = fontset[i];//        }

        for(int i = 0;i < collect.length;i++){
            memory[i + startIndex] = collect[i];
        }

    }

    public int fetchOpcode(){
        int high = memory[PC] & 0xFF;
        int low = memory[PC] & 0xFF;
        return (high << 8) | low;
    }
    //todo: convertir en fonction par opcode et deplacer le switch dans le core


    public void opFF65(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        for(var i = IR; i <= X;i++){
            V[i] = memory[i];
        }
    }

    public void opFF55(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        for(var i = IR; i <= X;i++){
            memory[i] = V[i];
        }
    }

    public int[][] opD000(int opcode){

        int[][] buffer = frameBuffer;
        //todo impiment DXYN pour affichier la sprint a (X,Y) et a N pixel d'hauteur (framebuffer dans onenote)
        V[0xF] = 0; //todo comprendre

        return buffer;
    }

    public void opFF33(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        memory[IR] = (byte)((V[X] & 0xFF) / 100);
        memory[IR + 1] = (byte)(((V[X] & 0xFF) / 10) % 10); // par ex 15 % 10 = 5
        memory[IR + 1] = (byte)((V[X] & 0xFF) % 10);
    }

    public void opFF29(int opcode) {
        //load sprit address dans IR (5 pour le 5 bytes de sprite (fontset)
        //0x50 80 -> adress de base ou les fontset sont stocké
        var X = (opcode & 0x0F00) >>8;
        IR = 0x50 + (V[X] & 0xFF) * 5;
    }

    public void opFF1E(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        IR = V[X];
    }

    public void opFF0A(int opcode) {
        //attendre qu'une key soit pressé
        var X = (opcode & 0x0F00) >>8;
        waitingKey = true;
        awaitingRegister = X; // pour indiquer ou est-ce qu'on attend le key
        //todo: Implimenter système de lecture de key: gestion de waitingKey et awaitingRegister
    }

    public void opFF07(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        delay_timer = V[X];
    }

    public void opEFFF(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        switch (opcode & 0x00FF){
            case 0x9E:
                if(key[V[X] & 0xFF] != 0){
                    PC += 4;
                    break;
                }
                PC +=2;
                break;
            case 0xA1:
                if(key[V[X] & 0xFF] == 0){
                    PC += 4;
                    break;
                }
                PC +=2;
                break;
        }
    }

    public void opDFFF() {
        //javafx ou jpanel jframe
        //displayPort draw()
        PC += 2;
    }

    public void opCFFF(int opcode) {
        var X = (opcode & 0x0F00) >> 8;
        // =(int)(Math.random() * 256)
        //int car & fonctionne  pas sur un double (math.random() envoi un double
        V[X] = (byte)((int)(Math.random() * 0xFF) & (opcode & 0x00FF));
        PC += 2;
    }

    public void opBFFF(int opcode) {
        //PC = V0 + NNN
        PC = V[0] + (opcode & 0x0FFF);
    }

    public void opAFFF(int opcode) {
        // on assigne la valeur de 0x0NNN à l'index register
        IR = opcode & 0x0FFF;
        PC += 2; //vu qu'on a utiliser 2 positions en un
    }

    public void op9FFF(int opcode) {
        if(V[(opcode & 0x0F00) >> 8] != V[(opcode & 0x00F0) >> 4] ){
            PC += 4;
            return;
        }
        PC += 2;
    }

    public void vx8FFFF(int opcode) {
        var X = (opcode & 0x0F00) >> 8;
        var Y = (opcode & 0x00F0) >> 4;
        switch (opcode & 0x000F){
            //8XY1, 8XY2, 8XY3, 8XY4, 8XY5, 8XY6, 8XY7, 8XYE
            case 0x0000:
                V[X] = V[Y];
                break;
            case 0x0001:
                V[X] |= V[Y];
                break;
            case 0x0002:
                V[X] &= V[Y];
                break;
            case 0x0003:
                V[X] ^= V[Y];
                break;
            case 0x0004:
                var sum = (V[X] & 0xFF) + (V[Y] & 0xFF);
                V[0xF] = (byte)(sum > 255 ? 1 : 0);
                V[X] = (byte)sum;
                break;
            case 0x0005:
                V[0xF] = (byte)((V[X] & 0xFF) >= (V[Y] & 0xFF) ? 1 : 0);
                var sub = (V[X] & 0xFF) - (V[Y] & 0xFF);
                V[X] = (byte)sub;
                break;
            case 0x0006:
                V[0xF] = (byte)(V[X] & 0x1);
                V[X] = (byte)((V[X] & 0xFF) >> 1);
                break;
            case 0x0007:
                V[0xF] = (byte)((V[Y] & 0xFF) >= (V[X] & 0xFF) ? 1 : 0);
                V[X] = (byte)((V[Y] & 0xFF) - (V[X] & 0xFF));
                break;
            case 0x000E:
                V[0xF] = (byte)((V[X] & 0x80) >> 7); //sauve le chiffre le plus gros (qui sera degagé a la
                //prochaine ligne:
                V[X] = (byte)((V[Y] & 0xFF) << 1);
                break;
        }
        PC += 2; //prochain  opcode
    }

    public void addNnToVx(int opcode) {
        //0x7000
        var X = (opcode & 0x0F00) >> 8;
        var NN = (opcode & 0x00FF) >> 4;
        V[X] += (byte)NN;
        PC += 2;
    }

    public void setVxToVn(int opcode) {
        //0x6000
        var X = (opcode & 0x0F00) >> 8;
        var NN = (opcode & 0x00FF) >> 4;
        V[X] = (byte)NN; // convertir en byte
        PC += 2;
    }

    public void skipNext3(int opcode) {
        //0x5000
        var X = (opcode & 0x0F00) >>8;
        var Y = (opcode & 0x00F0) >> 4;
        if(V[X] == V[Y]){
            PC += 4;
            return;
        }
        PC += 2;
    }

    public void skipNext2(int opcode) {
        //0x4000
       var X = (opcode & 0x0F00) >> 8;
       var NN = opcode & 0x00FF;
        if(V[X] != NN){
            PC += 4;
            return;
        }
        PC += 2;
    }

    public void skipNext1(int opcode) {
        //0x3000
        /*
         * if (Vx == NN)Skips the next instruction if VX equals NN
         * (usually the next instruction is a jump to skip a code block).
         *
         * */
       var  X = (opcode & 0x0F00) >> 8;
       var  NN = opcode & 0x00FF;
        //on next l'opcode prochain, un vu qu'un opcode = 2 bytes -> 4
        if(V[X] == NN){
            PC += 4;
            return;
        }
        PC += 2;
    }

    public void callSubRoutine(int opcode) {
        //0x2000
        //appeller une subroutine
        stack[sp] = PC;// pour y revenir si on appelle 0xA000
        sp++;
        PC = opcode & 0x0FFF; // on assigne la valeur de NNN du 0x2NNN au PC
    }

    public void jumpToAddres(int opcode) {
        //0x1000
        //on saut a l'adress NNN, rappel: PC -> program counter, ou on se trouve dans le programme
        PC = opcode & 0x0FFF;
    }

    public void returnFromSubroutine() {
        //return d'un subroutine
        //0x000E
        sp--;// on revient en arrière vu qu'on a assigné le PC a ça initialement
        PC = stack[sp];
    }

}
