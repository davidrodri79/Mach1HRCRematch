package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class NewShipAvailableScreen implements Screen
{
    Main game;
    float cur_wait;
    ShaderProgram shader;

    public NewShipAvailableScreen(Main game)
    {
        this.game = game;
        game.counter = 0;
        cur_wait = 0f;

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

        game.gdata.available[game.new_ship]=1;
        game.pl[0]=new ship(game.new_ship,0,null);
        game.save_game_data();
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
        game.fuente.show_text(game.batch, 220,440,"CONGRATULATIONS!",1);
        game.fuente.show_text(game.batch,130,400,"A NEW SHIP BECAME AVAILABLE",1);
        game.fuente.show_text(game.batch, 90,30,"check it at ship selection screen",0);
        game.batch.end();

        // 3D Layer
        game.camera.position.set(0f, 2f, 12f);
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

        if(game.counter>1)
            if(game.counter<60) game.pl[0].mesh.render(shader, game.camera,0,0,(float)(game.counter)-60.0f,0,game.counter/100.0f,0);
		else		   game.pl[0].mesh.render(shader, game.camera,0,0,0,0,game.counter/100.0f,0);

        shader.end();

        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);


        // LOGIC =======================================

        //ctr->actualiza();
        if(game.counter>=30)
            if((game.ctr.algun_boton(game.gdata.controls[0])) /*|| (game.ctr.tecla(DIK_ESCAPE))*/)
            {
                game.pl[0] = null;
                game.setScreen(new MainMenuScreen(game));
                dispose();
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
