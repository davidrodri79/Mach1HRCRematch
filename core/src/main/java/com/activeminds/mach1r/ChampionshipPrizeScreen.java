package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ChampionshipPrizeScreen implements Screen {

    Main game;
    course.champ_event event;
    float cur_wait;
    ShaderProgram shader;

    public ChampionshipPrizeScreen(Main game)
    {
        this.game = game;
        game.counter = 0;
        cur_wait = 0;
        event = course.championship.champ_events.get(game.gdata.sel_champ);

        // Crear shaders
        String vertexShader = Gdx.files.internal("shader/ship_vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shader/ship_fragment.glsl").readString();

        fragmentShader = "#define LIGHTING_ENABLED 1\n" +
            "#define SPECULAR_ENABLED 1\n" +
            fragmentShader;

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + shader.getLog());
        }
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        float cup_color[][] = {
            {234/255.0f,207/255.0f,21/255.0f},
            {192/255.0f,192/255.0f,192/255.0f},
            {244/255.0f,128/255.0f,11/255.0f}};
        int p = 0;
        game.counter += delta * 70;
        if(cur_wait > 0) cur_wait -= delta;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();

        game.fuente.show_text(game.batch, 40,440,Main.loc.get(event.name),0);
        game.fuente.show_text(game.batch, 230,400,Main.loc.get("finalResults"),0);
        String s = Main.loc.get("youPlaced")+" "+Main.loc.get(game.rank_str[game.ranking_position(0)]);
        game.fuente.show_text(game.batch, 220,360,s,1);
        if(game.ranking_position(0)<3){
            switch(game.ranking_position(0)){
                case 0 : p=event.reward; break;
                case 1 : p=(int)(event.reward/2.0); break;
                case 2 : p=(int)(event.reward/4.0); break;
            };
            game.fuente.show_text(game.batch, 200,110,Main.loc.get("congratulations"),0);
            s = Main.loc.get("receivedAPrizeOf")+" "+p+" "+Main.loc.get("points");
            game.fuente.show_text(game.batch,110,80,s,1);
        }else game.fuente.show_text(game.batch,175,80,Main.loc.get("sorryNoPrize"),1);

        game.fuente.show_text(game.batch,150,30,Main.loc.get("pushAnyButtonAccept"),1);
        game.batch.end();

        // 3D Layer
        game.camera.position.set(0f, 2f, 110f);
        game.camera.lookAt(0, 0, 0);
        game.camera.near = 0.1f;
        game.camera.far = 10000f;
        game.camera.update();

        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        shader.begin();

        shader.setUniformf("u_ambientColor", 0.2f, 0.2f, 0.2f);

        shader.setUniformi("u_numLights", 1);
        shader.setUniformf("u_lightPos[0]", new Vector3(3, 1000, 1000));
        shader.setUniformf("u_lightColor[0]", new Vector3(0.8f, 0.8f, 0.8f));
        shader.setUniformf("u_lightIntensity[0]", 1.0f);

        shader.setUniformf("u_fogColor", 1f, 1f, 1f); // gris claro
        shader.setUniformf("u_fogStart", 10.0f);
        shader.setUniformf("u_fogEnd", 1000.0f);


        //game.pl[0].mesh.render(shader, game.camera,sel_offset,0,0,0,game.counter/100.0f,0);

        if(game.ranking_position(0)<3) {
            game.cup.set_color_coef(cup_color[game.ranking_position(0)][0], cup_color[game.ranking_position(0)][1], cup_color[game.ranking_position(0)][2]);
            if (game.counter < 120)
                game.cup.render(shader, game.camera, 0, 0, 120 - (float) (game.counter), 0, game.counter / 100.0f, 0);
            else game.cup.render(shader, game.camera,0,0,0,0,game.counter/100.0f,0);
        }

        shader.end();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        // LOGIC =====================

        if((cur_wait==0) && (game.counter>30)){
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))){
                if(game.ranking_position(0)<3)
                    switch(game.ranking_position(0)){
                        case 0 : game.gdata.score_champ+=event.reward; break;
                        case 1 : game.gdata.score_champ+=(int)(event.reward/2.0); break;
                        case 2 : game.gdata.score_champ+=(int)(event.reward/4.0); break;
                    };
                game.save_game_data();
                if((game.gdata.score_champ>=15) && (game.gdata.available[ship.ICARUS]==0)){
                    game.new_ship=ship.ICARUS;
                    game.setScreen(new NewShipAvailableScreen(game));
                }else if((game.gdata.score_champ>=150) && (game.gdata.available[ship.HYDRA]==0)){
                    game.new_ship=ship.HYDRA;
                    game.setScreen(new NewShipAvailableScreen(game));
                }else if((game.gdata.score_champ>=1500) && (game.gdata.available[ship.TORNADO]==0)){
                    game.new_ship=ship.TORNADO;
                    game.setScreen(new NewShipAvailableScreen(game));
                }else if((game.gdata.score_champ>=150000) && (game.gdata.available[ship.VERTIGO]==0)){
                    game.new_ship=ship.VERTIGO;
                    game.setScreen(new NewShipAvailableScreen(game));
                }else
                    game.setScreen(new ChampionshipSelectScreen(game));
                    dispose();
            };
            //if(ctr->tecla(DIK_ESCAPE)) set_state(CHAMP_SEL);
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
