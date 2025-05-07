package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class OptionsScreen implements Screen {

    Main game;
    float cur_wait = 0;
    int cursor = 0;

    public OptionsScreen(Main game)
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

        game.fuente.show_text(game.batch,150,350,"graphics options",0);
        game.fuente.show_text(game.batch,150,310,"control options",0);
        game.fuente.show_text(game.batch,150,270,"sound options",0);
        game.fuente.show_text(game.batch,150,230,"view credits",0);
        game.fuente.show_text(game.batch,150,190,"back to main menu",0);
        game.fuente.show_text(game.batch,280,440,"OPTIONS",1);

        game.show_menu_cursor(85,342-(40*cursor));

        game.batch.end();

        // LOGIC ====================================

        //ctr->actualiza();
        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0]))){ if(cursor>0) cursor--; cur_wait=20;};
            if((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0]))){ if(cursor<4) cursor++; cur_wait=20;};
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
                switch(cursor){
                    case 0 :
                        game.setScreen(new GraphicsOptionsScreen(game));
                        dispose();
                        break;
                    case 1 :
                        game.setScreen(new ControlOptionsScreen(game));
                        dispose();
                        break;
                    /*case 2 : set_state(SOUND_OP); break;
                    case 3 : set_state(CREDITS); break;*/
                    case 4 :
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                        break;
                };
            if(game.ctr.atr(controlm.TEC1)) {
                game.setScreen(new MainMenuScreen(game));
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
