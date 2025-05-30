package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.ScreenUtils;

public class StartupScreen implements Screen {

    Main game;
    int counter = 0;
    console con;
    float wait;

    public StartupScreen(Main game)
    {
        this.game = game;
        con = new console();
        wait = 0f;
    }

    void delay(int millis)
    {
        wait += millis/1000.f;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        String s;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        //game.batch.draw(game.image, 140, 210);
        for(int i = 0; i < con.mess.size(); i++)
        {
            game.fuente.show_text(game.batch, 20, 450-25*i, con.mess.get(i), 0);
        }
        game.batch.end();

        if(wait > 0)
        {
            wait -= delta;
        }
        else {
            switch (counter) {

                case 1:
                    con.add_mess(game.loc.get("consStart1")+"1.0");
                    con.add_mess(game.loc.get("consStart2"));
                    con.add_mess(game.loc.get("consStart3")+"2025 april 5");
                    con.add_mess("-------------------------------------------");
                    con.add_mess(game.loc.get("startSystem"));
                    //changed=TRUE;
                    break;

                case 2:
                    delay(1000);

                    int SCREENX = 0, SCREENY = 0;
                    switch (game.gdata.resol) {
                        case 0:
                            SCREENX = 320;
                            SCREENY = 240;
                            break;
                        case 1:
                            SCREENX = 400;
                            SCREENY = 300;
                            break;
                        case 2:
                            SCREENX = 512;
                            SCREENY = 384;
                            break;
                        case 3:
                            SCREENX = 640;
                            SCREENY = 480;
                            break;
                        case 4:
                            SCREENX = 800;
                            SCREENY = 600;
                            break;
                        case 5:
                            SCREENX = 1024;
                            SCREENY = 768;
                            break;
                    }
                    ;
                    s = game.loc.get("gfxMode")+" " + SCREENX + "x" + SCREENY + "x16b "+game.loc.get("initialized");
                    //s = "graphics mode "+Gdx.graphics.getDisplayMode().width+"x"+Gdx.graphics.getDisplayMode().height+"x"+Gdx.graphics.getDisplayMode().bitsPerPixel+"b initialized.";
                    con.add_mess(s);
                    s = game.loc.get("usingZbuffer")+"24b";
                    con.add_mess(s);
                    /*changed=TRUE;*/
                    break;

                case 3:
                delay(500);
                s = "gl_renderer : "+Gdx.gl.glGetString(GL20.GL_RENDERER).toLowerCase();
                con.add_mess(s);
                /*if(!accelerated){
                    con->add_mess("warning : no 3d hardware found!");
                    con->add_mess("running software version.");
                };
                changed=TRUE;*/
                    break;

                case 4:
                delay(300);
                //changed=TRUE;
                    break;


                case 5:
                    delay(300);
                    con.add_mess(game.loc.get("loadMiscText"));
                    game.flame = new texture("sprite/light.png", texture.TEX_PCX, false, true);
                    game.shield = new texture("sprite/shield.png", texture.TEX_PCX, false, true);
                    game.explos = new texture("sprite/explos.png", texture.TEX_PCX, false, true);
                    game.smoke = new texture("sprite/smoke.png", texture.TEX_PCX, false, true);
                    game.smokeCloud = new texture("sprite/smoke-cloud.png", texture.TEX_PCX, false, true);
                    game.spark = new texture("sprite/spark.png", texture.TEX_PCX, false, true);
                    game.moon = new texture("sprite/moon.png", texture.TEX_PCX, false, true);
                    /*changed=TRUE;*/
                    break;

                case 6:
                    delay(300);
                    con.add_mess(game.loc.get("loadMiscModel"));
                /*burnt=new solid();
                burnt->load_mesh("model\\burnt.msh");
                burnt->centrate(TRUE,TRUE,TRUE);*/
                    game.cup = new solid();
                    game.cup.load_mesh("model/cup.msh", true);
                    game.cup.centrate(true, true, true);
                    game.brain = new solid();
                    game.brain.load_mesh("intro/brain.msh", true);
                    game.brain.centrate(true, true, true);
                    game.active = new sprite(256, 64, "intro/active.png");
                    game.minds = new sprite(256, 64, "intro/minds.png");
                    game.presents = new sprite(256, 64, "intro/presents.png");
                    /*changed=TRUE;*/
                    break;

                case 7:
                    delay(300);
                    con.add_mess(game.loc.get("initInput"));
                    game.ctr = new controlm(game.camera2d, game.manager);
                    game.ctr.loadButtonLayoutFromJson("joypad.json");
                    for (int i = 0; i < Controllers.getControllers().size; i++) {
                        con.add_mess(Controllers.getControllers().get(i).getName().toLowerCase() + game.loc.get("found."));
                    }
                /*ctr->initialize(hwnd);
                if(ctr->joystick_available())
                    con->add_mess("directinput compatible joystick found.");
                changed=TRUE;*/
                    break;

                case 8:
                    delay(300);
                    con.add_mess(game.loc.get("loadGameSprites"));
                    /*light=new sprite(2,2,"sprite\\light");*/
                    game.posnumber = new sprite(64, 64, "sprite/positionhd.png");
                    game.speed = new sprite(64, 64, "sprite/speedhd.png");
                    game.kmh = new sprite(128, 32, "sprite/kmhhd.png");
                    game.power[0] = new sprite(256, 128, "sprite/power.png");
                    game.power[1] = new sprite(256, 128, "sprite/fullpowe.png");
                    game.spower[0] = new sprite(256, 32, "sprite/spower.png");
                    game.spower[1] = new sprite(256, 32, "sprite/sfullpow.png");
                    game.recover = new sprite(128, 64, "sprite/recover.png");
                    game.start[0] = new sprite(256, 256, "sprite/start3.png");
                    game.start[1] = new sprite(256, 256, "sprite/start2.png");
                    game.start[2] = new sprite(256, 256, "sprite/start1.png");
                    game.start[3] = new sprite(256, 256, "sprite/startgo.png");
                    game.wallp = new sprite(128, 128, "sprite/wallp.png");
                    game.title[0] = new sprite(256, 256, "sprite/title1.png", false);
                    game.title[1] = new sprite(256, 256, "sprite/title2.png", false);
                    for (int i = 0; i < 6; i++) {
                        s = "scene/" + course.scenes.course_scenes.get(i).name + "pr.png";
                        game.preview[i] = new sprite(128, 128, s);
                    }
                    ;
                    game.shadow=new texture("sprite/shade.png",texture.TEX_PCX,false,true);
                    for (int i = 0; i < Main.NSHIPS; i++) {
                        s = "model/" + ship.models.ships.get(i).file + "sm.png";
                        game.mini[i] = new sprite(128, 128, s);
                    }
                    ;
                    game.mistery = new sprite(128, 128, "sprite/mistery.png");
                    int i = 0;
                    while (i < 10) {
                        s = "cursor/c" + (i + 1) + ".png";
                        game.plcursor[i] = new sprite(32, 32, s);
                        i++;
                    }
                    ;
                    game.arrow = new sprite(32, 32, "cursor/arrow.png");
                    game.statbar = new sprite(128, 16, "sprite/statbar.png");
                    game.menucur = new sprite(256, 256, "cursor/menucur.png");
                    game.oppico = new sprite(32, 32, "cursor/opp.png");
                    //changed=TRUE;*/
                    break;

                case 9:
                    delay(300);
                    con.add_mess(game.loc.get("loadGameSamples"));
                    game.wthree=new wave();
                    game.wthree.load("sound/trhee.wav");
                    game.wtwo=new wave();
                    game.wtwo.load("sound/two.wav");
                    game.wone=new wave();
                    game.wone.load("sound/one.wav");
                    game.wgo=new wave();
                    game.wgo.load("sound/go.wav");
                    game.wconfirm=new wave();
                    game.wconfirm.load("sound/takepow.wav");

                // Load intro music
                /*ZeroMemory(&XMParams, sizeof XMParams);
                XMParams.classID = CLSID_TRACKERXM;
                sprintf(s,"intro\\am.mus");
                XMParams.fileName = s;
                XMParams.flags = FLAG_LOOP;
                XMParams.input = INPUT_DISK;
                XMParams.output = OUTPUT_DSOUND;

                gAudio->getSoundClass(SOUNDCLASS_XM, &XMParams, (void**)&mus);
                changed=TRUE;*/
                    break;
            }
            ;
            counter++;
            if (counter > 12) {
                //lock=FALSE;
                game.setScreen(new IntroScreen(game));
                dispose();
            }
            ;
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
