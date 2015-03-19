package kenthacks.eric.composr;

import android.media.MediaPlayer;
import android.content.Context;

public class Metronome {

    MediaPlayer m;

    /**
     * Constructor for metronome object
     *
     * @param ctx Context, required to play sound
     */
    public Metronome(Context ctx){
        this.m = MediaPlayer.create(ctx, R.raw.tick);
    }

    /**
     * Plays the ticking sound
     */
    void playTick(){
        m.start();
    }
}
