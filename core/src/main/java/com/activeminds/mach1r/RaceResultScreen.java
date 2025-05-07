package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class RaceResultScreen implements Screen {

    Main game;
    float counter;

    public RaceResultScreen(Main game)
    {
        this.game = game;
        counter = 0f;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta * 70;
        counter += delta * 70;
        //if(cur_wait > 0) cur_wait -= delta;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();

        game.fuente.show_text(game.batch, 250,440,"RACE RESULTS",1);
        for(int j=0; j<game.nplayers; j++){

            int i=game.position[j];
            String s;
            if(game.pl[i].state==ship.DESTR)
            s=(j+1)+"."+game.pl[i].data.name+" - DISQUALIFIED";
		    else if(!game.pl[i].raceover)
                s=(j+1)+"."+game.pl[i].data.name+" - NOT FINISHED";
		    else{
                String s2=game.pl[i].time_str(game.pl[i].totaltime);
                s = (j+1)+"."+game.pl[i].data.name+" - "+s2;
            };
            game.fuente.show_text(game.batch,135,380-40*j,s,0);
            if(i<game.nhumans) game.plcursor[game.gdata.icons[i]].render2d( game.batch,0,0,32,32,100,375-40*j,130,375+30-40*j,1.0f);
        };
        if((game.game_mode==Main.ENDURANCE) && (game.pl[0].raceover) && (game.position[0]==0))
            game.fuente.show_text(game.batch, 110,140,"CONGRATULATIONS,STAGE CLEARED!",0);

        game.fuente.show_text(game.batch, 40,30,"PUSH ANY BUTTON OF PLAYER ONE TO PROCEED",1);

        game.batch.end();

        // LOGIC ===================================
        //ctr->actualiza();
        //if(counter==1) { mus->stop(); for(i=0; i<nplayers; i++) pl[i]->stop_sound();};
        if(counter>=30)
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])) /*|| (game.ctr.tecla(DIK_ESCAPE))*/){
                switch(game.game_mode){
                    case Main.SINGLE_R :
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                    break;
                    case Main.CHAMPIONSHIP :
                    case Main.VERSUS_R :
                        game.setScreen(new RankingScreen(game));
                        dispose();
                        break;
                    case Main.ENDURANCE :
                        // Winner
                        if((game.pl[0].raceover) && (game.position[0]==0)){
                        if(game.gdata.sel_endur==game.gdata.res_endur-1)
                            if(game.gdata.res_endur==15) {
                                game.new_ship=ship.GOLDEN_QUEEN;
                                game.setScreen(new NewShipAvailableScreen(game));
                                dispose();
                            }
                            else {
                                game.gdata.res_endur+=1;
                                game.setScreen(new MainMenuScreen(game));
                                dispose();
                            };
                    }else {
                            game.setScreen(new MainMenuScreen(game));
                            dispose();
                        }
                    break;
                };
                //destroy_course();
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
