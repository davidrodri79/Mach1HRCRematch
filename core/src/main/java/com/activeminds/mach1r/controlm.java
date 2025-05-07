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

    public static final int CARR = 0;
    public static final int CABA = 1;
    public static final int CIZQ = 2;
    public static final int CDER = 3;
    public static final int CBU1 = 4;
    public static final int CBU2 = 5;
    public static final int CBU3 = 6;
    public static final int CBU4 = 7;
    public static final int CBU5 = 8;
    public static final int CBU6 = 9;
    public static final int CATR = 10;



    int p_tec[][]={ {Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,  Input.Keys.SHIFT_RIGHT, Input.Keys.CONTROL_RIGHT, Input.Keys.ENTER, Input.Keys.INSERT, Input.Keys.ESCAPE},
                    {Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, Input.Keys.U, Input.Keys.I, Input.Keys.O, Input.Keys.P, Input.Keys.ESCAPE}};
    boolean carr[] = new boolean[CMET];
    boolean caba[] = new boolean[CMET];
    boolean cizq[] = new boolean[CMET];
    boolean cder[] = new boolean[CMET];
    boolean catr[] = new boolean[CMET];
    boolean cboton[][] = new boolean[CMET][CMAXB];
    int joy_xaxis[] = new int[CMET], joy_yaxis[] = new int[CMET];

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

    boolean atr(int t)
    {
        return catr[t];
    }

    boolean boton(int t, int b) { return cboton[t][b]; }

    boolean algun_boton(int t)
    {
        if((cboton[t][0]) || (cboton[t][1]) || (cboton[t][2]) ||
            (cboton[t][3]))
            return true;
        else     return false;
    }

    void activa(int t, int c)
    {
        switch(c){
            case CARR : carr[t]=true; joy_yaxis[t]=-1000; break;
            case CABA : caba[t]=true; joy_yaxis[t]=1000; break;
            case CIZQ : cizq[t]=true; joy_xaxis[t]=-1000; break;
            case CDER : cder[t]=true; joy_xaxis[t]=1000; break;
            case CBU1 : cboton[t][0]=true; break;
            case CBU2 : cboton[t][1]=true; break;
            case CBU3 : cboton[t][2]=true; break;
            case CBU4 : cboton[t][3]=true; break;
            case CBU5 : cboton[t][4]=true; break;
            case CBU6 : cboton[t][5]=true; break;
        };
    }

    @Override
    public boolean keyDown(int keycode) {

        for(int j = 0; j < 2; j++)
        {
            if (keycode == p_tec[j][0])
                carr[TEC1 + j] = true;
            if (keycode == p_tec[j][1])
                caba[TEC1 + j] = true;
            if (keycode == p_tec[j][2]) {
                cizq[TEC1 + j] = true;
                joy_xaxis[TEC1 + j] = -750;
            }
            if (keycode == p_tec[j][3]) {
                cder[TEC1 + j] = true;
                joy_xaxis[TEC1 + j] = 750;
            }
            for (int i = 0; i < CMAXB; i++) {
                if (keycode == p_tec[j][4 + i]) {
                    cboton[TEC1 + j][i] = true;
                }
            }
            if (keycode == p_tec[j][8])
                catr[TEC1 + j] = true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for(int j = 0; j < 2; j++) {
            if (keycode == p_tec[j][0])
                carr[TEC1 + j] = false;
            if (keycode == p_tec[j][1])
                caba[TEC1 + j] = false;
            if (keycode == p_tec[j][2]) {
                cizq[TEC1 + j] = false;
                joy_xaxis[TEC1 + j] = 0;
            }
            if (keycode == p_tec[j][3]) {
                cder[TEC1 + j] = false;
                joy_xaxis[TEC1 + j] = 0;
            }
            for (int i = 0; i < CMAXB; i++) {
                if (keycode == p_tec[j][4 + i]) {
                    cboton[TEC1 + j][i] = false;
                }
            }
            if (keycode == p_tec[j][8])
                catr[TEC1 + j] = false;
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
