package com.activeminds.mach1r;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {


    public static final int MAXPLAYERS = 8;
    public static final int NSHIPS = 15;

    public static final int SINGLE_R = 0;
    public static final int CHAMPIONSHIP = 1;
    public static final int VERSUS_R = 2;
    public static final int ENDURANCE = 3;
    public static final int DEMO = 4;


    String dif_name[]={
        "beginner",
        "amateur",
        "professional",
        "elite"
    };


    public class game_data
    {
        int resol;
        int dummy[] = new int[4];
        int available[] = new int[NSHIPS];
        short sel_ship[] = new short[MAXPLAYERS], dif, cour_type, scene, nlaps;
        long score_champ;
        int controls[] = new int[4];
        boolean music, sound, skygrfog;
        int music_volume, drawdist, daytime, icons[] = new int[4], sel_endur, res_endur, sel_champ;

        game_data()
        {
            controls[0] = controlm.TEC1;
            controls[1] = controlm.NOTC;
            controls[2] = controlm.NOTC;
            controls[3] = controlm.NOTC;
            for(int i=0;i<NSHIPS;i++)
                available[i] = 1;
            drawdist = 2;
            nlaps = 3;
            skygrfog = true;
        }
    }

    public SpriteBatch batch;
    Texture image;

    font fuente;

    solid brain;

    controlm ctr;

    course cour;

    sprite active, minds, presents, title[] = new sprite[2], wallp, menucur, statbar;
    texture flame, shield, explos, smoke;

    ship pl[] = new ship[MAXPLAYERS];
    game_data gdata;
    int nplayers, racing_ship[] = new int[MAXPLAYERS], position[] = new int[MAXPLAYERS];

    PerspectiveCamera camera;
    OrthographicCamera camera2d;

    float counter;

    int nhumans = 1, game_mode;


    @Override
    public void create() {
        batch = new SpriteBatch();
        image = new Texture("libgdx.png");

        fuente = new font("sprite/FONT.png",16,16,14);

        camera = new PerspectiveCamera(67, 640, 480);
        camera.position.set(0f, 0f, 200f);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 1, 0);
        camera.near = 0.1f;
        camera.far = 2000f;
        camera.update();

        camera2d = new OrthographicCamera();
        camera2d.setToOrtho(false,  640, 480);

        gdata = new game_data();

        course.load_static_data();

        setScreen(new StartupScreen(this));

    }

    void show_scrolling_wallp()
    {
        int i,j;
        for(i=0; i<5; i++)
            for(j=-1; j<5; j++)
                wallp.render2d(batch, 0,0,128,128,128*i,(int)(-(counter%128)+((i%2)*64)+128*j),128*(i+1),(int)(-(counter%128)+((i%2)*64)+128*(j+1)),1.0f);

        //RENDERED_TRIANGLES+=2;
    }

    void show_menu_cursor(int x, int y)
    {
        int f,i,j;
        f=(int)(counter/5)%16;
        i=f%4;
        j=4-(int)(f/4);
        menucur.render2d(batch, i*64,j*64,(i+1)*64,(j+1)*64,x,y,x+32,y+32,1.0f);
    }

    void startup_course(int nlaps, int dif, int type, int scene)
    {
        //char s[200], song[100];
        int i,j;

        course.course_info ci = new course.course_info();

        // Generate the course
        ci.radx=1600+(300*dif);
        ci.radz=1000+(200*dif);
        ci.nsegments=(int)((ci.radx+ci.radz)/20);
        ci.width=70;

        ci.xznoisegap=30-(4*dif);
        ci.xznoisewidth=450+(75*dif);

        ci.ynoisegap=20-3*dif;
        ci.ynoisewidth=59+5*dif;

        ci.nboost=1+(2*dif);

        ci.nice=0; ci.nicenob=0; ci.njump=0; ci.nnoborder=0; ci.ntunnel=0;

        if((type==course.TH) || (type==course.JH) || (type==course.NTH) || (type==course.NJTH) || (type==course.IJHN)){
            ci.nice=dif+1; ci.lice=20+(3*dif);
        };

        if((type==course.I) || (type==course.IJHN)){
            ci.nicenob=dif+1; ci.licenob=10+(2*dif);
        };

        if((type==course.NT) || (type==course.NTH) || (type==course.NJ) || (type==course.NJTH) || (type==course.IJHN)){
            ci.nnoborder=dif+1; ci.lnoborder=15+(2*dif);
        };

        if((type==course.TH) || (type==course.T) || (type==course.NT) || (type==course.NTH) || (type==course.NJTH)){
            ci.ntunnel=dif+1; ci.ltunnel=30;
        };

        if((type==course.J) || (type==course.JH) || (type==course.NJ) || (type==course.NJTH) || (type==course.IJHN)){
            ci.njump=dif+1; ci.ljump=2;
        };

        if(type==course.T){ //Mach 1 Speedway
            ci.ynoisewidth=0; ci.xznoisewidth=0;
        };

        ci.nlaps=nlaps; ci.scene=scene;

        if(nhumans<=1) ci.quality=2;
        else if(nhumans==2) ci.quality=1;
        else ci.quality=0;

        cour=new course(ci);
        cour.generate();

        // Load data
        //sprintf(s,"scene\\%sgr",scenes[scene].name);
        //ground=new texture(s,TEX_BMP,TRUE,FALSE);


        for(i=0; i<nplayers; i++){
            pl[i]=new ship(racing_ship[i],i,cour);
            position[i]=i;
        };

        //hour_environment();

        // Load music
        /*ZeroMemory(&XMParams, sizeof XMParams);
        XMParams.classID = CLSID_TRACKERXM;
        sprintf(song,"sound\\song%d.mus",(scene%3)+1);
        XMParams.fileName = song;
        XMParams.flags = FLAG_LOOP;
        XMParams.input = INPUT_DISK;
        XMParams.output = OUTPUT_DSOUND;

        gAudio->getSoundClass(SOUNDCLASS_XM, &XMParams, (void**)&mus);

        mus->setVolume(gdata.music_volume/100.0);
        if(gdata.music) mus->play();

        paused=FALSE;*/

    }

    void random_ships(int npl)
    {
        boolean used[] = new boolean[NSHIPS];
        int i,j;

        nhumans=npl;
        if(npl<2) nplayers=6; else nplayers=npl;

        //Random ships for opponents
        for(i=0; i<NSHIPS; i++)
            used[i]=false;

        for(i=0; i<nhumans; i++){
            racing_ship[i]=gdata.sel_ship[i];
            used[racing_ship[i]]=true;
        };

        for(i=nhumans; i<nplayers; i++){
            do{
                j=course.rand()%NSHIPS;
            }while((used[j]==true) || (gdata.available[j]==0));

            racing_ship[i]=j; used[j]=true;
        };

    }

    void show_3d_sprite(PerspectiveCamera cam, ShaderProgram shader, texture tex, float u1, float w1, float u2, float w2, float x, float y, float z, float r, float g, float b, float sx, float sy, float a)
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

        float[] vertices = new float[] {
            -0.5f, -0.5f, 0, 0, 0,
            0.5f, -0.5f, 0, 1, 0,
            0.5f,  0.5f, 0, 1, 1,
            -0.5f,  0.5f, 0, 0, 1
        };

        short[] indices = new short[] { 0, 1, 2, 2, 3, 0 };

        Mesh billboard = new Mesh(true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );
        billboard.setVertices(vertices);
        billboard.setIndices(indices);

        Vector3 billboardPos = new Vector3(x, y, z);

// Dirección desde el billboard a la cámara (solo en XZ)
        Vector3 camDir = new Vector3(cam.position.x - x, 0, cam.position.z - z);
        camDir.nor();

// Ángulo en Y entre billboard y cámara (plano XZ)
        float angleY = (float)Math.atan2(camDir.x, camDir.z); // NO usa Y

// Construir la matriz modelo: solo rotación Y y posición
        Matrix4 model = new Matrix4()
            .idt()
            .translate(billboardPos)
            .rotate(Vector3.Y, (float)Math.toDegrees(angleY))
            .scale(sx,sy,1);


        Matrix4 MVP = new Matrix4(camera.combined).mul(model);

        shader.begin();
        shader.setUniformMatrix("u_mvp", MVP);
        shader.setUniformi("u_texture", 0);
        tex.gdxTexture.bind(0);
        billboard.render(shader, GL20.GL_TRIANGLES);
        shader.end();


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
