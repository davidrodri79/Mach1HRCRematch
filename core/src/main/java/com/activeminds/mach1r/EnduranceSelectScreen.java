package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class EnduranceSelectScreen implements Screen {

    Main game;
    float cur_wait;
    course.endur_race race;

    public EnduranceSelectScreen(Main game)
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

        race = course.endurance.races.get(game.gdata.sel_endur);

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.fuente.show_text(game.batch,190,440,"ENDURANCE CHALLENGE",1);
        String s = "stage "+(game.gdata.sel_endur+1);
        game.fuente.show_text(game.batch, 40,390,s,0);
        s = game.dif_name[race.dif] +"'s "+
            course.ctype_name[race.type]+" at "+
            course.scenes.course_scenes.get(race.scene).descr;
        game.fuente.show_text(game.batch,40,350,s,0);
        s = "racing for "+race.nlaps +" laps versus "+
            ship.models.ships.get(race.op).name;
        game.fuente.show_text(game.batch, 40,310,s,0);
        game.preview[course.endurance.races.get(game.gdata.sel_endur).scene].render2d(game.batch,0,0,128,128,224,140,224+192,140+144,1);

        if(game.gdata.sel_endur>0) game.fuente.show_text(game.batch,80,190,"<<",0);
        if(game.gdata.sel_endur<game.gdata.res_endur-1) {
            game.fuente.show_text(game.batch, 540,190,">>",0);
            game.fuente.show_text(game.batch,230,100,"STAGE CLEARED",1);
        };
        game.fuente.show_text(game.batch,150,30,"PUSH ANY BUTTON TO RACE",1);
        game.batch.end();

        // LOGIC ============================

        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))) {
                //wconfirm->playonce(); lock=TRUE;
                game.random_ships(game.nhumans);
                game.setScreen(new LoadingScreen(game));
                dispose();
                //set_state(LOADING);
            };
            if((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0]))){ if(game.gdata.sel_endur>0) game.gdata.sel_endur--; cur_wait=20;};
            if((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0]))){ if(game.gdata.sel_endur<game.gdata.res_endur-1) game.gdata.sel_endur++; cur_wait=20;};
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
