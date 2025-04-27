package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public class controlm implements InputProcessor {


    public static final int NOTC=0;
    public static final int TEC1=1;
    public static final int TEC2=2;
    public static final int JOY1=3;
    public static final int MOUS=4;

    public static final int CMET=5;
    public static final int CMAXB=4;

    int p_tec[][]={ {Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,  Input.Keys.SHIFT_RIGHT, Input.Keys.CONTROL_RIGHT, Input.Keys.ENTER, Input.Keys.INSERT}, {}};
    boolean carr[] = new boolean[CMET];
    boolean caba[] = new boolean[CMET];
    boolean cizq[] = new boolean[CMET];
    boolean cder[] = new boolean[CMET];
    boolean cboton[][] = new boolean[CMET][CMAXB];

    public controlm()
    {
        Gdx.input.setInputProcessor(this);
    }

    boolean aba(int t)
    {
        return caba[t];
    }

    boolean arr(int t)
    {
        return carr[t];
    }

    boolean izq(int t)
    {
        return cizq[t];
    }

    boolean der(int t)
    {
        return cder[t];
    }

    boolean algun_boton(int t)
    {
        if((cboton[t][0]) || (cboton[t][1]) || (cboton[t][2]) ||
            (cboton[t][3]))
            return true;
        else     return false;
    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == p_tec[0][0])
            carr[TEC1]= true;
        if(keycode == p_tec[0][1])
            caba[TEC1]= true;
        if(keycode == p_tec[0][2])
            cizq[TEC1]= true;
        if(keycode == p_tec[0][3])
            cder[TEC1]= true;
        for(int i=0;i<CMAXB;i++) {
            if (keycode == p_tec[0][4 + i]) {
                cboton[TEC1][i] = true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == p_tec[0][0])
            carr[TEC1]= false;
        if(keycode == p_tec[0][1])
            caba[TEC1]= false;
        if(keycode == p_tec[0][2])
            cizq[TEC1]= false;
        if(keycode == p_tec[0][3])
            cder[TEC1]= false;
        for(int i=0;i<CMAXB;i++) {
            if (keycode == p_tec[0][4 + i]) {
                cboton[TEC1][i] = false;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
