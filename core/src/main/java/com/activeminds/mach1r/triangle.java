package com.activeminds.mach1r;

public class triangle {

    vertex v1, v2, v3;
    float []uv;
    float []rgb;
    boolean textured;

    public triangle()
    {

    }

    public triangle(vertex ve1, vertex ve2, vertex ve3, float u1, float v1, float u2, float v2, float u3, float v3)
    {
        this.v1 = ve1;
        this.v2 = ve2;
        this.v3 = ve3;

        uv = new float[6];
    }

    public triangle(vertex v1, vertex v2, vertex v3, float r1, float g1, float b1, float r2, float g2, float b2, float r3, float g3, float b3)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        rgb = new float[9];
    }

}
