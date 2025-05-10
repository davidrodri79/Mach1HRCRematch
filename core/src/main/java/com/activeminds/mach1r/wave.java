package com.activeminds.mach1r;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class wave {

    public static final int DSBFREQUENCY_ORIGINAL = 10000;
    public static final int DSBFREQUENCY_MIN = 100;
    public static final int DSBFREQUENCY_MAX = 100000;
    public static final int DSBVOLUME_MIN = -10000;
    public static final int DSBVOLUME_MAX = 0;

    public static boolean WAVE_ENABLE=true;

    Sound sound;
    long soundId = -1;

    wave()
    {
    }

    /*wave(char *szWaveFile)
    {

    }*/

    void load(String waveFile)
    {
        sound = Gdx.audio.newSound(Gdx.files.internal(waveFile));

    }

    void playonce()
    {
        if (WAVE_ENABLE)
            soundId = sound.play(1f);

    }

    void playlooped()
    {
        if (WAVE_ENABLE)
            soundId = sound.loop(1f);

    }


    void stop()
    {
        if (WAVE_ENABLE)
            if(soundId >= 0) {
                sound.stop(soundId);
                soundId = -1;
            }

    }

    void setpan(int pan)
    {
        if(soundId >= 0)
            sound.setPan(soundId, pan, 1f);
    }


    void setvolume(int volume)
    {
        if(soundId >= 0)
            sound.setVolume(soundId, (volume + 10000)/10000f);
    }

    void setfreq(int freq)
    {
        if(soundId >= 0)
        sound.setPitch(soundId, freq/10000f);
    }
}
