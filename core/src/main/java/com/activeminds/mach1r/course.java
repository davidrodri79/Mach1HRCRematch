package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

import static com.activeminds.mach1r.wave.*;

public class course {

    /*#define GROUNDY -40
        #define SKYY 90

    enum {NORMAL=0, START, END, BOOSTER, TUNNEL, JUMP, NOBORDER, ICE, NBICE, EMPTY};

    enum {NONE=0, ENERGY, BOOST, SHIELD, POWER, MINE};*/

    public static final int NONE = 0;
    public static final int ENERGY = 1;
    public static final int BOOST = 2;
    public static final int SHIELD = 3;
    public static final int POWER = 4;
    public static final int MINE = 5;

    /*enum {TH=0,J,JH,NT,NTH,NJ,NJTH,I,IJHN,T};

    enum {CITY=0, DESERT, VOLCANO, INDUSTRY, RUINS, SEA};*/

    public static final int GROUNDY = -40;
    public static final int SKYY = 90;

    public static final int NORMAL = 0;
    public static final int START = 1;
    public static final int END = 2;
    public static final int BOOSTER = 3;
    public static final int TUNNEL = 4;
    public static final int JUMP = 5;
    public static final int NOBORDER = 6;
    public static final int ICE = 7;
    public static final int NBICE = 8;
    public static final int EMPTY = 9;

    public static final int TH=0;
    public static final int J=1;
    public static final int JH=2;
    public static final int NT=3;
    public static final int NTH=4;
    public static final int NJ=5;
    public static final int NJTH=6;
    public static final int I=7;
    public static final int IJHN=8;
    public static final int T=9;

    public static final float BORDERW = 5.0f;
    public static final float BORDERH = 2.0f;
    public static final float GROUNDH = 1.0f;
    public static final float TUNNELH = 30.0f;

    public static final float DRAWDIST = 700*700;
    public static final float VRPDIST = 450f;

    public static final float TDX = 1.0f/8.0f;
    public static final float TDY = 1.0f/8.0f;

    public static class course_scene {
            String name;
            String descr;
            boolean modelGouraud[] = new boolean[3];
            float fogcolor[] = new float[3];
            float skycolor[] = new float[3];
            float roadcolor[] = new float[3];
    }

    static class course_sceneJson {

        ArrayList<course_scene> course_scenes;
    }

    public static class course_info
    {
        int radx, radz, width, nsegments, xznoisewidth, ynoisewidth;
        int xznoisegap, ynoisegap,
            ntunnel, ltunnel, nnoborder, lnoborder, nice, lice, nicenob, licenob, njump, ljump, nboost,
            nlaps, scene, quality;
    }

    public static class node {

        float x[] = new float[11], y[] = new float[11], z[] = new float[11], a, b, c, d, dex, dez, itemfade, dscalexz, dscaley;
        short xznoise, ynoise;
        short type, detype, item;
        solid mesh;
    }

    public static final String ctype_name[]={
        "courseType0",
        "courseType1",
        "courseType2",
        "courseType3",
        "courseType4",
        "courseType5",
        "courseType6",
        "courseType7",
        "courseType8",
        "courseType9"
    };

    static class champ_race {

        int type, dif, nlaps, scene;
    };

    static class champ_event{

        String name;
        int minimum, reward;
        int nraces;
        ArrayList<champ_race> races;
    };

    static class champJson
    {
        ArrayList<champ_event> champ_events;
    }

    static class endur_race {
        int type, dif, nlaps, scene, op;
    }

    static class enduranceJson {
        ArrayList<endur_race> races;
    }


    public course_info info;
    node[] nodes;
    int counter;
    texture road;
    solid decorate[] = new solid[3], scube, ecube, bcube, power, mine;

    static course_sceneJson scenes;
    static champJson championship;
    static enduranceJson endurance;

    static void load_static_data()
    {
        Json json = new Json();
        FileHandle file = Gdx.files.internal("course_scenes.json");
        String fileText = file.readString();
        scenes = json.fromJson(course_sceneJson.class, fileText);

        file = Gdx.files.internal("championships.json");
        fileText = file.readString();
        championship = json.fromJson(champJson.class, fileText);

        file = Gdx.files.internal("endurance.json");
        fileText = file.readString();
        endurance = json.fromJson(enduranceJson.class, fileText);
    }

    course(course_info ci)
    {
        info = ci;
        counter = 0;
        String s;

        if(info.quality==2)
            s = "scene/" + scenes.course_scenes.get(info.scene).name + ".png";
        else s = "scene/" + scenes.course_scenes.get(info.scene).name + "lr.png";
        road= new texture(s,texture.TEX_PCX,true,false);

        for(int i=0; i<3; i++){
            decorate[i]=new solid();
            decorate[i].load_mesh("scene/"+scenes.course_scenes.get(ci.scene).name+(i+1)+".msh", scenes.course_scenes.get(ci.scene).modelGouraud[i]);
            decorate[i].centrate(true,false,true);
        };

        //Item cubes
        scube=generateCube(4, "sprite/scube.bmp");

        ecube=generateCube(4, "sprite/ecube.bmp");

        bcube=generateCube(4, "sprite/bcube.bmp");

        power=new solid();
        power.load_mesh("model/power.msh", true);
        power.centrate(true, true, true);

        mine=new solid();
        mine.load_mesh("model/mine.msh", true);
        mine.centrate(true, true, true);
    }

    solid generateCube(float side, String texture)
    {
        //Item cubes
        solid cube=new solid();

        cube.vertexs = new ArrayList<>();
        cube.vertexs.add(new vertex(-side*0.5f,side*0.5f,-side*0.5f)); // 0
        cube.vertexs.add(new vertex(-side*0.5f, side*0.5f, side*0.5f)); // 1
        cube.vertexs.add(new vertex(side*0.5f,side*0.5f,side*0.5f)); // 2
        cube.vertexs.add(new vertex(side*0.5f,side*0.5f,-side*0.5f)); // 3
        cube.vertexs.add(new vertex(-side*0.5f,-side*0.5f,-side*0.5f)); // 0
        cube.vertexs.add(new vertex(-side*0.5f, -side*0.5f, side*0.5f)); // 1
        cube.vertexs.add(new vertex(side*0.5f,-side*0.5f,side*0.5f)); // 2
        cube.vertexs.add(new vertex(side*0.5f,-side*0.5f,-side*0.5f)); // 3

        cube.triangles = new ArrayList<>();
        cube.textures = new Texture[1];
        cube.textures[0] = new Texture(texture);
        cube.addQuad(cube.vertexs.get(0), cube.vertexs.get(1), cube.vertexs.get(2), cube.vertexs.get(3),0,0, 0, 1,0, 1,1, 0,1);
        cube.addQuad(cube.vertexs.get(4), cube.vertexs.get(0), cube.vertexs.get(3), cube.vertexs.get(7),0,0, 0, 1,0, 1,1, 0,1);
        cube.addQuad(cube.vertexs.get(3), cube.vertexs.get(2), cube.vertexs.get(6), cube.vertexs.get(7),0,0, 0, 1,0, 1,1, 0,1);
        cube.addQuad(cube.vertexs.get(7), cube.vertexs.get(6), cube.vertexs.get(5), cube.vertexs.get(4),0,0, 0, 1,0, 1,1, 0,1);
        cube.addQuad(cube.vertexs.get(1), cube.vertexs.get(0), cube.vertexs.get(4), cube.vertexs.get(5),0,0, 0, 1,0, 1,1, 0,1);
        cube.addQuad(cube.vertexs.get(2), cube.vertexs.get(1), cube.vertexs.get(5), cube.vertexs.get(6),0,0, 0, 1,0, 1,1, 0,1);

        //cube.centrate(true, true, true);
        cube.buildGdxMesh();

        return cube;
    }

    void update()
    {
        counter++;
        for(int i=0; i<info.nsegments; i++){

            if((nodes[i].itemfade<1.0) && (nodes[i].item!=NONE)){
                nodes[i].itemfade-=0.01;
                if(nodes[i].itemfade<=0.0) nodes[i].item=NONE;
            };
        };
    }

    static int rand()
    {
        return (int)(Math.random()*1000);
    }

    void generate()
    {
        int i,j,k;

        float angle, par, u[] = new float[3], v[] =  new float[3];
        int noise, gap, mid;


        nodes=new node[info.nsegments];
        for(i = 0; i < nodes.length; i++)
        {
            nodes[i] = new node();
        }

        //Add the noise to the generical ellipse
        i=0; k=0;
        if(info.xznoisewidth!=0)
            while(i<info.nsegments-info.xznoisegap){

                gap=info.xznoisegap-(rand()%5);
                // Horizontal noise (XZ)
                if(i!=0){
                    noise=rand()%(int)(info.xznoisewidth/2); if(k==1) noise=-noise;
                    nodes[i].xznoise+=noise;
                    nodes[(i+1)%info.nsegments].xznoise+=noise;
                    for(j=1; j<=(int)(gap/2); j++){
                        nodes[(i+j+1)%info.nsegments].xznoise+=noise*((int)(gap/2)-j)/(int)(gap/2);
                        nodes[(i-j)%info.nsegments].xznoise+=noise*((int)(gap/2)-j)/(int)(gap/2);
                    };
                }
                i+=gap; k=1-k;
            };

        i=0;
        if(info.ynoisewidth!=0)
            while(i<info.nsegments-info.ynoisegap){
                // Vertical noise (Y)
                if((i%info.ynoisegap==0) && (i!=0)){
                    noise=(rand()%info.ynoisewidth)-(int)(info.ynoisewidth/2);
                    mid=(int)(info.ynoisegap/2);
                    par=(int)(noise/mid);
                    nodes[i].ynoise+=noise;
                    nodes[(i+1)%info.nsegments].ynoise+=noise;
                    for(j=1; j<=mid; j++){
                        nodes[(i+j+1)%info.nsegments].ynoise+=noise-(j*par);
                        nodes[(i-j)%info.nsegments].ynoise+=noise-(j*par);
                    };
                }
                i+=info.ynoisegap;
            };

        // Create node coordinates
        for(i=0; i<info.nsegments; i++){

            angle=(float)(i*2*Math.PI/info.nsegments);

            nodes[i].y[0]=BORDERH+nodes[i].ynoise; nodes[i].y[1]=nodes[i].ynoise;
            nodes[i].y[2]=nodes[i].ynoise; nodes[i].y[3]=BORDERH+nodes[i].ynoise;
            nodes[i].y[4]=-GROUNDH+nodes[i].ynoise; nodes[i].y[5]=-GROUNDH+nodes[i].ynoise;
            nodes[i].y[6]=(TUNNELH/2)+nodes[i].ynoise;
            nodes[i].y[7]=TUNNELH+nodes[i].ynoise;
            nodes[i].y[8]=(TUNNELH/2)+nodes[i].ynoise;

            nodes[i].x[0]= (float) ((info.radx+nodes[i].xznoise-info.width/2-BORDERW)*Math.cos(angle));
            nodes[i].x[1]= (float) ((info.radx+nodes[i].xznoise-info.width/2)*Math.cos(angle));
            nodes[i].x[2]= (float) ((info.radx+nodes[i].xznoise+info.width/2)*Math.cos(angle));
            nodes[i].x[3]= (float) ((info.radx+nodes[i].xznoise+info.width/2+BORDERW)*Math.cos(angle));
            nodes[i].x[4]= (float) ((info.radx+nodes[i].xznoise+info.width/2+BORDERW)*Math.cos(angle));
            nodes[i].x[5]= (float) ((info.radx+nodes[i].xznoise-info.width/2-BORDERW)*Math.cos(angle));
            nodes[i].x[6]=nodes[i].x[0];
            nodes[i].x[7]= (float) ((info.radx+nodes[i].xznoise)*Math.cos(angle));
            nodes[i].x[8]=nodes[i].x[3];
            nodes[i].x[9]= (float) ((info.radx+nodes[i].xznoise-(info.width/2)-50.0)*Math.cos(angle));
            nodes[i].x[10]= (float) ((info.radx+nodes[i].xznoise+(info.width/2)+50.0)*Math.cos(angle));

            nodes[i].z[0]= (float) ((info.radz+nodes[i].xznoise-info.width/2-BORDERW)*Math.sin(angle));
            nodes[i].z[1]= (float) ((info.radz+nodes[i].xznoise-info.width/2)*Math.sin(angle));
            nodes[i].z[2]= (float) ((info.radz+nodes[i].xznoise+info.width/2)*Math.sin(angle));
            nodes[i].z[3]= (float) ((info.radz+nodes[i].xznoise+info.width/2+BORDERW)*Math.sin(angle));
            nodes[i].z[4]= (float) ((info.radz+nodes[i].xznoise+info.width/2+BORDERW)*Math.sin(angle));
            nodes[i].z[5]= (float) ((info.radz+nodes[i].xznoise-info.width/2-BORDERW)*Math.sin(angle));
            nodes[i].z[6]=nodes[i].z[0];
            nodes[i].z[7]= (float) ((info.radz+nodes[i].xznoise)*Math.sin(angle));
            nodes[i].z[8]=nodes[i].z[3];
            nodes[i].z[9]= (float) ((info.radz+nodes[i].xznoise-(info.width/2)-50.0)*Math.sin(angle));
            nodes[i].z[10]= (float) ((info.radz+nodes[i].xznoise+(info.width/2)+50.0)*Math.sin(angle));


            //The decorate coordinates
            nodes[i].detype= (short) (rand()%10);
            noise=info.width+30+(rand()%150);
            nodes[i].dscalexz= (float) (0.5+(rand()%100)*0.01);
            nodes[i].dscaley= (float) (0.5+(rand()%100)*0.01);
            if(rand()%2==0){

                nodes[i].dex= (float) ((info.radx+nodes[i].xznoise+noise)*Math.cos(angle));
                nodes[i].dez= (float) ((info.radz+nodes[i].xznoise+noise)*Math.sin(angle));
            }else{

                nodes[i].dex= (float) ((info.radx+nodes[i].xznoise-noise)*Math.cos(angle));
                nodes[i].dez= (float) ((info.radz+nodes[i].xznoise-noise)*Math.sin(angle));
            };


        };

        // Road segment types
        nodes[0].type=START;
        for(i=info.nsegments-5; i<info.nsegments; i++) nodes[i].type=END;


        // Iced segments
        for(i=0; i<info.nice; i++){
            k=(rand()%(info.nsegments-6-info.lice))+1;
            for(j=0; j<info.lice; j++)
                nodes[k+j].type=ICE;
        };

        // No bordered segments
        for(i=0; i<info.nnoborder; i++){
            k=(rand()%(info.nsegments-6-info.lnoborder))+1;
            for(j=0; j<info.lnoborder; j++)
                nodes[k+j].type=NOBORDER;
        };

        // No bordered iced segments
        for(i=0; i<info.nicenob; i++){
            k=(rand()%(info.nsegments-6-info.licenob))+1;
            for(j=0; j<info.licenob; j++)
                nodes[k+j].type=NBICE;
        };

        // Tunnels
        for(i=0; i<info.ntunnel; i++){
            k=(rand()%(info.nsegments-6-info.ltunnel))+1;
            for(j=0; j<info.ltunnel; j++)
                nodes[k+j].type=TUNNEL;
        };

        // Boosters
        for(i=0; i<info.nboost; i++){
            k=(rand()%(info.nsegments-6))+1;
            nodes[k].type=BOOSTER;
        };

        // Jumps
        for(i=0; i<info.njump; i++){
            k=(rand()%(info.nsegments-6-info.ljump))+1;
            nodes[k].type=JUMP;
            for(j=1; j<info.ljump; j++)
                nodes[k+j].type=EMPTY;
        };


        // Create ground plane equation (ax+by+cz+d=0) for each node
        for(i=0; i<info.nsegments; i++){

            j=(i+1)%info.nsegments;

            // Directing vectors of the plane, u and v
            u[0]=nodes[j].x[1]-nodes[i].x[1];
            u[1]=nodes[j].y[1]-nodes[i].y[1];
            u[2]=nodes[j].z[1]-nodes[i].z[1];

            v[0]=nodes[i].x[2]-nodes[i].x[1];
            v[1]=nodes[i].y[2]-nodes[i].y[1];
            v[2]=nodes[i].z[2]-nodes[i].z[1];

            if(nodes[j].type==JUMP) u[1]=nodes[j].y[6]-nodes[i].y[1];


            // Vectorial product
            nodes[i].a=(v[1]*u[2])-(v[2]*u[1]);
            nodes[i].b=(v[2]*u[0])-(v[0]*u[2]);
            nodes[i].c=(v[0]*u[1])-(v[1]*u[0]);
            nodes[i].d=-((nodes[i].a*nodes[i].x[1])+(nodes[i].b*nodes[i].y[1])+(nodes[i].c*nodes[i].z[1]));

        };

        // CREATE MESH
        for(i = 0; i < nodes.length; i++) {
            vertex v1[] = new vertex[9], v2[] = new vertex[9];

            nodes[i].mesh = new solid();
            nodes[i].mesh.triangles = new ArrayList<>();
            nodes[i].mesh.textures = new Texture[1];
            nodes[i].mesh.textures[0] = road.gdxTexture;


            v1[0] = new vertex(nodes[i].x[0], nodes[i].y[0], nodes[i].z[0]);
            v1[1] = new vertex(nodes[i].x[1], nodes[i].y[1], nodes[i].z[1]);
            v1[2] = new vertex(nodes[i].x[2], nodes[i].y[2], nodes[i].z[2]);
            v1[3] = new vertex(nodes[i].x[3], nodes[i].y[3], nodes[i].z[3]);
            v1[4] = new vertex(nodes[i].x[4], nodes[i].y[4], nodes[i].z[4]);
            v1[5] = new vertex(nodes[i].x[5], nodes[i].y[5], nodes[i].z[5]);
            v1[6] = new vertex(nodes[i].x[6], nodes[i].y[6], nodes[i].z[6]);
            v1[7] = new vertex(nodes[i].x[7], nodes[i].y[7], nodes[i].z[7]);
            v1[8] = new vertex(nodes[i].x[8], nodes[i].y[8], nodes[i].z[8]);

            v2[0] = new vertex(nodes[(i + 1) % info.nsegments].x[0], nodes[(i + 1) % info.nsegments].y[0], nodes[(i + 1) % info.nsegments].z[0]);
            v2[1] = new vertex(nodes[(i + 1) % info.nsegments].x[1], nodes[(i + 1) % info.nsegments].y[1], nodes[(i + 1) % info.nsegments].z[1]);
            v2[2] = new vertex(nodes[(i + 1) % info.nsegments].x[2], nodes[(i + 1) % info.nsegments].y[2], nodes[(i + 1) % info.nsegments].z[2]);
            v2[3] = new vertex(nodes[(i + 1) % info.nsegments].x[3], nodes[(i + 1) % info.nsegments].y[3], nodes[(i + 1) % info.nsegments].z[3]);
            v2[4] = new vertex(nodes[(i + 1) % info.nsegments].x[4], nodes[(i + 1) % info.nsegments].y[4], nodes[(i + 1) % info.nsegments].z[4]);
            v2[5] = new vertex(nodes[(i + 1) % info.nsegments].x[5], nodes[(i + 1) % info.nsegments].y[5], nodes[(i + 1) % info.nsegments].z[5]);
            v2[6] = new vertex(nodes[(i + 1) % info.nsegments].x[6], nodes[(i + 1) % info.nsegments].y[6], nodes[(i + 1) % info.nsegments].z[6]);
            v2[7] = new vertex(nodes[(i + 1) % info.nsegments].x[7], nodes[(i + 1) % info.nsegments].y[7], nodes[(i + 1) % info.nsegments].z[7]);
            v2[8] = new vertex(nodes[(i + 1) % info.nsegments].x[8], nodes[(i + 1) % info.nsegments].y[8], nodes[(i + 1) % info.nsegments].z[8]);

            float tx = 0f, ty = 0f;
            switch(nodes[i].type){
                case TUNNEL :
                case NOBORDER :
                case NORMAL : tx=0; ty= 0.01F; break;
                case END    : tx=4*TDX; ty= 0.01F; break;
                case START  : tx=4*TDX; ty= (float) (2*TDY+0.01); break;
                case NBICE :
                case ICE    : tx=4*TDX; ty=5*TDY; break;
                case BOOSTER :
                case JUMP    : tx=0; ty=3*TDY; break;
            };

            float r = scenes.course_scenes.get(info.scene).roadcolor[0];
            float g = scenes.course_scenes.get(info.scene).roadcolor[1];
            float b = scenes.course_scenes.get(info.scene).roadcolor[2];

            switch(nodes[i].type) {
                case EMPTY : break;

                case NOBORDER:
                case NBICE:
                    nodes[i].mesh.addQuadFromStripe(v1[1], v2[1], v1[2], v2[2], 0, tx+3*TDX,ty, tx+3*TDX,ty+2*TDY, tx+TDX,ty, tx+TDX,ty+2*TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[2], v2[2], v1[4], v2[4], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v1[4], v2[4], v1[5], v2[5], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v1[5], v2[5], v1[1], v2[1], r, g, b);
                    break;

                case JUMP:
                    nodes[i].mesh.addQuadFromStripe(v1[0], v2[6], v1[1], new vertex(v2[1].x,v2[6].y,v2[1].z), 0, tx+4*TDX,ty, tx+4*TDX,ty+2*TDY, tx+3*TDX,ty, tx+3*TDX,ty+2*TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[1], new vertex(v2[1].x,v2[6].y,v2[1].z), v1[2], new vertex(v2[2].x,v2[8].y,v2[2].z), 0, tx+3*TDX,ty, tx+3*TDX,ty+2*TDY, tx+TDX,ty, tx+TDX,ty+2*TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[2], new vertex(v2[2].x,v2[8].y,v2[2].z), v1[3], v2[8], 0, tx+4*TDX,ty, tx+4*TDX,ty+2*TDY, tx+3*TDX,ty, tx+3*TDX,ty+2*TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[3], v2[8], v1[4], v2[4], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v1[4], v2[4], v1[5], v2[5], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v1[5], v2[5], v1[0], v2[6], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v2[5], v2[4], v2[6], v2[8], r, g, b);
                    break;

                default:
                    nodes[i].mesh.addQuadFromStripe(v1[0], v2[0], v1[1], v2[1], 0, tx + 4 * TDX, ty, tx + 4 * TDX, ty + 2 * TDY, tx + 3 * TDX, ty, tx + 3 * TDX, ty + 2 * TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[1], v2[1], v1[2], v2[2], 0, tx + 3 * TDX, ty, tx + 3 * TDX, ty + 2 * TDY, tx + TDX, ty, tx + TDX, ty + 2 * TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[2], v2[2], v1[3], v2[3], 0, tx + TDX, ty, tx + TDX, ty + 2 * TDY, tx, ty, tx, ty + 2 * TDY);
                    nodes[i].mesh.addQuadFromStripe(v1[3], v2[3], v1[4], v2[4], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v1[4], v2[4], v1[5], v2[5], r, g, b);
                    nodes[i].mesh.addQuadFromStripe(v1[5], v2[5], v1[0], v2[0], r, g, b);
                    break;
            }

            if(nodes[i].type==TUNNEL){

                nodes[i].mesh.addQuadFromStripeDoubleSided(v1[0], v2[0], v1[6], v2[6], 0, 0, 6*TDY, 0, 8*TDY, TDX, 6*TDY, TDX, 8*TDY);
                nodes[i].mesh.addQuadFromStripeDoubleSided(v1[6], v2[6], v1[7], v2[7], 0,  TDX, 6*TDY, TDX, 8*TDY, 2*TDX, 6*TDY, 2*TDX, 8*TDY);
                nodes[i].mesh.addQuadFromStripeDoubleSided(v1[7], v2[7], v1[8], v2[8], 0, 2*TDX, 6*TDY, 2*TDX, 8*TDY, 3*TDX,6*TDY, 3*TDX,8*TDY);
                nodes[i].mesh.addQuadFromStripeDoubleSided(v1[8], v2[8], v1[3], v2[3], 0, 3*TDX,6*TDY, 3*TDX,8*TDY, 4*TDX,6*TDY, 4*TDX,8*TDY);
            }

            nodes[i].mesh.buildGdxMesh();
        }

    }

    void render(ShaderProgram shader, PerspectiveCamera cam, int startseg, long counter, int range)
    {
        int i,j,k,s;
        //vertex v1[9], v2[9];
        float dx, dz, tx, ty;
        //GLfloat ambientLight[4]={0.2f,0.2f,0.2f,1.0f}, diffuseLight[4]={0.8f,0.8f,0.8f,1.0f};
        boolean low=false;

        //glDisable(GL_LIGHTING);
        //glDisable(GL_BLEND);

        for(s=0; s<range; s++){

            //glEnable(GL_CULL_FACE);
            i=(startseg-(range/2)+s);
            if(i<0) i+=info.nsegments;
            if(i>=info.nsegments) i-=info.nsegments;
            if((s<(range/4)) || (s>=(3*range/4))) low=true; else low=false;

            /*v1[0]=vertex(nodes[i].x[0],nodes[i].y[0],nodes[i].z[0]);
            v1[1]=vertex(nodes[i].x[1],nodes[i].y[1],nodes[i].z[1]);
            v1[2]=vertex(nodes[i].x[2],nodes[i].y[2],nodes[i].z[2]);
            v1[3]=vertex(nodes[i].x[3],nodes[i].y[3],nodes[i].z[3]);
            v1[4]=vertex(nodes[i].x[4],nodes[i].y[4],nodes[i].z[4]);
            v1[5]=vertex(nodes[i].x[5],nodes[i].y[5],nodes[i].z[5]);
            v1[6]=vertex(nodes[i].x[6],nodes[i].y[6],nodes[i].z[6]);
            v1[7]=vertex(nodes[i].x[7],nodes[i].y[7],nodes[i].z[7]);
            v1[8]=vertex(nodes[i].x[8],nodes[i].y[8],nodes[i].z[8]);

            v2[0]=vertex(nodes[(i+1)%info.nsegments].x[0],nodes[(i+1)%info.nsegments].y[0],nodes[(i+1)%info.nsegments].z[0]);
            v2[1]=vertex(nodes[(i+1)%info.nsegments].x[1],nodes[(i+1)%info.nsegments].y[1],nodes[(i+1)%info.nsegments].z[1]);
            v2[2]=vertex(nodes[(i+1)%info.nsegments].x[2],nodes[(i+1)%info.nsegments].y[2],nodes[(i+1)%info.nsegments].z[2]);
            v2[3]=vertex(nodes[(i+1)%info.nsegments].x[3],nodes[(i+1)%info.nsegments].y[3],nodes[(i+1)%info.nsegments].z[3]);
            v2[4]=vertex(nodes[(i+1)%info.nsegments].x[4],nodes[(i+1)%info.nsegments].y[4],nodes[(i+1)%info.nsegments].z[4]);
            v2[5]=vertex(nodes[(i+1)%info.nsegments].x[5],nodes[(i+1)%info.nsegments].y[5],nodes[(i+1)%info.nsegments].z[5]);
            v2[6]=vertex(nodes[(i+1)%info.nsegments].x[6],nodes[(i+1)%info.nsegments].y[6],nodes[(i+1)%info.nsegments].z[6]);
            v2[7]=vertex(nodes[(i+1)%info.nsegments].x[7],nodes[(i+1)%info.nsegments].y[7],nodes[(i+1)%info.nsegments].z[7]);
            v2[8]=vertex(nodes[(i+1)%info.nsegments].x[8],nodes[(i+1)%info.nsegments].y[8],nodes[(i+1)%info.nsegments].z[8]);*/


            //dx=((v1[1].x+v1[2].x+v2[1].x+v2[2].x)/4)-(cam->x+(VRPDIST*cos(cam->ry+(PI/2))));
            //dz=((v1[1].z+v1[2].z+v2[1].z+v2[2].z)/4)-(cam->z-(VRPDIST*sin(cam->ry+(PI/2))));

            //power->render(cam,cam->x+(VRPDIST*cos(cam->ry+(PI/2))),0,cam->z-(VRPDIST*sin(cam->ry+(PI/2))),0,0,0);

            //if(dx*dx+dz*dz<DRAWDIST)
            {
                nodes[i].mesh.render(shader, cam, 0, 0, 0, 0, 0, 0);
                /*
                k=i-1; if(k<0) k+=info.nsegments;

                for(j=0; j<9; j++){
                    if((j<6) || (nodes[i].type==TUNNEL) || (nodes[i].type==JUMP) || (nodes[k].type==TUNNEL) || (nodes[k].type==JUMP)){

                        v1[j].reset();
                        v1[j].translate(-cam->x,-cam->y,-cam->z);
                        v1[j].rotate(-cam->rx,-cam->ry,-cam->rz);

                        v2[j].reset();
                        v2[j].translate(-cam->x,-cam->y,-cam->z);
                        v2[j].rotate(-cam->rx,-cam->ry,-cam->rz);
                    };
                };

                // Texture coordinates
                switch(nodes[i].type){
                    case TUNNEL :
                    case NOBORDER :
                    case NORMAL : tx=0; ty= 0.01F; break;
                    case END    : tx=4*TDX; ty= 0.01F; break;
                    case START  : tx=4*TDX; ty= (float) (2*TDY+0.01); break;
                    case NBICE :
                    case ICE    : tx=4*TDX; ty=5*TDY; break;
                    case BOOSTER :
                    case JUMP    : tx=0; ty=3*TDY; break;
                };

                glBindTexture(GL_TEXTURE_2D,road->id);
                switch(nodes[i].type){
                    case EMPTY:
                        break;

                    case NOBORDER:
                    case NBICE:
                        set_textured(low);
                        glBegin(GL_QUADS);
                        glTexCoord2f(tx+3*TDX,ty);
                        glVertex3f(v1[1].nx,v1[1].ny,v1[1].nz);
                        glTexCoord2f(tx+3*TDX,ty+2*TDY);
                        glVertex3f(v2[1].nx,v2[1].ny,v2[1].nz);
                        glTexCoord2f(tx+TDX,ty+2*TDY);
                        glVertex3f(v2[2].nx,v2[2].ny,v2[2].nz);
                        glTexCoord2f(tx+TDX,ty);
                        glVertex3f(v1[2].nx,v1[2].ny,v1[2].nz);
                        glEnd();
                        glDisable(GL_TEXTURE_2D);
                        glBegin(GL_QUAD_STRIP);
                        glColor3f(scenes[info.scene].roadcolor[0],scenes[info.scene].roadcolor[1],scenes[info.scene].roadcolor[2]);
                        glVertex3f(v1[2].nx,v1[2].ny,v1[2].nz);
                        glVertex3f(v2[2].nx,v2[2].ny,v2[2].nz);
                        glVertex3f(v1[4].nx,v1[4].ny,v1[4].nz);
                        glVertex3f(v2[4].nx,v2[4].ny,v2[4].nz);
                        glVertex3f(v1[5].nx,v1[5].ny,v1[5].nz);
                        glVertex3f(v2[5].nx,v2[5].ny,v2[5].nz);
                        glVertex3f(v1[1].nx,v1[1].ny,v1[1].nz);
                        glVertex3f(v2[1].nx,v2[1].ny,v2[1].nz);
                        glEnd();
                        RENDERED_TRIANGLES+=6;
                        break;

                    case JUMP :
                        set_textured(low);
                        glBegin(GL_QUAD_STRIP);
                        glTexCoord2f(tx+4*TDX,ty);
                        glVertex3f(v1[0].nx,v1[0].ny,v1[0].nz);
                        glTexCoord2f(tx+4*TDX,ty+2*TDY);
                        glVertex3f(v2[6].nx,v2[6].ny,v2[6].nz);
                        glTexCoord2f(tx+3*TDX,ty);
                        glVertex3f(v1[1].nx,v1[1].ny,v1[1].nz);
                        glTexCoord2f(tx+3*TDX,ty+2*TDY);
                        glVertex3f(v2[1].nx,v2[6].ny,v2[1].nz);
                        glTexCoord2f(tx+TDX,ty);
                        glVertex3f(v1[2].nx,v1[2].ny,v1[2].nz);
                        glTexCoord2f(tx+TDX,ty+2*TDY);
                        glVertex3f(v2[2].nx,v2[8].ny,v2[2].nz);
                        glTexCoord2f(tx,ty);
                        glVertex3f(v1[3].nx,v1[3].ny,v1[3].nz);
                        glTexCoord2f(tx,ty+2*TDY);
                        glVertex3f(v2[8].nx,v2[8].ny,v2[8].nz);
                        glEnd();
                        glDisable(GL_TEXTURE_2D);
                        glBegin(GL_QUAD_STRIP);
                        glColor3f(scenes[info.scene].roadcolor[0],scenes[info.scene].roadcolor[1],scenes[info.scene].roadcolor[2]);
                        glVertex3f(v1[3].nx,v1[3].ny,v1[3].nz);
                        glVertex3f(v2[8].nx,v2[8].ny,v2[8].nz);
                        glVertex3f(v1[4].nx,v1[4].ny,v1[4].nz);
                        glVertex3f(v2[4].nx,v2[4].ny,v2[4].nz);
                        glVertex3f(v1[5].nx,v1[5].ny,v1[5].nz);
                        glVertex3f(v2[5].nx,v2[5].ny,v2[5].nz);
                        glVertex3f(v1[0].nx,v1[0].ny,v1[0].nz);
                        glVertex3f(v2[6].nx,v2[6].ny,v2[6].nz);
                        glEnd();
                        glBegin(GL_QUADS);
                        glVertex3f(v2[5].nx,v2[5].ny,v2[5].nz);
                        glVertex3f(v2[4].nx,v2[4].ny,v2[4].nz);
                        glVertex3f(v2[8].nx,v2[8].ny,v2[8].nz);
                        glVertex3f(v2[6].nx,v2[6].ny,v2[6].nz);
                        glEnd();
                        RENDERED_TRIANGLES+=8;
                        break;

                    default:
                        set_textured(low);
                        glBegin(GL_QUAD_STRIP);
                        glTexCoord2f(tx+4*TDX,ty);
                        glVertex3f(v1[0].nx,v1[0].ny,v1[0].nz);
                        glTexCoord2f(tx+4*TDX,ty+2*TDY);
                        glVertex3f(v2[0].nx,v2[0].ny,v2[0].nz);
                        glTexCoord2f(tx+3*TDX,ty);
                        glVertex3f(v1[1].nx,v1[1].ny,v1[1].nz);
                        glTexCoord2f(tx+3*TDX,ty+2*TDY);
                        glVertex3f(v2[1].nx,v2[1].ny,v2[1].nz);
                        glTexCoord2f(tx+TDX,ty);
                        glVertex3f(v1[2].nx,v1[2].ny,v1[2].nz);
                        glTexCoord2f(tx+TDX,ty+2*TDY);
                        glVertex3f(v2[2].nx,v2[2].ny,v2[2].nz);
                        glTexCoord2f(tx,ty);
                        glVertex3f(v1[3].nx,v1[3].ny,v1[3].nz);
                        glTexCoord2f(tx,ty+2*TDY);
                        glVertex3f(v2[3].nx,v2[3].ny,v2[3].nz);
                        glEnd();
                        glDisable(GL_TEXTURE_2D);
                        glBegin(GL_QUAD_STRIP);
                        glColor3f(scenes[info.scene].roadcolor[0],scenes[info.scene].roadcolor[1],scenes[info.scene].roadcolor[2]);
                        glVertex3f(v1[3].nx,v1[3].ny,v1[3].nz);
                        glVertex3f(v2[3].nx,v2[3].ny,v2[3].nz);
                        glVertex3f(v1[4].nx,v1[4].ny,v1[4].nz);
                        glVertex3f(v2[4].nx,v2[4].ny,v2[4].nz);
                        glVertex3f(v1[5].nx,v1[5].ny,v1[5].nz);
                        glVertex3f(v2[5].nx,v2[5].ny,v2[5].nz);
                        glVertex3f(v1[0].nx,v1[0].ny,v1[0].nz);
                        glVertex3f(v2[0].nx,v2[0].ny,v2[0].nz);
                        glEnd();
                        RENDERED_TRIANGLES+=6;
                        break;
                };

                if(nodes[i].type==TUNNEL){
                    set_textured(low);
                    glDisable(GL_CULL_FACE);
                    glBegin(GL_QUAD_STRIP);
                    glTexCoord2f(0,6*TDY);
                    glVertex3f(v1[0].nx,v1[0].ny,v1[0].nz);
                    glTexCoord2f(0,8*TDY);
                    glVertex3f(v2[0].nx,v2[0].ny,v2[0].nz);
                    glTexCoord2f(TDX,6*TDY);
                    glVertex3f(v1[6].nx,v1[6].ny,v1[6].nz);
                    glTexCoord2f(TDX,8*TDY);
                    glVertex3f(v2[6].nx,v2[6].ny,v2[6].nz);
                    glTexCoord2f(2*TDX,6*TDY);
                    glVertex3f(v1[7].nx,v1[7].ny,v1[7].nz);
                    glTexCoord2f(2*TDX,8*TDY);
                    glVertex3f(v2[7].nx,v2[7].ny,v2[7].nz);
                    glTexCoord2f(3*TDX,6*TDY);
                    glVertex3f(v1[8].nx,v1[8].ny,v1[8].nz);
                    glTexCoord2f(3*TDX,8*TDY);
                    glVertex3f(v2[8].nx,v2[8].ny,v2[8].nz);
                    glTexCoord2f(4*TDX,6*TDY);
                    glVertex3f(v1[3].nx,v1[3].ny,v1[3].nz);
                    glTexCoord2f(4*TDX,8*TDY);
                    glVertex3f(v2[3].nx,v2[3].ny,v2[3].nz);
                    glEnd();
                    RENDERED_TRIANGLES+=6;
                };*/

            };
        };

        //Item cube
        /*
        glEnable(GL_LIGHTING);
        glEnable(GL_CULL_FACE);
        glEnable(GL_LIGHT0); glDisable(GL_LIGHT1); glDisable(GL_LIGHT2); glDisable(GL_LIGHT3);
        glDisable(GL_LIGHT4); glDisable(GL_LIGHT5); glDisable(GL_LIGHT6); glDisable(GL_LIGHT7);*/

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for(s=0; s<range; s++){
            i=(startseg-(range/2)+s)%info.nsegments;
            if(i < 0) i += info.nsegments;

            if(nodes[i].item!=NONE){

                switch(nodes[i].item){
                    default:
                    case BOOST : bcube.alpha_render(shader, cam,cube_x(i),cube_y(i)+3.5f,cube_z(i),counter/50f, counter/70f,0,nodes[i].itemfade); break;
                    case ENERGY: ecube.alpha_render(shader, cam,cube_x(i),cube_y(i)+3.5f,cube_z(i),counter/50f, counter/70f,0,nodes[i].itemfade); break;
                    case SHIELD: scube.alpha_render(shader, cam,cube_x(i),cube_y(i)+3.5f,cube_z(i),counter/50f, counter/70f,0,nodes[i].itemfade); break;
                    case POWER : power.alpha_render(shader, cam,cube_x(i),cube_y(i)+3.5f,cube_z(i),counter/50f, counter/70f,0,nodes[i].itemfade); break;
                    case MINE  : mine.alpha_render(shader, cam,cube_x(i), (float) (cube_y(i)+3.5f+2f*Math.sin(counter/50.0f)),cube_z(i),counter/50.0f,counter/75.0f,0,nodes[i].itemfade); break;
                };
            };
        };

        Gdx.gl.glDisable(GL20.GL_BLEND);


        /*glLightfv(GL_LIGHT0,GL_AMBIENT,ambientLight);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,diffuseLight);*/

        //Decorates
        if(info.quality>0)
            for(s=0; s<range*2; s++){
                i=(startseg-range+s)%info.nsegments;
                if(i<0) i+=info.nsegments;
                if(nodes[i].detype<=2){
                    decorate[nodes[i].detype].set_scale(nodes[i].dscalexz,nodes[i].dscaley,nodes[i].dscalexz);
                    decorate[nodes[i].detype].render(shader, cam, nodes[i].dex,GROUNDY,nodes[i].dez,0,0,0);
                };
            };

    }

    float cube_x(int s)
    {
        int s2=(s+1)%info.nsegments;
        return (float) ((nodes[s].x[1]+nodes[s].x[2]+nodes[s2].x[1]+nodes[s2].x[2])/4.0);
    }
    float cube_y(int s)
    {
        int s2=(s+1)%info.nsegments;
        return (float) ((nodes[s].y[1]+nodes[s].y[2]+nodes[s2].y[1]+nodes[s2].y[2])/4.0);
    }
    float cube_z(int s)
    {
        int s2=(s+1)%info.nsegments;
        return (float) ((nodes[s].z[1]+nodes[s].z[2]+nodes[s2].z[1]+nodes[s2].z[2])/4.0);
    }

    boolean inside_segment(float xx, float zz, int s)
    {
        int s2=(s+1)%info.nsegments;


        if((inside_triangle(xx,zz,nodes[s].x[9],nodes[s].y[9],nodes[s].z[9],
            nodes[s2].x[9],nodes[s2].y[9],nodes[s2].z[9],
            nodes[s2].x[10],nodes[s2].y[10],nodes[s2].z[10])) ||
            (inside_triangle(xx,zz,nodes[s].x[9],nodes[s].y[9],nodes[s].z[9],
                nodes[s2].x[10],nodes[s2].y[10],nodes[s2].z[10],
                nodes[s].x[10],nodes[s].y[10],nodes[s].z[10])))
            return true;

        else return false;
    }
    boolean inside_triangle(float xx, float zz, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3)
    {
        float temp;

        if((xx<x1) && (xx<x2) && (xx<x3)) return false;
        if((xx>x1) && (xx>x2) && (xx>x3)) return false;
        if((zz<z1) && (zz<z2) && (zz<z3)) return false;
        if((zz>z1) && (zz>z2) && (zz>z3)) return false;

        if(z1>z3){

            temp=x3; x3=x1; x1=temp;
            temp=z3; z3=z1; z1=temp;
        }
        if(z1>z2){

            temp=x2; x2=x1; x1=temp;
            temp=z2; z2=z1; z1=temp;
        }
        if(z2>z3){

            temp=x3; x3=x2; x2=temp;
            temp=z3; z3=z2; z2=temp;
        }

        if((z1==z2) && (z2==z3)) return false;
        if((zz<z1) || (zz>=z3)) return false;

        return true;
    }

    Vector2 edge_perpendicular_vector(int s, int e, boolean invert)
    {
        float a,b,x1,x2,y1,y2,m;
        int s2=(s+1)%info.nsegments;
        Vector2 v = new Vector2();

        // Calculate perpendicular vector to edge e from segment (s,s+1)
        x1=nodes[s].x[e]; x2=nodes[s2].x[e];
        y1=nodes[s].z[e]; y2=nodes[s2].z[e];

        a=x2-x1;
        b=y2-y1;
        m= (float) Math.sqrt(a*a+b*b);

        // Make unit
        v.x=-b/m;
        v.y=a/m;

        //Invert if needed
        if(invert){
            v.x=-v.x;
            v.y=-v.y;
        };

        return v;
    }

    float distance_to_segment(float x, float y, float z, int s)
    {
        float dx, dz;
        int s2=(s+1)%info.nsegments;
        dx=x-((nodes[s].x[1]+nodes[s].x[2]+nodes[s2].x[1]+nodes[s2].x[2])/4);
        dz=z-((nodes[s].z[1]+nodes[s].z[2]+nodes[s2].z[1]+nodes[s2].z[2])/4);

        return (dx*dx+dz*dz);
    }

    float distance_to_edge(float x,float y,float z, int s, int e)
    {
        float a, b, c,x1,x2,y1,y2;
        int s2=(s+1)%info.nsegments;

        // Vector between the two points of the edge segment
        x1=nodes[s].x[e]; x2=nodes[s2].x[e];
        y1=nodes[s].z[e]; y2=nodes[s2].z[e];

        // Equation ax+by+c=0 from the edge e
        a=-(y2-y1);
        b=x2-x1;
        c=-a*x1-b*y1;

        // Dist from (x,y,z) to edge e
        return (float) (Math.abs(a*x+b*z+c)/Math.sqrt(a*a+b*b));

    }

    float beginning_x(int i)
    {
        float x1,x2,z1,z2,x,z,v1,v2,s= (float) (info.width*0.25);
        int n=info.nsegments-2,j;
        x1= (float) ((nodes[0].x[1]+nodes[0].x[2])/2.0); z1= (float) ((nodes[0].z[1]+nodes[0].z[2])/2.0);
        x2= (float) ((nodes[n].x[1]+nodes[n].x[2])/2.0); z2= (float) ((nodes[n].z[1]+nodes[n].z[2])/2.0);
        Vector2 v = edge_perpendicular_vector(n,1, true);

        j=(int)(i/2);
        x= (float) (x2+((x1-x2)*j*0.25));
        if(i%2==0) x+=(v.x*s); else x-=(v.x*s);
        return x;
    }
    float beginning_z(int i)
    {
        float x1,x2,z1,z2,x,z,v1,v2,s= (float) (info.width*0.25);
        int n=info.nsegments-2,j;
        x1= (float) ((nodes[0].x[1]+nodes[0].x[2])/2.0); z1= (float) ((nodes[0].z[1]+nodes[0].z[2])/2.0);
        x2= (float) ((nodes[n].x[1]+nodes[n].x[2])/2.0); z2= (float) ((nodes[n].z[1]+nodes[n].z[2])/2.0);
        Vector2 v = edge_perpendicular_vector(n,1 ,true);

        j=(int)(i/2);
        z= (float) (z2+((z1-z2)*j*0.25));
        if(i%2==0) z+=(v.y*s); else z-=(v.y*s);
        return z;
    }

    float y_at_xz(float x,float z,int s)
    {
        int s2;
        float d,d2;

        // Solve the equation
        return ((nodes[s].a*x)+(nodes[s].c*z)+nodes[s].d)/(-nodes[s].b);
    }

    void add_random_item()
    {
        int i;

        do{
            i=(rand()%(info.nsegments-10))+1;

        }while(nodes[i].item!=NONE);

        nodes[i].item= (short) ((rand()%5)+1);
        nodes[i].itemfade=1.0f;
    }

    void play_3d_sample(PerspectiveCamera cam, float x, float y, float z, wave w)
    {
        w.playonce();
        update_3d_sample(cam,x,y,z,DSBFREQUENCY_ORIGINAL,w);
    }
    void update_3d_sample(PerspectiveCamera cam, float x, float y, float z, int freq, wave w)
    {

        float dx, dy, dz, dist;
        vertex pos = new vertex(x,y,z);

        /*pos.reset();
        pos.translate(-cam->x,-cam->y,-cam->z);
        pos.rotate(-cam->rx,-cam->ry,-cam->rz);

        if(pos.nx<-500) w.setpan(DSBPAN_LEFT);
        else if(pos.nx>500) w.setpan(DSBPAN_RIGHT);
        else w->setpan(pos.nx*DSBPAN_RIGHT/500);*/

        // Dirección izquierda/derecha relativa
        Vector3 sourcePos = new Vector3(x,y,z);
        Vector3 direction = sourcePos.cpy().sub(cam.position);
        float pan = MathUtils.clamp(direction.x / 700, -1f, 1f);

        w.setpan(-pan);

        dx=cam.position.x-x; dy=cam.position.y-y; dz=cam.position.z-z;
        dist= (float) Math.sqrt(dx*dx+dy*dy+dz*dz);

        if(dist>=700)
            w.setvolume(DSBVOLUME_MIN);
        else
            w.setvolume(DSBVOLUME_MAX-(int)((DSBVOLUME_MAX-DSBVOLUME_MIN)*dist/700f));

        w.setfreq(freq);
    }
}
