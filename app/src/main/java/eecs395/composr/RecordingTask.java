package eecs395.composr;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.util.Log;

public class RecordingTask {

    // given as user inputs
    int bpm;
    int beatsPerMeasure;    // top of time signature
    int beatDuration;       // bottom of time signature
    int samplesPerBeat;

    // initial values
    boolean countdownComplete = false;
    boolean recording = false;

    SampleBeatPair previousPosition;
    SampleBeatPair currentPosition;

    String previousNote;
    int sameNoteStreak;

    // instances of other classes in the application
    Metronome metronome;
    FrequencyAnalyzer fr;
    RecordedFrequencies rf;

    // created by toggle
    Timer timer;
    TimerTask task;
    public String pattern = "";
    public String displayPattern = "";

    int start;
    int end;
    int count;

    public RecordingTask(int tempo, int beats){
        this.bpm = tempo;
        this.beatsPerMeasure = beats;

        // determines the accuracy of the application
        this.samplesPerBeat = 4;

        this.metronome = new Metronome();
        this.fr = new FrequencyAnalyzer();
        this.rf = new RecordedFrequencies();

        this.currentPosition = new SampleBeatPair(0, 0, 0);
        this.start = 0;
        this.previousNote = "R";
    }


    public void addFreq(Float freq){
        count++;
        rf.addFrequency(freq);

    }

    public TimerTask createTimerTask(){
        task = new TimerTask(){

            @Override
            public void run() {

                previousPosition = new SampleBeatPair(currentPosition);
                incrementPosition();

                if(countdownComplete){
                    end = count;
                    Log.i("samplecount", Integer.toString(count));

                    float median = rf.getMedian(start, end);

                    Log.i("median", Float.toString(median));
                    
                    String note = fr.getNoteFromFreq(median);

                    if (note.equals(previousNote)){
                        Log.i("recording-task", "same note...");
                        sameNoteStreak++;
                    } else {
                        Log.i("recording-task",
                                "\n\tend of note: " + note +
                                "\n\tprevious note: " +
                                "\n\tsamples: " + Integer.toString(sameNoteStreak));
                        updatePattern(note, sameNoteStreak);
                        //updateDisplayPattern(note);

                        // reset same note streak
                        sameNoteStreak = 0;

                        // update previousFreq
                        previousNote = note;
                    }
                    start = end;
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
        Log.i("current-pattern:", pattern);
    }

    /**
     * Update the pattern with a character but no duration (for barlines)
     * @param note the character to update the pattern with
     */
    public void updatePattern(String note){
        updatePattern(note, 0);
    }

    public String getDurationString(int duration){

        // TODO: do this better
        switch(duration){
            case 1:
                return "s";
            case 2:
                return "i";
            case 3:
                return "i.";
            case 4:
                return "q";
            case 5:
                return "qs"; // TODO find out what 5 samples should be
            case 6:
                return "q.";
            case 7:
                return "q.s"; // TODO ... 7 samples
            case 8:
                return "h";
            case 9:
                return "hs"; // TODO ... 9 samples
            case 10:
                return "hi"; // TODO... 10 samples
            case 11:
                return "hi."; // TODO... 11
            case 12:
                return "h.";
            case 13:
                return "hqs"; // TODO... 13
            case 14:
                return "hq."; // TODO... 14
            case 15:
                return "hq.s"; // TODO... 15
            case 16:
                return "w";
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

    public void updateTimeSignature(int beatsPerMeasure, int beatDuration){
        this.beatsPerMeasure = beatsPerMeasure;
        this.beatDuration = beatDuration;


    }

}
