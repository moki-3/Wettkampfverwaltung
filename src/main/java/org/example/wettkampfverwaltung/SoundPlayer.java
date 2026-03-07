package org.example.wettkampfverwaltung;
import javafx.scene.media.AudioClip;


public class SoundPlayer {
    public void playEndFight(){
        AudioClip clip = new AudioClip(getClass().getResource("/sounds/sound.m4a").toExternalForm());
        clip.play();
        System.out.println("Sound abgespielt theoretisch");
    }

//    public void playEndFight(){
//        try {
//            var url = getClass().getResource("/sounds/sound.m4a");
//            System.out.println("URL: " + url);
//
//            AudioClip clip = new AudioClip(url.toExternalForm());
//            System.out.println("Clip erstellt: " + clip);
//            System.out.println("Clip valid: " + clip.isPlaying());
//
//            clip.play();
//            System.out.println("play() aufgerufen");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
