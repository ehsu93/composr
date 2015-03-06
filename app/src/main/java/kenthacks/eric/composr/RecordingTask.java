package kenthacks.eric.composr;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;
import java.util.LinkedList;

import android.content.Context;

public class RecordingTask {

    // given as user inputs
    int bpm;
    int beats;
    int samplesPerBeat;

    // sent in from MyActivity
    Context ctx;

    // initial values
    int currentBeat = 0;
    int currentSampleNumber = 0;
    boolean countdownComplete = false;
    boolean recording = false;

    Float previousFreq;
    int sameNoteStreak;

    // instances of other classes in the application
    Metronome metronome;
    FrequencyRecorder fr;
    RecordedFrequencies rf;

    // created by toggle
    Timer timer;
    TimerTask task;
    public String pattern = "";
    public String displayPattern = "";

    public RecordingTask(int tempo, int beats, Context ctx){
        this.bpm = tempo;
        this.beats = beats;
        this.ctx = ctx;

        this.samplesPerBeat = 4;

        this.metronome = new Metronome(ctx);
        this.fr = new FrequencyRecorder(ctx);
        this.rf = new RecordedFrequencies();
    }

    public void addFreq(Float freq){
        rf.addFrequency(freq);
    }

    public TimerTask createTimerTask(){
        task = new TimerTask(){

            @Override
            public void run() {
                RecordedFrequencies last = new RecordedFrequencies(new LinkedList<Float>(rf.frequencies));
                rf.reset();

                currentSampleNumber++;
                if (currentSampleNumber == samplesPerBeat) {

                    if (!countdownComplete){
                        int countDown = 4 - currentBeat;
                        displayPattern = ""+ countDown;
                        if(currentBeat == beats) {
                            displayPattern = "";
                            countdownComplete = true;
                            currentBeat = 0;
                        }
                    }

                    currentBeat++;
                    currentSampleNumber = 0;

                    metronome.playTick();
                }

                if (countdownComplete) {
                    float median = last.getMedian();
                    String note = fr.getNoteFromFreq(median);

                    if (median == previousFreq){
                        sameNoteStreak++;
                    } else {


                        updatePattern(note);
                        updateDisplayPattern(note);

                        // reset same note streak
                        sameNoteStreak = 0;

                        // update previousFreq
                        previousFreq = median;
                    }

                    if (currentSampleNumber == samplesPerBeat) {
                        if (currentBeat == beats) {
                            endMeasure();
                        }
                    }
                }
            }
        };
        return task;
    }

    public void toggleRecordingTask(){
        this.recording = !this.recording;

        Log.i("rt_state", String.valueOf(this.recording));

        if (this.recording){
            pattern = "";
            displayPattern = "";
            this.timer = new Timer();
            this.task = createTimerTask();
            this.timer.schedule(this.task, new Date(), (60000/bpm)/samplesPerBeat);
        }

        else {
            while(currentBeat < beats) {
                pattern += "R ";
                currentBeat++;
            }
            countdownComplete = false;
            timer.cancel();
            timer.purge();
            currentBeat = 0;
        }
    }

    public void updatePattern(String note, int duration){
        String durationString = getDurationString(duration);
        pattern += note + durationString + " ";
    }

    public void updatePattern(String note){
        updatePattern(note, 0);
    }

    public String getDurationString(int duration){

        if (duration != 0){
            int samplePortionOfMeasure = samplesPerBeat / beats;
            int x = duration * samplePortionOfMeasure / beats;
            return Integer.toString(x);
        }

        return "";

    }

    public void updateDisplayPattern(String note, String duration){
        if (note == "R"){
            // TODO find a different way to represent different length rests
            displayPattern += "- ";
        } else {
            displayPattern += note + duration + " ";
        }

        if (displayPattern.length() > 20) {
            truncateDisplayPattern();
        }
    }

    public void updateDisplayPattern(String note){
        updateDisplayPattern(note, "");
    }

    public void truncateDisplayPattern(){
        displayPattern = displayPattern.substring(displayPattern.length() - 20, displayPattern.length());
    }

    public void endMeasure(){
        updatePattern("|");
        updateDisplayPattern("|");
        currentBeat = 0;
    }

}
