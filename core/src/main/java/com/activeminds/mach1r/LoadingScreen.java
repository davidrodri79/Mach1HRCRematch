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

        switch(game.game_mode){
            case Main.SINGLE_R:
            case Main.VERSUS_R:
                game.startup_course(game.gdata.nlaps,game.gdata.dif,game.gdata.cour_type,game.gdata.scene);
            break;
            case Main.DEMO:
                game.startup_course(2,course.rand()%4,course.rand()%10,course.rand()%6);
                game.pl[0].raceover=true;
                break;
            /*default : startup_course(gdata.nlaps,gdata.dif,gdata.cour_type,gdata.scene); break;*/
            case Main.ENDURANCE :
                course.endur_race race = course.endurance.races.get(game.gdata.sel_endur);
                game.nplayers=2;
                game.racing_ship[1]=race.op;
                game.startup_course(race.nlaps,race.dif,race.type,race.scene);
                break;
            case Main.CHAMPIONSHIP :
                course.champ_event event = course.championship.champ_events.get(game.gdata.sel_champ);
                course.champ_race racec = event.races.get(game.champ_stage);
                game.startup_course(racec.nlaps, racec.dif, racec.type, racec.scene);
                break;
        };

        RaceScreen scr = new RaceScreen(game);
        if(game.game_mode==game.VERSUS_R) scr.set_state(RaceScreen.VERSUS);
        else if (game.game_mode==game.DEMO) scr.set_state(RaceScreen.DEMO);
        else scr.set_state(RaceScreen.SINGLE);
        game.setScreen(scr);
        dispose();
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
