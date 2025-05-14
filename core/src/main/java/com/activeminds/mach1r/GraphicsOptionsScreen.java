package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class GraphicsOptionsScreen implements Screen {

    Main game;
    float cur_wait = 0;
    int cursor = 0;



    String resol_str[]={"320x200","400x300","512x384","640x480","800x600","1024x768"},
           dist_str[]={"distStr0","distStr1","distStr2"},
            time_str[]={"timeStr0","timeStr1","timeStr2","timeStr3"};

    public GraphicsOptionsScreen(Main game)
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

        game.fuente.show_text(game.batch,100,350,Main.loc.get("resolution"),0);
        game.fuente.show_text(game.batch,400,350,resol_str[game.gdata.resol],0);
        game.fuente.show_text(game.batch,100,310,Main.loc.get("drawingDistance"),0);
        game.fuente.show_text(game.batch,420,310,Main.loc.get(dist_str[game.gdata.drawdist]),0);
        game.fuente.show_text(game.batch,100,270,Main.loc.get("skyAndFog"),0);
        if(game.gdata.skygrfog) game.fuente.show_text(game.batch,440,270,Main.loc.get("on"),0);
        else game.fuente.show_text(game.batch,440,270,Main.loc.get("off"),0);
        game.fuente.show_text(game.batch,100,230,Main.loc.get("dayHour"),0);
        game.fuente.show_text(game.batch,420,230,Main.loc.get(time_str[game.gdata.daytime]),0);
        game.fuente.show_text(game.batch,100,190,Main.loc.get("language"),0);
        game.fuente.show_text(game.batch,420,190,Main.loc.get("currentLanguage"),0);

        game.fuente.show_text(game.batch,100,150,Main.loc.get("backToOptions"),0);
        game.fuente.show_text(game.batch,210,440,Main.loc.get("gfxOptionsTitle"),1);
        game.fuente.show_text(game.batch,30,30,Main.loc.get("someOptionsMustRestart"),1);

        game.show_menu_cursor(55,342-(40*cursor));

        game.batch.end();

        // LOGIC ====================================

        //game.ctr.actualiza();
        if((cur_wait<=0) && (game.counter>30)){
            if((game.ctr.arr(controlm.TEC1)) || (game.ctr.arr(game.gdata.controls[0]))){ if(cursor>0) cursor--; cur_wait=20;};
            if((game.ctr.aba(controlm.TEC1)) || (game.ctr.aba(game.gdata.controls[0]))){ if(cursor<5) cursor++; cur_wait=20;};
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))
                if(cursor==5) {
                    game.setScreen(new OptionsScreen(game));
                    dispose();
                }

            if((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0]))){
                switch(cursor){
                    case 0 : if(game.gdata.resol<5) {game.gdata.resol++; cur_wait=20;}; break;
                    case 1 : if(game.gdata.drawdist<2) {game.gdata.drawdist++; cur_wait=20;}; break;
                    case 2 : if(game.gdata.skygrfog==false) game.gdata.skygrfog=false; else game.gdata.skygrfog=false; cur_wait=20; break;
                    case 3 : if(game.gdata.daytime<3) {game.gdata.daytime++; cur_wait=20;}; break;
                    case 4 : game.gdata.language = (game.gdata.language + 1) % 2; Main.loc.loadLanguage(game.gdata.language); cur_wait=20; break;
                };
            };

            if((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0]))){
                switch(cursor){
                    case 0 : if(game.gdata.resol>0) {game.gdata.resol--; cur_wait=20;}; break;
                    case 1 : if(game.gdata.drawdist>0) {game.gdata.drawdist--; cur_wait=20;}; break;
                    case 2 : if(game.gdata.skygrfog==false) game.gdata.skygrfog=true; else game.gdata.skygrfog=false; cur_wait=20; break;
                    case 3 : if(game.gdata.daytime>0) {game.gdata.daytime--; cur_wait=20;}; break;
                    case 4 : game.gdata.language = (game.gdata.language - 1 + 2) % 2; Main.loc.loadLanguage(game.gdata.language); cur_wait=20; break;
                };
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
