package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class sprite {

    texture texture;
    float sx, sy;

    public sprite()
    {

    }

    public sprite(float x, float y, String tfile, boolean alpha)
    {
        sx = x; sy = y;
        if(Gdx.files.internal(tfile).exists())
        {
            texture = new texture(tfile, com.activeminds.mach1r.texture.TEX_PCX, true, alpha);
        }
        else
        {
            texture = null; //new Texture((int) x, (int)y, Pixmap.Format.RGBA8888);
        }
    }

    public sprite(float x, float y, String tfile)
    {
        sx = x; sy = y;
        if(Gdx.files.internal(tfile).exists())
        {
            texture = new texture(tfile, com.activeminds.mach1r.texture.TEX_PCX, true, false);
        }
        else
        {
            texture = null; //new Texture((int) x, (int)y, Pixmap.Format.RGBA8888);
        }
    }

    void render2d(SpriteBatch batch, float ox, float oy, float ex, float ey, int px, int py, int pex, int pey, float a)
    {
        ey = sy - ey;
        oy = sy - oy;
        batch.setColor(1, 1, 1, a);
        batch.draw(texture.gdxTexture,px,py,pex-px,pey-py, ox/sx, oy/sy, ex/sx, ey/sy);
        batch.setColor(1, 1, 1, 1);
    }

    void render2dcolor(SpriteBatch batch, float ox, float oy, float ex, float ey, int px, int py, int pex, int pey, float a, float r, float g, float b)
    {
        ey = sy - ey;
        oy = sy - oy;
        batch.setColor(r, g, b, a);
        batch.draw(texture.gdxTexture,px,py,pex-px,pey-py, ox/sx, oy/sy, ex/sx, ey/sy);
        batch.setColor(1, 1, 1, 1);
    }

}
