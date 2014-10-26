package kenthacks.eric.composr;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by root on 10/25/14.
 */
public class RecordingTask {

    // set in the constructor
    int tempo;
    Metronome metronome;
    Context ctx;
    FrequencyRecorder fr;
    RecordedFrequencies rf;

    // set elsewhere
    Timer timer;
    TimerTask task;
    boolean recording = false;
    public String pattern = "";
    public String displayPattern = "";

    public RecordingTask(int tempo, Context ctx){
        this.tempo = tempo;
        this.metronome = new Metronome(tempo, ctx);
        this.ctx = ctx;
        this.fr = new FrequencyRecorder();
        fr.initializeMidiValues();
        rf = new RecordedFrequencies();
    }

    TimerTask createTimerTask(){
        task = new TimerTask(){
            @Override
            public void run() {
                float median = rf.getMedian();
                String note = fr.getNoteFromFrequency(median);
                pattern += note + " ";
                if (note == "R")
                    displayPattern += "- ";
                else {
                    displayPattern += note + " ";
                }
                if (displayPattern.length() > 20)
                    displayPattern = displayPattern.substring(displayPattern.length() - 20, displayPattern.length());

                rf.reset();
                Log.i("MEDIAN FREQ", "------------" + pattern + "--------------");

                metronome.playTick();
            }
        };
        return task;
    }

    void toggle(){
        this.recording = !this.recording;
        Log.i("rt_state", String.valueOf(this.recording));
        if (this.recording){
            pattern = "";
            this.timer = new Timer();
            this.task = createTimerTask();
            this.timer.schedule(this.task, new Date(), 60000/tempo);
        }
        else {
            pattern += "|";
            timer.cancel();
            timer.purge();
        }
    }

}
