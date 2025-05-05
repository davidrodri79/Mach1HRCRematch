package com.activeminds.mach1r;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class sprite {

    Texture texture;
    float sx, sy;

    public sprite()
    {

    }

    public sprite(float x, float y, String tfile, boolean alpha)
    {
        sx = x; sy = y;
        texture = new Texture(tfile);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public sprite(float x, float y, String tfile)
    {
        sx = x; sy = y;
        texture = new Texture(tfile);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    void render2d(SpriteBatch batch, float ox, float oy, float ex, float ey, int px, int py, int pex, int pey, float a)
    {
        ey = sy - ey;
        oy = sy - oy;
        batch.setColor(1, 1, 1, a);
        batch.draw(texture,px,py,pex-px,pey-py, ox/sx, oy/sy, ex/sx, ey/sy);
        batch.setColor(1, 1, 1, 1);
    }

    void render2dcolor(SpriteBatch batch, float ox, float oy, float ex, float ey, int px, int py, int pex, int pey, float a, float r, float g, float b)
    {
        ey = sy - ey;
        oy = sy - oy;
        batch.setColor(r, g, b, a);
        batch.draw(texture,px,py,pex-px,pey-py, ox/sx, oy/sy, ex/sx, ey/sy);
        batch.setColor(1, 1, 1, 1);
    }

}
