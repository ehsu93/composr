package kenthacks.eric.composr;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import android.content.Context;

/**
 * Created by root on 10/25/14.
 */
public class RecordingTask {

    // set in the constructor
    int tempo;
    Metronome metronome;
    Context ctx;
    FrequencyRecorder FREQ;

    // set elsewhere
    Timer timer;
    TimerTask task;
    boolean recording = false;

    public RecordingTask(int tempo, Context ctx){
        this.tempo = tempo;
        this.metronome = new Metronome(tempo, ctx);
        this.ctx = ctx;
        this.FREQ = new FrequencyRecorder(ctx);
    }

    TimerTask createTimerTask(){
        task = new TimerTask(){
            @Override
            public void run() {
                float[] last_freq = FREQ.fArray.frequencies;
                Log.i("tick", "tick");
                float median = FREQ.fArray.getMedian(last_freq);
                FREQ.fArray.reset();
                Log.i("MEDIAN FREQ", "------------" + String.valueOf(median) + "--------------");
                metronome.playTick();
            }
        };
        return task;
    }

    void toggle(){
        this.recording = !this.recording;
        Log.i("rt_state", String.valueOf(this.recording));
        if (this.recording){
            this.timer = new Timer();
            this.task = createTimerTask();
            this.timer.schedule(this.task, new Date(), 60000/tempo);
        }
        else {
            timer.cancel();
            timer.purge();
        }
    }

}
