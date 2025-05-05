package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.utils.ScreenUtils;

public class StartupScreen implements Screen {

    Main game;
    int counter = 0;
    console con;

    public StartupScreen(Main game)
    {
        this.game = game;
        con = new console();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.batch.begin();
        //game.batch.draw(game.image, 140, 210);
        for(int i = 0; i < con.mess.size(); i++)
        {
            game.fuente.show_text(game.batch, 20, 450-25*i, con.mess.get(i), 0);
        }
        game.batch.end();


        switch(counter){

            case 1 :
                con.add_mess("mach 1 hrc rematch                 -  v1.0");
                con.add_mess("by active minds, 2003-2025");
                con.add_mess("coded by nhsp - last update : 2025 april 5");
                con.add_mess("-------------------------------------------");
                con.add_mess("starting system...");
                //changed=TRUE;
                break;

            case 2 :
                /*delay(1000);

                switch(gdata.resol){
                    case 0 : SCREENX=320; SCREENY=240; break;
                    case 1 : SCREENX=400; SCREENY=300; break;
                    case 2 : SCREENX=512; SCREENY=384; break;
                    case 3 : SCREENX=640; SCREENY=480; break;
                    case 4 : SCREENX=800; SCREENY=600; break;
                    case 5 : SCREENX=1024; SCREENY=768; break;
                };
                sprintf(s,"graphics mode %dx%dx16b initialized.",SCREENX,SCREENY);
                con->add_mess(s);
                sprintf(s,"using z-buffer depth of 24b");
                con->add_mess(s);
                changed=TRUE;*/
                break;

            case 3 :
                /*delay(500);
                sprintf(s,"GL_RENDERER : %s", strlwr(renderer));
                con->add_mess(s);
                if(!accelerated){
                    con->add_mess("warning : no 3d hardware found!");
                    con->add_mess("running software version.");
                };
                changed=TRUE;*/
                break;

            case 4 :
                /*delay(300);
                changed=TRUE;*/
                break;

            case 5 :
                //delay(300);
                con.add_mess("loading miscellanous textures...");
                game.flame=new texture("sprite/light.png",texture.TEX_PCX,false,true);
                game.shield=new texture("sprite/shield.png",texture.TEX_PCX,false,true);
                game.explos=new texture("sprite/explos.png",texture.TEX_PCX,false,true);
                game.smoke=new texture("sprite/smoke.png",texture.TEX_PCX,false,true);
                /*changed=TRUE;*/
                break;

            case 6 :
                //delay(300);
                con.add_mess("loading miscellaneous 3d models...");
                /*burnt=new solid();
                burnt->load_mesh("model\\burnt.msh");
                burnt->centrate(TRUE,TRUE,TRUE);
                cup=new solid();
                cup->load_mesh("model\\cup.msh");
                cup->centrate(TRUE,TRUE,TRUE);*/
                game.brain=new solid();
                game.brain.load_mesh("intro/brain.msh", true);
                game.brain.centrate(true,true,true);
                game.active=new sprite(256,64,"intro/active.png");
                game.minds=new sprite(256,64,"intro/minds.png");
                game.presents=new sprite(256,64,"intro/presents.png");
                /*changed=TRUE;*/
                break;

            case 7 :
                /*delay(300);*/
                con.add_mess("initializing input devices...");
                game.ctr=new controlm();
                /*ctr->initialize(hwnd);
                if(ctr->joystick_available())
                    con->add_mess("directinput compatible joystick found.");
                changed=TRUE;*/
                break;

            case 8 :
                //delay(300);
                con.add_mess("loading game sprites...");
                /*light=new sprite(2,2,"sprite\\light");*/
                game.posnumber=new sprite(64,64,"sprite/position.png");
                game.speed=new sprite(64,64,"sprite/speed.png");
                game.kmh=new sprite(128,32,"sprite/kmh.png");
                game.power[0]=new sprite(256,128,"sprite/power.png");
                game.power[1]=new sprite(256,128,"sprite/fullpowe.png");
                game.spower[0]=new sprite(256,32,"sprite/spower.png");
                game.spower[1]=new sprite(256,32,"sprite/sfullpow.png");
                game.recover=new sprite(128,64,"sprite/recover.png");
                game.start[0]=new sprite(256,256,"sprite/start3.png");
                game.start[1]=new sprite(256,256,"sprite/start2.png");
                game.start[2]=new sprite(256,256,"sprite/start1.png");
                game.start[3]=new sprite(256,256,"sprite/startgo.png");
                game.wallp=new sprite(128,128,"sprite/wallp.png");
                game.title[0]=new sprite(256,256,"sprite/title1.png",false);
                game.title[1]=new sprite(256,256,"sprite/title2.png",false);
                /*for(i=0; i<6; i++){
                    sprintf(s,"scene\\%spr",scenes[i].name);
                    preview[i]=new sprite(128,128,s);
                };
                shadow=new texture("sprite\\shade",TEX_PCX,FALSE,TRUE);*/
                for(int i=0; i<Main.NSHIPS; i++){
                    String s = "model/"+ship.models.ships.get(i).file+"sm.png";
                    game.mini[i]=new sprite(128,128,s);
                };
                /*mistery=new sprite(128,128,"sprite\\mistery");*/
                int i=0;
                while(i<10){
                    String s = "cursor/c"+(i+1)+".png";
                    game.plcursor[i]=new sprite(32,32,s);
                    i++;
                };
                game.arrow=new sprite(32,32,"cursor/arrow.png");
                game.statbar=new sprite(128,16,"sprite/statbar.png");
                game.menucur=new sprite(256,256,"cursor/menucur.png");
                game.oppico=new sprite(32,32,"cursor/opp.png");
                //changed=TRUE;*/
                break;

            case 9 :
                //delay(300);
                con.add_mess("loading game samples...");
                /*wthree=new wave();
                wthree->load("sound\\trhee.smp");
                wtwo=new wave();
                wtwo->load("sound\\two.smp");
                wone=new wave();
                wone->load("sound\\one.smp");
                wgo=new wave();
                wgo->load("sound\\go.smp");
                wconfirm=new wave();
                wconfirm->load("sound\\takepow.smp");

                // Load intro music
                ZeroMemory(&XMParams, sizeof XMParams);
                XMParams.classID = CLSID_TRACKERXM;
                sprintf(s,"intro\\am.mus");
                XMParams.fileName = s;
                XMParams.flags = FLAG_LOOP;
                XMParams.input = INPUT_DISK;
                XMParams.output = OUTPUT_DSOUND;

                gAudio->getSoundClass(SOUNDCLASS_XM, &XMParams, (void**)&mus);
                changed=TRUE;*/
                break;
        };
        counter++;
        if(counter>12) {
            //lock=FALSE;
            game.setScreen(new IntroScreen(game));
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
