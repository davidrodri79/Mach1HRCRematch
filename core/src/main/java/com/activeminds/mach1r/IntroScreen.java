package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class IntroScreen implements Screen {

    Main game;
    ShaderProgram shader;
    float angle = 0f;
    float counter = 0;

    public IntroScreen(Main game)
    {
        this.game = game;

        // Crear shaders
        String vertexShader = Gdx.files.internal("shader/ship_vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shader/ship_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + shader.getLog());
        }

        game.play_music("sound/am.mp3");
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        counter += delta*70;

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);

        game.camera.position.set(0f, 2f, 350f);
        game.camera.lookAt(0, 0, 0);
        game.camera.near = 0.1f;
        game.camera.far = 10000f;
        game.camera.update();


        shader.begin();

        shader.setUniformf("u_ambientColor", 0.2f, 0.2f, 0.2f);

        shader.setUniformi("u_numLights", 1);
        shader.setUniformf("u_lightPos[0]", new Vector3(3, 1000, 1000));
        shader.setUniformf("u_lightColor[0]", new Vector3(0.8f, 0.8f, 0.8f));
        shader.setUniformf("u_lightIntensity[0]", 1.0f);

        shader.setUniformf("u_fogColor", 0f, 0f, 0f); // gris claro
        shader.setUniformf("u_fogStart", 5000.0f);
        shader.setUniformf("u_fogEnd", 10000.0f);

        if(counter<490)
            game.brain.render(shader, game.camera,0,0,-9800+20*(float)(counter),0,counter/100f,(490-counter)/100f);
	    else
            game.brain.render(shader, game.camera,0,0,0,0,counter/100f,0);

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        if(counter>=1050){
            game.active.render2d(game.batch,0,0,256,64,50,380,300,420,1f);
            game.minds.render2d(game.batch,0,0,256,64,320,380,620,420,1f);
        }else if (counter>700){
            game.active.render2d(game.batch,0,0,256,64,50,380,300,420,(counter-700)/350f);
            game.minds.render2d(game.batch,0,0,256,64,320,380,620,420,(counter-700)/350f);
        }
        if(counter>=1400)
            game.presents.render2d(game.batch,0,0,256,64,200,50,440,85,1f);
        else if (counter>1050)
            game.presents.render2d(game.batch,0,0,256,64,200,50,440,85,(counter-1050)/350f);
        game.batch.end();

        if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])) || (counter>=1680)){
            //mus->stop(); mus->release();
            game.setScreen(new TitleScreen(game));
            dispose();
        }


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

        game.stop_music();
    }
}
