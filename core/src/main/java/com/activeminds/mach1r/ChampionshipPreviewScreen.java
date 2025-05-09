package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class ChampionshipPreviewScreen implements Screen {


    Main game;
    float cur_wait;

    public ChampionshipPreviewScreen(Main game)
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

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.fuente.show_text(game.batch, 60,440,event.name,0);
        game.fuente.show_text(game.batch, 20,400,"EVENT OF "+event.nraces+" RACES WITH PRIZE OF "+event.reward+" POINTS",1);
        for(int i=0; i<event.nraces; i++){

            s = i+1+"-"+
                game.dif_name[event.races.get(i).dif]+"'s "+
                course.ctype_name[event.races.get(i).type]+" at "+
                course.scenes.course_scenes.get(event.races.get(i).scene).descr;

            game.fuente.show_text(game.batch,20,360-(30*i),s,0);
        };
        s = "need "+event.minimum+" or more points to play";
        game.fuente.show_text(game.batch, 120,100,s,0);

        if(game.gdata.score_champ>=event.minimum)
            game.fuente.show_text(game.batch,150,50,"PUSH ANY BUTTON TO ENTER",1);
        game.fuente.show_text(game.batch,190,20,"ESCAPE TO GO BACK",1);

        game.batch.end();

        // LOGIC ========================================
        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))){
                if(game.gdata.score_champ>=event.minimum) {
                    game.pl[0]=new ship(game.gdata.sel_ship[0],0,null);
                    game.nplayers=6; game.reset_ranking();
                    game.champ_stage=0; game.abort_champ=false;
                    game.setScreen(new ShipSelectSingleScreen(game));
                    dispose();
                };
            };
            if(game.ctr.atr(controlm.TEC1)) {
                game.setScreen(new ChampionshipSelectScreen(game));
                dispose();
            }
        };

    }

    @Override
    public void resize(int i, int i1) {

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
