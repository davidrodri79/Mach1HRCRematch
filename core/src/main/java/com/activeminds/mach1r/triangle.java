package com.activeminds.mach1r;

public class triangle {

    vertex v1, v2, v3;
    float []uv;
    float []rgb;
    int textureId;

    public triangle()
    {

    }

    // Con textura
    public triangle(vertex ve1, vertex ve2, vertex ve3, int textureId, float u1, float v1, float u2, float v2, float u3, float v3)
    {
        this.v1 = ve1;
        this.v2 = ve2;
        this.v3 = ve3;

        this.textureId = textureId;

        uv = new float[6];
        rgb = new float[9];

        uv[0] = u1;
        uv[1] = 1f - v1;
        uv[2] = u2;
        uv[3] = 1f - v2;
        uv[4] = u3;
        uv[5] = 1f - v3;

        rgb[0] = 1f;
        rgb[1] = 1f;
        rgb[2] = 1f;
        rgb[3] = 1f;
        rgb[4] = 1f;
        rgb[5] = 1f;
        rgb[6] = 1f;
        rgb[7] = 1f;
        rgb[8] = 1f;
    }

    // Sin textura
    public triangle(vertex v1, vertex v2, vertex v3, float r1, float g1, float b1, float r2, float g2, float b2, float r3, float g3, float b3)
    {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;

        this.textureId = -1;

        uv = new float[6];
        rgb = new float[9];

        uv[0] = 0f;
        uv[1] = 0f;
        uv[2] = 0f;
        uv[3] = 0f;
        uv[4] = 0f;
        uv[5] = 0f;

        rgb[0] = r1;
        rgb[1] = g1;
        rgb[2] = b1;
        rgb[3] = r2;
        rgb[4] = g2;
        rgb[5] = b2;
        rgb[6] = r3;
        rgb[7] = g3;
        rgb[8] = b3;

    }

}
