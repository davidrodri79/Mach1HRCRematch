package com.activeminds.mach1r;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class sprite {

    Texture texture;

    public sprite()
    {

    }

    public sprite(float x, float y, String tfile)
    {
        texture = new Texture(tfile);
    }

    void render2d(SpriteBatch batch, float ox, float oy, float ex, float ey, int px, int py, int pex, int pey, float a)
    {
        batch.setColor(1, 1, 1, a);
        batch.draw(texture,px,py);
        batch.setColor(1, 1, 1, 1);
    }

}
