package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.*;

public class controlm implements InputProcessor, ControllerListener {


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
    public static final int CPAU = 11;



    int p_tec[][]={ {Input.Keys.UP, Input.Keys.DOWN, Input.Keys.LEFT, Input.Keys.RIGHT,  Input.Keys.SHIFT_RIGHT, Input.Keys.CONTROL_RIGHT, Input.Keys.ENTER, Input.Keys.INSERT, Input.Keys.ESCAPE, Input.Keys.F5},
                    {Input.Keys.W, Input.Keys.S, Input.Keys.A, Input.Keys.D, Input.Keys.U, Input.Keys.I, Input.Keys.O, Input.Keys.P, Input.Keys.ESCAPE, Input.Keys.F5}};
    boolean carr[] = new boolean[CMET];
    boolean caba[] = new boolean[CMET];
    boolean cizq[] = new boolean[CMET];
    boolean cder[] = new boolean[CMET];
    boolean catr[] = new boolean[CMET];
    boolean cpau[] = new boolean[CMET];
    boolean cboton[][] = new boolean[CMET][CMAXB];
    int joy_xaxis[] = new int[CMET], joy_yaxis[] = new int[CMET];

    public controlm()
    {
        Gdx.input.setInputProcessor(this);
        Controllers.addListener(this);
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

    boolean pau(int t)
    {
        return cpau[t];
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
            if (keycode == p_tec[j][9])
                cpau[TEC1 + j] = true;
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
            if (keycode == p_tec[j][9])
                cpau[TEC1 + j] = false;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    public static final int MOUSSENS = 100;
    boolean mouseLeft = false, mouseRight = false;
    int lastScreenX, lastScreenY;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if(button == Input.Buttons.LEFT)
        {
            mouseLeft = true;
        }
        if(button == Input.Buttons.RIGHT)
        {
            mouseRight = true;
        }

        cboton[MOUS][0]=(mouseLeft && !mouseRight);
        cboton[MOUS][1]=(!mouseLeft && mouseRight);
        cboton[MOUS][2]=(mouseLeft && mouseRight);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(button == Input.Buttons.LEFT)
        {
            mouseLeft = false;
        }
        if(button == Input.Buttons.RIGHT)
        {
            mouseRight = false;
        }

        cboton[MOUS][0]=(mouseLeft && !mouseRight);
        cboton[MOUS][1]=(!mouseLeft && mouseRight);
        cboton[MOUS][2]=(mouseLeft && mouseRight);

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

        int ix = screenX - lastScreenX, iy = screenY - lastScreenY;
        lastScreenX = screenX; lastScreenY = screenY;

        cizq[MOUS]=(ix<-10); cder[MOUS]=(ix>10);
        carr[MOUS]=(iy<-10); caba[MOUS]=(iy>10);
        if(ix>0) joy_xaxis[MOUS]=ix*MOUSSENS;
        else if(ix<0) joy_xaxis[MOUS]=ix*MOUSSENS;
        /*else if(joy_xaxis[MOUS]>0) joy_xaxis[MOUS]-=25;
        else if(joy_xaxis[MOUS]<0) joy_xaxis[MOUS]+=25;*/
        if(joy_xaxis[MOUS]<-1000) joy_xaxis[MOUS]=-1000;
        if(joy_xaxis[MOUS]>1000) joy_xaxis[MOUS]=1000;

        if(iy>0) joy_yaxis[MOUS]=iy*MOUSSENS;
        else if(iy<0) joy_yaxis[MOUS]=iy*MOUSSENS;
        else if(joy_yaxis[MOUS]>0) joy_yaxis[MOUS]-=25;
        else if(joy_yaxis[MOUS]<0) joy_yaxis[MOUS]+=25;
        if(joy_yaxis[MOUS]<-1000) joy_yaxis[MOUS]=-1000;
        if(joy_yaxis[MOUS]>1000) joy_yaxis[MOUS]=1000;

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int i)
    {
        if(i < CMAXB)
            cboton[JOY1][i] = true;
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int i)
    {
        if(i < CMAXB)
            cboton[JOY1][i] = false;
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int i, float v) {
        if(i == 0)
        {
            if(v < -0.5f)
            {
                cizq[JOY1] = true; cder[JOY1] = false;
            }
            else if (v > 0.5f)
            {
                cder[JOY1] = true; cizq[JOY1] = false;
            }
            else
            {
                cder[JOY1] = false; cizq[JOY1] = false;
            }

            joy_xaxis[JOY1] = (int)(v*1000f);
        }

        if(i == 1)
        {
            if(v < -0.5f)
            {
                carr[JOY1] = true; caba[JOY1] = false;
            }
            else if (v > 0.5f)
            {
                caba[JOY1] = true; carr[JOY1] = false;
            }
            else
            {
                carr[JOY1] = false; caba[JOY1] = false;
            }

            joy_yaxis[JOY1] = (int)(v*1000f);
        }
        System.out.println("Se mueveeeeee");
        return false;
    }
}
