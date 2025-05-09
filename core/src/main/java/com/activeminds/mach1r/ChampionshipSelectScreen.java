package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class ChampionshipSelectScreen implements Screen {

    Main game;
    float cur_wait;

    ChampionshipSelectScreen(Main game)
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

        game.fuente.show_text(game.batch,40,440,"CHAMPIONSHIP EVENTS",1);
        game.fuente.show_text(game.batch,450,440,"SCORE:"+game.gdata.score_champ,1);
        for(int i=0; i< course.championship.champ_events.size(); i++)
            game.fuente.show_text(game.batch, 60,400-20*i,course.championship.champ_events.get(i).name,0);
        game.show_menu_cursor(5,392-(20*game.gdata.sel_champ));
        game.fuente.show_text(game.batch,70,30,"PUSH ANY BUTTON TO VIEW DESCRIPTION",1);
        game.batch.end();


        // LOGIC ===================================

        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
            {
                //set_state(CHAMP_PREVIEW);
                game.setScreen(new ChampionshipPreviewScreen(game));
                dispose();
            };
            if((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0])))
            {
                if(game.gdata.sel_champ>0) game.gdata.sel_champ--; cur_wait=20;
            };
            if((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0])))
            {
                if(game.gdata.sel_champ<course.championship.champ_events.size()-1) game.gdata.sel_champ++;
                cur_wait=20;
            };
            if(game.ctr.atr(controlm.TEC1)) {
                game.setScreen(new MainMenuScreen(game));
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
