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

    Timer timer;
    TimerTask task;
    boolean recording;
    Metronome m;
    Context ctx;
    FrequencyRecorder FREQ;
    FrequencyRecorder.FrequencyArray ARR;

    public RecordingTask(Context ctx){
        this.timer = new Timer();
        this.task = createTimerTask();
        this.m = new Metronome(60, ctx);
        this.ctx = ctx;
    }

    TimerTask createTimerTask(){
        task = new TimerTask(){
            @Override
            public void run() {
                float last_freq = ARR.reset();
                Log.i("MEDIAN FREQ", "------------" + String.valueOf(last_freq) + "--------------");
                m.playTick();
            }
        };
        return task;
    }

    void toggle(){
        this.recording = !this.recording;
    }

}
