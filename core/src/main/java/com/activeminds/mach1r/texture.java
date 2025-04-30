package com.activeminds.mach1r;

import com.badlogic.gdx.graphics.Texture;

public class texture {

    public static final int TEX_PCX = 0;
    public static final int TEX_BMP = 1;

    Texture gdxTexture;

    public texture(String tname, int type, boolean repeat, boolean alpha)
    {
        if(type == TEX_BMP) LoadBMP(tname);
        if(type == TEX_PCX) LoadPCX(tname, alpha);

        gdxTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        if(repeat) gdxTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }
    boolean LoadBMP(String szFileName)
    {
        gdxTexture = new Texture(szFileName);
        return true;
    }

    boolean LoadPCX(String szFileName, boolean alpha) {
        gdxTexture = new Texture(szFileName);
        return true;
    }

}
