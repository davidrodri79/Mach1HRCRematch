package com.activeminds.mach1r;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {


    public static final int MAXPLAYERS = 4;
    public static final int NSHIPS = 15;


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
            for(int i=0;i<NSHIPS;i++)
                available[i] = 1;
        }
    }

    public SpriteBatch batch;
    Texture image;

    font fuente;

    solid brain;

    controlm ctr;

    sprite active, minds, presents, title[] = new sprite[2], wallp, menucur;

    ship pl[] = new ship[MAXPLAYERS];
    game_data gdata;

    PerspectiveCamera camera;
    OrthographicCamera camera2d;

    float counter;


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

        gdata = new game_data();

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
