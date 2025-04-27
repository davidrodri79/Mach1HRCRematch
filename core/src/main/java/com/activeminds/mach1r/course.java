package com.activeminds.mach1r;

public class course {

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

    public static class course_info
    {
        int radx, radz, width, nsegments, xznoisewidth, ynoisewidth;
        int xznoisegap, ynoisegap,
            ntunnel, ltunnel, nnoborder, lnoborder, nice, lice, nicenob, licenob, njump, ljump, nboost,
            nlaps, scene, quality;
    }

    course_info info;

    course(course_info ci)
    {
        info = ci;
    }

    void generate()
    {
        /*int i,j,k;

        float angle, par, u[3], v[3];
        int noise, gap, mid;


        nodes=new node[info.nsegments];


        //Add the noise to the generical ellipse
        i=0; k=0;
        if(info.xznoisewidth!=0)
            while(i<info.nsegments-info.xznoisegap){

                gap=info.xznoisegap-(rand()%5);
                // Horizontal noise (XZ)
                if(i!=0){
                    noise=rand()%int(info.xznoisewidth/2); if(k==1) noise=-noise;
                    nodes[i].xznoise+=noise;
                    nodes[(i+1)%info.nsegments].xznoise+=noise;
                    for(j=1; j<=int(gap/2); j++){
                        nodes[(i+j+1)%info.nsegments].xznoise+=noise*(int(gap/2)-j)/int(gap/2);
                        nodes[(i-j)%info.nsegments].xznoise+=noise*(int(gap/2)-j)/int(gap/2);
                    };
                }
                i+=gap; k=1-k;
            };

        i=0;
        if(info.ynoisewidth!=0)
            while(i<info.nsegments-info.ynoisegap){
                // Vertical noise (Y)
                if((i%info.ynoisegap==0) && (i!=0)){
                    noise=(rand()%info.ynoisewidth)-int(info.ynoisewidth/2);
                    mid=int(info.ynoisegap/2);
                    par=int(noise/mid);
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

            angle=i*2*PI/info.nsegments;

            nodes[i].y[0]=BORDERH+nodes[i].ynoise; nodes[i].y[1]=nodes[i].ynoise;
            nodes[i].y[2]=nodes[i].ynoise; nodes[i].y[3]=BORDERH+nodes[i].ynoise;
            nodes[i].y[4]=-GROUNDH+nodes[i].ynoise; nodes[i].y[5]=-GROUNDH+nodes[i].ynoise;
            nodes[i].y[6]=(TUNNELH/2)+nodes[i].ynoise;
            nodes[i].y[7]=TUNNELH+nodes[i].ynoise;
            nodes[i].y[8]=(TUNNELH/2)+nodes[i].ynoise;

            nodes[i].x[0]=(info.radx+nodes[i].xznoise-info.width/2-BORDERW)*cos(angle);
            nodes[i].x[1]=(info.radx+nodes[i].xznoise-info.width/2)*cos(angle);
            nodes[i].x[2]=(info.radx+nodes[i].xznoise+info.width/2)*cos(angle);
            nodes[i].x[3]=(info.radx+nodes[i].xznoise+info.width/2+BORDERW)*cos(angle);
            nodes[i].x[4]=(info.radx+nodes[i].xznoise+info.width/2+BORDERW)*cos(angle);
            nodes[i].x[5]=(info.radx+nodes[i].xznoise-info.width/2-BORDERW)*cos(angle);
            nodes[i].x[6]=nodes[i].x[0];
            nodes[i].x[7]=(info.radx+nodes[i].xznoise)*cos(angle);
            nodes[i].x[8]=nodes[i].x[3];
            nodes[i].x[9]=(info.radx+nodes[i].xznoise-(info.width/2)-50.0)*cos(angle);
            nodes[i].x[10]=(info.radx+nodes[i].xznoise+(info.width/2)+50.0)*cos(angle);

            nodes[i].z[0]=(info.radz+nodes[i].xznoise-info.width/2-BORDERW)*sin(angle);
            nodes[i].z[1]=(info.radz+nodes[i].xznoise-info.width/2)*sin(angle);
            nodes[i].z[2]=(info.radz+nodes[i].xznoise+info.width/2)*sin(angle);
            nodes[i].z[3]=(info.radz+nodes[i].xznoise+info.width/2+BORDERW)*sin(angle);
            nodes[i].z[4]=(info.radz+nodes[i].xznoise+info.width/2+BORDERW)*sin(angle);
            nodes[i].z[5]=(info.radz+nodes[i].xznoise-info.width/2-BORDERW)*sin(angle);
            nodes[i].z[6]=nodes[i].z[0];
            nodes[i].z[7]=(info.radz+nodes[i].xznoise)*sin(angle);
            nodes[i].z[8]=nodes[i].z[3];
            nodes[i].z[9]=(info.radz+nodes[i].xznoise-(info.width/2)-50.0)*sin(angle);
            nodes[i].z[10]=(info.radz+nodes[i].xznoise+(info.width/2)+50.0)*sin(angle);


            //The decorate coordinates
            nodes[i].detype=rand()%10;
            noise=info.width+30+(rand()%150);
            nodes[i].dscalexz=0.5+(rand()%100)*0.01;
            nodes[i].dscaley=0.5+(rand()%100)*0.01;
            if(rand()%2==0){

                nodes[i].dex=(info.radx+nodes[i].xznoise+noise)*cos(angle);
                nodes[i].dez=(info.radz+nodes[i].xznoise+noise)*sin(angle);
            }else{

                nodes[i].dex=(info.radx+nodes[i].xznoise-noise)*cos(angle);
                nodes[i].dez=(info.radz+nodes[i].xznoise-noise)*sin(angle);
            };


        };

        // Road segment types
        nodes[0].type=START;
        for(i=info.nsegments-5; i<=info.nsegments; i++) nodes[i].type=END;


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
        */
    }
}
