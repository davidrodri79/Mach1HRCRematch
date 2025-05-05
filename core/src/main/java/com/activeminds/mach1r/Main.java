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

    sprite active, minds, presents, title[] = new sprite[2], wallp, menucur, statbar, posnumber;
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
