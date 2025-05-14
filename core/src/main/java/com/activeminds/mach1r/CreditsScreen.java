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

        game.play_music("sound/song4.mp3");
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
        game.fuente.show_text(game.batch,100,i,Main.loc.get("credits0"),1);
        game.fuente.show_text(game.batch,190,i-40,Main.loc.get("credits1"),1);
        game.fuente.show_text(game.batch,300,i-80,Main.loc.get("credits2"),1);
        game.fuente.show_text(game.batch,100,i-180,Main.loc.get("credits3"),0);
        game.fuente.show_text(game.batch,150,i-220,Main.loc.get("credits4"),0);
        game.fuente.show_text(game.batch,100,i-320,Main.loc.get("credits5"),0);
        game.fuente.show_text(game.batch,150,i-360,Main.loc.get("credits6"),0);
        game.fuente.show_text(game.batch,100,i-460,Main.loc.get("credits7"),0);
        game.fuente.show_text(game.batch,150,i-500,Main.loc.get("credits8"),0);
        game.fuente.show_text(game.batch,100,i-600,Main.loc.get("credits9"),0);
        game.fuente.show_text(game.batch,150,i-640,Main.loc.get("credits10"),0);
        game.fuente.show_text(game.batch,150,i-680,Main.loc.get("credits11"),0);
        game.fuente.show_text(game.batch,100,i-780,Main.loc.get("credits12"),0);
        game.fuente.show_text(game.batch,150,i-820,Main.loc.get("credits13"),0);
        game.fuente.show_text(game.batch,100,i-920,Main.loc.get("credits14"),0);
        game.fuente.show_text(game.batch,150,i-960,Main.loc.get("credits15"),0);
        game.fuente.show_text(game.batch,150,i-1000,Main.loc.get("credits16"),0);
        game.fuente.show_text(game.batch,150,i-1040,Main.loc.get("credits17"),0);
        game.fuente.show_text(game.batch,150,i-1080,Main.loc.get("credits18"),0);
        game.fuente.show_text(game.batch,150,i-1120,Main.loc.get("credits19"),0);
        game.fuente.show_text(game.batch,100,i-1220,Main.loc.get("credits20"),0);
        game.fuente.show_text(game.batch,150,i-1260,Main.loc.get("credits21"),0);
        game.fuente.show_text(game.batch,150,i-1300,Main.loc.get("credits22"),0);
        game.fuente.show_text(game.batch,150,i-1340,Main.loc.get("credits23"),0);
        game.fuente.show_text(game.batch,190,i-1440,Main.loc.get("credits24"),1);

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

        game.stop_music();

    }
}
