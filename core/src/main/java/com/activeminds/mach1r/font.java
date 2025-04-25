package com.activeminds.mach1r;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class font {

    texture tex;

    public font()
    {

    }
    public font(String file,int x,int y,int dxx)
    {
        tex = new texture(file, texture.TEX_PCX, false, false);
    }
    void show_text(SpriteBatch batch, int x, int y, String text, int set)
    {
        batch.draw(tex.gdxTexture, /*new TextureRegion(tex.gdxTexture,16,16,16,16)*/ 0,0);
    }
}
