package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class RankingScreen implements Screen {

    Main game;

    public RankingScreen(Main game)
    {
        this.game = game;
        game.counter = 0;
        game.update_ranking();
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta * 70;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.fuente.show_text(game.batch,220,440,"CURRENT RANKING",1);
        for(int j=0; j<game.nplayers; j++){

            int i=game.ranking[j];

            String s = (j+1)+"."+ship.models.ships.get(game.racing_ship[i]).name+" - "+game.scores[i]+" POINTS";
            game.fuente.show_text(game.batch, 135,380-40*j,s,0);
            if(i<game.nhumans) game.plcursor[game.gdata.icons[i]].render2d( game.batch,0,0,32,32,100,375-40*j,130,375+30-40*j,1.0f);
        };
        game.fuente.show_text(game.batch, 30,60,"PUSH ANY BUTTON OF PLAYER ONE TO CONTINUE",1);
        game.fuente.show_text(game.batch, 180,30,"OR ESCAPE TO FINISH",1);
        game.batch.end();

        // LOGIC =============================================

        //ctr->actualiza();
        //if(game.counter==1) {game.update_ranking();};
        if(game.counter>=30){
            if(game.game_mode==Main.CHAMPIONSHIP){
                //if((ctr->tecla(DIK_ESCAPE)) || (abort_champ==TRUE))
                //    set_state(CHAMP_SEL);
                if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))){
                    game.champ_stage++;
                    /*if(game.champ_stage>=game.championship[gdata.sel_champ].nraces)
                        set_state(CHAMP_PRIZE); //Cup
                    else set_state(CHAMP_STAGE);*/
                };
            }else{
                //if(ctr->tecla(DIK_ESCAPE)) set_state(MAIN_MENU);
                if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
                    //set_state(SHIP_SEL_VERSUS);
                    game.setScreen(new ShipSelectVersusScreen(game));
                    dispose();
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
