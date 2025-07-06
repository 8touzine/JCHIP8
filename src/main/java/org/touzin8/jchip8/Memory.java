package org.touzin8.jchip8;




import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

@Getter
@Setter
public class Memory {


    byte[] memory;
    private byte[] V;
    private int IR;
    private int PC;
    private int delay_timer;
    private int sound_timer;
    private int[] stack;
    private int sp;
    private byte[] key;
    private byte[] fontset;
    private boolean waitingKey;
    private int awaitingRegister;


    public static final int screen_width = 64;
    public static final int screen_heigth = 32;
    private int[][] frameBuffer = new int[screen_width][screen_heigth];
    private boolean drawFlag;


    public Memory(){
        this.memory = new byte[4096];
        this.waitingKey = false;
        this.PC = 0x200;
        this.sp = 0;
        this.IR = 0;
        //this.opcode = 0;
        this.fontset = new Fontset().getFontset();
        this.V = new byte[16];
        this.stack = new int[16];
        this.key = new byte[16];
        this.drawFlag = false;
    }

    public void loadFontset(){
        loadToMemory(fontset, 0);
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
    public void loadROM(File file){
        this.memory = new byte[4096];
        this.waitingKey = false;
        this.PC = 0x200;
        this.sp = 0;
        this.IR = 0;
        //this.opcode = 0;
        this.fontset = new Fontset().getFontset();
        this.V = new byte[16];
        this.stack = new int[16];
        this.key = new byte[16];
        this.drawFlag = false;
        File readROM = file;
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
        int low = memory[PC+1] & 0xFF;
        return (high << 8) | low;
    }

    public void opFF65(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        for(var i = 0; i <= X;i++){
            V[i] = memory[IR + i];
        }
        PC +=2;
    }

    public void opFF55(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        for(var i = 0; i <= X;i++){
            memory[IR + i] = V[i];
        }
        PC +=2;
    }

    private int detectCollision(int x, int y){
        //wrap
        x &= 0x3F;// si 67 alors -> 3 ça va dessiner de l'autre coté (wrap)
        y &= 0x1F;
        int previous = frameBuffer[x][y]; // le pixel d'avant dans le buffer
        // le XOR pour checker les collision (1 ^ 1 = 0)
        frameBuffer[x][y] ^= 1; //on applique le XOR avec 1, chaque 0 etait un pixel deja allumé -> colili
        //on dessine a la ligne precedente, on verifie avec le XOR si y avait deja des pixel
        return (previous == 1 && frameBuffer[x][y] == 0) ? 1 : 0;// si XOR  (1) alors un pixel a changé donc collision
        // on detecte si le pixel a été eteind du a une collsion
    }

    public int[][] opD000(int opcode){

        int x = V[(opcode & 0x0F00) >> 8] & 0xFF; //ici on pioche les pixel a allumer x et y et n hauteur
        int y = V[(opcode & 0x00F0) >> 4] & 0xFF;
        int n  = (opcode & 0x000F);
        int[][] buffer = frameBuffer;
        V[0xF] = 0;
        System.out.println("Draw at x= " +x + "y= " + y +" n= " + n + "  IR= " + IR);
        for(var row = 0; row < n; row++){ //la hauteur
            var spriteByte = memory[IR + row] & 0xFF; // IR ou on se trouve( opcode)
            // exemple de sprite (row) : Sprite : 0b11110000  (donc 4 pixels allumés à gauche)
            for(var bit = 0;bit < 8; bit++){
                if((spriteByte & (0x80 >> bit)) != 0){
                    if(detectCollision(x+bit, y+row) == 1){
                        V[0xF] = 1;// collision detecton flag allumeyyy
                    }
                }
            }
        }
        drawFlag = true;
        PC += 2;
        return buffer;
    }

    public void opFF33(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        memory[IR] = (byte)((V[X] & 0xFF) / 100);
        memory[IR + 1] = (byte)(((V[X] & 0xFF) / 10) % 10); // par ex 15 % 10 = 5
        memory[IR + 2] = (byte)((V[X] & 0xFF) % 10);
        PC +=2;
    }

    public void opFF29(int opcode) {
        //load sprit address dans IR (5 pour le 5 bytes de sprite (fontset)
        //0x50 80 -> adress de base ou les fontset sont stocké
        var X = (opcode & 0x0F00) >>8;
        IR = 0x50 + (V[X] & 0xFF) * 5;
        PC +=2;
    }

    public void opFF1E(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        IR = (IR + (V[X] & 0x00FF)) & 0xFFF ;
        PC +=2;
    }

    public void opFF0A(int opcode) {
        //attendre qu'une key soit pressé
        System.out.println("FFOA!!!!!!!!!!");
        var X = (opcode & 0x0F00) >>8;
        waitingKey = true;
        awaitingRegister = X; // pour indiquer ou est-ce qu'on attend le key
    }

    public void setKey(int index, boolean pressed){
        //pour lire les touches, et utilisé si besoin (awaiting)
        System.out.println("index: " + index + "pressed: " + pressed);
        key[index] = (byte)(pressed ? 1 : 0);//donc si la touche a cette index est pressé, 1
        if(waitingKey && pressed){
            V[awaitingRegister] = (byte)index;
            waitingKey = false;
            PC +=2;
        }
    }
    public void opFF07(int opcode){
        var X = (opcode & 0x0F00) >>8;
        V[X] = (byte)delay_timer;
        PC +=2;
    }
    public void opFF15(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        delay_timer = V[X] & 0xFF;
        PC +=2;
    }
    public void opFF18(int opcode) {
        var X = (opcode & 0x0F00) >>8;
        sound_timer = V[X] & 0xFF;
        PC +=2;
    }

    public void tickTimers(){
        if (delay_timer > 0){
            delay_timer--;
        }
        if(sound_timer > 0){
            sound_timer--;
            if(sound_timer == 0){
                beeping();
            }
        }
    }
    private void beeping(){
        java.awt.Toolkit.getDefaultToolkit().beep();
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

    public void opCFFF(int opcode) {
        var X = (opcode & 0x0F00) >> 8;
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
        int NN = opcode & 0x00FF;
        V[X] = (byte)((V[X] & 0xFF) + NN);
        PC += 2;
    }

    public void setVxToVn(int opcode) {
        //0x6000
        var X = (opcode & 0x0F00) >> 8;
        var NN = opcode & 0x00FF;
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
        stack[sp] = PC + 2;// pour y revenir si on appelle 0xA000
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

    public void clearFrame() {
        for(int x=0; x<screen_width; x++) {
            Arrays.fill(frameBuffer[x], 0);
        }
        drawFlag= true;
    }
}
