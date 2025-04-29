package com.activeminds.mach1r;

import com.badlogic.gdx.math.Vector2;

public class course {

    /*#define GROUNDY -40
        #define SKYY 90

    enum {NORMAL=0, START, END, BOOSTER, TUNNEL, JUMP, NOBORDER, ICE, NBICE, EMPTY};

    enum {NONE=0, ENERGY, BOOST, SHIELD, POWER, MINE};

    enum {TH=0,J,JH,NT,NTH,NJ,NJTH,I,IJHN,T};

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
    }

    course_info info;
    node[] nodes;
    int counter;

    course(course_info ci)
    {
        info = ci;
        counter = 0;
    }

    void update()
    {
        counter++;
        /*^for(int i=0; i<info.nsegments; i++){

            if((nodes[i].itemfade<1.0) && (nodes[i].item!=NONE)){
                nodes[i].itemfade-=0.01;
                if(nodes[i].itemfade<=0.0) nodes[i].item=NONE;
            };
        };*/
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
}
