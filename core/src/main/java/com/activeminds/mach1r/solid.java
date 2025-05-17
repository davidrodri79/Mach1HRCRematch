package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class solid {

    ArrayList<vertex> vertexs;
    ArrayList<String> textureFiles;
    ArrayList<triangle> triangles;

    vertex vmin, vmax, vcenter;
    float []scale=new float[3], col_coef = new float[3];

    Mesh mesh;
    Texture[] textures;
    boolean gouraud = false;

    public solid()
    {
        vmin = new vertex(1000,1000,1000);
        vmax = new vertex(-1000,-1000,-1000);
        vcenter = new vertex(0,0,0);
        scale[0] = 1f; scale[1] = 1f; scale[2] = 1f;
        col_coef[0] = 1f; col_coef[1] = 1f; col_coef[2] = 1f;
    }

    boolean load_mesh(String file, boolean gouraud)
    {
        this.gouraud = gouraud;

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

                if(x < vmin.x) vmin.x = x;
                if(y < vmin.y) vmin.y = y;
                if(z < vmin.z) vmin.z = z;

                if(x > vmax.x) vmax.x = x;
                if(y > vmax.y) vmax.y = y;
                if(z > vmax.z) vmax.z = z;
            }

            vcenter.x = (vmin.x + vmax.x) / 2f;
            vcenter.y = (vmin.y + vmax.y) / 2f;
            vcenter.z = (vmin.z + vmax.z) / 2f;

            // Vertexs
            int numTextures = readCPlusInt(dis);

            textureFiles = new ArrayList<>();

            for(int i = 0; i < numTextures; i++)
            {
                String texFile = readCPlusString(dis, 40);
                textureFiles.add(texFile);
            }

            textures = new Texture[textureFiles.size()];
            for(int i = 0; i < textureFiles.size(); i++)
            {
                textures[i] = new Texture(textureFiles.get(i).toLowerCase()+".bmp");
                textures[i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                textures[i].setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
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

                triangle tri;
                if(textured)
                {
                    int num = readCPlusByte(dis);
                    float tx1 = readCPlusFloat(dis);
                    float tx2 = readCPlusFloat(dis);
                    float tx3 = readCPlusFloat(dis);
                    float ty1 = readCPlusFloat(dis);
                    float ty2 = readCPlusFloat(dis);
                    float ty3 = readCPlusFloat(dis);

                    tri = new triangle(vertexs.get(v1), vertexs.get(v2), vertexs.get(v3), num, tx1, ty1, tx2, ty2, tx3, ty3);
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

                    tri = new triangle(vertexs.get(v1), vertexs.get(v2), vertexs.get(v3), r1, g1, b1, r2, g2, b2, r3, g3, b3);

                }

                tri.do_normal();
                triangles.add(tri);
            }

            if(gouraud)
            {
                for(int i = 0; i < triangles.size(); i++)
                {
                    triangle tri = triangles.get(i);
                    tri.v1.addNormal(tri.normal);
                    tri.v2.addNormal(tri.normal);
                    tri.v3.addNormal(tri.normal);
                }

                for(int i = 0; i < vertexs.size(); i++)
                {
                    vertex v = vertexs.get(i);
                    v.gouraudNormal();
                }
            }

            buildGdxMesh();

            return true;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void set_scale(float x, float y, float z)
    {
        scale[0] = x; scale[1] = x; scale[2] = z;
    }
    void centrate(boolean x, boolean y, boolean z)
    {
        for(int i = 0; i < vertexs.size(); i++)
        {
            vertex v = vertexs.get(i);
            if(x) v.x-=vcenter.x;
            if(y) v.y-=vcenter.y;
            if(z) v.z-=vcenter.z;
            vertexs.set(i, v);
        }

        buildGdxMesh();
    }

    void buildGdxMesh()
    {
        float[] vertices = new float[triangles.size() * 39];
        short[] indices = new short[triangles.size() * 3];

        for(int i = 0; i < triangles.size(); i++)
        {
            vertices[39 * i]        = triangles.get(i).v1.x;
            vertices[39 * i + 1]    = triangles.get(i).v1.y;
            vertices[39 * i + 2]    = triangles.get(i).v1.z;
            if(gouraud)
            {
                vertices[39 * i + 3]    = triangles.get(i).v1.sumNormals.x;
                vertices[39 * i + 4]    = triangles.get(i).v1.sumNormals.y;
                vertices[39 * i + 5]    = triangles.get(i).v1.sumNormals.z;
            }
            else
            {
                vertices[39 * i + 3]    = triangles.get(i).normal.x;
                vertices[39 * i + 4]    = triangles.get(i).normal.y;
                vertices[39 * i + 5]    = triangles.get(i).normal.z;
            }
            vertices[39 * i + 6]    = triangles.get(i).rgb[0];
            vertices[39 * i + 7]    = triangles.get(i).rgb[1];
            vertices[39 * i + 8]    = triangles.get(i).rgb[2];
            vertices[39 * i + 9]    = 1f;
            vertices[39 * i + 10]   = triangles.get(i).uv[0];
            vertices[39 * i + 11]   = triangles.get(i).uv[1];
            vertices[39 * i + 12]   = triangles.get(i).textureId;

            vertices[39 * i + 13]   = triangles.get(i).v2.x;
            vertices[39 * i + 14]   = triangles.get(i).v2.y;
            vertices[39 * i + 15]   = triangles.get(i).v2.z;
            if(gouraud)
            {
                vertices[39 * i + 16]    = triangles.get(i).v2.sumNormals.x;
                vertices[39 * i + 17]    = triangles.get(i).v2.sumNormals.y;
                vertices[39 * i + 18]    = triangles.get(i).v2.sumNormals.z;
            }
            else
            {
                vertices[39 * i + 16] = triangles.get(i).normal.x;
                vertices[39 * i + 17] = triangles.get(i).normal.y;
                vertices[39 * i + 18] = triangles.get(i).normal.z;
            }
            vertices[39 * i + 19]   = triangles.get(i).rgb[3];
            vertices[39 * i + 20]   = triangles.get(i).rgb[4];
            vertices[39 * i + 21]   = triangles.get(i).rgb[5];
            vertices[39 * i + 22]   = 1f;
            vertices[39 * i + 23]   = triangles.get(i).uv[2];
            vertices[39 * i + 24]   = triangles.get(i).uv[3];
            vertices[39 * i + 25]   = triangles.get(i).textureId;

            vertices[39 * i + 26]   = triangles.get(i).v3.x;
            vertices[39 * i + 27]   = triangles.get(i).v3.y;
            vertices[39 * i + 28]   = triangles.get(i).v3.z;
            if(gouraud)
            {
                vertices[39 * i + 29]    = triangles.get(i).v3.sumNormals.x;
                vertices[39 * i + 30]    = triangles.get(i).v3.sumNormals.y;
                vertices[39 * i + 31]    = triangles.get(i).v3.sumNormals.z;
            }
            else
            {
                vertices[39 * i + 29] = triangles.get(i).normal.x;
                vertices[39 * i + 30] = triangles.get(i).normal.y;
                vertices[39 * i + 31] = triangles.get(i).normal.z;
            }
            vertices[39 * i + 32]   = triangles.get(i).rgb[6];
            vertices[39 * i + 33]   = triangles.get(i).rgb[7];
            vertices[39 * i + 34]   = triangles.get(i).rgb[8];
            vertices[39 * i + 35]   = 1f;
            vertices[39 * i + 36]   = triangles.get(i).uv[4];
            vertices[39 * i + 37]   = triangles.get(i).uv[5];
            vertices[39 * i + 38]   = triangles.get(i).textureId;

            indices[3*i] = (short)(3 * i);
            indices[3*i + 1] = (short)(3 * i + 1);
            indices[3*i + 2] = (short)(3 * i + 2);
        }

/*
        float[] vertices_cubo = new float[] {
            // --- Cara frontal ---
            -1, -1,  1,  0, 0,
            1, -1,  1,  1, 0,
            1,  1,  1,  1, 1,
            -1,  1,  1,  0, 1,

            // --- Cara trasera ---
            1, -1, -1,  0, 0,
            -1, -1, -1,  1, 0,
            -1,  1, -1,  1, 1,
            1,  1, -1,  0, 1,

            // --- Cara izquierda ---
            -1, -1, -1,  0, 0,
            -1, -1,  1,  1, 0,
            -1,  1,  1,  1, 1,
            -1,  1, -1,  0, 1,

            // --- Cara derecha ---
            1, -1,  1,  0, 0,
            1, -1, -1,  1, 0,
            1,  1, -1,  1, 1,
            1,  1,  1,  0, 1,

            // --- Cara superior ---
            -1,  1,  1,  0, 0,
            1,  1,  1,  1, 0,
            1,  1, -1,  1, 1,
            -1,  1, -1,  0, 1,

            // --- Cara inferior ---
            -1, -1, -1,  0, 0,
            1, -1, -1,  1, 0,
            1, -1,  1,  1, 1,
            -1, -1,  1,  0, 1,
        };

        short[] indices_cubo = new short[] {
            0, 1, 2, 2, 3, 0,       // frontal
            4, 5, 6, 6, 7, 4,       // trasera
            8, 9,10,10,11, 8,       // izquierda
            12,13,14,14,15,12,       // derecha
            16,17,18,18,19,16,       // arriba
            20,21,22,22,23,20        // abajo
        };*/

        /*mesh = new Mesh(true, vertices.length / 5, indices.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );*/

        mesh = new Mesh(true, vertices.length / 13, indices.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"),
            new VertexAttribute(VertexAttributes.Usage.ColorUnpacked, 4, "a_color"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0"),
            new VertexAttribute(VertexAttributes.Usage.Generic, 1, "a_textureID")
        );

        mesh.setVertices(vertices);
        mesh.setIndices(indices);
    }

    void addQuad(vertex ve1, vertex ve2, vertex ve3, vertex ve4, int texId, float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4)
    {
        triangles.add( new triangle(ve1, ve2, ve3, texId, u1, v1, u2, v2, u3, v3));
        triangles.add( new triangle(ve3, ve4, ve1, texId, u3, v3, u4, v4, u1, v1));
    }

    void addQuadFromStripe(vertex ve1, vertex ve2, vertex ve3, vertex ve4, int texId, float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4)
    {
        triangles.add( new triangle(ve1, ve2, ve3, texId, u1, v1, u2, v2, u3, v3));
        triangles.add( new triangle(ve2, ve4, ve3, texId, u2, v2, u4, v4, u3, v3));
    }

    void addQuadFromStripeDoubleSided(vertex ve1, vertex ve2, vertex ve3, vertex ve4, int texId, float u1, float v1, float u2, float v2, float u3, float v3, float u4, float v4)
    {
        triangles.add( new triangle(ve1, ve2, ve3, texId, u1, v1, u2, v2, u3, v3));
        triangles.add( new triangle(ve1, ve3, ve2, texId, u1, v1, u3, v3, u2, v2));
        triangles.add( new triangle(ve2, ve4, ve3, texId, u2, v2, u4, v4, u3, v3));
        triangles.add( new triangle(ve2, ve3, ve4, texId, u2, v2, u3, v3, u4, v4));
    }

    void addQuadFromStripe(vertex ve1, vertex ve2, vertex ve3, vertex ve4, float r, float g, float b)
    {
        triangles.add( new triangle(ve1, ve2, ve3, r, g, b, r, g, b, r, g, b));
        triangles.add( new triangle(ve2, ve4, ve3, r, g, b, r, g, b, r, g, b));

    }

    void render(ShaderProgram shader, PerspectiveCamera camera, float px, float py, float pz, float rx, float ry, float rz)
    {
        Quaternion qx = new Quaternion();
        Quaternion qy = new Quaternion();
        Quaternion qz = new Quaternion();

        qx.setEulerAnglesRad(0, 0, rx);
        qy.setEulerAnglesRad(ry, 0, 0);
        qz.setEulerAnglesRad(0, rz, 0);

        Quaternion combined = qx.mul(qy).mul(qz);
        Matrix4 rot = new Matrix4().set(combined);



        Matrix4 model = new Matrix4().idt().translate(px,py,pz)
                                            .mul(rot)
                                            .scale( scale[0], scale[1], scale[2]);
        Matrix4 view = camera.view;
        Matrix4 proj = camera.projection;
        Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);

        shader.begin();
        shader.setUniformMatrix("u_mvp", MVP);
        shader.setUniformMatrix("u_model", model);
        shader.setUniformf("u_alpha", 1f);
        shader.setUniformi("u_textures[0]", 0);
        shader.setUniformi("u_textures[1]", 1);
        shader.setUniformi("u_textures[2]", 2); // Dummy (no usamos)
        shader.setUniformi("u_textures[3]", 3); // Dummy (no usamos)
        shader.setUniformi("u_textures[4]", 4); // Dummy (no usamos)
        shader.setUniformi("u_textures[5]", 5); // Dummy (no usamos)
        shader.setUniformf("u_colorCoef", col_coef[0], col_coef[1], col_coef[2]);
        shader.setUniformi("u_shadowMap", 6);

        for(int i = 0; i < textures.length; i++)
            textures[i].bind(i);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();

    }

    void alpha_render(ShaderProgram shader, PerspectiveCamera camera, float px, float py, float pz, float rx, float ry, float rz, float alpha)
    {
        Quaternion qx = new Quaternion();
        Quaternion qy = new Quaternion();
        Quaternion qz = new Quaternion();

        qx.setEulerAnglesRad(0, 0, rx);
        qy.setEulerAnglesRad(ry, 0, 0);
        qz.setEulerAnglesRad(0, rz, 0);

        Quaternion combined = qx.mul(qy).mul(qz);
        Matrix4 rot = new Matrix4().set(combined);

        Matrix4 model = new Matrix4().idt().translate(px,py,pz)
            .mul(rot)
            .scale( scale[0], scale[1], scale[2]);
        Matrix4 view = camera.view;
        Matrix4 proj = camera.projection;
        Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);

        shader.begin();
        shader.setUniformMatrix("u_mvp", MVP);
        shader.setUniformMatrix("u_model", model);
        shader.setUniformf("u_alpha", alpha);
        shader.setUniformi("u_textures[0]", 0);
        shader.setUniformi("u_textures[1]", 1);
        shader.setUniformi("u_textures[2]", 2); // Dummy (no usamos)
        shader.setUniformi("u_textures[3]", 3); // Dummy (no usamos)
        shader.setUniformi("u_textures[4]", 4); // Dummy (no usamos)
        shader.setUniformi("u_textures[5]", 5); // Dummy (no usamos)

        for(int i = 0; i < textures.length; i++)
            textures[i].bind(i);
        mesh.render(shader, GL20.GL_TRIANGLES);
        shader.end();

    }

    private String readCPlusString(DataInputStream dis, int size) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        boolean endReached = false;
        for(int i = 0; i < size; i++)
        {
            char c = (char)dis.readByte();
            if(c == '\0') endReached = true;
            if(!endReached) stringBuilder.append(c);
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

    public void set_color_coef(float r, float g, float b) {

        col_coef[0] = r;
        col_coef[1] = g;
        col_coef[2] = b;
    }
}
