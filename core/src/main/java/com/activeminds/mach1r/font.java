package com.activeminds.mach1r;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class font {

    texture tex;

    TextureRegion []glyphs;

    public font()
    {

    }
    public font(String file,int x,int y,int dxx)
    {
        tex = new texture(file, texture.TEX_PCX, false, false);
        glyphs = new TextureRegion[256];
        for(int i = 0; i < 256; i++)
        {
            glyphs[i] = new TextureRegion(tex.gdxTexture, (i % 16) * x, (int)(i / 16) * y, x, y);
        }
    }
    void show_text(SpriteBatch batch, int x, int y, String text, int set)
    {
        for(int i = 0; i < text.length(); i++)
        {
            int c = text.charAt(i) - 32 + (128 * set);
            batch.draw(glyphs[c], /*new TextureRegion(tex.gdxTexture,16,16,16,16)*/ x + i*16, y);
        }

    }
}
