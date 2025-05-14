package com.activeminds.mach1r;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Arrays;

public class ShipSelectVersusScreen implements Screen {

    Main game;
    boolean sel_confirm[];
    ShapeRenderer shapeRenderer;

    public ShipSelectVersusScreen(Main game)
    {
        this.game = game;
        game.counter = 0;
        shapeRenderer = new ShapeRenderer();
        sel_confirm = new boolean[4];
        Arrays.fill(sel_confirm, false);

        game.play_voice("selectship.wav");
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {


        String car[]={Main.loc.get("statsShort0"),
            Main.loc.get("statsShort1"),
            Main.loc.get("statsShort2"),
            Main.loc.get("statsShort3"),
            Main.loc.get("statsShort4")
        };

        game.counter += delta*70f;
        //if (cur_wait > 0) cur_wait--;

        ScreenUtils.clear(0f, 0f, 0f, 1f);
        game.camera2d.update();
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();
        game.show_scrolling_wallp();

        for(int i=0; i<Main.NSHIPS; i++){
            int x=((i%5)*112)+40;
            int y=340-(100*(int)(i/5));
            if(game.gdata.available[i] != 0) game.mini[i].render2d(game.batch, 0,0,128,128,x,y,x+112,y+93,1.0f);
		    else {
                game.mistery.render2d(game.batch, 0,0,128,128,x,y,x+112,y+93,1.0f);
            }
        };

        game.fuente.show_text(game.batch, 150,450, Main.loc.get("selectShip"),1);
        for(int i=0; i<game.nhumans; i++){
            int x=20+150*i;

            if(sel_confirm[i]){

                game.fuente.show_text(game.batch, x,100,ship.models.ships.get(game.gdata.sel_ship[i]).file,0);
                game.fuente.show_text(game.batch,x,60, Main.loc.get("getReady1"),0);
                game.fuente.show_text(game.batch,x,40, Main.loc.get("getReady2"),0);

            }else if(game.gdata.available[game.gdata.sel_ship[i]] != 0){

                game.fuente.show_text(game.batch, x,100,ship.models.ships.get(game.gdata.sel_ship[i]).file,0);
                String s = ship.models.ships.get(game.gdata.sel_ship[i]).weight + " KG";
                game.fuente.show_text(game.batch, x,80,s,0);
                game.fuente.show_text(game.batch, x,60, Main.loc.get("boostShort"),0); game.fuente.show_text(game.batch, x+105,60,car[ship.models.ships.get(game.gdata.sel_ship[i]).enginef],0);
                game.fuente.show_text(game.batch, x,40, Main.loc.get("handlingShort"),0); game.fuente.show_text(game.batch, x+105,40,car[ship.models.ships.get(game.gdata.sel_ship[i]).handling],0);
                int sp=ship.models.ships.get(game.gdata.sel_ship[i]).enginef-(int)(ship.models.ships.get(game.gdata.sel_ship[i]).weight/500)+1;
                game.fuente.show_text(game.batch, x,20, Main.loc.get("speedShort"),0); game.fuente.show_text(game.batch, x+105,20,car[sp],0);

            }else{
                game.fuente.show_text(game.batch, x,100,"????",0);
            }

            // Show line square and icon

            x=((game.gdata.sel_ship[i]%5)*112)+40;
            int y=340-(100*(int)(game.gdata.sel_ship[i]/5));

            switch(i){
                case 0 : y+=65; break;
                case 1 : x+=80; y+=65; break;
                case 2 : break;
                case 3 : x+=80; break;
            }
            game.plcursor[game.gdata.icons[i]].render2d(game.batch,0,0,32,32,x,y,x+30,y+30,1.0f);
        };
        game.batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for(int i = 0; i < game.nhumans; i++)
        {
            int x=((game.gdata.sel_ship[i]%5)*112)+40;
            int y=340-(100*(int)(game.gdata.sel_ship[i]/5));

            if(!sel_confirm[i]){

                shapeRenderer.line(x,y,x+110,y);
                shapeRenderer.line(x+110,y, x+110, y+95);
                shapeRenderer.line(x+110, y+95, x, y+95);
                shapeRenderer.line(x, y+95, x ,y);
            };
        }
        shapeRenderer.end();

        /*
        char car[5][4]={"L","M","H","VH","A"}, s[30];
	int x, y, i,sp;

	glMatrixMode (GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(0, 640, 0, 480);
	glDisable(GL_LIGHTING);
	glDisable(GL_DEPTH_TEST);
	show_scrolling_wallp();

	for(i=0; i<NSHIPS; i++){
		x=((i%5)*112)+40;
		y=340-(100*int(i/5));
		if(gdata.available[i]) mini[i]->render2d(0,0,128,128,x,y,x+112,y+93,1.0);
		else {
			mistery->render2d(0,0,128,128,x,y,x+112,y+93,1.0);
		}
	};

	fuente->show_text(150,450,"PLEASE SELECT YOUR SHIP",1);
	for(i=0; i<nhumans; i++){
		x=20+150*i;

		if(sel_confirm[i]){

			fuente->show_text(x,100,models[gdata.sel_ship[i]].file,0);
			fuente->show_text(x,60,"get",0);
			fuente->show_text(x,40,"  ready!",0);

		}else if(gdata.available[gdata.sel_ship[i]]){

			fuente->show_text(x,100,models[gdata.sel_ship[i]].file,0);
			sprintf(s,"%d KG",models[gdata.sel_ship[i]].weight);
			fuente->show_text(x,80,s,0);
			fuente->show_text(x,60,"BOOST :",0); fuente->show_text(x+105,60,car[models[gdata.sel_ship[i]].enginef],0);
			fuente->show_text(x,40,"HANDL.:",0); fuente->show_text(x+105,40,car[models[gdata.sel_ship[i]].handling],0);
			sp=models[gdata.sel_ship[i]].enginef-int(models[gdata.sel_ship[i]].weight/500)+1;
			fuente->show_text(x,20,"SPEED :",0); fuente->show_text(x+105,20,car[sp],0);

		}else{
			fuente->show_text(x,100,"????",0);
		}

		// Show line square and icon

		x=((gdata.sel_ship[i]%5)*112)+40;
		y=340-(100*int(gdata.sel_ship[i]/5));

		if(!sel_confirm[i]){

			glLineWidth(1.0);
			glEnable(GL_LINE_SMOOTH);
			glBegin(GL_LINE_STRIP);
			glColor3f(1.0,1.0,1.0);
			glVertex2i(x,y);
			glVertex2i(x+110,y);
			glVertex2i(x+110,y+95);
			glVertex2i(x,y+95);
			glVertex2i(x,y);
			glEnd();
		};

		switch(i){
			case 0 : y+=65; break;
			case 1 : x+=80; y+=65; break;
			case 2 : break;
			case 3 : x+=80; break;
		}
		plcursor[gdata.icons[i]]->render2d(0,0,32,32,x,y,x+30,y+30,1.0);
	};

         */

        // LOGIC ==================================================

        //ctr->actualiza();
        int j=0;
        if(game.counter==1) for(int i=0; i<4; i++) sel_confirm[i]=false;
        //if(game.counter==5) play_voice("selectship.smp");
        for(int i=0; i<game.nhumans; i++){
            if(((int)game.counter%10==0) && (game.counter>30) && (!sel_confirm[i])){

                if(game.ctr.der(game.gdata.controls[i])) game.gdata.sel_ship[i]+=1;
                if(game.ctr.izq(game.gdata.controls[i])) game.gdata.sel_ship[i]-=1;
                if(game.ctr.arr(game.gdata.controls[i])) game.gdata.sel_ship[i]-=5;
                if(game.ctr.aba(game.gdata.controls[i])) game.gdata.sel_ship[i]+=5;
                if(game.gdata.sel_ship[i]<0) game.gdata.sel_ship[i]+=Main.NSHIPS;
                game.gdata.sel_ship[i]= (short) (game.gdata.sel_ship[i]%Main.NSHIPS);

                if(game.ctr.algun_boton(game.gdata.controls[i]))
                    if(game.gdata.available[game.gdata.sel_ship[i]]!=0){
                        sel_confirm[i]=true;
                        game.wconfirm.playonce();
                    }
            };
            if(sel_confirm[i]) j++;
        };
        if(j>=game.nhumans) {
            game.setScreen(new SingleRaceSelectScreen(game));
            dispose();
            //set_state(SINGLE_RACE_SEL);
        }
        if(game.ctr.atr(controlm.TEC1)) {
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
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
