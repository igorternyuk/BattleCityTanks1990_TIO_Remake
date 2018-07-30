package com.igorternyuk.tanks.audio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author igor
 */
public class Audio {

    private String path;
    private Clip clip;

    public Audio(String path) {
        this.path = path;
        try {
            InputStream bufferedIn = new BufferedInputStream(getClass().
                    getResourceAsStream(path));
            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
            this.clip = AudioSystem.getClip();
            this.clip.open(ais);

        } catch (UnsupportedAudioFileException | IOException
                | LineUnavailableException ex) {
            Logger.getLogger(Audio.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public boolean isPlaying() {
        if (this.clip == null) {
            return false;
        }
        return this.clip.isRunning();
    }

    public String getPath() {
        return this.path;
    }

    public void play() {
        if (this.clip == null) {
            return;
        }
        stop();
        this.clip.setFramePosition(0);
        clip.start();
    }

    public void loop() {
        if (this.clip == null || this.clip.isRunning()) {
            return;
        }
        stop();
        this.clip.setFramePosition(0);
        this.clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    public void stop() {
        if (this.clip.isRunning()) {
            this.clip.stop();
        }
    }

    public void disposeSound() {
        stop();
        this.clip.close();
    }

}
