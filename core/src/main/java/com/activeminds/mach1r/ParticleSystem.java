package com.activeminds.mach1r;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class ParticleSystem {

    static class Particle {

        Vector3 position, speed, color;
        Vector2 size;
        texture sprite;
        int timeLeft, fadeOut, fadeIn, counter;
        boolean active;
        boolean sinMove;
        float sizeIncrement, sinMovePhase, sinSize;


        Particle(Vector3 position, Vector3 speed, Vector3 color, Vector2 size, int timeLeft, Texture spr)
        {
            this.position = position;
            this.speed = speed;
            this.color = color;
            this.size = size;
            this.sprite = new texture(spr);
            this.timeLeft = timeLeft;
            active = true;
            sinMove = false; sinMovePhase = 0f; sinSize = 0.25f;
            counter = 0; fadeOut = 140; fadeIn = 0;
            sizeIncrement = 0f;
        }

        void update()
        {
            position.add(speed);

            size.x += sizeIncrement / 70f;
            size.y += sizeIncrement / 70f;

            if(sinMove) {
                position.x += sinSize*(float) Math.sin((timeLeft + sinMovePhase)/ 20);
                position.z += sinSize*(float) Math.cos((timeLeft + sinMovePhase)/ 20);
            }

            timeLeft--;
            counter++;
            if(timeLeft <= 0)
            {
                active = false;
            }
        }
    }

    ArrayList<Particle> particles;


    ParticleSystem()
    {
        particles = new ArrayList<>();
    }
    void addParticle(Particle p)
    {
        particles.add(p);
    }

    void update()
    {
        for(int i = 0; i < particles.size(); i++)
        {
            Particle p = particles.get(i);
            p.update();
            if(!p.active) {
                particles.remove(p);
                i--;
            }
        }
    }

    void render(PerspectiveCamera cam, RaceScreen raceScreen)
    {
        for(Particle p : particles)
        {
            float alpha = 1.0f;
            if(p.timeLeft < p.fadeOut)
                alpha = (float) p.timeLeft / p.fadeOut;
            if(p.counter < p.fadeIn)
                alpha = (float) p.counter / p.fadeIn;

            raceScreen.show_3d_sprite(cam, p.sprite, 0,0, 1, 1,
                p.position.x, p.position.y, p.position.z,
                p.color.x, p.color.y, p.color.z,
                p.size.x, p.size.y,
                alpha);
        }
    }
}
