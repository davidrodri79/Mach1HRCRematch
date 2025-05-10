package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    Main game;
    int cursor = 0;
    float cur_wait=0;


    MainMenuScreen(Main game)
    {
        this.game = game;
        game.save_game_data();
        game.counter = 0f;
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta * 70;
        if(cur_wait > 0) cur_wait -= delta;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.title[0].render2d(game.batch, 0,0,256,256,192,460-128,192+128,460,1.0f);
        game.title[1].render2d(game.batch, 0,0,256,256,192+128,460-128,192+256,460,1.0f);
        game.fuente.show_text(game.batch, 240,260,"single race",0);
        game.fuente.show_text(game.batch, 240,220,"championship",0);
        game.fuente.show_text(game.batch, 240,180,"versus battle",0);
        game.fuente.show_text(game.batch, 240,140,"endurance",0);
        game.fuente.show_text(game.batch, 240,100,"options",0);
        game.fuente.show_text(game.batch, 110,40,"A GAME CREATED BY ACTIVE MINDS",1);
        game.show_menu_cursor(195,252-(40*cursor));
        game.batch.end();


        if(((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0]))) && (cur_wait<=0)) {cursor=(cursor+1)%5; cur_wait=20/70f;};
        if(((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0]))) && (cur_wait<=0)) {cursor-=1; if(cursor<0) cursor=4; cur_wait=20/70f;};
        if(game.counter>2700) {
            game.setScreen(new TitleScreen(game));
        }
        else if(game.counter>60){
            if(game.ctr.atr(controlm.TEC1)) {
                dispose();
                Gdx.app.exit();
            }
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
                switch(cursor){
                    case 0 :
                        game.game_mode=Main.SINGLE_R;
                        game.nhumans=1;
                        game.pl[0]=new ship(game.gdata.sel_ship[0],0,null);
                        game.setScreen(new ShipSelectSingleScreen(game));
                        dispose();
                    break;
                    case 1 :
                        game.game_mode=Main.CHAMPIONSHIP;
                        game.nhumans=1;
                        game.setScreen(new ChampionshipSelectScreen(game));
                        dispose();
                        break;
                    case 2 :
                        game.game_mode=Main.VERSUS_R;
                        game.nhumans=2;
                        game.setScreen(new SelectNumPlayersScreen(game));
                        dispose();
                        break;
                    case 3 :
                        game.game_mode=Main.ENDURANCE;
                        game.nhumans=1;
                        game.pl[0]=new ship(game.gdata.sel_ship[0],0,null);
                        game.setScreen(new ShipSelectSingleScreen(game));
                        dispose();
                        break;
                    case 4 :
                        game.setScreen(new OptionsScreen(game));
                        dispose();
                        break;
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
