package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class SelectNumPlayersScreen implements Screen {

    Main game;
    int cur_wait = 0;

    public SelectNumPlayersScreen(Main game)
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
        game.fuente.show_text(game.batch, 150,440,"MULTIPLAYER VERSUS RACE",1);
        game.fuente.show_text(game.batch, 100,400,"PLEASE SELECT NUMBER OF PLAYERS",1);
        game.fuente.show_text(game.batch, 40,30,"PUSH ANY BUTTON OF PLAYER ONE TO PROCEED",1);
        int j=80;
        game.posnumber.render2d(game.batch,((game.nhumans-1)%4)*16,64-(16*((int)((game.nhumans-1)/4)+1)),(((game.nhumans-1)%4)+1)*16,64-(16*(int)((game.nhumans-1)/4)),320-j,240-j,320+j,240+j,1);
        game.batch.end();

        //LOGIC

        if((cur_wait==0) && (game.counter>30)){
            if(((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0]))) && (game.nhumans<4)) {game.nhumans++; cur_wait=20;};
            if(((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0]))) && (game.nhumans>2)) {game.nhumans--; cur_wait=20;};
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))) {
                game.nplayers=game.nhumans;
                //game.reset_ranking();
                //set_state(SHIP_SEL_VERSUS);
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
