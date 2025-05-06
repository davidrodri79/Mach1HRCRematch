package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

import java.awt.event.ActionListener;

public class SingleRaceSelectScreen implements Screen {

    Main game;
    int cur_wait = 20, cursor = 0;

    SingleRaceSelectScreen(Main game)
    {
        this.game = game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta*70f;
        if (cur_wait > 0) cur_wait--;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.fuente.show_text(game.batch, 160,440,"SINGLE RACE SELECTION",1);
        game.fuente.show_text(game.batch,100,400,"DIFFICULTY",1);
        game.fuente.show_text(game.batch,150,370, game.dif_name[game.gdata.dif],0);
        game.fuente.show_text(game.batch,100,330,"EVENT",1);
        game.fuente.show_text(game.batch,150,300,course.ctype_name[game.gdata.cour_type],0);
        game.fuente.show_text(game.batch,100,260,"SCENE",1);
        game.fuente.show_text(game.batch,150,230,course.scenes.course_scenes.get(game.gdata.scene).descr,0);
        game.fuente.show_text(game.batch,100,190,"NUMBER OF LAPS",1);
        game.fuente.show_text(game.batch,150,160,""+game.gdata.nlaps,1);
        game.preview[game.gdata.scene].render2d(game.batch, 0,0,128,128,390,160,390+192,160+144,1);
        game.fuente.show_text(game.batch, 40,30,"PUSH ANY BUTTON OF PLAYER ONE TO PROCEED",1);
        game.show_menu_cursor(85,362-(70*cursor));
        game.batch.end();



        // LOGIC ========================================================================

        if((cur_wait==0) && (game.counter>30)){
            if((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0]))){
                switch(cursor){
                    case 0 : if (game.gdata.dif<3) game.gdata.dif++; break;
                    case 1 : if (game.gdata.cour_type<9) game.gdata.cour_type++; break;
                    case 2 : if (game.gdata.scene<5) game.gdata.scene++; break;
                    case 3 : if (game.gdata.nlaps<10) game.gdata.nlaps++; break;
                };
                cur_wait=20;
            };
            if((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0]))){
                switch(cursor){
                    case 0 : if (game.gdata.dif>0) game.gdata.dif--; break;
                    case 1 : if (game.gdata.cour_type>0) game.gdata.cour_type--; break;
                    case 2 : if (game.gdata.scene>0) game.gdata.scene--; break;
                    case 3 : if (game.gdata.nlaps>2) game.gdata.nlaps--; break;
                };
                cur_wait=20;
            };
            if((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0]))){ if(cursor>0) cursor--; cur_wait=20;};
            if((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0]))){ if(cursor<3) cursor++; cur_wait=20;};
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))) {
                /*wconfirm->playonce();
                lock=TRUE;*/
                game.random_ships(game.nhumans);
                /*set_state(LOADING);*/
                game.setScreen(new LoadingScreen(game));
                dispose();
            };
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
