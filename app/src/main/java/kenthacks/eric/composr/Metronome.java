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
    Timer timer = new Timer();

    public Metronome(int tempo, Context c){
        this.tempo = tempo;

        final MediaPlayer m = MediaPlayer.create(c, R.raw.tick);

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                m.start();
            }

        };

        timer.schedule(task, new Date(), 60000/tempo);
    }


}
