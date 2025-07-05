package org.touzin8.jchip8;

public interface DisplayPort {

    void clear(int[][] framebuffer);

    void draw(int[][] framebuffer);

    void logOpcode(int opcode, int pc);
}
