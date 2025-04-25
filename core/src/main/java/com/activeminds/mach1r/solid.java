package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class solid {

    ArrayList<vertex> vertexs;
    ArrayList<String> textures;
    ArrayList<triangle> triangles;

    public solid()
    {
    }

    boolean load_mesh(String file)
    {
        FileHandle f = Gdx.files.internal(file);
        InputStream inputStream = f.read();
        if (inputStream == null) return false;
        DataInputStream dis = new DataInputStream(inputStream);

        try {

            // Header
            String header = readCPlusString(dis, 25);

            if(!header.equals("MESH Binary File NHSP/001"))
                return false;

            // Vertexs
            int numVertex = readCPlusInt(dis);

            vertexs = new ArrayList<>();

            for(int i = 0; i < numVertex; i++)
            {
                float x = readCPlusFloat(dis);
                float y = readCPlusFloat(dis);
                float z = readCPlusFloat(dis);

                vertexs.add(new vertex(x, y, z));
            }

            // Vertexs
            int numTextures = readCPlusInt(dis);

            textures = new ArrayList<>();

            for(int i = 0; i < numTextures; i++)
            {
                String texFile = readCPlusString(dis, 40);
                textures.add(texFile);
            }

            // Triangles
            int numTris = readCPlusInt(dis);
            triangles = new ArrayList<>();
            for(int i = 0; i < numTris; i++)
            {
                int v1 = readCPlusWord(dis);
                int v2 = readCPlusWord(dis);
                int v3 = readCPlusWord(dis);
                boolean textured = readCPlusBool(dis);

                if(textured)
                {
                    int num = readCPlusByte(dis);
                    float tx1 = readCPlusFloat(dis);
                    float tx2 = readCPlusFloat(dis);
                    float tx3 = readCPlusFloat(dis);
                    float ty1 = readCPlusFloat(dis);
                    float ty2 = readCPlusFloat(dis);
                    float ty3 = readCPlusFloat(dis);

                    triangles.add(new triangle(vertexs.get(v1), vertexs.get(v2), vertexs.get(v3), tx1, ty1, tx2, ty2, tx3, ty3));
                }
                else
                {
                    float r1 = readCPlusFloat(dis);
                    float g1 = readCPlusFloat(dis);
                    float b1 = readCPlusFloat(dis);
                    float r2 = readCPlusFloat(dis);
                    float g2 = readCPlusFloat(dis);
                    float b2 = readCPlusFloat(dis);
                    float r3 = readCPlusFloat(dis);
                    float g3 = readCPlusFloat(dis);
                    float b3 = readCPlusFloat(dis);

                    triangles.add(new triangle(vertexs.get(v1), vertexs.get(v2), vertexs.get(v3), r1, g1, b1, r2, g2, b2, r3, g3, b3));

                }
            }


            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String readCPlusString(DataInputStream dis, int size) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < size; i++)
        {
            char c = (char)dis.readByte();
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private float readCPlusFloat(DataInputStream dis) throws IOException {
        byte[] floatBytes = new byte[4];
        dis.readFully(floatBytes);
        float a = ByteBuffer.wrap(floatBytes)
            .order(ByteOrder.LITTLE_ENDIAN) // importante: C++ suele usar little endian
            .getFloat();

        return a;
    }

    private int readCPlusInt(DataInputStream dis) throws IOException {
        // Leer int (4 bytes)
        byte[] intBytes = new byte[4];
        dis.readFully(intBytes);
        int b = ByteBuffer.wrap(intBytes)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt();
        return b;
    }

    private int readCPlusByte(DataInputStream dis) throws IOException {
        return dis.readUnsignedByte();
    }

    private int readCPlusWord(DataInputStream dis) throws IOException {
        byte[] wordBytes = new byte[2];
        dis.readFully(wordBytes);
        int word = ByteBuffer.wrap(wordBytes)
            .order(ByteOrder.LITTLE_ENDIAN) // Importante si viene de C++
            .getShort() & 0xFFFF; // Convertir a unsigned
        return word;
    }

    private boolean readCPlusBool(DataInputStream dis) throws IOException {
        byte[] boolBytes = new byte[4];
        dis.readFully(boolBytes);
        int boolValue = ByteBuffer.wrap(boolBytes)
            .order(ByteOrder.LITTLE_ENDIAN)
            .getInt();
        boolean result = (boolValue != 0); // true si es distinto de 0
        return result;
    }
}
