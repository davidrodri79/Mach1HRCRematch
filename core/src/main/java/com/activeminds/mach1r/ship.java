package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class ship {

    public static final int MAX_LIGHTS = 3;
    public static final int MAXENERGY = 1000;
    public static final int NEXPLS = 5;

    public static final float GRAVITY = 9.8f/3600f;
    public static final float ICEINERTIA = 0.3f;

    public static final float MACH_1 = 1234.8f;

        public static final float DRAWFAR =	340*340;
        public static final float DRAWMEDIUM = 220*220;
        public static final float DRAWNEAR = 140*140;
    public static final float DISTCOLLIDE = 5.0f*5.0f;
    public static final float DISTPICKUP =	8.0f*8.0f;
    public static final float IMPACTVEL	= 0.1f;
    public static final float BOOSTDURATION = 400;
    public static final float SHIELDDURATION = 600;
    public static final float HYPERDURATION = 1800;

        public static final int PLAY=0;
        public static final int STUN=1;
        public static final int BURN=2;
        public static final int BIGEXPL=3;
        public static final int DESTR=4;

    static class ship_model
    {
        String name, file;
        float light[] = new float[3];
        int nlights;
        float lsize, lpos[][] = new float[MAX_LIGHTS][3];
        int weight, enginef, handling;
        float sizex, sizey, sizez;
    }

    static class shipModelsJson
    {
        ArrayList<ship_model> ships;
    }

    float x, y, z, rx, ry, rz, velocity, vel[] = new float[3], acceleration, acc[] = new float[3],
          accangle, inertia, an, engine, renderx, renderz, dist,
          lightcol[] = new float[3], exppos[][] = new float[NEXPLS][3], expframe[] = new float[NEXPLS], dif_an;
    int segment, nextsegment, lap, pos, energy, messcount, maxspeed, stunforce;
    short shield, boost, power, hypermode;
    short state, camtype, camchwait, nboosts;
    long counter, time, laptime, totaltime;
    boolean outofcourse, raceover, engwavplaying, burwavplaying, sliwavplaying, alawavplaying, scrwavplaying ,finallapflag,
        backwards;
    String message;
    solid mesh, lowres;
    ship_model data;
    sprite logo;
    vertex cam_pos, vrp;
    course cour;

    public ship(int model, int id, course cour) {

        Json json = new Json();
        FileHandle file = Gdx.files.internal("ships.json");
        String fileText = file.readString();
        shipModelsJson l = json.fromJson(shipModelsJson.class, fileText);

        data = l.ships.get(model);
        mesh = new solid();
        mesh.load_mesh("model/"+data.file+".msh", true);
        mesh.centrate(true,true,true);

        lowres = new solid();
        lowres.load_mesh("model/"+data.file+"lr.msh", true);
        lowres.centrate(true,true,true);

        logo=new sprite(256,128,"sprite/"+data.file+"lg.png");

        cam_pos = new vertex();
        vrp = new vertex();

        rx=0.0f; ry= (float) (Math.PI/2); rz=0.0f; counter=0; state=PLAY; outofcourse=false; raceover=false; time=0; laptime=0;
        velocity=0.0f; vel[0]=0.0f; vel[1]=0.0f; vel[2]=0.0f; engine=0.0f; stunforce=0;
        cam_pos=new vertex(x,y,z); vrp=new vertex(x,y,z+1);
        segment=0; nextsegment=0; lap=1; pos=0; camtype=0; camchwait=0;
        energy=MAXENERGY; shield=0; boost=0; power=0; maxspeed=0; hypermode=0;
        messcount=0; finallapflag=false; backwards=false; nboosts=3;

        this.cour = cour;

        // Begininnig position

        if(cour==null){
            x=0; y=0; z=0;
        }else{
            x=cour.beginning_x(id); z=cour.beginning_z(id);
            y= (float) (cour.y_at_xz(x,z,segment)+3.0);
        };

    }

    void update(PerspectiveCamera cam, controlm ctr, int tc)
    {
        float dold, dnew, v1, v2, ya, yb;
        float f[] = new float[3], ftrac[]={0.0f,0.0f,0.0f}, fdrag[]={0.0f,0.0f,0.0f}, frr[]={0.0f,0.0f,0.0f}, enginef;
        float radius, driveangle, omega;
        float dx, dy, dz, da;
        int seg, i;
        String s, t;

        rx=0.0f; rz=0.0f; accangle=0.0f; driveangle=0.0f; backwards=false;

        if(state!=PLAY) counter++;
        if(messcount>0) messcount-=1;

        // Drive
        if(ctr.joy_xaxis[tc]!=0) {
            driveangle= (float) (-(Math.PI/2.0)*(ctr.joy_xaxis[tc]/1500.0));
        };
        accangle=ry+driveangle;

        radius= (float) (7/Math.sin(driveangle));
        omega=velocity/radius;


        if(cour.counter>540) time++;

        if((state==PLAY) && (cour.counter>540)){


            // Angle diferential
            da=ry-optimal_direction();
            // Mantain the difference between (-PI,PI)
            if(da>=Math.PI) da-=2*Math.PI;
            if(da<=-Math.PI) da+=2*Math.PI;

            dif_an=da;

            //if(da>=2*Math.PI/3) new_message("           wrong way!");

            // Movement: Speed, acceleration
            enginef= (float) (110.0+(10.0*data.enginef));

            if(boost>0) enginef*=2.0;
            if(hypermode>0) enginef*=1.5;

            if(ctr.boton(tc,0)){

                ftrac[0]= (float) (enginef*Math.cos(ry)); ftrac[1]=0.0f; ftrac[2]= (float) (enginef*Math.sin(ry));
                engine+=0.01;

            }else if(ctr.boton(tc,2)){

                ftrac[0]= (float) (-0.3*enginef*Math.cos(ry)); ftrac[1]=0.0f; ftrac[2]= (float) (-0.3*enginef*Math.sin(ry));
                engine-=0.01; backwards=true;

                // Normal brake
            }else if((ctr.aba(tc)) && (velocity>0.1)){

                ftrac[0]= (float) (-20.0*Math.cos(ry)); ftrac[1]=0.0f; ftrac[2]= (float) (-20.0*Math.sin(ry));
                engine-=0.04;
            }else
                engine-=0.01;

            if((ctr.boton(tc,1)) && (boost==0) && (nboosts>0)){
                boost= (short) BOOSTDURATION; nboosts--;
                //cour.play_3d_sample(cam,x,y,z,takeb);
            }

        }else engine-=0.01f;

        if(engine<0.0) engine=0.0f;
        if(engine>1.0) engine=1.0f;

        fdrag[0]= (float) (-1.0*vel[0]*Math.abs(vel[0])); fdrag[2]= (float) (-1.0*vel[2]*Math.abs(vel[2]));

        // Ice slide
        if(((cour.nodes[segment].type==course.ICE) || (cour.nodes[segment].type==course.NBICE)) &&
            (cour.distance_to_edge(x,y,z,segment,1)>cour.info.width/4.0) &&
            (cour.distance_to_edge(x,y,z,segment,2)>cour.info.width/4.0)
        ){

            frr[0]= 0.0f; frr[2]=0.0f; ftrac[0]*=0.33;	ftrac[1]*=0.33;
            //if(!sliwavplaying) {sliwavplaying=true; slide->playlooped();};
            //cour.update_3d_sample(cam,x,y,z,DSBFREQUENCY_ORIGINAL,slide);

        }else{

            frr[0]= (float) (-30.0*vel[0]); frr[2]= (float) (-30.0*vel[2]);
            //if(sliwavplaying) {sliwavplaying=false; slide->stop();};
        };

        ry+=(omega/(20-(2*data.handling)));
        if(state==STUN) ry+=(stunforce-counter)*0.001;
        if(ry>=2*Math.PI) ry-=2*Math.PI;
        if(ry<0) ry+=2*Math.PI;

        for(i=0; i<3; i++){
            f[i]=ftrac[i]+fdrag[i]+frr[i];
            acc[i]=f[i]/data.weight;
            if(state==STUN) acc[i]=0.0f;
        };

        vel[0]+=acc[0]; vel[2]+=acc[2];

        x-=vel[0]; z+=vel[2];

        renderx= (float) (x+10*(Math.cos(ry+Math.PI))); renderz= (float) (z+10*(Math.cos(ry+Math.PI)));


        velocity=module(vel[0],vel[1],vel[2]);
        acceleration=module(acc[0],acc[1],acc[2]);


        /*if((engwavplaying==false) && (velocity>0.1)) {engsound->playlooped(); engwavplaying=true;};
        if((engwavplaying==true) && (velocity<=0.1)) {engsound->stop(); engwavplaying=false;};
        if(engwavplaying) cour.update_3d_sample(cam,x,y,z,(velocity*DSBFREQUENCY_MAX/15.0),engsound);*/

        //if(velocity_kmh()>=MACH_1) new_message("       mach-1 speed reached!");
        if(velocity_kmh()>maxspeed) maxspeed= (int) velocity_kmh();

        /*if((alawavplaying==false) && ((energy<(int)(MAXENERGY/4)) && (state<BURN))) {alarm->playlooped(); alawavplaying=true;};
        if((alawavplaying==true) && ((energy>=(int)(MAXENERGY/4)) || (state>=BURN))) {alarm->stop(); alawavplaying=false;};
        if(alawavplaying) cour.update_3d_sample(cam,x,y,z,int(((MAXENERGY/4)-energy)*DSBFREQUENCY_MAX/(MAXENERGY/4)),alarm);*/


        // Care not to get out of the course

        // Right side : Outside the course
        if(cour.distance_to_edge(renderx,y,renderz,segment,1)>cour.info.width-4.0){
            if(!outofcourse){
                if((cour.nodes[segment].type==course.NOBORDER) || (cour.nodes[segment].type==course.NBICE))
                    outofcourse=true;
                else{
                    /*if (velocity_kmh()>550) cour.play_3d_sample(cam,x,y,z,colsound);
                    else if(!scrwavplaying){
                        scratch->playlooped();
                        cour.update_3d_sample(cam,x,y,z,DSBFREQUENCY_ORIGINAL,scratch);
                        scrwavplaying=true;
                    };*/
                    Vector2 v = cour.edge_perpendicular_vector(segment,1,false);
                    x+=v.x*velocity; z+=v.y*velocity;
                    vel[0]*=0.5f; vel[1]*=0.5f; vel[2]*=0.5f;
                    lose_energy((int) (velocity*4));
                };
            }

            // Inside the course
        }else if(y>cour.y_at_xz(renderx,renderz,segment)){
            if(outofcourse) outofcourse=false;
        };

        // Left side : Outside the course
        if(cour.distance_to_edge(renderx,y,renderz,segment,2)>cour.info.width-4.0){
            if(!outofcourse){
                if((cour.nodes[segment].type==course.NOBORDER) || (cour.nodes[segment].type==course.NBICE))
                    outofcourse=true;
                else{
                    /*if (velocity_kmh()>550) cour.play_3d_sample(cam,x,y,z,colsound);
                    else if(!scrwavplaying){
                        scratch->playlooped();
                        cour.update_3d_sample(cam,x,y,z,DSBFREQUENCY_ORIGINAL,scratch);
                        scrwavplaying=true;
                    };*/
                    Vector2 v = cour.edge_perpendicular_vector(segment,2,true);
                    x+=v.x*velocity; z+=v.y*velocity;
                    vel[0]*=0.5; vel[1]*=0.5; vel[2]*=0.5;
                    lose_energy((int) (velocity*4));
                };
            }

            // Inside the course
        }else if(y>cour.y_at_xz(renderx,renderz,segment)){
            if(outofcourse) outofcourse=false;
        };


        if(cour.nodes[segment].type==course.EMPTY) outofcourse=true;

        if((cour.nodes[segment].type==course.BOOSTER) && (boost==0)){
            boost= (short) (BOOSTDURATION/4);
            //cour.play_3d_sample(cam,x,y,z,takeb);
        };

        if((cour.distance_to_edge(renderx,y,renderz,segment,1)>6.0) &&
            (cour.distance_to_edge(renderx,y,renderz,segment,2)>6.0) &&
            (scrwavplaying)){

            //scratch->stop(); scrwavplaying=false;
        }


        //Drawing: Update which is the course segment where the ship is

        seg=(segment+1)%cour.info.nsegments;
        if(cour.inside_segment(renderx,renderz,seg)) segment=seg;
        else{
            seg=segment-1; if(seg<0) seg+=cour.info.nsegments;
            if(cour.inside_segment(renderx,renderz,seg)) segment=seg;
        }
        // Check if ok by distances
        //if(!cour.inside_segment(renderx,renderz,segment)){
        seg=(segment+1)%cour.info.nsegments;
        dold=cour.distance_to_segment(renderx,y,renderz,segment);
        dnew=cour.distance_to_segment(renderx,y,renderz,seg);
        if(dnew<dold) segment=seg;
        else{
            seg=segment-1; if(seg<0) seg+=cour.info.nsegments;
            dnew=cour.distance_to_segment(renderx,y,renderz,seg);
            if(dnew<dold) segment=seg;
        };
        //}

        if(outofcourse) ya=course.GROUNDY;
        else ya=cour.y_at_xz(renderx,renderz,segment);

        // Gravity and floating force

        if(y>ya+4.5) vel[1]-=20*GRAVITY;
        if(y<ya+1.5) vel[1]+=20*GRAVITY;
        y+=vel[1]; vel[1]*=0.95;
        if(y<ya+1) y=ya+1;

        // Jump!
        if(((cour.nodes[segment].type==course.JUMP) || (cour.nodes[(segment+1)%cour.info.nsegments].type==course.JUMP))
            && (y<ya+10.0)) {vel[1]+=0.15;}

        set_camera();

        // Lap count data
        if(nextsegment==segment) nextsegment++;
        if((nextsegment>=cour.info.nsegments) && (!raceover)){
            nextsegment=0;
            if(lap==cour.info.nlaps) {totaltime=time; raceover=true;};
            if(lap<cour.info.nlaps) lap++;
            laptime=time-laptime;
            /*time_str(t,laptime);
            if(lap<cour.info.nlaps) sprintf(s,"        lap %d:%s",lap,t);
            else if((lap==cour.info.nlaps) && (!raceover)) {sprintf(s,"      final lap:%s",t); finallapflag=true;};
            new_message(s);*/
        };

        // Manage states
        //if((state==PLAY) && (energy==0)) {state=BURN; startup_expls(); counter=0;};
        if((state==STUN) && (counter>=stunforce)) {state=PLAY; counter=0;};
        //if((state==BURN) && (counter>=240)) {destroy_mesh(); state=BIGEXPL; counter=0;};
        if((state==BIGEXPL) && (counter>=100)) {state=DESTR; counter=0;};
        //if((outofcourse) && (y<=course.GROUNDY+2) && (state<BURN)) {destroy_mesh(); state=BIGEXPL; counter=0;};

        //if((state==BIGEXPL) && (counter==1)) cour.play_3d_sample(cam,x,y,z,bigexpl);
        //if((state==BURN) && (counter%20==0)) {litexpl->stop(); cour.play_3d_sample(cam,x,y,z,litexpl);};
        if(state==DESTR){
            if(!burwavplaying){
                //burning->playlooped();
                burwavplaying=true;
            };
            //cour.update_3d_sample(cam,x,y,z,DSBFREQUENCY_ORIGINAL,burning);

        };
        // Update of burning explosions
        if(state==BURN)
            for(i=0; i<NEXPLS; i++){
                expframe[i]+=1.0;
                if(expframe[i]>=16.0){
                    exppos[i][0]= (float) (renderx-3.0+(float)(course.rand()%6));
                    exppos[i][1]= (float) (y-3.0+(course.rand()%6));
                    exppos[i][2]= (float) (renderz-3.0+(course.rand()%6));
                    expframe[i]=0.0f;
                };
            };

        //Pick up items

        dx=renderx-cour.cube_x(segment); dy=y-cour.cube_y(segment); dz=renderz-cour.cube_z(segment);

        if(state==PLAY)
            if((dx*dx)+(dy*dy)+(dz*dz)<DISTPICKUP)
                if((cour.nodes[segment].itemfade==1.0) && (cour.nodes[segment].item!=course.NONE)){

                    switch(cour.nodes[segment].item){

                        case course.ENERGY : gain_energy(50);
                            //cour.play_3d_sample(cam,x,y,z,takee); break;
                        case course.BOOST  : nboosts++;
                            //cour.play_3d_sample(cam,x,y,z,takee); break;

                        case course.SHIELD : shield= (short) SHIELDDURATION;
                            //cour.play_3d_sample(cam,x,y,z,takes); break;
                        case course.POWER  : power++;
                            //new_message("           power tank");
                            //cour.play_3d_sample(cam,x,y,z,takepow);
                            break;
                        case course.MINE   : if((shield==0) && (hypermode==0)){
                            lose_energy(75);
                            counter=0; state=STUN; stunforce= (int) (velocity*40);
                            //cour.play_3d_sample(cam,x,y,z,litexpl);
                        };
                            break;

                    };
                    cour.nodes[segment].itemfade-=0.01;
                    if(power==5) {
                        hypermode= (short) HYPERDURATION;
                        //new_message("      hyper mode reached!");
                        //cour.play_3d_sample(cam,x,y,z,fullpower);
                        power=0;
                    };
                };


        if(boost>0) boost--;
        if(shield>0) shield--;
        if(hypermode>0){
            hypermode--;
            gain_energy(2);
        };

        //calc_light_color();

        //Camera change
        if(camchwait>0) camchwait--;

        if((ctr.boton(tc,3)) && (camchwait==0))
        {camtype= (short) ((camtype+1)%3); camchwait=30;};

    }

    void ia_update(PerspectiveCamera cam, controlm ctr)
    {
        float da;

        // Optimal orientation to go towards next node
        an=optimal_direction();

        // Angle diferential
        da=ry-an;

        // Mantain the difference between (-PI,PI)
        if(da>=Math.PI) da-=2*Math.PI;
        else if(da<=-Math.PI) da+=2*Math.PI;

        //ctr->reset();
        // Turn

        if(da>0.03) ctr.activa(controlm.NOTC,controlm.CDER);
        if(da<-0.03) ctr.activa(controlm.NOTC,controlm.CIZQ);
        // Rear gear
        if((da>0.15) || (da<-0.15)) ctr.activa(controlm.NOTC,controlm.CBU3);
            // Brake
        else if(((da>0.03) || (da<-0.03)) && (velocity>0.0)) ctr.activa(controlm.NOTC,controlm.CABA);

        // Accelerate
        if((da<0.15) && (da>-0.15)){
            ctr.activa(controlm.NOTC,controlm.CBU1);
            if((course.rand()%600==0) && (nboosts>0)) ctr.activa(controlm.NOTC,controlm.CBU2);
        }
        if(velocity<1.0) ctr.activa(controlm.NOTC,controlm.CBU1);

        //ry=an;

        update(cam,ctr,controlm.NOTC);
    }

    float optimal_direction()
    {
        int s=(segment+1)%cour.info.nsegments, s2=(segment+3)%cour.info.nsegments;
        float x1,x2,z1,z2,xx,zz;

        x1=(cour.nodes[s].x[1]+cour.nodes[s].x[2])/2;
        z1=(cour.nodes[s].z[1]+cour.nodes[s].z[2])/2;

        x2=(cour.nodes[s2].x[1]+cour.nodes[s2].x[2])/2;
        z2=(cour.nodes[s2].z[1]+cour.nodes[s2].z[2])/2;

        xx=(x1+x2)/2; zz=(z1+z2)/2;

        // Optimal orientation to go towards next node
        return (float) (Math.PI/2-Math.atan2(-xx+x,zz-z));

    }

    void set_camera()
    {
        float alfa, x2, z2, y2, cx, cy, cz,
            dist= (float) ((360-cour.counter)/360.0);
        int s;

        // View reference point and camera position
        if(Math.abs(velocity)<=0.01)
            alfa=ry;
        else /*if(!backwards)*/ alfa= (float) Math.atan2(vel[2],vel[0]);
        //else  alfa=atan2(vel[2],vel[0])+PI;

        // Camera position and vrp
        vrp=new vertex((float) (renderx+(10*Math.cos(alfa))),y, (float) (renderz+(10*Math.sin(alfa))));
        vrp=new vertex(renderx,y,renderz);

        switch(camtype){
            case 1: cx= (float) (30*Math.cos(alfa)); cz= (float) (30*Math.sin(alfa)); cy=8; break;
            case 2: cx= (float) (40*Math.cos(alfa)); cz= (float) (40*Math.sin(alfa)); cy=12; break;
            default:
            case 0: cx= (float) (20*Math.cos(alfa)); cz= (float) (20*Math.sin(alfa)); cy=4; break;
        };

        // Race over : TV cameras
        if(raceover){

            vrp=new vertex(renderx,y,renderz);
            switch((cour.counter/500)%4){

                case 0 :
                    s=(((segment/10)*10)+6)%cour.info.nsegments;
                    cam_pos=new vertex(cour.nodes[s].x[2],cour.nodes[s].y[2]+10,cour.nodes[s].z[2]);
                    break;

                case 1 :
                    cam_pos=new vertex((float) (renderx-(20*Math.cos(alfa+Math.PI/2))),y+4, (float) (renderz-(20*Math.sin(alfa+Math.PI/2))));
                    break;

                case 2 :
                    cam_pos=new vertex((float) (renderx-(20*Math.cos(alfa+Math.PI/2))+15),y+15, (float) (renderz-(20*Math.sin(alfa+Math.PI/2))+15));
                    break;

                case 3 :
                    cam_pos=new vertex((float) (renderx-(15*Math.cos(alfa+Math.PI))),y+10, (float) (renderz-(15*Math.sin(alfa+Math.PI))));
                    break;

            };
        }else if(cour.counter>360)
            cam_pos=new vertex(renderx+cx,y+cy,renderz-cz);
        else{

            x2=renderx-cx+((cour.nodes[2].x[1]-renderx)*dist);
            y2=y+cy+(40*dist);
            z2=renderz-cz+((cour.nodes[2].z[1]-renderz)*dist);
            cam_pos=new vertex(x2,y2,z2);
        };

    }

    boolean collide(ship s)
    {
        float dx=(renderx-s.renderx), dy=(y-s.y), dz=(renderz-s.renderz), dist,
        v1[] = new float[3], v2[] = new float[3], m1, m2, ix, iy, iz, c1, c2;

        dist=(dx*dx)+(dy*dy)+(dz*dz)+0.01f;

        if(dist<DISTCOLLIDE){

            // One ship inside another
            //if(dist<DISTCOLLIDE*0.75){

            ix=(x-s.x)/2.0f; iy=(y-s.y)/2.0f; iz=(z-s.z)/2.0f;

            x+=ix; y+=iy; z+=iz;
            s.x-=ix; s.y-=iy; s.z-=iz;
            //};

            v1[0]=vel[0]; v1[1]=vel[1]; v1[2]=vel[2];
            v2[0]=s.vel[0]; v2[1]=s.vel[1]; v2[2]=s.vel[2];

            // My reaction
            if((s.hypermode>0) || (s.shield>0)){
                c1=1.0f; c2=1.0f;
            }else{
                c1=0.35f; c2=0.65f;
            };
            if((hypermode==0) && (shield>0)) {vel[0]=c1*v1[0]+c2*v2[0]; vel[1]=c1*v1[1]+c2*v2[1]; vel[2]=c1*v1[2]+c2*v2[2];};
            if((shield==0) && (hypermode==0) && ((s.shield>0) || (s.hypermode>0))){
                lose_energy(25);
                counter=0; state=STUN; stunforce= (int) ((velocity+s.velocity)*30);
                //cour->play_3d_sample(cam,x,y,z,litexpl);
            }

            // Other's reaction
            if((hypermode>0) || (shield>0)){
                c1=1.0f; c2=1.0f;
            }else{
                c1=0.35f; c2=0.65f;
            };
            if((s.hypermode==0) && (s.shield>0)) {s.vel[0]=c1*v2[0]+c2*v1[0]; s.vel[1]=c1*v2[1]+c2*v1[1]; s.vel[2]=c1*v2[2]+c2*v1[2];};
            if((s.shield==0) && (s.hypermode==0) && ((shield>0) || (hypermode>0))){
                s.lose_energy(25);
                s.counter=0; s.state=STUN; s.stunforce= (int) ((velocity+s.velocity)*30);
                //cour->play_3d_sample(cam,s->x,s->y,s->z,s->litexpl);
            }

            lose_energy((int) (s.data.weight*velocity/100.0));
            s.lose_energy((int) (data.weight*s.velocity/100.0));

            return true;
        };
        return false;
    }

    float module(float x, float y, float z)
    {
        return (float) Math.sqrt(x*x+y*y+z*z);
    }

    float velocity_kmh()
    {
        return velocity*216;
    }

    void gain_energy(int a)
    {
        energy+=a;
        if(energy>MAXENERGY) energy=MAXENERGY;
    }
    void lose_energy(int a)
    {
        if(shield==0){
            energy-=a;
            if(energy<0) energy=0;
        };
    }

    void render(ShaderProgram shader, PerspectiveCamera cam, boolean l)
    {
        float dx=(x-cam.position.x), dy=(y-cam.position.y), dz=(z-cam.position.z);

        float dist=(dx*dx)+(dy*dy)+(dz*dz);

        //glEnable(GL_DEPTH_TEST);

        // Ship. at three diferent quality levels
        if(dist<DRAWNEAR){
            //if(l) glEnable(GL_LIGHTING);
            mesh.render(shader, cam,renderx,y,renderz,rx, (float) (ry+Math.PI),0);
        }else if(dist<DRAWMEDIUM){
            //if(l) glEnable(GL_LIGHTING);
            lowres.render(shader, cam,renderx,y,renderz,rx, (float) (ry+Math.PI),0);
        }else if(dist<DRAWFAR){
            //glDisable(GL_LIGHTING);
            lowres.render(shader, cam, renderx,y,renderz,rx, (float) (ry+Math.PI),0);
        };

    }

    String time_str(long t)
    {
        //sprintf(s,"%d'%02d.%02d",int(t/3600.0),int((t%3600)/60.0),int((t%60)*1.6667));
        return "00'00.00";
    }
}
