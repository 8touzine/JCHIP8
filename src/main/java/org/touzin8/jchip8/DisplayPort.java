package org.touzin8.jchip8;

public interface DisplayPort {


    void draw(int[][] framebuffer);

    void logOpcode(int opcode, int pc);
}
