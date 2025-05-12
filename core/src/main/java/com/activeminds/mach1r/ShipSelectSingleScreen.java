package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class ShipSelectSingleScreen implements Screen {

    Main game;
    ShaderProgram shader;
    int sel_offset = 0, sel_dir = 0;

    ShipSelectSingleScreen(Main game)
    {
        this.game = game;
        game.counter = 0f;

        // Crear shaders
        String vertexShader = Gdx.files.internal("shader/ship_vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shader/ship_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + shader.getLog());
        }

        game.play_voice("selectship.wav");
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta * 70;
        //if(cur_wait > 0) cur_wait -= delta;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();
        game.batch.end();


        game.camera.position.set(-3f, 6f, 15f);
        game.camera.lookAt(-3, 0, 0);
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


	    game.pl[0].mesh.render(shader, game.camera,sel_offset,0,0,0,game.counter/100.0f,0);
        shader.end();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        game.batch.begin();
        game.fuente.show_text(game.batch, 150,450,"PLEASE SELECT YOUR SHIP",1);
        //game.fuente.show_text(game.batch,30,350, game.pl[0].data.name,0);
        game.pl[0].logo.render2d(game.batch, 0,0,256,128,30,290,30+256,290+128,1);
        //sprintf(s,"%d KG",pl[0]->data.weight);
        game.fuente.show_text(game.batch,30,120,"WEIGHT :",0); game.fuente.show_text(game.batch, 175,120,game.pl[0].data.weight+" KG",0);
        game.fuente.show_text(game.batch,30,90,"BOOST    ",0); game.statbar.render2d(game.batch, 0,0,25*(game.pl[0].data.enginef+1),16,175,90,175+50*(game.pl[0].data.enginef+1),90+16,1-0);
        game.fuente.show_text(game.batch,30,60,"HANDLING ",0); game.statbar.render2d(game.batch, 0,0,25*(game.pl[0].data.handling+1),16,175,60,175+50*(game.pl[0].data.handling+1),60+16,1-0);
        float sp=game.pl[0].data.enginef-(int)(game.pl[0].data.weight/500)+1;
        game.fuente.show_text(game.batch,30,30,"MAX.SPEED",0); game.statbar.render2d(game.batch, 0,0,25*(sp+1),16,175,30,(int)(175+50*(sp+1)),30+16,1-0);
        game.batch.end();

        game.ctr.renderButtonLayout(game.shapeRenderer);


        /*
        char car[5][10]={"LOW","MEDIUM","HIGH","VERY HIGH","AWESOME"}, s[30];
        int sp;

        GLfloat lightPos[4] = {100.0f, 50.0f, 0.0f};
        GLfloat ambient[] = { 0.2f, 0.2f, 0.2f, 1.0f };
        GLfloat diffuse[] = { 0.8f, 0.8f, 0.8f, 1.0f };

        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 640, 0, 480);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);
        if(ctr->tecla(DIK_F1)){
            glClearColor(0.0,0.0,0.0,1.0);
            glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
        }else show_scrolling_wallp();

        glClear(GL_DEPTH_BUFFER_BIT);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45,1.3,0.1,ZFAR);
        gluLookAt(0,0,1,0,0,0,0.0,1.0,0.0);
        glEnable(GL_LIGHTING);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LIGHT0); glDisable(GL_LIGHT1); glDisable(GL_LIGHT2); glDisable(GL_LIGHT3);
        glDisable(GL_LIGHT4); glDisable(GL_LIGHT5); glDisable(GL_LIGHT6); glDisable(GL_LIGHT7);
        glLightfv(GL_LIGHT0,GL_AMBIENT,ambient);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,diffuse);
        glLightfv(GL_LIGHT0,GL_POSITION,lightPos);

        if(ctr->tecla(DIK_F1)) cam.look_at(0,4,9,0,0,0);
        else cam.look_at(-3,6,15,-3,0,0);
        if(ctr->tecla(DIK_F2)) pl[0]->mesh->render(&cam,sel_offset,0,0,0,3.8,0);
	else pl[0]->mesh->render(&cam,sel_offset,0,0,0,counter/100.0,0);

        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 640, 0, 480);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);


        if(!ctr->tecla(DIK_F1)){
            fuente->show_text(150,450,"PLEASE SELECT YOUR SHIP",1);
            //fuente->show_text(30,350,pl[0]->data.name,0);
            pl[0]->logo->render2d(0,0,256,128,30,290,30+256,290+128,1);
            sprintf(s,"%d KG",pl[0]->data.weight);
            fuente->show_text(30,120,"WEIGHT :",0); fuente->show_text(175,120,s,0);
            fuente->show_text(30,90,"BOOST    ",0); statbar->render2d(0,0,25*(pl[0]->data.enginef+1),16,175,90,175+50*(pl[0]->data.enginef+1),90+16,1-0);
            fuente->show_text(30,60,"HANDLING ",0); statbar->render2d(0,0,25*(pl[0]->data.handling+1),16,175,60,175+50*(pl[0]->data.handling+1),60+16,1-0);
            sp=pl[0]->data.enginef-int(pl[0]->data.weight/500)+1;
            fuente->show_text(30,30,"MAX.SPEED",0); statbar->render2d(0,0,25*(sp+1),16,175,30,175+50*(sp+1),30+16,1-0);
        };
    */

        // LOGIC ================================================

        //if(counter==5) play_voice("selectship.smp");
        if(((game.ctr.der(controlm.TEC1)) || (game.ctr.der(game.gdata.controls[0]))) && (sel_dir==0)) {sel_dir=1;};
        if(((game.ctr.izq(controlm.TEC1)) || (game.ctr.izq(game.gdata.controls[0]))) && (sel_dir==0)) {sel_dir=2;};
        if(((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))) && (sel_dir==0) && (game.counter>30)) {
            game.wconfirm.playonce();
            /*delete pl[0];*/
            switch(game.game_mode) {
                case Main.SINGLE_R :
                    game.setScreen(new SingleRaceSelectScreen(game));
                    dispose();
                    break;
                case Main.ENDURANCE :
                    game.setScreen(new EnduranceSelectScreen(game));
                    dispose();
                    break;
                case Main.CHAMPIONSHIP :
                    game.random_ships(1);
                    game.setScreen(new ChampionshipStageScreen(game));
                    dispose();
                    break;
            };
        };
        if(sel_dir==1){
            sel_offset--;
            if(sel_offset==-20){
                // Change ship
                do{
                    game.gdata.sel_ship[0]++;
                    if(game.gdata.sel_ship[0]>=Main.NSHIPS) game.gdata.sel_ship[0]-=Main.NSHIPS;
                }while(game.gdata.available[game.gdata.sel_ship[0]]==0);

                //delete pl[0];
                game.pl[0]=new ship(game.gdata.sel_ship[0],0,null);
                sel_offset=20;
            };
            if(sel_offset==0){
                sel_dir=0;
            };
        }
        if(sel_dir==2){
            sel_offset++;
            if(sel_offset==20){
                // Change ship
                do{
                    game.gdata.sel_ship[0]--;
                    if(game.gdata.sel_ship[0]<0) game.gdata.sel_ship[0]+=Main.NSHIPS;
                }while(game.gdata.available[game.gdata.sel_ship[0]]==0);

                //delete pl[0];
                game.pl[0]=new ship(game.gdata.sel_ship[0],0,null);
                sel_offset=-20;
            };
            if(sel_offset==0){
                sel_dir=0;
            };
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

    }
}
