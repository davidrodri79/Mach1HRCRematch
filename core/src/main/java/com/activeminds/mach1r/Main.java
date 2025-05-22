package com.activeminds.mach1r;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    public static final int SCREENX = 960;
    public static final int SCREENY = 480;

    public static final int FPS = 70;

    public static final int MAXPLAYERS = 8;
    public static final int NSHIPS = 15;

    public static final int SINGLE_R = 0;
    public static final int CHAMPIONSHIP = 1;
    public static final int VERSUS_R = 2;
    public static final int ENDURANCE = 3;
    public static final int DEMO = 4;


    String dif_name[]={
        "diffName0",
        "diffName1",
        "diffName2",
        "diffName3"
    };


    public class game_data
    {
        int resol;
        int dummy[] = new int[4];
        int available[] = new int[NSHIPS];
        short sel_ship[] = new short[MAXPLAYERS], dif, cour_type, scene, nlaps, nbots;
        long score_champ;
        int controls[] = new int[4];
        boolean music, sound, skygrfog, shadowmap, reflections, exhaustLights;
        int music_volume, drawdist, daytime, icons[] = new int[4], sel_endur, res_endur, sel_champ, language;

        game_data()
        {
            resol = 3;
            if (Gdx.app.getType() == Application.ApplicationType.Desktop)
                controls[0] = controlm.TEC1;
            else
                controls[0] = controlm.TOUC;
            controls[1] = controlm.NOTC;
            controls[2] = controlm.NOTC;
            controls[3] = controlm.NOTC;
            for(int i=0;i<NSHIPS;i++)
                available[i] = 1;//i < ship.ICARUS ? 1 : 0;
            drawdist = 2;
            nlaps = 3;
            nbots = 3;
            music = true; sound = true;
            music_volume = 75;
            skygrfog = true;
            shadowmap = true;
            reflections = true;
            exhaustLights = true;
            icons[0] = 0;
            icons[1] = 1;
            icons[2] = 2;
            icons[3] = 3;
            res_endur = 1;
            language=0;
        }
    }

    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer;
    Texture image, wallpCubemap;

    font fuente;

    solid brain, cup;

    controlm ctr;

    course cour;

    sprite active, minds, presents, title[] = new sprite[2], wallp, menucur, statbar, posnumber, speed, kmh,
        mini[] = new sprite[NSHIPS], power[] = new sprite[2], spower[] = new sprite[2], recover, start[] = new sprite[4], plcursor[] = new sprite[10],
        arrow, oppico, preview[] = new sprite[6], mistery;
    texture flame, shield, explos, smoke, shadow, moon;
    wave wthree, wtwo, wone, wgo, wconfirm, wvoice;
    Music mus;

    ship pl[] = new ship[MAXPLAYERS];
    game_data gdata;
    int nplayers, racing_ship[] = new int[MAXPLAYERS], position[] = new int[MAXPLAYERS];

    PerspectiveCamera camera;
    OrthographicCamera camera2d;

    AssetManager manager;

    float counter;

    int nhumans = 1, game_mode, ranking[] = new int[MAXPLAYERS], scores[] = new int[MAXPLAYERS], champ_stage, new_ship;
    boolean abort_champ = false;

    String  rank_str[]={"rank1", "rank2", "rank3", "rank4", "rank5" ,"rank6"};

    static LocalizationManager loc;


    @Override
    public void create() {
        batch = new SpriteBatch();
        //image = new Texture("libgdx.png");
        shapeRenderer = new ShapeRenderer();

        fuente = new font("sprite/FONT.png",16,16,14);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0f, 0f, 200f);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 1, 0);
        camera.near = 0.1f;
        camera.far = 2000f;
        camera.update();

        camera2d = new OrthographicCamera();
        camera2d.setToOrtho(false,  SCREENX, SCREENY);

        manager = new AssetManager();

        gdata = new game_data();
        load_game_data();

        loc = new LocalizationManager();
        loc.loadLanguage(gdata.language);

        ship.load_static_data();
        course.load_static_data();

        setScreen(new StartupScreen(this));

    }

    void show_scrolling_wallp()
    {
        int i,j;
        for(i=0; i<8; i++)
            for(j=-1; j<5; j++)
                wallp.render2d(batch, 0,0,128,128,128*i,(int)(-(counter%128)+((i%2)*64)+128*j),128*(i+1),(int)(-(counter%128)+((i%2)*64)+128*(j+1)),1.0f);

        //RENDERED_TRIANGLES+=2;
    }

    void generate_scrolling_wallp_cubemap()
    {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, 256, 256, false);
        fbo.begin();

        OrthographicCamera cam = new OrthographicCamera();
        cam.setToOrtho(false,  256, 256);


        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        int i,j;
        for(i=0; i<8; i++)
            for(j=-1; j<5; j++)
                wallp.render2d(batch, 0,0,128,128,128*i,(int)(-(counter%128)+((i%2)*64)+128*j),128*(i+1),(int)(-(counter%128)+((i%2)*64)+128*(j+1)),1.0f);

        batch.end();

        fbo.end();

        wallpCubemap = fbo.getColorBufferTexture();
        //RENDERED_TRIANGLES+=2;
    }

    void show_menu_cursor(int x, int y)
    {
        int f,i,j;
        f=(int)((int)counter/5)%16;
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

    void random_ships(int npl, int nbot)
    {
        boolean used[] = new boolean[NSHIPS];
        int i,j;

        nhumans=npl;
        //if(npl<2) nplayers=6; else nplayers=npl;
        nplayers = npl + nbot;

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

    void reset_ranking()
    {
        int i;
        for(i=0; i<nplayers; i++){
            ranking[i]=i;
            scores[i]=0;
        };
    }
    void update_ranking()
    {

        int prize[]={15,10,7,5,3,2,1,0}, i,j,k;

        for(i=0; i<nplayers; i++)
            scores[position[i]]+=prize[i+8-nplayers];

        for(i=0; i<nplayers; i++)
            for(j=0; j<nplayers-1; j++)
                if(scores[ranking[j+1]]>scores[ranking[j]]){

                    k=ranking[j];
                    ranking[j]=ranking[j+1];
                    ranking[j+1]=k;
                };

    }

    int ranking_position(int p)
    {
        int i=0;
        while(ranking[i]!=p) i++;
        return i;
    }

    /*
     int resol;
        int dummy[] = new int[4];
        int available[] = new int[NSHIPS];
        short sel_ship[] = new short[MAXPLAYERS], dif, cour_type, scene, nlaps;
        long score_champ;
        int controls[] = new int[4];
        boolean music, sound, skygrfog;
        int music_volume, drawdist, daytime, icons[] = new int[4], sel_endur, res_endur, sel_champ;
     */

    void save_game_data()
    {
        FileHandle file = Gdx.files.local("data.dat");

        ByteBuffer buffer = ByteBuffer.allocate(200);

        buffer.putInt(gdata.resol);
        for(int i = 0; i < 4; i++)
            buffer.putInt(course.rand()%256);
        for(int i = 0; i < NSHIPS; i++)
            buffer.putInt(gdata.available[i]);
        for(int i = 0; i < MAXPLAYERS; i++)
            buffer.putShort(gdata.sel_ship[i]);
        buffer.putShort(gdata.dif);
        buffer.putShort(gdata.cour_type);
        buffer.putShort(gdata.scene);
        buffer.putShort(gdata.nlaps);
        buffer.putShort(gdata.nbots);
        buffer.putLong(gdata.score_champ);
        for(int i = 0; i < 4; i++)
            buffer.putInt(gdata.controls[i]);
        buffer.put((byte) (gdata.music ? 1 : 0));
        buffer.put((byte) (gdata.sound ? 1 : 0));
        buffer.put((byte) (gdata.skygrfog ? 1 : 0));
        buffer.put((byte) (gdata.shadowmap ? 1 : 0));
        buffer.put((byte) (gdata.reflections ? 1 : 0));
        buffer.put((byte) (gdata.exhaustLights ? 1 : 0));
        buffer.putInt(gdata.music_volume);
        buffer.putInt(gdata.drawdist);
        buffer.putInt(gdata.daytime);
        for(int i = 0; i < 4; i++)
            buffer.putInt(gdata.icons[i]);
        buffer.putInt(gdata.sel_endur);
        buffer.putInt(gdata.res_endur);
        buffer.putInt(gdata.sel_champ);
        buffer.putInt(gdata.language);
        buffer.put((byte) 255);

        byte[] bytes = buffer.array();
        file.writeBytes(bytes,false);
    }



    void load_game_data()
    {
        FileHandle file0 = Gdx.files.local("data.dat");
        if(file0.exists()) {
            byte[] bytes = file0.readBytes();

            ByteBuffer buffer = ByteBuffer.wrap(bytes);

            gdata.resol = buffer.getInt();
            for (int i = 0; i < 4; i++)
                gdata.dummy[i] = buffer.getInt();
            for (int i = 0; i < NSHIPS; i++)
                gdata.available[i] = buffer.getInt();
            for (int i = 0; i < MAXPLAYERS; i++)
                gdata.sel_ship[i] = buffer.getShort();
            gdata.dif = buffer.getShort();
            gdata.cour_type = buffer.getShort();
            gdata.scene = buffer.getShort();
            gdata.nlaps = buffer.getShort();
            gdata.nbots = buffer.getShort();
            gdata.score_champ = buffer.getLong();
            for (int i = 0; i < 4; i++)
                gdata.controls[i] = buffer.getInt();
            gdata.music = buffer.get() == 1;
            gdata.sound = buffer.get() == 1;
            gdata.skygrfog = buffer.get() == 1;
            gdata.shadowmap = buffer.get() == 1;
            gdata.reflections = buffer.get() == 1;
            gdata.exhaustLights = buffer.get() == 1;
            gdata.music_volume = buffer.getInt();
            gdata.drawdist = buffer.getInt();
            gdata.daytime = buffer.getInt();
            for (int i = 0; i < 4; i++)
                gdata.icons[i] = buffer.getInt();
            gdata.sel_endur = buffer.getInt();
            gdata.res_endur = buffer.getInt();
            gdata.sel_champ = buffer.getInt();
            gdata.language = buffer.getInt();
        }

        wave.WAVE_ENABLE=gdata.sound;
    }

    void play_voice(String f)
    {
        if(wvoice != null){
            wvoice.stop();
            wvoice = null;
        };
        String s = "sound/"+f;
        wvoice=new wave();
        wvoice.load(s);
        wvoice.playonce();
    }
    void play_music(String f)
    {
        if(gdata.music) {
            mus = Gdx.audio.newMusic(Gdx.files.internal(f));
            mus.setLooping(true);
            mus.setVolume(gdata.music_volume / 100f);
            mus.play();
        }
    }

    void stop_music()
    {
        if(mus != null)
        {
            mus.stop();
            mus = null;
        }
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
