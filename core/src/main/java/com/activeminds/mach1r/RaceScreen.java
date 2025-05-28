package com.activeminds.mach1r;

import com.activeminds.mach1r.opensimplex.OpenSimplex2S;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.time.LocalTime;
import java.util.ArrayList;

public class RaceScreen implements Screen {

    public static final int NUM_SHADOW_MAPS = 3;
    //public static final int SHADOW_MAP_SIZE = 4096;
    public static final float GROUNDWIDTH = 4900.0f;
    public static final float SKYWIDTH = 6000.0f;
    public static final float GROUNDTILE = 12.0f;
    public static final float MAPSCALE = 40.0f;

    public static final int SINGLE = 0;
    public static final int VERSUS = 1;
    public static final int FINISHED = 2;
    public static final int DISQUAL = 3;
    public static final int DEMO = 4;

    public static final float DAMAGED_COL[]={1.0f,0.5f,0.5f};
    public static final float HYPER_COL[]={0.96f,0.85f,0.04f};

    Main game;
    ShaderProgram shipShader, sceneShader, groundShader, exhaustShader, shieldShader, skyShader, billboardShader, shadowShader, depthShader;
    solid groundMesh, skyMesh;
    Mesh billboard, shadow, shield, skyDome;
    texture ground, groundNormalMap, sky;
    Texture cloudTexture;

    PerspectiveCamera cameraSingle;
    PerspectiveCamera cameras2p[] = new PerspectiveCamera[2];
    PerspectiveCamera cameras4p[] = new PerspectiveCamera[4];
    SpriteBatch splitScreenBatch;
    OrthographicCamera splitScreenCamera2d;


    int state;
    long counter;
    float skyc[] = new float[4], fogc[] = new float[4], cloudc[] = new float[4], viewPortAspectRatio, hour;
    float accumulatedDelta;
    int updatesPending;
    vertex sun;
    boolean paused;

    FrameBuffer shadowFBO[];
    Texture shadowMap[];
    OrthographicCamera lightCamera[];
    int shadowMapSize[];

    public static int HUD_START_X;

    public RaceScreen(Main game)
    {
        this.game = game;
        game.counter = 0;

        accumulatedDelta = 0f;
        updatesPending = 0;
        counter = 0;

        if(Gdx.app.getType() == Application.ApplicationType.Desktop)
            HUD_START_X = 0;
        else
            HUD_START_X = (Main.SCREENX - 640) / 2;

        // Crear shaders
        String vertexShader = Gdx.files.internal("shader/ship_vertex.glsl").readString();
        String fragmentShader = Gdx.files.internal("shader/ship_fragment.glsl").readString();

        fragmentShader = "#define FOG_ENABLED 1\n" +
            "#define LIGHTING_ENABLED 1\n" +
            "#define SPECULAR_ENABLED 1\n" +
            (game.gdata.shadowmap ? "#define SHADOWMAP_ENABLED 1\n#define SHADOWPCF_ENABLED 1\n#define SHADOWMAP24B 1\n" : "") +
            (game.gdata.reflections ? "#define REFLECTION_ENABLED 1\n" : "") +
            fragmentShader;

        ShaderProgram.pedantic = false;
        shipShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shipShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar: " + shipShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/ship_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/ship_fragment.glsl").readString();

        fragmentShader = "#define FOG_ENABLED 1\n" +
            "#define LIGHTING_ENABLED 1\n" +
            (game.gdata.shadowmap ? "#define SHADOWMAP_ENABLED 1\n#define SHADOWPCF_ENABLED 1\n#define SHADOWMAP24B 1\n" : "") +
            fragmentShader;

        ShaderProgram.pedantic = false;
        sceneShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!sceneShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar scene shader: " + sceneShader.getLog());
        }

        fragmentShader = "#define FOG_ENABLED 1\n" +
            "#define NORMAL_MAP_ENABLED 1\n" +
            "#define LIGHTING_ENABLED 1\n" +
            (game.gdata.shadowmap ? "#define SHADOWMAP_ENABLED 1\n#define SHADOWPCF_ENABLED 1\n#define SHADOWMAP24B 1\n" : "") +
            fragmentShader;

        ShaderProgram.pedantic = false;
        groundShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!groundShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar ground shader: " + groundShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/exhaust_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/exhaust_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        exhaustShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!exhaustShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar exhaust shader: " + exhaustShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/shield_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/shield_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        shieldShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shieldShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar shield shader: " + shieldShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/sky_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/sky_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        skyShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!skyShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar sky shader: " + skyShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/billboard_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/billboard_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        billboardShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!billboardShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar billboard shader: " + billboardShader.getLog());
        }

       vertexShader = Gdx.files.internal("shader/depth_vertex.glsl").readString();
       fragmentShader = Gdx.files.internal("shader/depth_fragment.glsl").readString();

        fragmentShader = "#define SHADOWMAP24B 1\n" +
            fragmentShader;

        ShaderProgram.pedantic = false;
        depthShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!depthShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar (depth): " + depthShader.getLog());
        }

        vertexShader = Gdx.files.internal("shader/shadow_vertex.glsl").readString();
        fragmentShader = Gdx.files.internal("shader/shadow_fragment.glsl").readString();

        ShaderProgram.pedantic = false;
        shadowShader = new ShaderProgram(vertexShader, fragmentShader);

        if (!shadowShader.isCompiled()) {
            Gdx.app.error("Shader", "Error al compilar shadow shader: " + shadowShader.getLog());
        }

        // Ground mesh
        vertex v[] = new vertex[4];
        int TILE = 7;
        v[0]=new vertex(0f,course.GROUNDY,0f);
        v[1]=new vertex(0f,course.GROUNDY,(GROUNDWIDTH/TILE));
        v[2]=new vertex((GROUNDWIDTH/TILE),course.GROUNDY,(GROUNDWIDTH/TILE));
        v[3]=new vertex((GROUNDWIDTH/TILE),course.GROUNDY,0f);

        ground= new texture("scene/"+course.scenes.course_scenes.get(game.cour.info.scene).name+"grhd.bmp",texture.TEX_BMP,true,false);
        groundNormalMap = new texture("scene/"+course.scenes.course_scenes.get(game.cour.info.scene).name+"grnm.png",texture.TEX_BMP,true,false);
        groundMesh = new solid();
        groundMesh.triangles = new ArrayList<>();
        groundMesh.textures = new Texture[1];
        groundMesh.textures[0] = ground.gdxTexture;
        groundMesh.normalMap = groundNormalMap.gdxTexture;
        groundMesh.triangles.add(new triangle(v[0], v[1], v[2], 0, 0, 0f, 0f, GROUNDTILE, GROUNDTILE, GROUNDTILE));
        groundMesh.triangles.add(new triangle(v[2], v[3], v[0], 0, GROUNDTILE, GROUNDTILE, GROUNDTILE, 0f, 0f, 0f));
        groundMesh.buildGdxMesh();

        // Sky mesh
        TILE = 5;
        v[0]=new vertex(0f,course.SKYY,0f);
        v[1]=new vertex(0f,course.SKYY,(SKYWIDTH/TILE));
        v[2]=new vertex((SKYWIDTH/TILE),course.SKYY,(SKYWIDTH/TILE));
        v[3]=new vertex((SKYWIDTH/TILE),course.SKYY,0f);

        float r = course.scenes.course_scenes.get(game.cour.info.scene).skycolor[0];
        float g = course.scenes.course_scenes.get(game.cour.info.scene).skycolor[1];
        float b = course.scenes.course_scenes.get(game.cour.info.scene).skycolor[2];
        skyMesh = new solid();
        skyMesh.textures = new Texture[0];
        skyMesh.triangles = new ArrayList<>();
        skyMesh.triangles.add(new triangle(v[0], v[2], v[1], r, g, b, r, g, b, r, g, b));
        skyMesh.triangles.add(new triangle(v[2], v[0], v[3], r, g, b, r, g, b, r, g, b));
        skyMesh.buildGdxMesh();

        // Billboard mesh
        billboard = new Mesh(true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );

        // Quad en XY (como un sprite 2D)
        float[] vertices = new float[] {
            -0.5f, -0.5f, 0f, 0f, 1f,
            0.5f, -0.5f, 0f, 1f, 1f,
            0.5f,  0.5f, 0f, 1f, 0f,
            -0.5f,  0.5f, 0f, 0f, 0f
        };

        short[] indices = new short[] { 0, 1, 2, 2, 3, 0 };

        billboard.setVertices(vertices, 0 , 20);
        billboard.setIndices(indices);

        // Shadow mesh
        shadow = new Mesh(true, 4, 6,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord0")
        );

        // Quad en XY (como un sprite 2D)
        float[] vertices2 = new float[] {
            -1f, 0f, -1f, 0f, 1f,
            1f, 0f, -1f, 1f, 1f,
            0.5f,  0f, 1f, 1f, 0f,
            -1f,  0f, 1f, 0f, 0f
        };

        short[] indices2 = new short[] { 0, 2, 1, 2, 0, 3 };

        shadow.setVertices(vertices2, 0 , 20);
        shadow.setIndices(indices2);

        cameraSingle = new PerspectiveCamera(67,  Gdx.graphics.getWidth(),  Gdx.graphics.getHeight());
        for(int i = 0; i < 2; i++)
            cameras2p[i] = new PerspectiveCamera(67,  (float) Gdx.graphics.getWidth() / 2,  (float) Gdx.graphics.getHeight() );
        for(int i = 0; i < 4; i++)
            cameras4p[i] = new PerspectiveCamera(67,  (float) Gdx.graphics.getWidth() / 2,  (float) Gdx.graphics.getHeight() / 2);

        splitScreenBatch = new SpriteBatch();
        splitScreenCamera2d = new OrthographicCamera();
        splitScreenCamera2d.setToOrtho(false,  Main.SCREENX, Main.SCREENY);

        if(game.nhumans <= 1)
            viewPortAspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
        else if (game.nhumans == 2)
            viewPortAspectRatio = (float) (Gdx.graphics.getWidth() / 2) / Gdx.graphics.getHeight();
        else if (game.nhumans > 2)
            viewPortAspectRatio = (float) (Gdx.graphics.getWidth() / 2f) / (Gdx.graphics.getHeight() / 2f);


        if(game.nhumans <= 1) {

            for(int i = 0; i < game.nplayers; i++)
            {
                game.pl[i].soundCam = cameraSingle;
            }
        }
        else if (game.nhumans == 2)
        {
            game.pl[0].soundCam = cameras2p[0];
            game.pl[1].soundCam = cameras2p[1];
            for(int i = game.nhumans; i < game.nplayers; i++)
            {
                game.pl[i].soundCam = cameras2p[0];
            }
        }
        else if (game.nhumans > 2)
        {
            for(int i = 0; i < game.nhumans; i++)
                game.pl[i].soundCam = cameras4p[i];
            for(int i = game.nhumans; i < game.nplayers; i++)
            {
                game.pl[i].soundCam = cameras4p[0];
            }
        }


        // Shape Renderer

        sun = new vertex(0f,5000f,5000f);

        paused = false;

        // Shadow map
        if(game.gdata.shadowmap) {
            shadowFBO = new FrameBuffer[NUM_SHADOW_MAPS];
            shadowMap = new Texture[NUM_SHADOW_MAPS];
            lightCamera = new OrthographicCamera[NUM_SHADOW_MAPS];
            shadowMapSize = new int[NUM_SHADOW_MAPS];
            shadowMapSize[0] = 2048;
            shadowMapSize[1] = 1024;
            shadowMapSize[2] = 512;

            for(int i = 0; i < NUM_SHADOW_MAPS; i++) {
                shadowFBO[i] = new FrameBuffer(Pixmap.Format.RGBA8888, shadowMapSize[i], shadowMapSize[i], true);
                shadowMap[i] = shadowFBO[i].getColorBufferTexture();
                lightCamera[i] = new OrthographicCamera();
            }
        }

        // Exhaust mesh
        ship.generateExhaustMesh();

        // SHield
        shield = createSphereMesh(1,20);

        // Skydome
        createSkyDome(1000, 20);
        sky = new texture("scene/sky.png", texture.TEX_PCX, true, false);

        createCloudTexture();

        generate_cube_map();

        game.play_music("sound/song"+((game.cour.info.scene%3)+1)+".mp3");
    }

    int cubemapSize = 256;
    FrameBuffer[] cubemapFbos = new FrameBuffer[6];
    Texture[] cubemapFaces = new Texture[6];


    void generate_cube_map()
    {
        PerspectiveCamera[] cubeCameras = new PerspectiveCamera[6];
        Vector3 origin = new Vector3(0, 0, 0); // donde estás generando el reflejo

        Vector3[] dirs = {
            new Vector3(1, 0, 0), new Vector3(-1, 0, 0),
            new Vector3(0, 1, 0), new Vector3(0, -1, 0),
            new Vector3(0, 0, 1), new Vector3(0, 0, -1)
        };

        Vector3[] ups = {
            new Vector3(0, 1, 0), new Vector3(0, 1, 0),
            new Vector3(0, 0, 1), new Vector3(0, 0, -1),
            new Vector3(0, 1, 0), new Vector3(0, 1, 0)
        };

        for (int i = 0; i < 6; i++) {
            PerspectiveCamera cam = new PerspectiveCamera(90, cubemapSize, cubemapSize);
            cam.position.set(origin);
            cam.direction.set(dirs[i]);
            cam.up.set(ups[i]);
            cam.near = 1f;
            cam.far = 1200f;
            cam.update();
            cubeCameras[i] = cam;
        }

        for (int i = 0; i < 6; i++) {
            cubemapFbos[i] = new FrameBuffer(Pixmap.Format.RGBA8888, cubemapSize, cubemapSize, true);
            cubemapFaces[i] = cubemapFbos[i].getColorBufferTexture();
            cubemapFaces[i].setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            cubemapFaces[i].setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        }

        hour_environment();

        for (int i = 0; i < 6; i++) {
            cubemapFbos[i].begin();

            Gdx.gl.glViewport(0, 0, cubemapSize, cubemapSize);
            Gdx.gl.glClearColor(fogc[0], fogc[1], fogc[2], 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            //renderSceneWithCamera(cubeCameras[i]); // usa tu render() pasando esa cámara
            Gdx.gl.glDepthMask(false);

            //Sky
            if(game.gdata.skygrfog){

                //glEnable(GL_FOG);
                show_sky(cubeCameras[i]);
            };

            // Sun
            /*if(hour < 7.f || hour >= 19.f)
                show_3d_sprite(cubeCameras[i],game.moon,0,0,1,1,sun.x,sun.y,sun.z,1.0f,1.0f,1.0f,500, 500,1f);
            else
                show_3d_sprite(cubeCameras[i],game.flame,0,0,1,1,sun.x,sun.y,sun.z,1.0f,1.0f,1.0f,1000,1000,1f);*/

            Gdx.gl.glDisable(GL20.GL_BLEND);

            sceneShader.begin();
            sceneShader.setUniformf("u_ambientColor", 0.2f, 0.2f, 0.2f);

            sceneShader.setUniformi("u_numLights", 1);
            sceneShader.setUniformf("u_lightPos[0]", sun.x, sun.y, sun.z);
            sceneShader.setUniformf("u_lightColor[0]", new Vector3(0.8f, 0.8f, 0.8f));
            sceneShader.setUniformf("u_lightIntensity[0]", 1.0f);

            sceneShader.setUniformf("u_fogColor", fogc[0], fogc[1], fogc[2]); // gris claro
            sceneShader.setUniformf("u_fogStart", 10.0f);
            sceneShader.setUniformf("u_fogEnd", 1000.0f);

            if(game.gdata.shadowmap) {
                set_shadowmap_values(sceneShader);
            }

            Gdx.gl.glDepthMask(true);
            show_ground(cubeCameras[i]);

            sceneShader.end();

            cubemapFbos[i].end();

            cubemapFaces[i] = cubemapFbos[i].getColorBufferTexture();
        }

        // Ground
        OrthographicCamera camera2d = new OrthographicCamera();
        camera2d.setToOrtho(false,  cubemapSize, cubemapSize);
        camera2d.update();

        Vector3 lightDir = new Vector3(sun.x, sun.y, sun.z).nor();
        Vector3 groundNormal = new Vector3(0,1,0);
        float dot = lightDir.dot(groundNormal);
        cubemapFbos[3].begin();
        game.batch.setProjectionMatrix(camera2d.combined);
        game.batch.begin();
        game.batch.setColor(0.2f + 0.8f*dot, 0.2f + 0.8f*dot, 0.2f + 0.8f*dot, 1);
        game.batch.draw(game.cour.road.gdxTexture,0, 0, cubemapSize, cubemapSize, 16*8, 80*8, 32*8, 48*8, false, false);
        game.batch.end();
        cubemapFbos[3].end();
    }

    void refresh_shadow_map(int id, ship sh, int drawDist)
    {
        // O PerspectiveCamera
        lightCamera[id].setToOrtho(false, drawDist, drawDist);

        Vector3 lightDirInv = new Vector3(sun.x, sun.y, sun.z);
        lightDirInv.limit(drawDist/2f);
        Vector3 cameraToVrp = new Vector3(sh.vrp.x - sh.cam_pos.x, sh.vrp.y - sh.cam_pos.y, sh.vrp.z - sh.cam_pos.z);
        cameraToVrp.nor();
        cameraToVrp.scl((drawDist/2f) - 20f);
        Vector3 sceneCenter = new Vector3(sh.cam_pos.x, sh.cam_pos.y, sh.cam_pos.z);
        sceneCenter.add(cameraToVrp);
        Vector3 camPos = new Vector3(sceneCenter.x, sceneCenter.y, sceneCenter.z);
        camPos.add(lightDirInv);


        lightCamera[id].position.set(camPos);
        lightCamera[id].lookAt(sceneCenter);  // Mira hacia el centro de la escen
        lightCamera[id].up.set(0f,0f,1f);
        lightCamera[id].near = 0.1f;
        lightCamera[id].far = drawDist;
        lightCamera[id].update();

        shadowFBO[id].begin();
        // Usas un shader que solo grabe profundidad
        Gdx.gl.glClearColor(1f,1f,1f, 1f);
        Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);

        depthShader.begin();
        depthShader.setUniformMatrix("u_lightVP", lightCamera[id].combined);

        //renderSceneWith(depthShader);  // Tu función que dibuja las naves, etc.
        int range = 30+(15*game.gdata.drawdist);
        for(int seg = 0; seg < range; seg++)
        {
            Matrix4 model = new Matrix4().idt();

            int i=(sh.nextsegment-(range/2)+seg);
            if(i<0) i+=game.cour.info.nsegments;
            if(i>=game.cour.info.nsegments) i-=game.cour.info.nsegments;
            course.node n = game.cour.nodes[i];

            depthShader.setUniformMatrix("u_model", model);
            n.mesh.mesh.render(depthShader, GL20.GL_TRIANGLES);

            //Decorates

            if(n.detype<=2){
                //game.cour.decorate[n.detype].set_scale(n.dscalexz,n.dscaley,n.dscalexz);
                //decorate[nodes[i].detype].render(shader, cam, nodes[i].dex,GROUNDY,nodes[i].dez,0,0,0);
                Matrix4 m = new Matrix4().idt().translate(n.dex,course.GROUNDY,n.dez);
                depthShader.setUniformMatrix("u_model", m);
                game.cour.decorate[n.detype].mesh.render(depthShader, GL20.GL_TRIANGLES);
            };

            // Cubes

            if(n.item!=course.NONE){

                solid s;

                switch(n.item){
                    default:
                    case course.BOOST : s = game.cour.bcube; break;
                    case course.ENERGY: s = game.cour.ecube; break;
                    case course.SHIELD: s = game.cour.scube; break;
                    case course.POWER : s = game.cour.power; break;
                    case course.MINE  : s = game.cour.mine; break;
                };

                //s.alpha_render(shader, cam, cube_x(i),cube_y(i)+3.5f,cube_z(i),counter/50f, counter/70f,0,nodes[i].itemfade);

                Quaternion qx = new Quaternion();
                Quaternion qy = new Quaternion();
                Quaternion qz = new Quaternion();

                qx.setEulerAnglesRad(0, 0, counter/50f);
                qy.setEulerAnglesRad(counter/70f, 0, 0);
                qz.setEulerAnglesRad(0, 0, 0);

                Quaternion combined = qx.mul(qy).mul(qz);
                Matrix4 rot = new Matrix4().set(combined);

                Matrix4 m = new Matrix4().idt()
                    .translate(game.cour.cube_x(i), game.cour.cube_y(i), game.cour.cube_z(i))
                    .mul(rot);

                depthShader.setUniformMatrix("u_model", m);
                s.mesh.render(depthShader, GL20.GL_TRIANGLES);
            };

        }

        for(int i = 0; i < game.nplayers; i++)
        {
            ship s = game.pl[i];

            Quaternion qx = new Quaternion();
            Quaternion qy = new Quaternion();
            Quaternion qz = new Quaternion();

            qx.setEulerAnglesRad(0, 0, s.rx);
            qy.setEulerAnglesRad((float) (s.ry + Math.PI), 0, 0);
            qz.setEulerAnglesRad(0, s.rz, 0);

            Quaternion combined = qx.mul(qy).mul(qz);
            Matrix4 rot = new Matrix4().set(combined);

            Matrix4 model = new Matrix4().idt()
                .translate(s.renderx,s.y,s.renderz)
                .mul(rot)
                //.scale( scale[0], scale[1], scale[2]);
                ;
            /*Matrix4 view = camera.view;
            Matrix4 proj = camera.projection;
            Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);*/

            depthShader.setUniformMatrix("u_lightVP", lightCamera[id].combined);
            depthShader.setUniformMatrix("u_model", model);

            s.mesh.mesh.render(depthShader, GL20.GL_TRIANGLES);

        }
        depthShader.end();

        shadowFBO[id].end();

        shadowMap[id] = shadowFBO[id].getColorBufferTexture();

    }

    void set_state(int s)
    {
        state = s;
        counter = 0;
        if(state == FINISHED)
            viewPortAspectRatio = (float) Gdx.graphics.getWidth() / Gdx.graphics.getHeight();
    }

    void update_level_action()
    {
        /*int i, j, k, l;*/

        if((state==SINGLE) || (state==VERSUS)){
            if(counter==5)
                switch(course.rand()%3){
                    case 0 : game.play_voice("start1.wav"); break;
                    case 1 : game.play_voice("start2.wav"); break;
                    case 2 : game.play_voice("start3.wav"); break;
                };
            if(counter==360) game.wthree.playonce();
            if(counter==420) game.wtwo.playonce();
            if(counter==480) game.wone.playonce();
            if(counter==560) game.wgo.playonce();
        };

        counter++;

        //if(counter%1000==0)
            hour_environment();

        //Item cubes
        if(game.cour.counter%240==0)
            game.cour.add_random_item();

        game.cour.update();

        /*ctr->actualiza();

        //if(ctr->tecla(DIK_F1)){pl[0]->raceover=TRUE;};
        //if(ctr->tecla(DIK_F2)){pl[1]->raceover=TRUE;};*/

        for(int i=0; i<game.nhumans; i++){
            //cam.look_at(pl[i]->cam_pos.x,pl[i]->cam_pos.y,pl[i]->cam_pos.z,pl[i]->vrp.x,pl[i]->vrp.y,pl[i]->vrp.z);
            if(game.pl[i].raceover)
                game.pl[i].ia_update(null,game.ctr);
		    else
                game.pl[i].update(null,game.ctr,game.gdata.controls[i]);
            if(game.pl[i].finallapflag) { game.play_voice("finallap.wav"); game.pl[i].finallapflag=false;};
        };

        for(int i=game.nhumans; i<game.nplayers; i++)
            game.pl[i].ia_update(null,game.ctr);

        for(int i=0; i<game.nplayers; i++)
            for(int j=0; j<game.nplayers; j++)
                if(i<j)
                    if(game.pl[i].collide(game.pl[j]))
                    {
                        //cour->play_3d_sample(&cam,pl[i]->x,pl[i]->y,pl[i]->z,pl[i]->colsound);
                    }

        int l=0;
        while((l<game.nplayers) && (game.pl[game.position[l]].raceover)) l++;

        for(int i=0; i<game.nplayers; i++)
            for(int j=l; j<game.nplayers-1; j++)
                if(more_advanced(game.pl[game.position[j+1]],game.pl[game.position[j]])){

                    int k=game.position[j];
                    game.position[j]=game.position[j+1];
                    game.position[j+1]=k;

                    game.pl[game.position[j]].pos=j+1;
                    game.pl[game.position[j+1]].pos=j+2;
                };


        /*System.out.println("==========================================================");
        for(int i=0; i<game.nplayers; i++)
        {
            ship pl = game.pl[game.position[i]];
            System.out.println("Position: "+i+", player "+game.position[i]+" - Lap :"+pl.lap+", Next seg:"+pl.nextsegment+" Dist to next: "+game.cour.distance_to_segment(pl.renderx,pl.y,pl.renderz, pl.nextsegment));
        }*/
    }

    void show_level_action(int follow, PerspectiveCamera cam, int vpx, int vpy, int vpw, int vph)
    {
        /*float ry, size;
        int i,j,l;
        char s[200];
        vertex v1,v2,v3,v4;

        GLfloat lightPos[4];
        GLfloat ambientLight[] = { 0.0f, 0.0f, 0.0f, 1.0f };
        GLfloat diffusebackg[] = { 0.8f, 0.8f, 0.8f, 1.0f };
        */

        if(game.gdata.shadowmap) {
            refresh_shadow_map(0, game.pl[follow], 200);
            refresh_shadow_map(1, game.pl[follow], 500);
            refresh_shadow_map(2, game.pl[follow], 1500);
            //refresh_shadow_map(3, game.pl[follow], 1500);
        }

        Gdx.gl.glViewport(vpx, vpy, vpw, vph);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);

        cam.position.set(game.pl[follow].cam_pos.x, game.pl[follow].cam_pos.y, game.pl[follow].cam_pos.z);
        cam.lookAt(game.pl[follow].vrp.x, game.pl[follow].vrp.y, game.pl[follow].vrp.z);
        cam.up.set(0, 1, 0);
        cam.near = 0.1f;
        cam.far = 10000f;
        cam.update();

        /*
        cam.look_at(pl[follow]->cam_pos.x,pl[follow]->cam_pos.y,pl[follow]->cam_pos.z,pl[follow]->vrp.x,pl[follow]->vrp.y,pl[follow]->vrp.z);

        sun.reset();
        sun.translate(-cam.x,-cam.y,-cam.z);
        sun.rotate(-cam.rx,-cam.ry,-cam.rz);
        lightPos[0]=sun.nx; lightPos[1]=sun.ny; lightPos[2]=sun.nz; lightPos[3]=0.0;

        glEnable(GL_LIGHT0);
        glLightfv(GL_LIGHT0,GL_AMBIENT,ambientLight);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,fogc);
        glLightfv(GL_LIGHT0,GL_POSITION,lightPos);

        glDisable(GL_BLEND);

        glDisable(GL_LIGHTING);
        glDisable(GL_CULL_FACE);
        glDisable(GL_DEPTH_TEST);*/

        Gdx.gl.glDepthMask(false);

        //Sky
        if(game.gdata.skygrfog){

            //glEnable(GL_FOG);
            show_sky(cam);
        };




        Gdx.gl.glDepthMask(true);
        show_ground(cam);

        sceneShader.begin();
        sceneShader.setUniformf("u_ambientColor", 0.2f, 0.2f, 0.2f);

        sceneShader.setUniformi("u_numLights", 1);
        sceneShader.setUniformf("u_lightPos[0]", sun.x, sun.y, sun.z);
        sceneShader.setUniformf("u_lightColor[0]", new Vector3(0.8f, 0.8f, 0.8f));
        sceneShader.setUniformf("u_lightIntensity[0]", 1.0f);

        sceneShader.setUniformf("u_fogColor", fogc[0], fogc[1], fogc[2]); // gris claro
        sceneShader.setUniformf("u_fogStart", 10.0f);
        sceneShader.setUniformf("u_fogEnd", 1000.0f);

        if(game.gdata.shadowmap) {
            set_shadowmap_values(sceneShader);
        }

        int numLights = 1;
        // Flame lights
        if(game.gdata.exhaustLights)
            add_ship_flame_lights(sceneShader, cam);


        /*glClear(GL_DEPTH_BUFFER_BIT);

        // The course
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glLightfv(GL_LIGHT0,GL_DIFFUSE,diffusebackg);*/

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        int l=30+(15*game.gdata.drawdist);
        if (game.nhumans>1) l= (int) (0.75*l);

        game.cour.render(sceneShader,cam,game.pl[follow].segment, (long) game.counter,l);

        sceneShader.end();

        //Ships

        if(!game.gdata.shadowmap) {
            //    glDisable(GL_DEPTH_TEST);

            Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
            //Gdx.gl.glDisable(GL20.GL_CULL_FACE);
            Gdx.gl.glEnable(GL20.GL_BLEND);
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            for (int i = game.nplayers - 1; i >= 0; i--)
                show_shadow(cam, game.pl[i]);
            //};
        }

        shipShader.begin();
        shipShader.setUniformf("u_ambientColor", 0.2f, 0.2f, 0.2f);

        shipShader.setUniformi("u_numLights", 1);
        shipShader.setUniformf("u_lightPos[0]", sun.x, sun.y, sun.z);
        shipShader.setUniformf("u_lightColor[0]", new Vector3(0.8f, 0.8f, 0.8f));
        shipShader.setUniformf("u_lightIntensity[0]", 1.0f);

        if(game.gdata.exhaustLights)
            add_ship_flame_lights(shipShader, cam);

        shipShader.setUniformf("u_fogColor", fogc[0], fogc[1], fogc[2]); // gris claro
        shipShader.setUniformf("u_fogStart", 10.0f);
        shipShader.setUniformf("u_fogEnd", 1000.0f);

        if(game.gdata.shadowmap) {
            set_shadowmap_values(shipShader);
        }

        shipShader.setUniformi("u_face0", 10);
        shipShader.setUniformi("u_face1", 11);
        shipShader.setUniformi("u_face2", 12);
        shipShader.setUniformi("u_face3", 13);
        shipShader.setUniformi("u_face4", 14);
        shipShader.setUniformi("u_face5", 15);
        cubemapFaces[0].bind(10);
        cubemapFaces[1].bind(11);
        cubemapFaces[2].bind(12);
        cubemapFaces[3].bind(13);
        cubemapFaces[4].bind(14);
        cubemapFaces[5].bind(15);


        Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        for(int i=game.nplayers-1; i>=0; i--)
            show_ship(cam,game.pl[i]);

        shipShader.end();

        Gdx.gl.glDepthMask(false);

        for(int i=game.nplayers-1; i>=0; i--)
            show_ship_sprites(cam,game.pl[i]);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        game.cour.particles.render(cam, this);

        // The fuel wastes
        /*glDisable(GL_FOG);
        glEnable(GL_DEPTH_TEST);
        glDisable(GL_CULL_FACE);
        glDepthMask(0);*/
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        for(int i=game.nplayers-1; i>=0; i--) {

            show_ship_shield(cam, game.pl[i]);
        }
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        for(int i=game.nplayers-1; i>=0; i--) {

            show_ship_flame(cam, game.pl[i]);
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Opponent cursor in versus
        //glDisable(GL_CULL_FACE);
        for(int i=0; i<game.nhumans; i++){
            float size= (float) (0.1f*Math.sqrt(game.pl[i].dist));
            if((i!=follow) && (game.pl[i].dist>200*200) && (game.pl[i].dist<1000*1000)){
                show_3d_sprite(cam,game.plcursor[game.gdata.icons[i]].texture,0,1,1,0,game.pl[i].renderx,game.pl[i].y+(size*1.1f),game.pl[i].renderz,1.0f,1.0f,1.0f,size,size,1.0f);
                show_3d_sprite(cam,game.arrow.texture,0,1,1,0,game.pl[i].renderx,game.pl[i].y+(size/3.0f),game.pl[i].renderz,1.0f,1.0f,1.0f,size/2,size/2,1.0f);
            };
        };


        //glDepthMask(1);
        Gdx.gl.glDepthMask(true);
        /*

        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        gluOrtho2D(0, 640, 0, 480);
        glDisable(GL_LIGHTING);*/

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
        game.batch.setProjectionMatrix(game.camera2d.combined);
        game.batch.begin();

        // Ship messages
        if(game.pl[follow].messcount>0)
            game.fuente.show_text( game.batch, 260,320,game.pl[follow].message,0);

        if(game.pl[follow].state==ship.DESTR){
            game.fuente.show_text(game.batch, 340,350,game.loc.get("disqualified"),1);
            game.fuente.show_text(game.batch, 430,100,game.loc.get("gameOver"),1);
        };

        //Start countdown
        for(int j=0; j<4; j++)
            if((game.cour.counter>360+(60*j)) && (game.cour.counter<=420+(60*j))){

                long i=game.cour.counter-(360+(60*j));
                float size=2000f/(0.25f*i);

                game.start[j].render2d(game.batch, 0,0,256,256,(int)((Main.SCREENX/2)-(size/2)),(int)((Main.SCREENY/2)-(size/2)),(int)((Main.SCREENX/2)+(size/2)),(int)((Main.SCREENY/2)+(size/2)),1f-(i*0.015f));
            };

        game.batch.end();


        //debugSolidNormals(game.cour.nodes[game.pl[0].segment].mesh, cam);
        //debugSolidNormals(game.cour.nodes[game.pl[0].nextsegment].mesh, cam);


    }

    void debugSolidNormals(solid s, PerspectiveCamera cam)
    {
        game.shapeRenderer.setProjectionMatrix(cam.combined);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for(triangle t : s.triangles)
        {
            game.shapeRenderer.setColor(1f, 1f, 0f, 1f);
            game.shapeRenderer.line(new Vector3(t.v1.x, t.v1.y, t.v1.z), new Vector3(t.v2.x, t.v2.y, t.v2.z));
            game.shapeRenderer.line(new Vector3(t.v1.x, t.v1.y, t.v1.z), new Vector3(t.v3.x, t.v3.y, t.v3.z));
            game.shapeRenderer.line(new Vector3(t.v3.x, t.v3.y, t.v3.z), new Vector3(t.v2.x, t.v2.y, t.v2.z));
            game.shapeRenderer.setColor(0f, 1f, 0f, 1f);
            Vector3 or = new Vector3((t.v1.x + t.v2.x + t.v3.x) / 3f, (t.v1.y + t.v2.y + t.v3.y) / 3f, (t.v1.z + t.v2.z + t.v3.z) / 3f);
            Vector3 end = new Vector3(or.x + 2*t.normal.x, or.y + 2*t.normal.y, or.z + 2*t.normal.z);
            game.shapeRenderer.line(or, end);
        }
        game.shapeRenderer.end();
    }

    void set_shadowmap_values(ShaderProgram shader)
    {
        shader.setUniformMatrix("u_lightVP[0]", lightCamera[0].combined);
        shader.setUniformMatrix("u_lightVP[1]", lightCamera[1].combined);
        shader.setUniformMatrix("u_lightVP[2]", lightCamera[2].combined);
        shader.setUniformf("u_shadowMapSize[0]", shadowMapSize[0]);
        shader.setUniformf("u_shadowMapSize[1]", shadowMapSize[1]);
        shader.setUniformf("u_shadowMapSize[2]", shadowMapSize[2]);
        //shader.setUniformMatrix("u_lightVP[3]", lightCamera[3].combined);
        shadowMap[0].bind(6);
        shadowMap[1].bind(7);
        shadowMap[2].bind(8);
        //shadowMap[3].bind(9);
        shader.setUniformi("u_shadowMap[0]", 6);
        shader.setUniformi("u_shadowMap[1]", 7);
        shader.setUniformi("u_shadowMap[2]", 8);
        //shader.setUniformi("u_shadowMap[3]", 9);
        shader.setUniformf("u_cascadeEnds[0]", 50);
        shader.setUniformf("u_cascadeEnds[1]", 300);
        shader.setUniformf("u_cascadeEnds[2]", 1300);
        //shader.setUniformf("u_cascadeEnds[3]", 1300);
    }

    void add_ship_flame_lights(ShaderProgram shader, PerspectiveCamera cam)
    {
        int numLights = 1;
        // Flame lights
        for(int i = 0; i < game.nplayers; i++)
        {
            ship sh = game.pl[i];

            Vector3 camToShip = new Vector3(cam.position.x - sh.renderx, cam.position.y - sh.y, cam.position.z - sh.renderz);
            if(camToShip.len() < 700f && sh.engine > 0.001f) {

                // Versión una luz por nave
                /*Vector3 lightPos = new Vector3(0, 0, 0);
                for (int j = 0; j < sh.data.nlights; j++) {
                    lightPos.x += sh.light_x(j);
                    lightPos.y += sh.light_y(j);
                    lightPos.z += sh.light_z(j);
                }
                lightPos.x /= sh.data.nlights;
                lightPos.y /= sh.data.nlights;
                lightPos.z /= sh.data.nlights;

                shader.setUniformf("u_lightPos[" + numLights + "]", lightPos.x, lightPos.y, lightPos.z);
                shader.setUniformf("u_lightColor[" + numLights + "]", sh.lightcol[0], sh.lightcol[1], sh.lightcol[2]);
                shader.setUniformf("u_lightIntensity[" + numLights + "]", sh.engine);
                numLights++;
                shader.setUniformi("u_numLights", numLights);*/

                // Versión múltiples luces por nave
                for(int j = 0; j < sh.data.nlights; j++) {
                    shader.setUniformf("u_lightPos[" + numLights + "]", sh.light_x(j), sh.light_y(j), sh.light_z(j));
                    shader.setUniformf("u_lightColor[" + numLights + "]", sh.lightcol[0], sh.lightcol[1], sh.lightcol[2]);
                    shader.setUniformf("u_lightIntensity[" + numLights + "]", sh.engine);
                    numLights++;
                }
                shader.setUniformi("u_numLights", numLights);
            }

        }
    }

    void hour_environment()
    {
        float nightfog[]={0.0f,0.0f,0.0f},
            daysky[]={0.0f/255.0f,191.0f/255.0f,250.0f/255.0f},
            dawnsky[]={1.0f,128f/255.0f,0.0f},
            nightsky[]={0.0f/255.0f,0.0f,100.0f/255.0f},
            daycloud[]={1f, 1f, 1f},
            dawncloud[]={0.62f,0.34f, 0.50f},
            //nightcloud[]={0.42f,0.47f,0.54f},
            nightcloud[]={0f,0f,0f},
		s1[]={0f,0f,0f}, s2[]={0f,0f,0f};
        float g = 0f, ig, a;
        //struct tm *newtime;
        //time_t long_time;
        int i,j;

        // Different color of sky when dawn or sunset!
        /*time( &long_time );                // Get time as long integer.
        newtime = localtime( &long_time ); // Convert to local time.
        hour = (float) (newtime->tm_hour + (newtime->tm_min/60.0));*/

        LocalTime ahora = LocalTime.now();
        int hora = ahora.getHour();
        int minuto = ahora.getMinute();

        hour = hora + (minuto / 60f);

        switch(game.gdata.daytime){
            case 1 : hour=7.30f; break;
            case 2 : hour=12.0f; break;
            case 3 : hour=19.30f; break;
            case 4 : hour=22.0f; break;
        };

        /*hour = game.cour.counter / 60f;
        while (hour >= 24.f)
        {
            hour -=24.f;
        }*/

        if((hour>8.0) && (hour<18.0)) {g=1.0f; s1=daycloud; s2=nightcloud;}
        if((hour<6.0) || (hour>20.0)) {g=0.0f; s1=daycloud; s2=nightcloud;}
        if((hour>=6.0) && (hour<=8.0)) {g=((hour-6.0f)/2.0f); s1=daycloud; s2=nightcloud;}
        if((hour>=18.0) && (hour<=19.5)) {g=1.0f-((hour-18.0f)/1.5f); s1=daycloud; s2=dawncloud;}
        if((hour>=19.5) && (hour<=21.0)) {g=1.0f-((hour-19.5f)/1.5f); s1=dawncloud; s2=nightcloud;}
        ig=1.0f-g;
        for(i=0; i<3; i++) cloudc[i]=(s1[i]*g)+(s2[i]*ig); cloudc[3]=1.0f;

        if((hour>8.0) && (hour<18.0)) {g=1.0f; s1=daysky; s2=nightsky;}
        if((hour<6.0) || (hour>20.0)) {g=0.0f; s1=daysky; s2=nightsky;}
        if((hour>=6.0) && (hour<=8.0)) {g=((hour-6.0f)/2.0f); s1=daysky; s2=nightsky;}
        if((hour>=18.0) && (hour<=19.5)) {g=1.0f-((hour-18.0f)/1.5f); s1=daysky; s2=dawnsky;}
        if((hour>=19.5) && (hour<=21.0)) {g=1.0f-((hour-19.5f)/1.5f); s1=dawnsky; s2=nightsky;}
        ig=1.0f-g;
        for(i=0; i<3; i++) skyc[i]=(s1[i]*g)+(s2[i]*ig); skyc[3]=1.0f;

        if((hour>8.0) && (hour<18.0)) g=1.0f;
        if((hour<6.0) || (hour>20.0)) g=0.0f;
        if((hour>=6.0) && (hour<=8.0)) g=((hour-6.0f)/2.0f);
        if((hour>=18.0) && (hour<=21.0)) g=1.0f-((hour-18.0f)/3.0f);
        ig=1.0f-g;
        for(i=0; i<3; i++) fogc[i]=(course.scenes.course_scenes.get(game.cour.info.scene).fogcolor[i]*g)+(nightfog[i]*ig); fogc[3]=1.0f;

        //Sun position!

        a=((hour-7.0f)/12.0f)*3.1415f;
        sun=new vertex((float) (-5000f*Math.cos(a)), (float) Math.abs(5000f*Math.sin(a)),0f);
    }

    float nearest(int n, int m)
    {
        return n-(n%m);
    }
    void show_ground(PerspectiveCamera cam)
    {
        float gx, gz, TILE=7;
        int i,x,z;

        groundShader.begin();
        groundShader.setUniformf("u_ambientColor", 0f, 0f, 0f);

        groundShader.setUniformi("u_numLights", 1);
        groundShader.setUniformf("u_lightPos[0]", sun.x, sun.y, sun.z);
        groundShader.setUniformf("u_lightColor[0]", new Vector3(1f, 1f, 1f));
        groundShader.setUniformf("u_lightIntensity[0]", 1.0f);

        groundShader.setUniformf("u_fogColor", fogc[0], fogc[1], fogc[2]); // gris claro
        groundShader.setUniformf("u_fogStart", 10.0f);
        groundShader.setUniformf("u_fogEnd", 1000.0f);

        if(game.gdata.shadowmap) {
            set_shadowmap_values(groundShader);
        }

        int numLights = 1;
        // Flame lights
        if(game.gdata.exhaustLights)
            add_ship_flame_lights(groundShader, cam);

        gx=nearest((int) cam.position.x, (int) (GROUNDWIDTH/GROUNDTILE))-(GROUNDWIDTH/2);
        //gz=nearest((int) cam.position.z, (int) (GROUNDWIDTH/GROUNDTILE))-(GROUNDWIDTH/2);

        for(x=0; x<TILE; x++){

            gz=nearest((int) cam.position.z, (int) (GROUNDWIDTH/GROUNDTILE))-(GROUNDWIDTH/2);

            for(z=0; z<TILE; z++){

                groundMesh.render(groundShader, cam, gx, 0f, gz, 0f, 0f, 0f);
                gz+=GROUNDWIDTH/TILE;
            };

            gx+=GROUNDWIDTH/TILE;
        };
    }

    void show_sky(PerspectiveCamera cam)
    {
        /*float gx, gz, TILE=5;
        int i,x,z;

        skyShader.begin();
        skyShader.setUniformf("u_meshColor", skyc[0], skyc[1], skyc[2]);
        skyShader.setUniformf("u_fogColor", fogc[0], fogc[1], fogc[2]); // gris claro
        skyShader.setUniformf("u_fogStart", 10.0f);
        skyShader.setUniformf("u_fogEnd", 1000.0f);

        gx=nearest((int) cam.position.x, (int) (SKYWIDTH/GROUNDTILE))-(SKYWIDTH/2);
        gz=nearest((int) cam.position.z, (int) (SKYWIDTH/GROUNDTILE))-(SKYWIDTH/2);

        for(x=0; x<TILE; x++){

            gz=nearest((int) cam.position.z, (int) (SKYWIDTH/GROUNDTILE))-(SKYWIDTH/2);

            for(z=0; z<TILE; z++){

                skyMesh.render(skyShader, cam, gx, 0f, gz, 0f, 0f, 0f);
                gz+=SKYWIDTH/TILE;
            };

            gx+=SKYWIDTH/TILE;
        };

        skyShader.end();*/

        // Skydome version
        // Sky
        skyShader.begin();

        Matrix4 model = new Matrix4().idt().translate(cam.position.x, 0f, cam.position.z);
        Matrix4 view = cam.view;
        Matrix4 proj = cam.projection;
        Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);

        skyShader.setUniformMatrix("u_mvp", MVP);
        skyShader.setUniformMatrix("u_model", model);
        skyShader.setUniformf("u_skyColor", skyc[0], skyc[1], skyc[2]);
        skyShader.setUniformf("u_fogColor", fogc[0], fogc[1], fogc[2]); // gris claro
        skyShader.setUniformf("u_fogStart", 300.0f);
        skyShader.setUniformf("u_fogEnd", 0.0f);
        skyShader.setUniformi("u_skyMode", 0);

        skyDome.render(skyShader, GL20.GL_TRIANGLES);

        skyShader.end();

        // Sun & Moon
        //Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        if(hour < 7.f || hour >= 19.f)
            show_3d_sprite(cam, Main.moon,0,0,1,1,sun.x,sun.y,sun.z,1.0f,1.0f,1.0f,500, 500,1f);
        else
            show_3d_sprite(cam, Main.flame,0,0,1,1,sun.x,sun.y,sun.z,1.0f,1.0f,1.0f,1000,1000,1f);

        // Clouds
        skyShader.begin();
        skyShader.setUniformf("u_cloudColor", cloudc[0], cloudc[1], cloudc[2]);
        skyShader.setUniformf("u_cloudOffset", game.counter*0.0001f, 0f);
        skyShader.setUniformi("u_texture", 0);
        skyShader.setUniformi("u_skyMode", 1);
        cloudTexture.bind(0);

        skyDome.render(skyShader, GL20.GL_TRIANGLES);

        skyShader.end();




    }

    void show_ship(PerspectiveCamera cam, ship s)
    {
        int i,j, dur;
        float a, c, size, dy, sz, sy, dif;
        texture t;

        /*
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);

        if(accelerated)
            for(i=0; i<nplayers; i++)
                set_light(cam,i+1,pl[i]->x,pl[i]->y,pl[i]->z,pl[i]->lightcol[0],pl[i]->lightcol[1],pl[i]->lightcol[2],pl[i]->engine);*/

        dur=(int)(s.energy/4.0); if(dur==0) dur=1;
        if((s.energy<ship.MAXENERGY/4) && (counter%dur<(int)(dur/3.0))){
            s.mesh.set_color_coef(DAMAGED_COL[0],DAMAGED_COL[1],DAMAGED_COL[2]);
            s.lowres.set_color_coef(DAMAGED_COL[0],DAMAGED_COL[1],DAMAGED_COL[2]);
        };
        if(s.hypermode>0){

            s.mesh.set_color_coef(HYPER_COL[0],HYPER_COL[1],HYPER_COL[2]);
            s.lowres.set_color_coef(HYPER_COL[0],HYPER_COL[1],HYPER_COL[2]);
        };

        s.render(shipShader, cam,true);


        s.mesh.set_color_coef(1.0f,1.0f,1.0f);
        s.lowres.set_color_coef(1.0f,1.0f,1.0f);

    }

    void show_ship_sprites(PerspectiveCamera cam, ship s)
    {
        int i,j, dur;
        float a, c, size, dy, sz, sy, dif;
        texture t;

        /*if(s.shield>0){


            Vector3 dir = cam.direction;

            // Yaw: rotación alrededor del eje Y (horizontal)
            float yaw = (float) Math.atan2(dir.x, dir.z);

            // Pitch: rotación alrededor del eje X (vertical)
            float pitch = (float) Math.asin(dir.y);

            //sz=s->data.sizez+4.0;
            dif=Math.abs(s.ry-pitch);
            sz= (float) (((4.0+s.data.sizex)*Math.abs(Math.sin(dif)))+((4.0+s.data.sizez)*Math.abs(Math.cos(dif))));
            //sy=s->data.sizey+4.0;
            sy= (float) (((4.0f+s.data.sizez)*Math.abs(Math.sin(yaw)))+((1.0f+s.data.sizey)*Math.abs(Math.cos(yaw))));

            if(s.shield>80) a=1.0f;
            else a=1.0f-((80-s.shield)/80.0f);
            show_3d_sprite(cam,game.shield,0,0,1,1,s.renderx,s.y,s.renderz,0.3125f,0.5507f,0.9922f,sz,sy,a);
        };*/

        // Ship burning and destroyed
        if((s.state>=ship.BURN) || ((s.state==ship.STUN) && (s.counter<=50))){

            switch(s.state){
                case ship.STUN    : size=9; dur=50; t=game.explos; c=1.0f; dy=0.0f; break;
                case ship.BURN    : size=3; dur=20; t=game.explos; c=1.0f; dy=0.0f; break;
                case ship.BIGEXPL : size=15; dur=100; t=game.explos; c=1.0f; dy=0.0f; break;
                case ship.DESTR   : size=10; dur=60; t=game.smoke; c=0.0f; dy=3.0f; break;
                default: size=10; dur=60; t=game.smoke; c=0.0f; dy=3.0f; break;
            };

            a=1.0f-((s.counter%dur)*0.01f);
            i=(int)((s.counter%dur)*16/dur);

            if(s.state!=ship.BURN) show_3d_sprite(cam,t,0f+(0.25f*(i%4)),0.25f+(0.25f*(i/4)),0.25f+(0.25f*(i%4)),0+(0.25f*(i/4)),s.renderx,s.y+dy,s.renderz,c,c,c,size,size,a);
		else
            for(j=0; j<ship.NEXPLS; j++){
                i=(int)(s.expframe[j]);
                show_3d_sprite(cam,t,0+(0.25f*(i%4)),0.25f+(0.25f*(i/4)),0.25f+(0.25f*(i%4)),0+(0.25f*(i/4)),s.exppos[j][0],s.exppos[j][1],s.exppos[j][2],1.0f,1.0f,1.0f,size,size,1.0f-(s.expframe[j]*0.05f));
            };
        };
    }

    void show_ship_flame(PerspectiveCamera cam, ship s) {

        float a = s.engine, f;
        float sx = 5.0f * s.data.lsize * a, sy = 5.0f * s.data.lsize * a;
        int i;

        /* // Versión sprite
        for(i=0; i<s.data.nlights; i++)
            show_3d_sprite(cam,game.flame,0,0,1,1,s.light_x(i),s.light_y(i),s.light_z(i),s.lightcol[0],s.lightcol[1],s.lightcol[2],sx,sy,a);

         */

        exhaustShader.begin();

        for (i = 0; i < s.data.nlights; i++) {

            float scalex = (float) (a + Math.sin(game.cour.counter*2) * 0.1f * a);
            float scaley = (float) (s.data.lsize * a + Math.sin(game.cour.counter*2 + 4) * 0.1f * a);
            float scalez = (float) (s.data.lsize * a + Math.cos(game.cour.counter*2) * 0.1f * a);

            Quaternion qx = new Quaternion();
            Quaternion qy = new Quaternion();
            Quaternion qz = new Quaternion();

            qx.setEulerAnglesRad(0, 0, s.rx);
            qy.setEulerAnglesRad((float) (s.ry + Math.PI), 0, 0);
            qz.setEulerAnglesRad(0, s.rz, 0);

            Quaternion combined = qx.mul(qy).mul(qz);
            Matrix4 rot = new Matrix4().set(combined);

            Matrix4 model = new Matrix4().idt().translate(s.light_x(i), s.light_y(i), s.light_z(i))
                .mul(rot)
                .scale( scalex, scaley, scalez);
            Matrix4 view = cam.view;
            Matrix4 proj = cam.projection;
            Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);

            exhaustShader.setUniformMatrix("u_mvp", MVP);
            exhaustShader.setUniformf("u_meshColor", s.lightcol[0], s.lightcol[1], s.lightcol[2], a);
            ship.exhaustMesh.render(exhaustShader, GL20.GL_TRIANGLES);
        }

        exhaustShader.end();
    }

    void show_ship_shield(PerspectiveCamera cam, ship s) {

        if(s.shield <= 0) return;

        float a;
        if(s.shield>80) a=1.0f;
        else a=1.0f-((80-s.shield)/80.0f);

        shieldShader.begin();

        float scalex = (float) (s.data.sizez);
        float scaley = (float) (s.data.sizey);
        float scalez = (float) (s.data.sizex);

        Quaternion qx = new Quaternion();
        Quaternion qy = new Quaternion();
        Quaternion qz = new Quaternion();

        qx.setEulerAnglesRad(0, 0, s.rx);
        qy.setEulerAnglesRad((float) (s.ry + Math.PI), 0, 0);
        qz.setEulerAnglesRad(0, s.rz, 0);

        Quaternion combined = qx.mul(qy).mul(qz);
        Matrix4 rot = new Matrix4().set(combined);

        Matrix4 model = new Matrix4().idt().translate(s.renderx, s.y, s.renderz)
            .mul(rot)
            .scale( scalex, scaley, scalez);
        Matrix4 view = cam.view;
        Matrix4 proj = cam.projection;
        Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);

        shieldShader.setUniformMatrix("u_mvp", MVP);
        shieldShader.setUniformMatrix("u_model", model);
        shieldShader.setUniformf("u_cameraPos", cam.position.x, cam.position.y, cam.position.z);
        shieldShader.setUniformf("u_meshColor", 0.3125f,0.5507f,0.9922f,a);
        shield.render(shieldShader, GL20.GL_TRIANGLES);

        shieldShader.end();
    }

    void show_shadow(PerspectiveCamera cam, ship s)
    {

        float SX=s.data.sizex, SZ=s.data.sizez, y;

        if(s.outofcourse) y=course.GROUNDY;
        else y=game.cour.y_at_xz(s.renderx,s.renderz,s.segment);

        Matrix4 model = new Matrix4().idt().translate(s.renderx,y,s.renderz)
            .rotate(Vector3.Y, (float)(180f*s.ry/Math.PI) + 90)
            .scale( SX, 1f, SZ);
        Matrix4 view = cam.view;
        Matrix4 proj = cam.projection;
        Matrix4 MVP = new Matrix4(proj).mul(view).mul(model);

        shadowShader.begin();
        shadowShader.setUniformMatrix("u_mvp", MVP);
        shadowShader.setUniformi("u_texture", 0);

        game.shadow.gdxTexture.bind(0);
        shadow.render(shadowShader, GL20.GL_TRIANGLES);
        shadowShader.end();
    }

    boolean more_advanced(ship s1, ship s2)
    {
        if(s1.lap>s2.lap) return true;
        else if(s1.lap<s2.lap) return false;
        else {
            if(s1.nextsegment>s2.nextsegment) return true;
            else if(s1.nextsegment<s2.nextsegment) return false;
            else {
                if (game.cour.distance_to_segment(s1.renderx, s1.y, s1.renderz, s1.nextsegment) <
                    game.cour.distance_to_segment(s2.renderx, s2.y, s2.renderz, s2.nextsegment))
                    return true;
                else return false;
            }
        }
        //System.out.println("Should never reach this point!!!!!!");
        //return false;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        game.counter += delta * 70;

        accumulatedDelta += delta;
        float step = 1f / Main.FPS;
        while(accumulatedDelta >= step)
        {
            updatesPending++;
            accumulatedDelta -= step;
        }

        // 3D Layer : Scene ====================================================

        /*
        if(gdata.skygrfog)
            glEnable(GL_FOG);
        glFogfv(GL_FOG_COLOR, fogc);

        //3D Geometry
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45,1.3,0.1,ZFAR);
        gluLookAt(0,0,1,0,0,0,0.0,1.0,0.0);

        glClearColor(fogc[0],fogc[1],fogc[2],1.0);
        glClear(GL_COLOR_BUFFER_BIT);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();*/

        Gdx.gl.glClearColor(fogc[0], fogc[1], fogc[2], 1.f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (state == SINGLE) {
            show_level_action(0, cameraSingle, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else if (state == VERSUS) {
            if (game.nhumans == 2) {
                show_level_action(0, cameras2p[0], 0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
                show_level_action(1, cameras2p[1], Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
            }
            else if (game.nhumans >= 3)
            {
                show_level_action(0, cameras4p[0], 0, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/2);
                show_level_action(1, cameras4p[1], Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/2);
                show_level_action(2, cameras4p[2], 0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/2);
                if(game.nhumans == 4)
                {
                    show_level_action(3, cameras4p[3], Gdx.graphics.getWidth() / 2, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight()/2);
                }
            }
        } else if (state == FINISHED) {
            int i = 0;
            if(game.nhumans==1) i=0; else i=game.position[0];
            show_level_action(i, cameraSingle, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else if (state == DEMO) {
            show_level_action(0, cameraSingle, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        } else if (state == DISQUAL) {
            show_level_action(0, cameraSingle, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }

        // 2D Layer: HUD =================================================================================

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        if(state == SINGLE)
        {
            game.batch.begin();
            show_position(game.batch,0, HUD_START_X+20, 400);

            show_speed(game.batch, (int) game.pl[0].velocity_kmh(), HUD_START_X+20, 10);

            show_power(game.batch,0, Main.SCREENX-HUD_START_X-240, 340, 0);

            //Lap count
            String s = "LAP " + game.pl[0].lap + "/" + game.cour.info.nlaps;
            game.fuente.show_text(game.batch, HUD_START_X+200, 400, s, 1);
            s = "BOOST " + game.pl[0].nboosts;
            game.fuente.show_text(game.batch, Main.SCREENX-HUD_START_X-140, 345, s, 1);
            s = "FPS:"+(1f/delta);
            game.fuente.show_text(game.batch, 0, 440, s, 1);

            show_icon_rank();

            /*if(game.gdata.shadowmap) {
                game.batch.draw(shadowMap[0], 0, 0, 128, 128, 0, 0, shadowMapSize[0], shadowMapSize[0], false, true);
                game.batch.draw(shadowMap[1], 128, 0, 128, 128, 0, 0, shadowMapSize[1], shadowMapSize[1], false, true);
                game.batch.draw(shadowMap[2], 256, 0, 128, 128, 0, 0, shadowMapSize[2], shadowMapSize[2], false, true);
                //game.batch.draw(shadowMap[3], 256+128, 0, 128, 128, 0, 0, SHADOW_MAP_SIZE, SHADOW_MAP_SIZE, false, true);
            }*/


            /*game.batch.draw(cubemapFaces[0], 0, 0, 128, 128, 0, 0, cubemapSize, cubemapSize, false, true);
            game.batch.draw(cubemapFaces[1], 128, 0, 128, 128, 0, 0, cubemapSize,cubemapSize, false, true);
            game.batch.draw(cubemapFaces[2], 256, 0, 128, 128, 0, 0, cubemapSize,cubemapSize, false, true);
            game.batch.draw(cubemapFaces[3], 0, 128, 128, 128, 0, 0, cubemapSize,cubemapSize, false, true);
            game.batch.draw(cubemapFaces[4], 128, 128, 128, 128, 0, 0, cubemapSize,cubemapSize, false, true);
            game.batch.draw(cubemapFaces[5], 256, 128, 128, 128, 0, 0, cubemapSize,cubemapSize, false, true);*/


            //game.batch.draw(cloudTexture, 0, 0, 512, 256, 0, 0, 512,256, false, true);

            game.batch.end();

            show_map(Main.SCREENX-HUD_START_X-90, 70);
        }
        else if(state == VERSUS)
        {
            if(game.nhumans == 2)
            {
                game.batch.begin();

                show_position(game.batch,0, 0, 410);
                show_speed(game.batch,(int) game.pl[0].velocity_kmh(), 0, 0);
                show_power(game.batch,0, Main.SCREENX/2 - 220, 350, 0);
                String s = "BOOST " + game.pl[0].nboosts;
                game.fuente.show_text(game.batch, Main.SCREENX - 120, 357, s, 1);
                s = "LAP " + game.pl[0].lap + "/" + game.cour.info.nlaps;
                game.fuente.show_text(game.batch, 180, 435, s, 1);

                show_position(game.batch,1, Main.SCREENX/2, 410);
                show_speed(game.batch,(int) game.pl[1].velocity_kmh(), Main.SCREENX - 350, 0);
                show_power(game.batch,1, Main.SCREENX - 220, 350, 0);
                s = "BOOST " + game.pl[0].nboosts;
                game.fuente.show_text(game.batch, Main.SCREENX/2 - 120, 357, s, 1);
                s = "LAP " + game.pl[1].lap + "/" + game.cour.info.nlaps;
                game.fuente.show_text(game.batch, Main.SCREENX - 120 - 180, 435, s, 1);

                game.batch.end();

                show_map(Main.SCREENX / 2, 60);
            }
            else if(game.nhumans>=3){


               splitScreenCamera2d.update();
               splitScreenBatch.setProjectionMatrix(splitScreenCamera2d.combined);
               splitScreenBatch.begin();

                show_position(splitScreenBatch, 0,0,Main.SCREENY - 70);
                show_speed(splitScreenBatch, (int) game.pl[0].velocity_kmh(),0,Main.SCREENY/2 - 5);
                show_power(splitScreenBatch, 0,Main.SCREENX/2 - 200,Main.SCREENY - 130,1);

                show_position(splitScreenBatch, 1,Main.SCREENX - 170,Main.SCREENY - 70);
                show_speed(splitScreenBatch, (int) game.pl[1].velocity_kmh(),Main.SCREENX/2 + 70,Main.SCREENY/2 - 5);
                show_power(splitScreenBatch, 1,Main.SCREENX/2 - 40,Main.SCREENY - 130,1);

                show_position(splitScreenBatch, 2,0,10);
                show_speed(splitScreenBatch, (int) game.pl[2].velocity_kmh(),0,Main.SCREENY/2 - 60);
                show_power(splitScreenBatch, 2,Main.SCREENX/2 - 200,-40,1);

                if(game.nhumans==4) {
                    show_position(splitScreenBatch, 3, Main.SCREENX - 170, 10);
                    show_speed(splitScreenBatch, (int) game.pl[3].velocity_kmh(), Main.SCREENX/2 + 70, Main.SCREENY/2 - 60);
                    show_power(splitScreenBatch, 3, Main.SCREENX/2 - 40, -40, 1);
                }

                String s = "BOOST "+game.pl[0].nboosts;
                game.fuente.show_text(splitScreenBatch,Main.SCREENX/2 - 113,Main.SCREENY - 82,s,1);
                s = "LAP "+game.pl[0].lap+"/"+game.cour.info.nlaps;
                game.fuente.show_text(splitScreenBatch,165,Main.SCREENY - 25,s,1);

                s="BOOST "+game.pl[1].nboosts;
                game.fuente.show_text(splitScreenBatch, Main.SCREENX/2 + 15,Main.SCREENY - 82,s,1);
                s = "LAP "+game.pl[1].lap+"/"+game.cour.info.nlaps;
                game.fuente.show_text(splitScreenBatch, Main.SCREENX - 270,Main.SCREENY - 25,s,1);

                s = "BOOST "+game.pl[2].nboosts;
                game.fuente.show_text(splitScreenBatch, Main.SCREENX/2 - 113,10,s,1);
                s = "LAP "+game.pl[2].lap+"/"+game.cour.info.nlaps;
                game.fuente.show_text(splitScreenBatch, 165,10,s,1);

                if(game.nhumans==4){
                    s = "BOOST "+game.pl[3].nboosts;
                    game.fuente.show_text(splitScreenBatch,Main.SCREENX/2 + 15,10,s,1);
                    s =  "LAP "+game.pl[3].lap+"/"+game.cour.info.nlaps;
                    game.fuente.show_text(splitScreenBatch,Main.SCREENX - 270,10,s,1);
                }
                splitScreenBatch.end();

                if(game.nhumans==3)
                {
                    game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    game.shapeRenderer.setColor(0f,0f,0f, 1f);
                    game.shapeRenderer.rect(Main.SCREENX/2, 0, Main.SCREENX/2, Main.SCREENY/2);
                    game.shapeRenderer.end();
                    show_map(3*Main.SCREENX/4,Main.SCREENY/4);
                }
                else
                {
                    show_map(Main.SCREENX/2,Main.SCREENY/2);
                }
            };
        }
        else if (state == FINISHED)
        {
            int i = 0;
            if(game.nhumans==1) i=0; else i=game.position[0];
            game.batch.begin();
            show_position( game.batch, 0,20,390);
            show_speed(game.batch, game.pl[i].maxspeed,20,20);
            game.fuente.show_text(game.batch, 20,80,game.loc.get("maximumSpeed"),0);
            game.pl[i].logo.render2d(game.batch, 0,0,256,128,420,20,420+192,20+96,1.0f);
            if(game.pl[i].state!=ship.DESTR) game.fuente.show_text(game.batch, 270,350,game.loc.get("finished"),1);
            game.fuente.show_text(game.batch,400,420,game.loc.get("time"),0);
            String s = game.pl[i].time_str(game.pl[i].totaltime);
            game.fuente.show_text(game.batch, 500,420,s,0);
            game.batch.end();
        }
        else if (state == DEMO)
        {
            game.batch.begin();
            if((counter/20)%2==0) game.fuente.show_text(game.batch, 160,120,game.loc.get("pushAnyButtonPlay"),0);
            game.batch.end();
        }

        if(paused) {
            game.batch.begin();
            game.fuente.show_text(game.batch,280,230,game.loc.get("paused"),0);
            game.batch.end();
        }



        // LOGIC ============================================

        if (state == SINGLE)
        {
            while(updatesPending > 0) {
                if(!paused)
                    update_level_action();
                updatesPending--;
            }
            if (paused && ((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))) paused=false;
            if(game.pl[0].raceover) set_state(FINISHED);
            if(game.pl[0].state==ship.DESTR) set_state(DISQUAL);
            if(game.ctr.atr(controlm.TEC1) || game.ctr.atr(game.gdata.controls[0])) {game.abort_champ=true; game.setScreen(new RaceResultScreen(game)); dispose();};
            if((game.ctr.pau(controlm.TEC1) || game.ctr.pau(game.gdata.controls[0])) && (game.cour.counter>360)) {paused=true;};
        }
        else if (state == VERSUS)
        {
            while(updatesPending > 0) {
                if(!paused)
                    update_level_action();
                updatesPending--;
            }
            if (paused && ((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0])))) paused=false;
            if(all_finished()) set_state(FINISHED);
            //if(ctr->tecla(DIK_ESCAPE)) {set_state(RACE_RESULT);};
            if(game.ctr.atr(controlm.TEC1) || game.ctr.atr(game.gdata.controls[0])) {game.abort_champ=true; game.setScreen(new RaceResultScreen(game)); dispose();};
            if((game.ctr.pau(controlm.TEC1)) && (game.cour.counter>360)) {paused=true;};
        }
        else if (state == FINISHED || state == DISQUAL)
        {
            while(updatesPending > 0) {
                update_level_action();
                updatesPending--;
                if(counter==1) game.stop_music();
                if((counter==1) && (state==FINISHED)) game.play_voice("finished.wav");
                if((counter==5) && (state==DISQUAL)) game.play_voice("badluck.wav");
            }
            //ctr->actualiza();
            //ctr->actualiza();
            if((counter>=300) && (game.ctr.algun_boton(game.gdata.controls[0]))){
                game.setScreen(new RaceResultScreen(game));
                dispose();
            };
        }
        else if (state == DEMO)
        {
            while(updatesPending > 0) {
                update_level_action();
                updatesPending--;
            }
            //ctr->actualiza();
            if((game.ctr.algun_boton(controlm.TEC1)) || (game.ctr.algun_boton(game.gdata.controls[0]))|| (counter>7200) || (game.pl[0].state==ship.DESTR))
            {
                //destroy_course();
                game.setScreen(new TitleScreen(game));
                dispose();
            };
        }

        game.ctr.renderButtonLayout(game.shapeRenderer);

    }

    boolean all_finished()
    {
        boolean a=true;
        int i;

        for(i=0; i<game.nhumans; i++)
            if(((game.pl[i].raceover) || (game.pl[i].state==ship.DESTR)) && (a))
        a=true; else a=false;
        return a;
    }

    void show_position(SpriteBatch batch, int i, int x, int y)
    {

        int p;
        // Position indicator
        p=game.pl[i].pos-1;
        game.posnumber.render2d(batch,(p%4)*16,64-(16*((int)(p/4)+1)),((p%4)+1)*16,64-(16*(int)(p/4)),x,y,x+64,y+64,1);
        p=10;
        game.posnumber.render2d(batch,32,16,48,32,x+50,y,x+114,y+64,1);
        p=game.nplayers-1;
        game.posnumber.render2d(batch,(p%4)*16,64-(16*((int)(p/4)+1)),((p%4)+1)*16,64-(16*(int)(p/4)),x+105,y,x+153,y+48,1);


    }
    void show_speed(SpriteBatch batch, int sp, int x, int y)
    {

        int j,p;
        String v;
        // Speed counter
        v = ""+sp;
        if(sp < 1000) v = " "+v;
        if(sp < 100) v = " "+v;
        if(sp < 10) v = " "+v;

        for(j=0; j<4; j++){
            p=(int)(v.charAt(j)-'0');
            if(v.charAt(j)!=' ') game.speed.render2d(batch, (p%4)*16,64-(16*((int)(p/4)+1)),((p%4)+1)*16,64-(16*(int)(p/4)),x+48*j,y,x+64+48*j,y+64,1);
        };
        game.kmh.render2d(batch, 0,0,128,32,x+200,y+10,x+328,y+42,1);

    }
    void show_power(SpriteBatch batch, int i, int x, int y, int size)
    {
        float c1=1.0f,c2=1.0f,c3=1.0f;
        int dur;

        // Energy bar & recovery
        game.recover.render2d(batch,0,48,128,64,x+60,y+100,x+60+128,y+116,1);
        game.recover.render2d(batch,0,32,24*game.pl[i].power,48,x+60,y+100,x+60+24*game.pl[i].power,y+116,1);
        if((game.pl[i].hypermode>0) && ((int)(counter/10)%2==0))
            game.recover.render2d(batch,0,16,128,32,x+60,y+100,x+188,y+116,1);

        dur=(int)(game.pl[i].energy/4.0f); if(dur==0) dur=1;

        if(game.pl[i].hypermode>0){
            c1=HYPER_COL[0]; c2=HYPER_COL[1]; c3=HYPER_COL[2];
        }else if((game.pl[i].energy<ship.MAXENERGY/4) && (counter%dur<(int)(dur/3.0))){
            c1=DAMAGED_COL[0]; c2=DAMAGED_COL[1]; c3=DAMAGED_COL[2];
        }else{
            c1=1.0f; c2=1.0f; c3=1.0f;
        };

        if(size==0){
            game.power[0].render2dcolor(batch, 0,0,256,128,x,y,x+256,y+128,1,c1,c2,c3);
            game.power[1].render2dcolor(batch,0,0,
                75+((float) (116 * game.pl[i].energy) /ship.MAXENERGY),
                128,x,y,
                (int)(x+75+((float)(116*game.pl[i].energy)/ship.MAXENERGY)),
                y+128,1,c1,c2,c3);
        }else{
            x-=15; y+=70;
            game.spower[0].render2dcolor(batch,0,0,256,32,x,y,x+256,y+32,1,c1,c2,c3);
            game.spower[1].render2dcolor(batch,0,0,
                75+((float) (116 * game.pl[i].energy) /ship.MAXENERGY),
                32,x,y,
                x+75+(116*game.pl[i].energy/ship.MAXENERGY),y+32,1,c1,c2,c3);

        };

    }
    void show_icon_rank()
    {
        int i,x,y;
        float s=1.0f, c;

        for(i=0; i<game.nplayers; i++){

            if(i<3) s=1.4f-(0.2f*i); else s=1.0f;
            x=HUD_START_X+70; y=300-40*i;
            if(game.pl[game.position[i]].state==ship.DESTR) c=0.1f; else c=1.0f;
            game.mini[game.racing_ship[game.position[i]]].render2dcolor(game.batch, 0,0,128,128,x,y,(int)(x+61*s),(int)(y+46*s),1.0f,c,c,c);
            x=HUD_START_X+30; y=310-40*i;
            game.posnumber.render2d(game.batch, (i%4)*16,64-(16*((int)(i/4)+1)),((i%4)+1)*16,64-(16*(int)(i/4)),x,y,x+32,y+32,1);
        };

    }
    void show_map(int x,int y)
    {
        int i,j,k,l;
        /*

        glMatrixMode (GL_PROJECTION);
        glLoadIdentity();
        glDisable(GL_DEPTH_TEST);
        gluOrtho2D(0, 640, 0, 480);


        glEnable(GL_LINE_SMOOTH);

        glColor3f(1.0,1.0,1.0);

        glLineWidth(3.0);
        glBegin(GL_LINES);
        // Starnting line
        glVertex2i(x-8-((cour->nodes[0].x[1]+cour->nodes[0].x[2])/(MAPSCALE*2.0)),
            y+((cour->nodes[0].z[1]+cour->nodes[0].z[2])/(MAPSCALE*2.0)));
        glVertex2i(x+8-((cour->nodes[0].x[1]+cour->nodes[0].x[2])/(MAPSCALE*2.0)),
            y+((cour->nodes[0].z[1]+cour->nodes[0].z[2])/(MAPSCALE*2.0)));
        glEnd();

        glLineWidth(2.0);
        glBegin(GL_LINE_STRIP);
        // Course
        for(i=0; i<cour->info.nsegments-1; i++)
            glVertex2i(x-((cour->nodes[i].x[1]+cour->nodes[i].x[2])/(MAPSCALE*2.0)),
                y+((cour->nodes[i].z[1]+cour->nodes[i].z[2])/(MAPSCALE*2.0)));

        glVertex2i(x-((cour->nodes[0].x[1]+cour->nodes[0].x[2])/(MAPSCALE*2.0)),
            y+((cour->nodes[0].z[1]+cour->nodes[0].z[2])/(MAPSCALE*2.0)));
        glEnd();

        for(i=nplayers-1; i>=0; i--){

            if(i<nhumans) plcursor[gdata.icons[i]]->render2d(0,0,32,32,x-int((pl[i]->x)/MAPSCALE)-8,y+int((pl[i]->z)/MAPSCALE)-8,x-int((pl[i]->x)/MAPSCALE)+8,y+int((pl[i]->z)/MAPSCALE)+8,1.0);
		else oppico->render2d(0,0,32,32,x-int((pl[i]->x)/MAPSCALE)-8,y+int((pl[i]->z)/MAPSCALE)-8,x-int((pl[i]->x)/MAPSCALE)+8,y+int((pl[i]->z)/MAPSCALE)+8,1.0);
        };
        */

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        game.shapeRenderer.setColor(1,1,1,1);
        // Starting line
        game.shapeRenderer.line(x-8-((game.cour.nodes[0].x[1]+game.cour.nodes[0].x[2])/(MAPSCALE*2.0f)),
            y+((game.cour.nodes[0].z[1]+game.cour.nodes[0].z[2])/(MAPSCALE*2.0f)),
            x+8-((game.cour.nodes[0].x[1]+game.cour.nodes[0].x[2])/(MAPSCALE*2.0f)),
            y+((game.cour.nodes[0].z[1]+game.cour.nodes[0].z[2])/(MAPSCALE*2.0f)));

        for(i=0; i< game.cour.info.nsegments; i++)
            game.shapeRenderer.line(x-((game.cour.nodes[i].x[1]+game.cour.nodes[i].x[2])/(MAPSCALE*2.0f)),
                                y+((game.cour.nodes[i].z[1]+game.cour.nodes[i].z[2])/(MAPSCALE*2.0f)),
                                x-((game.cour.nodes[(i+1)%game.cour.info.nsegments].x[1]+game.cour.nodes[(i+1)%game.cour.info.nsegments].x[2])/(MAPSCALE*2.0f)),
                                y+((game.cour.nodes[(i+1)%game.cour.info.nsegments].z[1]+game.cour.nodes[(i+1)%game.cour.info.nsegments].z[2])/(MAPSCALE*2.0f)));
        game.shapeRenderer.end();

        game.batch.begin();
        for(i=game.nplayers-1; i>=0; i--){
            if(i<game.nhumans) game.plcursor[game.gdata.icons[i]].render2d(game.batch, 0,0,32,32,x-(int)((game.pl[i].x)/MAPSCALE)-8,y+(int)((game.pl[i].z)/MAPSCALE)-8,x-(int)((game.pl[i].x)/MAPSCALE)+8,y+(int)((game.pl[i].z)/MAPSCALE)+8,1.0f);
		else game.oppico.render2d(game.batch, 0,0,32,32,x-(int)((game.pl[i].x)/MAPSCALE)-8,y+(int)((game.pl[i].z)/MAPSCALE)-8,x-(int)((game.pl[i].x)/MAPSCALE)+8,y+(int)((game.pl[i].z)/MAPSCALE)+8,1.0f);
        };
        game.batch.end();
    }

    void show_3d_sprite(PerspectiveCamera cam, texture tex, float u1, float v1, float u2, float v2, float x, float y, float z, float r, float g, float b, float sx, float sy, float a)
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

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        float aspect = viewPortAspectRatio;

        v1 = 1.f - v1; v2 = 1.f - v2;

        billboardShader.begin();
        billboardShader.setUniformi("u_texture", 0);
        tex.gdxTexture.bind(0);
        billboardShader.setUniformMatrix("u_projTrans", cam.combined);
        billboardShader.setUniformf("u_billboardPos", x, y, z); // Posición del sprite
        billboardShader.setUniformf("u_sizeX", sx * aspect);            // Tamaño del sprite
        billboardShader.setUniformf("u_sizeY", sy);            // Tamaño del sprite
        billboardShader.setUniformf("u_color", r, g, b, a);
        billboardShader.setUniformf("u_uvOffset", u1, v1);
        billboardShader.setUniformf("u_uvSize", u2 - u1, v2 - v1);


        billboard.render(billboardShader, GL20.GL_TRIANGLES);
        billboardShader.end();

    }

    public Mesh createSphereMesh(float radius, int numSegments)
    {
        float[] vertices = new float[(numSegments+1) * (numSegments) * 6];
        short[] indices = new short[(numSegments+1) * numSegments * 2 * 3];

        float angleStep = (float) (2*Math.PI / numSegments);
        float verticalAngleStep = (float) (Math.PI / numSegments);

        for(int j = 0; j <=numSegments; j++)
            for(int i = 0; i < numSegments; i++)
            {
                float x = (float) (radius * Math.cos(angleStep * i) * Math.abs(Math.sin(verticalAngleStep * j)));
                float y = (float) (radius * Math.cos(verticalAngleStep * j));
                float z = (float) (radius * Math.sin(angleStep * i) * Math.abs(Math.sin(verticalAngleStep * j)));

                vertices[(numSegments*6*j) + 6*i + 0] = x;
                vertices[(numSegments*6*j) + 6*i + 1] = y;
                vertices[(numSegments*6*j) + 6*i + 2] = z;

                Vector3 normal = new Vector3(x,y,z);
                normal = normal.nor();

                vertices[(numSegments*6*j) + 6*i + 3] = normal.x;
                vertices[(numSegments*6*j) + 6*i + 4] = normal.y;
                vertices[(numSegments*6*j) + 6*i + 5] = normal.z;
            }

        for(int j = 0; j < numSegments; j++)
            for(int i = 0; i < numSegments; i++)
            {
                indices[(j*numSegments+i)*6 + 0] = (short) ((j*numSegments) + i);
                indices[(j*numSegments+i)*6 + 1] = (short) ((j*numSegments) + (i + 1)%numSegments);
                indices[(j*numSegments+i)*6 + 2] = (short) (((j+1)*numSegments) + i);

                indices[(j*numSegments+i)*6 + 3] = (short) ((j*numSegments) + (i + 1)%numSegments);
                indices[(j*numSegments+i)*6 + 4] = (short) (((j+1)*numSegments) + (i + 1)%numSegments);
                indices[(j*numSegments+i)*6 + 5] = (short) (((j+1)*numSegments) + i);
            }


        Mesh m = new Mesh(true, vertices.length / 6, indices.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal")
        );

        m.setVertices(vertices);
        m.setIndices(indices);

        return m;
    }

    public void createSkyDome(float radius, int numSegments)
    {
        int numSegmentsVert = numSegments / 2;
        float[] vertices = new float[(numSegmentsVert+1) * (numSegments+1) * 8];
        short[] indices = new short[(numSegmentsVert+1) * (numSegments+1) * 2 * 3];

        float angleStep = (float) (2*Math.PI / numSegments);
        float verticalAngleStep = (float) (Math.PI / numSegments);

        for(int j = 0; j <=numSegmentsVert; j++)
            for(int i = 0; i < numSegments+1; i++)
            {
                float x = (float) (radius * Math.cos(angleStep * i) * Math.abs(Math.sin(verticalAngleStep * j)));
                float y = (float) (radius * Math.cos(verticalAngleStep * j));
                float z = (float) (radius * Math.sin(angleStep * i) * Math.abs(Math.sin(verticalAngleStep * j)));

                vertices[(numSegments*8*j) + 8*i + 0] = x;
                vertices[(numSegments*8*j) + 8*i + 1] = y;
                vertices[(numSegments*8*j) + 8*i + 2] = z;

                // Normal
                Vector3 normal = new Vector3(-x,-y,-z);
                normal = normal.nor();

                vertices[(numSegments*8*j) + 8*i + 3] = normal.x;
                vertices[(numSegments*8*j) + 8*i + 4] = normal.y;
                vertices[(numSegments*8*j) + 8*i + 5] = normal.z;

                // Tex coords
                vertices[(numSegments*8*j) + 8*i + 6] = (float) (0.5f + Math.atan2(z/(float)radius, x/(float)radius) / (2.0f * Math.PI));
                vertices[(numSegments*8*j) + 8*i + 7] = y/(float)radius * 0.5f + 0.5f;
            }

        for(int j = 0; j < numSegmentsVert; j++)
            for(int i = 0; i < numSegments; i++)
            {
                // First triangle
                indices[(j*numSegments+i)*6 + 0] = (short) ((j*numSegments) + i);
                indices[(j*numSegments+i)*6 + 2] = (short) ((j*numSegments) + (i + 1));
                indices[(j*numSegments+i)*6 + 1] = (short) (((j+1)*numSegments) + i);

                // Second triangle
                indices[(j*numSegments+i)*6 + 3] = (short) ((j*numSegments) + (i + 1));
                indices[(j*numSegments+i)*6 + 5] = (short) (((j+1)*numSegments) + (i + 1));
                indices[(j*numSegments+i)*6 + 4] = (short) (((j+1)*numSegments) + i);
            }


        Mesh m = new Mesh(true, vertices.length / 8, indices.length,
            new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
            new VertexAttribute(VertexAttributes.Usage.Normal, 3, "a_normal"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoord")
        );

        m.setVertices(vertices);
        m.setIndices(indices);

        skyDome = m;
    }

    double getFractalNoise(double x, double y, int octaves, double baseFreq, double persistence, double lacunarity) {
        double total = 0.0;
        double amplitude = 1.0;
        double frequency = baseFreq;
        double max = 0.0;

        for (int i = 0; i < octaves; i++) {
            total += OpenSimplex2S.noise2(12345, x * frequency, y * frequency) * amplitude;
            max += amplitude;

            amplitude *= persistence;   // 0.5 por ejemplo
            frequency *= lacunarity;   // 2.0 por ejemplo
        }

        return total / max; // Normalizado a [-1,1]
    }

    void createCloudTexture()
    {
        int width = 1024;
        int height = 512;
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);

       // OpenSimplex2S noise = new OpenSimplex2S(); // semilla

        float scale = 0.01f;
        float scale2 = 0.01f;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //float value1 = (float) OpenSimplex2S.noise2(12345, x * scale, y * scale);
                //float value2 = (float) OpenSimplex2S.noise2(67890, x * scale2, y * scale2);
                //float value = (value + 1f) * 0.5f;  // normalizar de [-1,1] a [0,1]
                //float value = (value1 + value2) * 0.5f;
                //if(value < 0) value = 0f;
                //value = value * value;
                //float density = (value1 + 1.0f) / 2f;
                //density = (float) Math.pow(density, 4.0);
                float value = (float) getFractalNoise(x, y, 6, 0.01, 0.5, 2.0);
                //value = (value + 1f) * 0.5f;  // normalizar de [-1,1] a [0,1]

                if (value < 0f) value = 0f;
                value = 2 * value;
                if (value > 1f) value = 1f;

                int alpha = (int)(value * 255);
                pixmap.setColor(1f, 1f, 1f, value);
                pixmap.drawPixel(x, y);
            }
        }

        cloudTexture = new Texture(pixmap);
        cloudTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        cloudTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
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

        game.stop_music();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL20.GL_CULL_FACE);
        Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);

        for(int i = 0; i < game.nplayers; i++)
        {
            game.pl[i].dispose();
        }
    }
}
