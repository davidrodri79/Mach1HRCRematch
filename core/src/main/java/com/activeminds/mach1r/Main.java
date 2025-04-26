package com.activeminds.mach1r;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {
    public SpriteBatch batch;
    Texture image;

    font fuente;

    solid brain;

    sprite active, minds, presents;

    PerspectiveCamera camera;
    OrthographicCamera camera2d;


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        fuente = new font("sprite/FONT.png",16,16,14);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 200f);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 2000f;
        camera.update();

        camera2d = new OrthographicCamera();
        camera2d.setToOrtho(false,  Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        setScreen(new StartupScreen(this));

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        image.dispose();
    }
}
