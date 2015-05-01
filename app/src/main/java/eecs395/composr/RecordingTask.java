package eecs395.composr;

import java.util.Date;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import eecs395.composr.draw.Drawer;
import eecs395.composr.io.SoundPlayer;
import eecs395.composr.musicUtils.TimeSignature;
import eecs395.composr.process.FrequencyAnalyzer;
import eecs395.composr.process.RecordedFrequencies;
import eecs395.composr.process.RecordedNote;
import eecs395.composr.process.SampleBeatPair;

public class RecordingTask {

    // given as user inputs
    int bpm;
    TimeSignature timeSignature;
    int samplesPerBeat;


    // initial values
    boolean countdownComplete = false;
    boolean recording = false;

    SampleBeatPair previousPosition;
    SampleBeatPair currentPosition;

    String previousPitch;
    int sameNoteStreak = 0;

    // instances of other classes in the application
    FrequencyAnalyzer fr;
    RecordedFrequencies rf;
    Drawer d;

    // created by toggle
    Timer timer;
    TimerTask task;
    public String pattern = "";
    public String displayPattern = "";

    int start;
    int end;
    int count;

    private static LinkedList<RecordedNote> recordedNotes;

    public RecordingTask(int tempo, TimeSignature timeSignature, Drawer d){
        this.bpm = tempo;
        this.timeSignature = timeSignature;
        this.d = d;

        // determines the accuracy of the application
        this.samplesPerBeat = 4;

        this.fr = new FrequencyAnalyzer();
        this.rf = new RecordedFrequencies();

        this.currentPosition = new SampleBeatPair(0, 0, 0);
        this.start = 0;
        this.previousPitch = "R";

        this.recordedNotes = new LinkedList<>();
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

                    float median = rf.getMedian(start, end);
                    
                    String pitch = fr.getNoteFromFreq(median);

                    if (pitch.equals(previousPitch)){
                        sameNoteStreak++;
                    } else {
                        RecordedNote note = new RecordedNote(pitch, getDurationString(sameNoteStreak));

                        sameNoteStreak = 0;

                        previousPitch = pitch;
                    }
                    start = end;
                }

                else if (currentPosition.isNewBeat()){
                    int countDown = timeSignature.getTop() - currentPosition.getBeat();
                    displayPattern = ""+ countDown;

                    if(currentPosition.isNewMeasure()) {
                        displayPattern = "";
                        countdownComplete = true;
                    }
                }

                if (currentPosition.isNewBeat()){
                    SoundPlayer.Metronome.playTick();
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
        recordedNotes.add(new RecordedNote(note, durationString));
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

    public void updateDisplay(RecordedNote recordedNote){
        if (!recordedNote.getPitch().equals("R")){
            // TODO find a different way to represent different length rests
            d.drawRest(recordedNote.getDuration());
        } else {
            d.drawNote(recordedNote);
        }

        if (displayPattern.length() > 20) {
            truncateDisplayPattern();
        }
    }

    public void updateDisplayPattern(String note){
        //updateDisplay(note, 1);
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
        while(currentPosition.getBeat() < timeSignature.getTop()) {
            pattern += "R ";
            currentPosition.incrementBeat();
        }
    }

    public void incrementPosition(){
        currentPosition.incrememntSample();

        if (currentPosition.getSample() == samplesPerBeat){
            currentPosition.incrementBeat();
        }

        if (currentPosition.getBeat() == timeSignature.getTop()){
            currentPosition.incrementMeasure();
            processEndOfMeasure();
        }
    }

    public void setTimeSignature(TimeSignature timeSignature){
        this.timeSignature = timeSignature;
    }

    public void updateTimeSignature(int i){
        setTimeSignature(TimeSignature.getTimeSignatureFromIndex(i));
    }

    public void updateTempo(int i){
        bpm = i;
    }

}
