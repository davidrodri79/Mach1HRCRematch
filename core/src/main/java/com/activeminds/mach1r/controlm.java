package com.activeminds.mach1r;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class controlm implements InputProcessor, ControllerListener {


    public static final int NOTC=0;
    public static final int TEC1=1;
    public static final int TEC2=2;
    public static final int JOY1=3;
    public static final int MOUS=4;
    public static final int TOUC=5;


    public static final int CMET=6;
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


    // TActile ==================================
    class Widget {
        boolean pressed;
    }

    class Stick extends Widget {

        int x;
        int y;
        int radius;
        int stickx;
        int sticky;

        Stick(int x, int y, int radius)
        {
            this.x = x;
            this.y = y;
            stickx = x;
            sticky = y;
            this.radius = radius;
            pressed = false;
        }
    }
    class Button extends Widget {

        Rectangle rect;
        String action;
        String text;
        String imageOn, imageOff;
        int keyboard = -1, pushes, releases;

        Button(int x, int y, int sx, int sy, String action, int keyboard, String text, String on, String off)
        {
            rect = new Rectangle(x, y, sx, sy);
            this.action = action;
            this.text = text;
            this.imageOn = on;
            this.imageOff = off;
            this.keyboard = keyboard;
            pressed = false;
            pushes = 0;
            releases = 0;
        }
    }

    ArrayList<Stick> sticks;
    Map<String,Button> buttons;
    Map<Integer,Widget> pointers;
    final OrthographicCamera camera;
    AssetManager manager;

    public controlm(OrthographicCamera camera, AssetManager manager)
    {
        this.camera = camera;
        this.manager = manager;
        //this.font = font;
        sticks = new ArrayList<>();
        buttons = new HashMap<>();
        pointers = new HashMap<>();


        Gdx.input.setInputProcessor(this);
        Controllers.addListener(this);
    }

    public void loadButtonLayoutFromJson(String fileName)
    {
        Json json = new Json();
        FileHandle file = Gdx.files.internal(fileName);
        String fileText = file.readString();
        ButtonLayoutJson l = json.fromJson(ButtonLayoutJson.class, fileText);

        for(StickJson s : l.sticks)
        {
            addStick(s.x, s.y, s.radius);
        }

        for(ButtonJson b : l.buttons)
        {
            addButton(b.x, b.y, b.width, b.height, b.action, b.keyboard, b.text, b.image_on, b.image_off);
        }
    }

    public void addStick(int x, int y, int radius)
    {
        Stick s = new Stick(x, y, radius);
        sticks.add(s);
    }

    public void updateStick(Stick s)
    {
        if(s.pressed)
        {
            joy_xaxis[TOUC] = (int) (((s.stickx - s.x) / (float)s.radius) * 1000);
            joy_yaxis[TOUC] = (int) (((s.sticky - s.y) / (float)s.radius) * 1000);;
        }
        else
        {
            joy_xaxis[TOUC] = 0;
            joy_yaxis[TOUC] = 0;
        }
        cder[TOUC] = joy_xaxis[TOUC] > 500;
        cizq[TOUC] = joy_xaxis[TOUC] < -500;
        carr[TOUC] = joy_yaxis[TOUC] > 500;
        caba[TOUC] = joy_yaxis[TOUC] < -500;
    }

    public void addButton(int x, int y, int sx, int sy, String action, int keyboard, String text, String imageOn, String imageOff)
    {
        Button b = new Button(x, y, sx, sy, action, keyboard, text, imageOn, imageOff);
        buttons.put(action, b);
    }

    void pressButton(Button b)
    {
        if(b.action.equals("Up"))
        {
            carr[TOUC] = true;
        }
        else if(b.action.equals("Down"))
        {
            caba[TOUC] = true;
        }
        else if(b.action.equals("Left"))
        {
            cizq[TOUC] = true;
            joy_xaxis[TOUC] = -750;
        }
        else if(b.action.equals("Right"))
        {
            cder[TOUC] = true;
            joy_xaxis[TOUC] = 750;
        }
        else if(b.action.equals("Button1"))
        {
            cboton[TOUC][0] = true;
        }
        else if(b.action.equals("Button2"))
        {
            cboton[TOUC][1] = true;
        }
        else if(b.action.equals("Button3"))
        {
            cboton[TOUC][2] = true;
        }
        else if(b.action.equals("Button4"))
        {
            cboton[TOUC][3] = true;
        }
        else if(b.action.equals("Back"))
        {
            catr[TOUC] = true;
        }
    }

    void releaseButton(Button b)
    {
        if(b.action.equals("Up"))
        {
            carr[TOUC] = false;
        }
        else if(b.action.equals("Down"))
        {
            caba[TOUC] = false;
        }
        else if(b.action.equals("Left"))
        {
            cizq[TOUC] = false;
            joy_xaxis[TOUC] = 0;
        }
        else if(b.action.equals("Right"))
        {
            cder[TOUC] = false;
            joy_xaxis[TOUC] = 0;
        }
        else if(b.action.equals("Button1"))
        {
            cboton[TOUC][0] = false;
        }
        else if(b.action.equals("Button2"))
        {
            cboton[TOUC][1] = false;
        }
        else if(b.action.equals("Button3"))
        {
            cboton[TOUC][2] = false;
        }
        else if(b.action.equals("Button4"))
        {
            cboton[TOUC][3] = false;
        }
        else if(b.action.equals("Back"))
        {
            catr[TOUC] = false;
        }
    }

        public void renderButtonLayout(ShapeRenderer shapeRenderer)
        {
            camera.update();
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

            for(int i = 0; i < sticks.size(); i++)
            {
                Stick s = sticks.get(i);
                shapeRenderer.setColor(Color.WHITE);
                shapeRenderer.circle(s.x, s.y, s.radius);
            }
            shapeRenderer.end();

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for(int i = 0; i < sticks.size(); i++)
            {
                Stick s = sticks.get(i);
                shapeRenderer.setColor(s.pressed ? Color.YELLOW : Color.BLACK);
                shapeRenderer.circle(s.stickx,  s.sticky, 50);
            }

            for(String i:buttons.keySet())
            {
                Button b = buttons.get(i);
                shapeRenderer.setColor(b.pressed ? Color.YELLOW : Color.BLACK);
                shapeRenderer.ellipse(b.rect.x, b.rect.y, b.rect.width, b.rect.height, 2);
                shapeRenderer.rect(b.rect.x, b.rect.y, b.rect.width, b.rect.height);
            }
            shapeRenderer.end();
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

        //if(Gdx.app.getType() == Application.ApplicationType.Desktop)
        //    return true;

        Vector3 touchPos = new Vector3();
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        for(Stick s: sticks)
        {
            Vector2 v = new Vector2(touchPos.x - s.x, touchPos.y -s.y);
            if(v.len() < s.radius)
            {
                pointers.put(pointer, s);
                s.stickx = screenX;
                s.sticky = Gdx.graphics.getHeight() - screenY;
                s.pressed = true;
                updateStick(s);
            }
        }

        for(String i:buttons.keySet())
        {
            if(buttons.get(i).rect.contains(touchPos.x,touchPos.y))
            {
                // Button has been pressed
                pressButton(buttons.get(i));
                pointers.put(pointer,buttons.get(i));
                buttons.get(i).pressed = true;
                buttons.get(i).pushes ++;
            }
        }

        return true; // return true to indicate the event was handled
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

        //if(Gdx.app.getType() == Application.ApplicationType.Desktop)
        //    return true;

        if(pointers.get(pointer) != null)
        {
            // This pointer was linked to a button, so release it
            if(pointers.get(pointer).getClass() == Stick.class)
            {
                pointers.get(pointer).pressed = false;
                ((Stick)pointers.get(pointer)).stickx = ((Stick)pointers.get(pointer)).x;
                ((Stick)pointers.get(pointer)).sticky = ((Stick)pointers.get(pointer)).y;
                updateStick((Stick)pointers.get(pointer));
                pointers.remove(pointer);

            }
            else if(pointers.get(pointer).getClass() == Button.class)
            {
                releaseButton((Button)pointers.get(pointer));
                pointers.get(pointer).pressed = false;
                ((Button)pointers.get(pointer)).releases++;
                pointers.remove(pointer);
            }
        }
        return true; // return true to indicate the event was handled
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            return true;

        // Get screen camera coordinates of touch
        Vector3 touchPos = new Vector3();
        touchPos.set(screenX, screenY, 0);
        camera.unproject(touchPos);

        if(pointers.get(pointer).getClass() == Stick.class)
        {
            Stick s = (Stick)pointers.get(pointer);
            Vector2 v = new Vector2(screenX - s.x, Gdx.graphics.getHeight() - screenY - s.y);
            if(v.len() > s.radius)
            {
                v = v.limit(s.radius);
            }
            s.stickx = (int) (s.x + v.x);
            s.sticky = (int) (s.y + v.y);

            updateStick(s);
        }

        for(String i:buttons.keySet())
        {
            // Pointer is now inside a new button; release previous button
            if(buttons.get(i).rect.contains(touchPos.x,touchPos.y))
            {
                if(pointers.get(pointer) != null)
                {
                    if(pointers.get(pointer).getClass() == Button.class)
                        releaseButton((Button)pointers.get(pointer));
                    pointers.get(pointer).pressed = false;
                }
                pointers.put(pointer,buttons.get(i));
                buttons.get(i).pressed = true;
                pressButton(buttons.get(i));
            }
        }
        return true;
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
        return false;
    }
}
