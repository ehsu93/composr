package kenthacks.eric.composr;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


import android.media.MediaPlayer;
import android.content.Context;

/**
 * Created by Meaghan on 10/25/14.
 */
public class Metronome {

    int tempo;
    MediaPlayer m;
    Boolean sound = false;
    Timer timer = new Timer();
    TimerTask task;

    // metronome must be initialized to work
    public Metronome(int tempo, Context ctx){
        this.tempo = tempo;
        this.m = MediaPlayer.create(ctx, R.raw.tick);
    }

    // play the ticking sound
    void playTick(){
        m.start();
    }
    void toggleMetronome() {

    }
}
