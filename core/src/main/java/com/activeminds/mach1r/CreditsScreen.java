package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class CreditsScreen implements Screen {

    Main game;
    float cur_wait = 0;
    int cursor = 0;


    public CreditsScreen(Main game)
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

        int i=(int)(game.counter/2)-40;
        game.fuente.show_text(game.batch,100,i,"MACH 1 HYPER RACING CHAMPIONSHIP",1);
        game.fuente.show_text(game.batch,190,i-40,"BY ACTIVE MINDS 2003",1);
        game.fuente.show_text(game.batch,300,i-80,"STAFF",1);
        game.fuente.show_text(game.batch,100,i-180,"ORIGINAL CONCEPT",0);
        game.fuente.show_text(game.batch,150,i-220,"david rodriguez",0);
        game.fuente.show_text(game.batch,100,i-320,"GAME CODING",0);
        game.fuente.show_text(game.batch,150,i-360,"david rodriguez",0);
        game.fuente.show_text(game.batch,100,i-460,"3D ENGINE AND COURSE GENERATOR",0);
        game.fuente.show_text(game.batch,150,i-500,"david rodriguez",0);
        game.fuente.show_text(game.batch,100,i-600,"SHIP 3D MODELLING AND TEXTURING",0);
        game.fuente.show_text(game.batch,150,i-640,"david rodriguez",0);
        game.fuente.show_text(game.batch,150,i-680,"eduard royo",0);
        game.fuente.show_text(game.batch,100,i-780,"MUSIC COMPOSING AND SOUND EFFECTS",0);
        game.fuente.show_text(game.batch,150,i-820,"eduard royo",0);
        game.fuente.show_text(game.batch,100,i-920,"SHIP DESIGN",0);
        game.fuente.show_text(game.batch,150,i-960,"david rodriguez",0);
        game.fuente.show_text(game.batch,150,i-1000,"eduard royo",0);
        game.fuente.show_text(game.batch,150,i-1040,"mario ruz",0);
        game.fuente.show_text(game.batch,150,i-1080,"marc estape",0);
        game.fuente.show_text(game.batch,150,i-1120,"jose adan",0);
        game.fuente.show_text(game.batch,100,i-1220,"GAME TESTING",0);
        game.fuente.show_text(game.batch,150,i-1260,"jordi poch",0);
        game.fuente.show_text(game.batch,150,i-1300,"mario ruz",0);
        game.fuente.show_text(game.batch,150,i-1340,"raul fernandez",0);
        game.fuente.show_text(game.batch,190,i-1440,"THANKS FOR PLAYING",1);

        game.batch.end();

        // LOGIC ====================================

        //game.ctr.actualiza();
        if(game.counter>=30)
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])) || (game.ctr.atr(controlm.TEC1))){
                //mus->stop(); mus->release();
                game.setScreen(new OptionsScreen(game));
                dispose();
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
