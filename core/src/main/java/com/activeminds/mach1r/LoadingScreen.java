package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;

public class LoadingScreen implements Screen {

    Main game;
    LoadingScreen(Main game)
    {
        this.game = game;
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        // LOGIC =======================================================================
        game.startup_course(game.gdata.nlaps,game.gdata.dif,game.gdata.cour_type,game.gdata.scene);
        /*switch(game_mode){
            default : startup_course(gdata.nlaps,gdata.dif,gdata.cour_type,gdata.scene); break;
            case ENDURANCE : nplayers=2; racing_ship[1]=endurance[gdata.sel_endur].op; startup_course(endurance[gdata.sel_endur].nlaps,endurance[gdata.sel_endur].dif,endurance[gdata.sel_endur].type,endurance[gdata.sel_endur].scene); break;
            case CHAMPIONSHIP : startup_course(championship[gdata.sel_champ].races[champ_stage].nlaps,
                championship[gdata.sel_champ].races[champ_stage].dif,
                championship[gdata.sel_champ].races[champ_stage].type,
                championship[gdata.sel_champ].races[champ_stage].scene); break;
        };
        lock=FALSE;
        if(game_mode==VERSUS_R) set_state(VERSUS);
        else set_state(SINGLE);*/


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
