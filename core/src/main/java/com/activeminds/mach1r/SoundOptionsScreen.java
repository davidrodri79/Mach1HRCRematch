package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class SoundOptionsScreen implements Screen {

    Main game;
    float cur_wait = 0;
    int cursor = 0;


    public SoundOptionsScreen(Main game)
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

       game.fuente.show_text(game.batch,100,350,Main.loc.get("soundEffects"),0);
        if(game.gdata.sound)game.fuente.show_text(game.batch,400,350,Main.loc.get("on"),0);
        else game.fuente.show_text(game.batch,400,350,Main.loc.get("off"),0);
       game.fuente.show_text(game.batch,100,310,Main.loc.get("backgroundMusic"),0);
        if(game.gdata.music)game.fuente.show_text(game.batch,400,310,Main.loc.get("on"),0);
        else game.fuente.show_text(game.batch,400,310,Main.loc.get("off"),0);
       game.fuente.show_text(game.batch,100,270,Main.loc.get("musicVolume"),0);
       game.fuente.show_text(game.batch,400,270,""+game.gdata.music_volume,1);

       game.fuente.show_text(game.batch,100,230, Main.loc.get("backToOptions"),0);
       game.fuente.show_text(game.batch,210,440, Main.loc.get("soundOptionsTitle"),1);

        game.show_menu_cursor(55,342-(40*cursor));

        game.batch.end();

        // LOGIC ====================================

        //game.ctr.actualiza();
        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0]))){ if(cursor>0) cursor--; cur_wait=20;};
            if((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0]))){ if(cursor<3) cursor++; cur_wait=20;};
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
                if(cursor==3) {
                    game.setScreen(new OptionsScreen(game));
                    dispose();
                }

            if((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0]))){
                switch(cursor){
                    case 0 : if(game.gdata.sound==false) game.gdata.sound=true; else game.gdata.sound=false; cur_wait=20; break;
                    case 1 : if(game.gdata.music==false) game.gdata.music=true; else game.gdata.music=false; cur_wait=20; break;
                    case 2 : if((game.gdata.music_volume<100) && ((int)game.counter%2==0)) game.gdata.music_volume+=1; break;
                };
                wave.WAVE_ENABLE=game.gdata.sound;
            };

            if((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0]))){
                switch(cursor){
                    case 0 : if(game.gdata.sound==false) game.gdata.sound=true; else game.gdata.sound=false; cur_wait=20; break;
                    case 1 : if(game.gdata.music==false) game.gdata.music=true; else game.gdata.music=false; cur_wait=20; break;
                    case 2 : if((game.gdata.music_volume>0) && ((int)game.counter%2==0)) game.gdata.music_volume-=1; break;
                };
                wave.WAVE_ENABLE=game.gdata.sound;
            };
            if(game.ctr.atr(controlm.TEC1)) {
                game.setScreen(new OptionsScreen(game));
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
