package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class RaceScreen implements Screen {

    public static final float GROUNDWIDTH = 4900.0f;
    public static final float SKYWIDTH = 6000.0f;
    public static final float GROUNDTILE = 12.0f;
    public static final float MAPSCALE = 40.0f;

    public static final int SINGLE = 0;
    public static final int VERSUS = 1;
    public static final int FINISHED = 2;
    public static final int DISQUAL = 3;
    public static final int DEMO = 4;

    public static final float DAMAGED_COL[]={1.0f,0.5f,0.5f};
    public static final float HYPER_COL[]={0.96f,0.85f,0.04f};

    Main game;
    ShaderProgram shipShader, skyShader, billboardShader;
    solid groundMesh, skyMesh;
    Mesh billboard;
    texture ground;

    int state;
    long counter;

    public RaceScreen(Main game)
    {
        this.game = game;

        // Crear shaders
        String vertexShader = Gdx.files.internal("shader/ship_vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shader/ship_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        shipShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shipShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + shipShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/sky_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/sky_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        skyShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!skyShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + skyShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/billboard_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/billboard_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        billboardShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!billboardShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + billboardShader.getLog());
        }




        // Ground mesh
        vertex v[] = new vertex[4];
        int TILE = 7;
        v[0]=new vertex(0f,course.GROUNDY,0f);
        v[1]=new vertex(0f,course.GROUNDY,(GROUNDWIDTH/TILE));
        v[2]=new vertex((GROUNDWIDTH/TILE),course.GROUNDY,(GROUNDWIDTH/TILE));
        v[3]=new vertex((GROUNDWIDTH/TILE),course.GROUNDY,0f);

        ground= new texture("scene/"+course.scenes.course_scenes.get(game.cour.info.scene).name+"gr.bmp",texture.TEX_BMP,true,false);
        groundMesh = new solid();
        groundMesh.triangles = new ArrayList<>();
        groundMesh.textures = new Texture[1];
        groundMesh.textures[0] = ground.gdxTexture;
        groundMesh.triangles.add(new triangle(v[0], v[1], v[2], 0, 0, 0f, 0f, GROUNDTILE, GROUNDTILE, GROUNDTILE));
        groundMesh.triangles.add(new triangle(v[2], v[3], v[0], 0, GROUNDTILE, GROUNDTILE, 0f, GROUNDTILE, 0f, 0f));
        groundMesh.buildGdxMesh();

        // Sky mesh
        TILE = 5;
        v[0]=new vertex(0f,course.SKYY,0f);
        v[1]=new vertex(0f,course.SKYY,(SKYWIDTH/TILE));
        v[2]=new vertex((SKYWIDTH/TILE),course.SKYY,(SKYWIDTH/TILE));
        v[3]=new vertex((SKYWIDTH/TILE),course.SKYY,0f);

        float r = course.scenes.course_scenes.get(game.cour.info.scene).skycolor[0];
        float g = course.scenes.course_scenes.get(game.cour.info.scene).skycolor[1];
        float b = course.scenes.course_scenes.get(game.cour.info.scene).skycolor[2];
        skyMesh = new solid();
        skyMesh.textures = new Texture[0];
        skyMesh.triangles = new ArrayList<>();
        skyMesh.triangles.add(new triangle(v[0], v[2], v[1], r, g, b, r, g, b, r, g, b));
        skyMesh.triangles.add(new triangle(v[2], v[0], v[3], r, g, b, r, g, b, r, g, b));
        skyMesh.buildGdxMesh();

        // Billboard mesh
        billboard = new Mesh(true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );

        // Quad en XY (como un sprite 2D)
        float[] vertices = new float[] {
            -0.5f, -0.5f, 0f, 0f, 1f,
            0.5f, -0.5f, 0f, 1f, 1f,
            0.5f,  0.5f, 0f, 1f, 0f,
            -0.5f,  0.5f, 0f, 0f, 0f
        };

        short[] indices = new short[] { 0, 1, 2, 2, 3, 0 };

        billboard.setVertices(vertices, 0 , 20);
        billboard.setIndices(indices);

    }

    void set_state(int s)
    {
        state = s;
        counter = 0;
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
            hour_environment();*/

        //Item cubes
        if(game.cour.counter%240==0)
            game.cour.add_random_item();

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

        for(int i=0; i<game.nplayers; i++)
            for(int j=0; j<game.nplayers; j++)
                if(i<j)
                    if(game.pl[i].collide(game.pl[j]))
                    {
                        //cour->play_3d_sample(&cam,pl[i]->x,pl[i]->y,pl[i]->z,pl[i]->colsound);
                    }

        int l=0;
        while((l<game.nplayers) && (game.pl[game.position[l]].raceover)) l++;

        for(int i=0; i<game.nplayers; i++)
            for(int j=l; j<game.nplayers-1; j++)
                if(more_advanced(game.pl[game.position[j+1]],game.pl[game.position[j]])){

                    int k=game.position[j];
                    game.position[j]=game.position[j+1];
                    game.position[j+1]=k;

                    game.pl[game.position[j]].pos=j+1;
                    game.pl[game.position[j+1]].pos=j+2;
                };
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
        Gdx.gl.glClearColor(course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[0], course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[1], course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[2], 1.f);
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
        glDisable(GL_DEPTH_TEST);*/

        //Ground
        if(game.gdata.skygrfog){

            //glEnable(GL_FOG);
            show_sky();
        };
        show_ground();

        /*glClear(GL_DEPTH_BUFFER_BIT);

        // The course
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,diffusebackg);*/

        shipShader.setUniformf("u_ambientColor", 0.1f, 0.1f, 0.1f);

        shipShader.setUniformi("u_numLights", 1);
        shipShader.setUniformf("u_lightPos[0]", new Vector3(3, 1000, 1000));
        shipShader.setUniformf("u_lightColor[0]", new Vector3(1, 1, 1));
        shipShader.setUniformf("u_lightIntensity[0]", 1.0f);

        shipShader.setUniformf("u_fogColor", course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[0], course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[1], course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[2]); // gris claro
        shipShader.setUniformf("u_fogStart", 10.0f);
        shipShader.setUniformf("u_fogEnd", 1000.0f);

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
        glDepthMask(0);*/
        Gdx.gl.glDepthMask(false);
        for(int i=game.nplayers-1; i>=0; i--)
            show_ship_flame(game.camera,game.pl[i]);

        /*// Opponent cursor in versus
        glDisable(GL_CULL_FACE);
        for(i=0; i<nhumans; i++){
            size=0.1*sqrt(pl[i]->dist);
            if((i!=follow) && (pl[i]->dist>200*200) && (pl[i]->dist<1000*1000)){
                show_3d_sprite(&cam,plcursor[gdata.icons[i]]->tex,0,0,1,1,pl[i]->renderx,pl[i]->y+(size*1.1),pl[i]->renderz,1.0,1.0,1.0,size,size,1.0);
                show_3d_sprite(&cam,arrow->tex,0,0,1,1,pl[i]->renderx,pl[i]->y+(size/3.0),pl[i]->renderz,1.0,1.0,1.0,size/2,size/2,1.0);
            };
        };
        */
        //glDepthMask(1);
        Gdx.gl.glDepthMask(true);
        /*

        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 640, 0, 480);
        glDisable(GL_LIGHTING);*/

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        game.batch.begin();

        // Ship messages
        if(game.pl[follow].messcount>0)
            game.fuente.show_text( game.batch, 100,320,game.pl[follow].message,0);

        if(game.pl[follow].state==ship.DESTR){
            game.fuente.show_text(game.batch, 180,350,"DISQUALIFIED FROM RACE",1);
            game.fuente.show_text(game.batch, 270,100,"GAME OVER",1);
        };

        //Start countdown
        for(int j=0; j<4; j++)
            if((game.cour.counter>360+(60*j)) && (game.cour.counter<=420+(60*j))){

                long i=counter-(360+(60*j));
                float size=2000f/(0.25f*i);

                game.start[j].render2d(game.batch, 0,0,256,256,(int)(320-(size/2)),(int)(240-(size/2)),(int)(320+(size/2)),(int)(240+(size/2)),1f-(i*0.015f));
            };

        game.batch.end();

    }

    float nearest(int n, int m)
    {
        return n-(n%m);
    }
    void show_ground()
    {
        float gx, gz, TILE=7;
        int i,x,z;

        gx=nearest((int) game.camera.position.x, (int) (GROUNDWIDTH/GROUNDTILE))-(GROUNDWIDTH/2);
        gz=nearest((int) game.camera.position.z, (int) (GROUNDWIDTH/GROUNDTILE))-(GROUNDWIDTH/2);

        for(x=0; x<TILE; x++){

            gz=nearest((int) game.camera.position.z, (int) (GROUNDWIDTH/GROUNDTILE))-(GROUNDWIDTH/2);

            for(z=0; z<TILE; z++){

                groundMesh.render(shipShader, game.camera, gx, 0f, gz, 0f, 0f, 0f);
                gz+=GROUNDWIDTH/TILE;
            };

            gx+=GROUNDWIDTH/TILE;
        };
    }

    void show_sky()
    {
        float gx, gz, TILE=5;
        int i,x,z;

        skyShader.begin();
        skyShader.setUniformf("u_fogColor", course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[0], course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[1], course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[2]); // gris claro
        skyShader.setUniformf("u_fogStart", 10.0f);
        skyShader.setUniformf("u_fogEnd", 1000.0f);

        gx=nearest((int) game.camera.position.x, (int) (SKYWIDTH/GROUNDTILE))-(SKYWIDTH/2);
        gz=nearest((int) game.camera.position.z, (int) (SKYWIDTH/GROUNDTILE))-(SKYWIDTH/2);

        for(x=0; x<TILE; x++){

            gz=nearest((int) game.camera.position.z, (int) (SKYWIDTH/GROUNDTILE))-(SKYWIDTH/2);

            for(z=0; z<TILE; z++){

                skyMesh.render(skyShader, game.camera, gx, 0f, gz, 0f, 0f, 0f);
                gz+=SKYWIDTH/TILE;
            };

            gx+=SKYWIDTH/TILE;
        };

        skyShader.end();

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
                set_light(cam,i+1,pl[i]->x,pl[i]->y,pl[i]->z,pl[i]->lightcol[0],pl[i]->lightcol[1],pl[i]->lightcol[2],pl[i]->engine);*/

        dur=(int)(s.energy/4.0); if(dur==0) dur=1;
        if((s.energy<ship.MAXENERGY/4) && (counter%dur<(int)(dur/3.0))){
            s.mesh.set_color_coef(DAMAGED_COL[0],DAMAGED_COL[1],DAMAGED_COL[2]);
            s.lowres.set_color_coef(DAMAGED_COL[0],DAMAGED_COL[1],DAMAGED_COL[2]);
        };
        if(s.hypermode>0){

            s.mesh.set_color_coef(HYPER_COL[0],HYPER_COL[1],HYPER_COL[2]);
            s.lowres.set_color_coef(HYPER_COL[0],HYPER_COL[1],HYPER_COL[2]);
        };

        s.render(shipShader, cam,true);

        /*
        s->mesh->set_color_coef(1.0,1.0,1.0);
        s->lowres->set_color_coef(1.0,1.0,1.0);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDepthMask(0);*/


        Gdx.gl.glDepthMask(false);
        if(s.shield>0){


            Vector3 dir = cam.direction;

            // Yaw: rotación alrededor del eje Y (horizontal)
            float yaw = (float) Math.atan2(dir.x, dir.z);

            // Pitch: rotación alrededor del eje X (vertical)
            float pitch = (float) Math.asin(dir.y);

            //sz=s->data.sizez+4.0;
            dif=Math.abs(s.ry-pitch);
            sz= (float) (((4.0+s.data.sizex)*Math.abs(Math.sin(dif)))+((4.0+s.data.sizez)*Math.abs(Math.cos(dif))));
            //sy=s->data.sizey+4.0;
            sy= (float) (((4.0f+s.data.sizez)*Math.abs(Math.sin(yaw)))+((1.0f+s.data.sizey)*Math.abs(Math.cos(yaw))));

            if(s.shield>80) a=1.0f;
            else a=1.0f-((80-s.shield)/80.0f);
            show_3d_sprite(cam,game.shield,0,0,1,1,s.renderx,s.y,s.renderz,0.3125f,0.5507f,0.9922f,sz,sy,a);
        };

        // Ship burning and destroyed
        if((s.state>=ship.BURN) || ((s.state==ship.STUN) && (s.counter<=50))){

            switch(s.state){
                case ship.STUN    : size=9; dur=50; t=game.explos; c=1.0f; dy=0.0f; break;
                case ship.BURN    : size=3; dur=20; t=game.explos; c=1.0f; dy=0.0f; break;
                case ship.BIGEXPL : size=15; dur=100; t=game.explos; c=1.0f; dy=0.0f; break;
                case ship.DESTR   : size=10; dur=60; t=game.smoke; c=0.0f; dy=3.0f; break;
                default: size=10; dur=60; t=game.smoke; c=0.0f; dy=3.0f; break;
            };

            a=1.0f-((s.counter%dur)*0.01f);
            i=(int)((s.counter%dur)*16/dur);

            if(s.state!=ship.BURN) show_3d_sprite(cam,t,0f+(0.25f*(i%4)),0.25f+(0.25f*(i/4)),0.25f+(0.25f*(i%4)),0+(0.25f*(i/4)),s.renderx,s.y+dy,s.renderz,c,c,c,size,size,a);
		else
            for(j=0; j<ship.NEXPLS; j++){
                i=(int)(s.expframe[j]);
                show_3d_sprite(cam,t,0+(0.25f*(i%4)),0.25f+(0.25f*(i/4)),0.25f+(0.25f*(i%4)),0+(0.25f*(i/4)),s.exppos[j][0],s.exppos[j][1],s.exppos[j][2],1.0f,1.0f,1.0f,size,size,1.0f-(s.expframe[j]*0.05f));
            };
        };
        Gdx.gl.glDepthMask(true);
        //glDepthMask(1);

    }

    void show_ship_flame(PerspectiveCamera cam, ship s)
    {

        float a=s.engine,f;
        float sx=5.0f*s.data.lsize*a, sy=5.0f*s.data.lsize*a;
        int i;

        for(i=0; i<s.data.nlights; i++)
            show_3d_sprite(cam,game.flame,0,0,1,1,s.light_x(i),s.light_y(i),s.light_z(i),s.lightcol[0],s.lightcol[1],s.lightcol[2],sx,sy,a);

    }

    boolean more_advanced(ship s1, ship s2)
    {
        if(s1.lap>s2.lap) return true;
        else if(s1.lap<s2.lap) return false;
        else if(s1.lap==s2.lap){
            if(s1.nextsegment>s2.nextsegment) return true;
            else if(s1.nextsegment<s2.nextsegment) return true;
            else if(game.cour.distance_to_segment(s1.renderx,s1.y,s1.renderz,s1.nextsegment)<
                game.cour.distance_to_segment(s2.renderx,s2.y,s2.renderz,s2.nextsegment))
                return true;
            else return false;
        };
        return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        counter++;
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
        glDisable(GL_DEPTH_TEST);*/

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        game.batch.begin();
        show_position(0,20,400);

        show_speed((int) game.pl[0].velocity_kmh(),20,10);

        show_power(0,400,340,0);

        //Lap count
        String s = "LAP "+game.pl[0].lap+"/"+game.cour.info.nlaps;
        game.fuente.show_text(game.batch, 200,400,s,1);
        s = "BOOST "+game.pl[0].nboosts;
        game.fuente.show_text(game.batch,500,345,s,1);


        show_icon_rank();

        show_map(550,70);

        /*if(paused) fuente->show_text(280,230,"paused",0);*/

        game.batch.end();

        // LOGIC ============================================

        if (state == SINGLE)
        {
            //if(!paused)
                update_level_action();
            //else if ((ctr->algun_boton(TEC1)) || (ctr->algun_boton(gdata.controls[0]))) paused=FALSE;
            if(game.pl[0].raceover) set_state(FINISHED);
            if(game.pl[0].state==ship.DESTR) set_state(DISQUAL);
            //if(ctr->tecla(DIK_ESCAPE)) {abort_champ=TRUE; set_state(RACE_RESULT);};
            //if((ctr->tecla(DIK_F5)) && (cour->counter>360)) {paused=TRUE;};
        }
        else if (state == VERSUS)
        {

        }
        else if (state == FINISHED || state == DISQUAL)
        {
            update_level_action();
            //if(counter==1) mus->stop();
            //if((counter==1) && (state==FINISHED)) play_voice("finished.smp");
            //if((counter==5) && (state==DISQUAL)) play_voice("badluck.smp");
            //ctr->actualiza();
            //ctr->actualiza();
            if((counter>=300) && (game.ctr.algun_boton(game.gdata.controls[0]))){
                game.setScreen(new RaceResultScreen(game));
                dispose();
            };
        }
        else if (state == DEMO)
        {
            update_level_action();
            //ctr->actualiza();
            if((game.ctr.algun_boton(controlm.TEC1)) || (counter>7200) || (game.pl[0].state==ship.DESTR))
            {
                //destroy_course();
                game.setScreen(new TitleScreen(game));
                dispose();
            };
        }
    }

    void show_position(int i, int x, int y)
    {

        int p;
        // Position indicator
        p=game.pl[i].pos-1;
        game.posnumber.render2d(game.batch,(p%4)*16,64-(16*((int)(p/4)+1)),((p%4)+1)*16,64-(16*(int)(p/4)),x,y,x+64,y+64,1);
        p=10;
        game.posnumber.render2d(game.batch,32,16,48,32,x+50,y,x+114,y+64,1);
        p=game.nplayers-1;
        game.posnumber.render2d(game.batch,(p%4)*16,64-(16*((int)(p/4)+1)),((p%4)+1)*16,64-(16*(int)(p/4)),x+105,y,x+153,y+48,1);


    }
    void show_speed(int sp, int x, int y)
    {

        int j,p;
        String v;
        // Speed counter
        v = ""+sp;
        if(sp < 1000) v = " "+v;
        if(sp < 100) v = " "+v;
        if(sp < 10) v = " "+v;

        for(j=0; j<4; j++){
            p=(int)(v.charAt(j)-'0');
            if(v.charAt(j)!=' ') game.speed.render2d(game.batch, (p%4)*16,64-(16*((int)(p/4)+1)),((p%4)+1)*16,64-(16*(int)(p/4)),x+48*j,y,x+64+48*j,y+64,1);
        };
        game.kmh.render2d(game.batch, 0,0,128,32,x+200,y+10,x+328,y+42,1);

    }
    void show_power(int i, int x, int y, int size)
    {
        float c1=1.0f,c2=1.0f,c3=1.0f;
        int dur;

        // Energy bar & recovery
        game.recover.render2d(game.batch,0,48,128,64,x+60,y+100,x+60+128,y+116,1);
        game.recover.render2d(game.batch,0,32,24*game.pl[i].power,48,x+60,y+100,x+60+24*game.pl[i].power,y+116,1);
        if((game.pl[i].hypermode>0) && ((int)(counter/10)%2==0))
            game.recover.render2d(game.batch,0,16,128,32,x+60,y+100,x+188,y+116,1);

        dur=(int)(game.pl[i].energy/4.0f); if(dur==0) dur=1;

        if(game.pl[i].hypermode>0){
            c1=HYPER_COL[0]; c2=HYPER_COL[1]; c3=HYPER_COL[2];
        }else if((game.pl[i].energy<ship.MAXENERGY/4) && (counter%dur<(int)(dur/3.0))){
            c1=DAMAGED_COL[0]; c2=DAMAGED_COL[1]; c3=DAMAGED_COL[2];
        }else{
            c1=1.0f; c2=1.0f; c3=1.0f;
        };

        if(size==0){
            game.power[0].render2dcolor(game.batch, 0,0,256,128,x,y,x+256,y+128,1,c1,c2,c3);
            game.power[1].render2dcolor(game.batch,0,0,
                75+((float) (116 * game.pl[i].energy) /ship.MAXENERGY),
                128,x,y,
                (int)(x+75+((float)(116*game.pl[i].energy)/ship.MAXENERGY)),
                y+128,1,c1,c2,c3);
        }else{
            x-=15; y+=70;
            game.spower[0].render2dcolor(game.batch,0,0,256,32,x,y,x+256,y+32,1,c1,c2,c3);
            game.spower[1].render2dcolor(game.batch,0,0,
                75+((float) (116 * game.pl[i].energy) /ship.MAXENERGY),
                32,x,y,
                x+75+(116*game.pl[i].energy/ship.MAXENERGY),y+32,1,c1,c2,c3);

        };

    }
    void show_icon_rank()
    {
        int i,x,y;
        float s=1.0f, c;

        for(i=0; i<game.nplayers; i++){

            if(i<3) s=1.4f-(0.2f*i); else s=1.0f;
            x=70; y=300-40*i;
            if(game.pl[game.position[i]].state==ship.DESTR) c=0.1f; else c=1.0f;
            game.mini[game.racing_ship[game.position[i]]].render2dcolor(game.batch, 0,0,128,128,x,y,(int)(x+61*s),(int)(y+46*s),1.0f,c,c,c);
            x=30; y=310-40*i;
            game.posnumber.render2d(game.batch, (i%4)*16,64-(16*((int)(i/4)+1)),((i%4)+1)*16,64-(16*(int)(i/4)),x,y,x+32,y+32,1);
        };

    }
    void show_map(int x,int y)
    {
        int i,j,k,l;
        /*

        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        glDisable(GL_DEPTH_TEST);
        gluOrtho2D(0, 640, 0, 480);


        glEnable(GL_LINE_SMOOTH);

        glColor3f(1.0,1.0,1.0);

        glLineWidth(3.0);
        glBegin(GL_LINES);
        // Starnting line
        glVertex2i(x-8-((cour->nodes[0].x[1]+cour->nodes[0].x[2])/(MAPSCALE*2.0)),
            y+((cour->nodes[0].z[1]+cour->nodes[0].z[2])/(MAPSCALE*2.0)));
        glVertex2i(x+8-((cour->nodes[0].x[1]+cour->nodes[0].x[2])/(MAPSCALE*2.0)),
            y+((cour->nodes[0].z[1]+cour->nodes[0].z[2])/(MAPSCALE*2.0)));
        glEnd();

        glLineWidth(2.0);
        glBegin(GL_LINE_STRIP);
        // Course
        for(i=0; i<cour->info.nsegments-1; i++)
            glVertex2i(x-((cour->nodes[i].x[1]+cour->nodes[i].x[2])/(MAPSCALE*2.0)),
                y+((cour->nodes[i].z[1]+cour->nodes[i].z[2])/(MAPSCALE*2.0)));

        glVertex2i(x-((cour->nodes[0].x[1]+cour->nodes[0].x[2])/(MAPSCALE*2.0)),
            y+((cour->nodes[0].z[1]+cour->nodes[0].z[2])/(MAPSCALE*2.0)));
        glEnd();

        for(i=nplayers-1; i>=0; i--){

            if(i<nhumans) plcursor[gdata.icons[i]]->render2d(0,0,32,32,x-int((pl[i]->x)/MAPSCALE)-8,y+int((pl[i]->z)/MAPSCALE)-8,x-int((pl[i]->x)/MAPSCALE)+8,y+int((pl[i]->z)/MAPSCALE)+8,1.0);
		else oppico->render2d(0,0,32,32,x-int((pl[i]->x)/MAPSCALE)-8,y+int((pl[i]->z)/MAPSCALE)-8,x-int((pl[i]->x)/MAPSCALE)+8,y+int((pl[i]->z)/MAPSCALE)+8,1.0);
        };
        */
    }

    void show_3d_sprite(PerspectiveCamera cam, texture tex, float u1, float v1, float u2, float v2, float x, float y, float z, float r, float g, float b, float sx, float sy, float a)
    {
        /*vertex v1,v2,v3,v4,v5;
        float dx=cam->x-x, dy=cam->y-y, dz=cam->z-z;
        int i;

        if(a==0.0) return;

        glDisable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, tex->id);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        v5.reset();

        //Translation & Rotation (Object)
        v5.translate(x,y,z);

        //Translation & Rotation by the position of the camera
        v5.translate(-cam->x,-cam->y,-cam->z);
        v5.rotate(-cam->rx,-cam->ry,-cam->rz);

        v1.nx=v5.nx-sx/2; v1.ny=v5.ny+sy/2; v1.nz=v5.nz;
        v2.nx=v5.nx+sx/2; v2.ny=v5.ny+sy/2; v2.nz=v5.nz;
        v3.nx=v5.nx-sx/2; v3.ny=v5.ny-sy/2; v3.nz=v5.nz;
        v4.nx=v5.nx+sx/2; v4.ny=v5.ny-sy/2; v4.nz=v5.nz;

        glBegin(GL_QUADS);

        glColor4f(r,g,b,a);
        glNormal3f(0,0,1);
        glTexCoord2f(u1,w2);
        glVertex3f(v1.nx,v1.ny,v1.nz);
        glTexCoord2f(u2,w2);
        glVertex3f(v2.nx,v2.ny,v2.nz);
        glTexCoord2f(u2,w1);
        glVertex3f(v4.nx,v4.ny,v4.nz);
        glTexCoord2f(u1,w1);
        glVertex3f(v3.nx,v3.ny,v3.nz);

        glEnd();

        glDisable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);

        RENDERED_TRIANGLES+=2;*/

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float aspect = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();

        v1 = 1.f - v1; v2 = 1.f - v2;

        billboardShader.begin();
        billboardShader.setUniformi("u_texture", 0);
        tex.gdxTexture.bind(0);
        billboardShader.setUniformMatrix("u_projTrans", cam.combined);
        billboardShader.setUniformf("u_billboardPos", x, y, z); // Posición del sprite
        billboardShader.setUniformf("u_sizeX", sx * aspect);            // Tamaño del sprite
        billboardShader.setUniformf("u_sizeY", sy);            // Tamaño del sprite
        billboardShader.setUniformf("u_color", r, g, b, a);
        billboardShader.setUniformf("u_uvOffset", u1, v1);
        billboardShader.setUniformf("u_uvSize", u2 - u1, v2 - v1);


        billboard.render(billboardShader, GL20.GL_TRIANGLES);
        billboardShader.end();

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

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
    }
}
