package kenthacks.eric.composr;

import java.util.Date;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

import android.content.Context;

public class RecordingTask {

    // given as user inputs
    int bpm;
    int beatsPerMeasure;
    int samplesPerBeat;

    // sent in from MyActivity
    Context ctx;

    // initial values
    boolean countdownComplete = false;
    boolean recording = false;

    SampleBeatPair previousPosition;
    SampleBeatPair currentPosition;

    Hashtable<SampleBeatPair, RecordedFrequencies> frequencies = new Hashtable<>();

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

    int count;

    public RecordingTask(int tempo, int beats, Context ctx){
        this.bpm = tempo;
        this.beatsPerMeasure = beats;
        this.ctx = ctx;

        // determines the accuracy of the application
        this.samplesPerBeat = 4;

        this.metronome = new Metronome(ctx);
        this.fr = new FrequencyRecorder(ctx);
        this.rf = new RecordedFrequencies();

        this.currentPosition = new SampleBeatPair(0, 0, 0);
    }

    public void addFreq(Float freq){
        count++;
        /*
        RecordedFrequencies recordedFrequencies = frequencies.get(currentPosition);

        if (recordedFrequencies == null) {
            frequencies.put(currentPosition, new RecordedFrequencies());
        }
        frequencies.get(currentPosition).addFrequency(freq);*/
    }

    public TimerTask createTimerTask(){
        task = new TimerTask(){

            @Override
            public void run() {

                previousPosition = new SampleBeatPair(currentPosition);
                incrementPosition();

                if(countdownComplete){
                    Log.i("count", Integer.toString(count));
                    /*
                    RecordedFrequencies previousFrequencies = frequencies.get(previousPosition);
                    float median = previousFrequencies.getMedian();
                    String note = fr.getNoteFromFreq(median);

                    if (median == previousFreq){
                        sameNoteStreak++;
                    } else {
                        updatePattern(note, sameNoteStreak);
                        updateDisplayPattern(note);

                        // reset same note streak
                        sameNoteStreak = 0;

                        // update previousFreq
                        previousFreq = median;
                    }*/
                }

                else if (currentPosition.isNewBeat()){
                    int countDown = beatsPerMeasure - currentPosition.getBeat();
                    displayPattern = ""+ countDown;

                    if(currentPosition.isNewMeasure()) {
                        displayPattern = "";
                        countdownComplete = true;
                    }
                }

                if (currentPosition.isNewBeat()){
                    metronome.playTick();
                }

            }
        };
        return task;
    }

    public void toggleRecordingTask(){
        this.recording = !this.recording;

        if (this.recording){
            resetPatterns();

            this.timer = new Timer();
            this.task = createTimerTask();

            this.currentPosition = new SampleBeatPair();
            this.timer.schedule(this.task, new Date(), (60000/bpm)/samplesPerBeat);
        }

        else {
            fillCurrentMeasureWithRests();

            countdownComplete = false;
            timer.cancel();
            timer.purge();
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
            int samplePortionOfMeasure = samplesPerBeat / beatsPerMeasure;
            int x = duration * samplePortionOfMeasure / beatsPerMeasure;
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

    public void resetPatterns(){
        resetPattern();
        resetDisplayPattern();
    }

    public void resetPattern(){
        pattern = "";
    }

    public void resetDisplayPattern(){
        displayPattern = "";
    }

    public void processEndOfMeasure(){
        updatePattern("|");
        updateDisplayPattern("|");
    }

    public void fillCurrentMeasureWithRests(){
        //TODO handle parts of a beat
        while(currentPosition.getBeat() < beatsPerMeasure) {
            pattern += "R ";
            currentPosition.incrementBeat();
        }
    }

    public void incrementPosition(){
        currentPosition.incrememntSample();

        if (currentPosition.getSample() == samplesPerBeat){
            currentPosition.incrementBeat();
        }

        if (currentPosition.getBeat() == beatsPerMeasure){
            currentPosition.incrementMeasure();
            processEndOfMeasure();
        }
    }

}
