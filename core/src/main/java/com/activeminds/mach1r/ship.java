package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;

public class ship {

    public static final int MAX_LIGHTS = 3;

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

    solid mesh, lowres;
    ship_model data;
    sprite logo;

    public ship(int model, int id, Object course) {

        Json json = new Json();
        FileHandle file = Gdx.files.internal("ships.json");
        String fileText = file.readString();
        shipModelsJson l = json.fromJson(shipModelsJson.class, fileText);

        data = l.ships.get(model);
        mesh = new solid();
        mesh.load_mesh("model/"+data.file+".msh");
        mesh.centrate(true,true,true);

        lowres = new solid();
        lowres.load_mesh("model/"+data.file+"lr.msh");
        lowres.centrate(true,true,true);

        logo=new sprite(256,128,"sprite/"+data.file+"lg.png");


    }
}
