package com.activeminds.mach1r;

import com.badlogic.gdx.math.Vector3;

public class vertex {
    float x, y , z;
    Vector3 sumNormals;
    int numNormals;

    public vertex() {
        sumNormals = new Vector3(0,0,0);
        numNormals = 0;
    }

    public vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;

        sumNormals = new Vector3(0,0,0);
        numNormals = 0;
    }

    public void addNormal(Vector3 n)
    {
        sumNormals.add(n);
        numNormals++;
    }

    public void gouraudNormal()
    {
        sumNormals.x /= numNormals;
        sumNormals.y /= numNormals;
        sumNormals.z /= numNormals;
    }
}
