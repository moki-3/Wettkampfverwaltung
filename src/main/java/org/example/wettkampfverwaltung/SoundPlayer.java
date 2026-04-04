package org.example.wettkampfverwaltung;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class SoundPlayer {
    public void playEndFight() {
        try {
            InputStream is = getClass().getResourceAsStream("/sounds/sound.wav");
            BufferedInputStream bis = new BufferedInputStream(is);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            System.out.println("Sound abgespielt");

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });

        } catch (Exception e) {
            System.err.println("Sound konnte nicht abgespielt werden: " + e.getMessage());
        }
    }
}