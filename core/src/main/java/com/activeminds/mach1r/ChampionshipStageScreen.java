package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class ChampionshipStageScreen implements Screen {

    Main game;
    float cur_wait;

    public ChampionshipStageScreen(Main game)
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
        if(cur_wait > 0) cur_wait -= delta;
        String s;

        course.champ_event event = course.championship.champ_events.get(game.gdata.sel_champ);
        course.champ_race race = event.races.get(game.champ_stage);

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.fuente.show_text(game.batch,40,440,event.name,0);
        s ="RACE "+(game.champ_stage+1)+" OF "+event.nraces;
        game.fuente.show_text(game.batch,240,390,s,1);
        s = game.dif_name[race.dif]+"'s "+
            course.ctype_name[race.type]+" at "+
            course.scenes.course_scenes.get(race.scene).descr;
        game.fuente.show_text(game.batch,40,350,s,0);
        s = "RACING FOR "+race.nlaps+" LAPS";
        game.fuente.show_text(game.batch, 200,310,s,1);
        game.preview[race.scene].render2d(game.batch,0,0,128,128,224,140,224+192,140+144,1);

        game.fuente.show_text(game.batch,150,30,"PUSH ANY BUTTON TO RACE",1);
        game.batch.end();

        // LOGIC ===========================================
        if((cur_wait==0) && (game.counter>30)){
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))){
                game.setScreen(new LoadingScreen(game));
                dispose();
            };
            //if(ctr.tecla(DIK_ESCAPE)) set_state(CHAMP_SEL);
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
