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
    Boolean sound = true;
    Timer timer = new Timer();
    TimerTask task;

    public Metronome(int tempo, Context ctx){
        this.tempo = tempo;
        this.m = MediaPlayer.create(ctx, R.raw.tick);
        this.startMetronome();
    }

    public void createTimerTask(){
        this.task = new TimerTask(){
            @Override
            public void run() {
                m.start();
            }
        };
    }

    public void startMetronome(){
        this.timer = new Timer();
        createTimerTask();
        this.timer.schedule(this.task, new Date(), 60000/this.tempo);
    }

    public void cancelMetronome(){
        this.timer.cancel();
        this.timer.purge();
    }

    public void toggleMetronome() {
        this.sound = !this.sound;
        if (this.sound) {
            startMetronome();
        }
        else {
            cancelMetronome();
        }
    }


}
