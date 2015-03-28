package eecs395.composr;

import android.media.MediaPlayer;
import android.content.Context;

import eecs395.proj.composr.R;

public class Metronome {

    MediaPlayer m;

    /**
     * Constructor for metronome object
     */
    public Metronome(){
        this.m = MediaPlayer.create(MyActivity.getContext(), R.raw.tick);
    }

    /**
     * Plays the ticking sound
     */
    void playTick(){
        m.start();
    }
}
