package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class TitleScreen implements Screen {

    Main game;

    float counter = 0f;

    public TitleScreen(Main game)
    {
        this.game = game;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        counter += delta * 70;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.title[0].render2d(game.batch, 0,0,256,256,50,200,320,460,1.0f);
        game.title[1].render2d(game.batch, 0,0,256,256,320,200,590,460,1.0f);
        if(((int)counter/20)%2==0) game.fuente.show_text(game.batch, 160,120,"push any button to begin",0);
        game.fuente.show_text(game.batch, 200,40,"ACTIVE MINDS 2003",1);
        game.batch.end();


        //ctr->actualiza();
        if (counter>60)
            if((game.ctr.algun_boton(controlm.TEC1)) /*|| (ctr->algun_boton(gdata.controls[0]))*/) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        //if(counter>1800) {random_ships(0); startup_course(2,rand()%4,rand()%10,rand()%6); pl[0]->raceover=TRUE; set_state(DEMO);};


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
