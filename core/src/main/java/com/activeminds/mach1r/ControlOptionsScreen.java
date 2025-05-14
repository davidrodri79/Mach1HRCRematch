package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class ControlOptionsScreen implements Screen {

    Main game;
    float cur_wait = 0;
    int cursor = 0;

    String[] ctr_str={"       disabled","cursor+shft+ctrl+enter","    wasduiop keys","   joystick/joypad","       mouse", "    tactile"};


    public ControlOptionsScreen(Main game)
    {
        this.game = game;
        game.counter = 0;
        cur_wait = 0;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta * 70;
        if(cur_wait > 0) cur_wait -= delta * 70;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();

        for(int i=0; i<4; i++){
            String s = "control pl "+(i+1);
            game.fuente.show_text(game.batch,100,350-40*i,s,0);
            game.fuente.show_text(game.batch,300,350-40*i,ctr_str[game.gdata.controls[i]],0);
        };
        for(int i=0; i<4; i++){
            String s = "player "+(i+1)+" versus icon";
            game.fuente.show_text(game.batch,100,190-40*i,s,0);
            game.plcursor[game.gdata.icons[i]].render2d(game.batch, 0,0,32,32,440,184-40*i,468,212-40*i,1.0f);

        };
        game.fuente.show_text(game.batch,100,30,"back to options menu",0);
        game.fuente.show_text(game.batch,210,440,"CONTROL OPTIONS",1);

        game.show_menu_cursor(55,342-(40*cursor));

        game.batch.end();

        // LOGIC ====================================

        //game.ctr.actualiza();
        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0]))){ if(cursor>0) cursor--; cur_wait=20;};
            if((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0]))){ if(cursor<8) cursor++; cur_wait=20;};
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
                if(cursor==8) {
                    game.setScreen(new OptionsScreen(game));
                    dispose();
                }

            if(cursor<4){
                if((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0])))
                    if(game.gdata.controls[cursor]<controlm.MOUS) {game.gdata.controls[cursor]=(game.gdata.controls[cursor]+1); cur_wait=20;};

                if((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0])))
                    if(game.gdata.controls[cursor]>controlm.NOTC) {game.gdata.controls[cursor]=(game.gdata.controls[cursor]-1); cur_wait=20;};
            }else{
                if((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0])))
                    if(game.gdata.icons[cursor-4]<9) {game.gdata.icons[cursor-4]++; cur_wait=20;};

                if((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0])))
                    if(game.gdata.icons[cursor-4]>0) {game.gdata.icons[cursor-4]--; cur_wait=20;};
            };
            if(game.ctr.atr(controlm.TEC1)) {
                game.setScreen(new OptionsScreen(game));
                dispose();
            }
        };

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
