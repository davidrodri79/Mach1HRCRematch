package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class RaceScreen implements Screen {

    Main game;
    ShaderProgram shipShader;

    public RaceScreen(Main game)
    {
        this.game = game;

        // Crear shaders
        String vertexShader = Gdx.files.internal("vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        shipShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shipShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + shipShader.getLog());
        }
    }

    void update_level_action()
    {
        /*int i, j, k, l;

        if((state==SINGLE) || (state==VERSUS)){
            if(counter==5)
                switch(rand()%3){
                    case 0 : play_voice("start1.smp"); break;
                    case 1 : play_voice("start2.smp"); break;
                    case 2 : play_voice("start3.smp"); break;
                };
            if(counter==360) wthree->playonce();
            if(counter==420) wtwo->playonce();
            if(counter==480) wone->playonce();
            if(counter==560) wgo->playonce();
        };

        if(counter%1000==0)
            hour_environment();

        //Item cubes
        if(counter%240==0)
            cour->add_random_item();*/

        game.cour.update();

        /*ctr->actualiza();

        //if(ctr->tecla(DIK_F1)){pl[0]->raceover=TRUE;};
        //if(ctr->tecla(DIK_F2)){pl[1]->raceover=TRUE;};*/

        for(int i=0; i<game.nhumans; i++){
            //cam.look_at(pl[i]->cam_pos.x,pl[i]->cam_pos.y,pl[i]->cam_pos.z,pl[i]->vrp.x,pl[i]->vrp.y,pl[i]->vrp.z);
            if(game.pl[i].raceover) game.pl[i].ia_update(game.camera,game.ctr);
		else
            game.pl[i].update(game.camera,game.ctr,game.gdata.controls[i]);
            //if(pl[i]->finallapflag) { play_voice("finallap.smp"); pl[i]->finallapflag=FALSE;};
        };

        for(int i=game.nhumans; i<game.nplayers; i++)
            game.pl[i].ia_update(game.camera,game.ctr);

        /*for(i=0; i<nplayers; i++)
            for(j=0; j<nplayers; j++)
                if(i<j)
                    if(pl[i]->collide(pl[j]))
        cour->play_3d_sample(&cam,pl[i]->x,pl[i]->y,pl[i]->z,pl[i]->colsound);

        l=0;
        while((l<nplayers) && (pl[position[l]]->raceover)) l++;

        for(i=0; i<nplayers; i++)
            for(j=l; j<nplayers-1; j++)
                if(more_advanced(pl[position[j+1]],pl[position[j]])){

                    k=position[j];
                    position[j]=position[j+1];
                    position[j+1]=k;

                    pl[position[j]]->pos=j+1;
                    pl[position[j+1]]->pos=j+2;
                };*/
    }

    void show_level_action(int follow)
    {
        /*float ry, size;
        int i,j,l;
        char s[200];
        vertex v1,v2,v3,v4;

        GLfloat lightPos[4];
        GLfloat ambientLight[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        GLfloat diffusebackg[] = { 0.8f, 0.8f, 0.8f, 1.0f };
        */

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);

        game.camera.position.set(game.pl[follow].cam_pos.x, game.pl[follow].cam_pos.y, game.pl[follow].cam_pos.z);
        game.camera.lookAt(game.pl[follow].vrp.x, game.pl[follow].vrp.y, game.pl[follow].vrp.z);
        game.camera.up.set(0, 1, 0);
        game.camera.near = 0.1f;
        game.camera.far = 10000f;
        game.camera.update();

        /*
        cam.look_at(pl[follow]->cam_pos.x,pl[follow]->cam_pos.y,pl[follow]->cam_pos.z,pl[follow]->vrp.x,pl[follow]->vrp.y,pl[follow]->vrp.z);

        sun.reset();
        sun.translate(-cam.x,-cam.y,-cam.z);
        sun.rotate(-cam.rx,-cam.ry,-cam.rz);
        lightPos[0]=sun.nx; lightPos[1]=sun.ny; lightPos[2]=sun.nz; lightPos[3]=0.0;

        glEnable(GL_LIGHT0);
        glLightfv(GL_LIGHT0,GL_AMBIENT,ambientLight);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,fogc);
        glLightfv(GL_LIGHT0,GL_POSITION,lightPos);

        glDisable(GL_BLEND);

        glDisable(GL_LIGHTING);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);

        //Ground
        if(gdata.skygrfog){

            glEnable(GL_FOG);
            show_sky();
        };
        show_ground();

        glClear(GL_DEPTH_BUFFER_BIT);

        // The course
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,diffusebackg);*/

        int l=30+(15*game.gdata.drawdist);
        if (game.nhumans>1) l= (int) (0.75*l);

        game.cour.render(shipShader, game.camera,game.pl[follow].segment, (long) game.counter,l);

        /*//Ships
        if(accelerated){
            glDisable(GL_DEPTH_TEST);

            for(i=nplayers-1; i>=0; i--)
                show_shadow(&cam,pl[i]);
        };*/

        for(int i=game.nplayers-1; i>=0; i--)
            show_ship(game.camera,game.pl[i]);

        // The fuel wastes
        /*glDisable(GL_FOG);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDepthMask(0);
        for(i=nplayers-1; i>=0; i--)
            show_ship_flame(&cam,pl[i]);

        // Opponent cursor in versus
        glDisable(GL_CULL_FACE);
        for(i=0; i<nhumans; i++){
            size=0.1*sqrt(pl[i]->dist);
            if((i!=follow) && (pl[i]->dist>200*200) && (pl[i]->dist<1000*1000)){
                show_3d_sprite(&cam,plcursor[gdata.icons[i]]->tex,0,0,1,1,pl[i]->renderx,pl[i]->y+(size*1.1),pl[i]->renderz,1.0,1.0,1.0,size,size,1.0);
                show_3d_sprite(&cam,arrow->tex,0,0,1,1,pl[i]->renderx,pl[i]->y+(size/3.0),pl[i]->renderz,1.0,1.0,1.0,size/2,size/2,1.0);
            };
        };
        glDepthMask(1);


        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 640, 0, 480);
        glDisable(GL_LIGHTING);

        // Ship messages
        if(pl[follow]->messcount>0)
        fuente->show_text(100,320,pl[follow]->message,0);

        if(pl[follow]->state==DESTR){
        fuente->show_text(180,350,"DISQUALIFIED FROM RACE",1);
        fuente->show_text(270,100,"GAME OVER",1);
    };

        //Start countdown
        for(j=0; j<4; j++)
            if((cour->counter>360+(60*j)) && (cour->counter<=420+(60*j))){

                i=counter-(360+(60*j));
                size=2000/(0.25*i);

                start[j]->render2d(0,0,256,256,320-(size/2),240-(size/2),320+(size/2),240+(size/2),1-(i*0.015));
            };
*/
    }

    void show_ship(PerspectiveCamera cam, ship s)
    {
        int i,j, dur;
        float a, c, size, dy, sz, sy, dif;
        texture t;

        /*
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        if(accelerated)
            for(i=0; i<nplayers; i++)
                set_light(cam,i+1,pl[i]->x,pl[i]->y,pl[i]->z,pl[i]->lightcol[0],pl[i]->lightcol[1],pl[i]->lightcol[2],pl[i]->engine);

        dur=int(s->energy/4.0); if(dur==0) dur=1;
        if((s->energy<MAXENERGY/4) && (counter%dur<int(dur/3.0))){
        s->mesh->set_color_coef(DAMAGED_COL[0],DAMAGED_COL[1],DAMAGED_COL[2]);
        s->lowres->set_color_coef(DAMAGED_COL[0],DAMAGED_COL[1],DAMAGED_COL[2]);
        };
        if(s->hypermode>0){

            s->mesh->set_color_coef(HYPER_COL[0],HYPER_COL[1],HYPER_COL[2]);
            s->lowres->set_color_coef(HYPER_COL[0],HYPER_COL[1],HYPER_COL[2]);
        };*/
        s.render(shipShader, cam,true);

        /*
        s->mesh->set_color_coef(1.0,1.0,1.0);
        s->lowres->set_color_coef(1.0,1.0,1.0);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDepthMask(0);


        if(s->shield>0){

            //sz=s->data.sizez+4.0;
            dif=fabs(s->ry-cam->ry);
            sz=((4.0+s->data.sizex)*fabs(sin(dif)))+((4.0+s->data.sizez)*fabs(cos(dif)));
            //sy=s->data.sizey+4.0;
            sy=((4.0+s->data.sizez)*fabs(sin(cam->rx)))+((1.0+s->data.sizey)*fabs(cos(cam->rx)));

            if(s->shield>80) a=1.0;
            else a=1.0-((80-s->shield)/80.0);
            show_3d_sprite(cam,shield,0,0,1,1,s->renderx,s->y,s->renderz,0.3125,0.5507,0.9922,sz,sy,a);
        };

        // Ship burning and destroyed
        if((s->state>=BURN) || ((s->state==STUN) && (s->counter<=50))){

            switch(s->state){
                case STUN    : size=9; dur=50; t=explos; c=1.0; dy=0.0; break;
                case BURN    : size=3; dur=20; t=explos; c=1.0; dy=0.0; break;
                case BIGEXPL : size=15; dur=100; t=explos; c=1.0; dy=0.0; break;
                case DESTR   : size=10; dur=60; t=smoke; c=0.0; dy=3.0; break;
            };

            a=1.0-((s->counter%dur)*0.01);
            i=int((s->counter%dur)*16/dur);

            if(s->state!=BURN) show_3d_sprite(cam,t,0+(0.25*(i%4)),0.25+(0.25*int(i/4)),0.25+(0.25*(i%4)),0+(0.25*int(i/4)),s->renderx,s->y+dy,s->renderz,c,c,c,size,size,a);
		else
            for(j=0; j<NEXPLS; j++){
                i=int(s->expframe[j]);
                show_3d_sprite(cam,t,0+(0.25*(i%4)),0.25+(0.25*int(i/4)),0.25+(0.25*(i%4)),0+(0.25*int(i/4)),s->exppos[j][0],s->exppos[j][1],s->exppos[j][2],1.0,1.0,1.0,size,size,1.0-(s->expframe[j]*0.05));
            };
        };
        glDepthMask(1);*/

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        /*
        if(gdata.skygrfog)
            glEnable(GL_FOG);
        glFogfv(GL_FOG_COLOR, fogc);

        //3D Geometry
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45,1.3,0.1,ZFAR);
        gluLookAt(0,0,1,0,0,0,0.0,1.0,0.0);

        glClearColor(fogc[0],fogc[1],fogc[2],1.0);
        glClear(GL_COLOR_BUFFER_BIT);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();*/
        show_level_action(0);

        /*glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 640, 0, 480);
        glDisable(GL_LIGHTING);
        glDisable(GL_DEPTH_TEST);

        show_position(0,20,400);

        show_speed(pl[0]->velocity_kmh(),20,10);

        show_power(0,400,340,0);

        //Lap count
        sprintf(s,"LAP %d/%d",pl[0]->lap,cour->info.nlaps);
        fuente->show_text(200,400,s,1);
        sprintf(s,"BOOST %d",pl[0]->nboosts);
        fuente->show_text(500,345,s,1);


        show_icon_rank();

        show_map(550,70);

        if(paused) fuente->show_text(280,230,"paused",0);*/



        // LOGIC ============================================

        update_level_action();
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
