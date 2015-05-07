package eecs395.composr;

import android.util.Log;

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
    private int bpm;
    private TimeSignature timeSignature;

    // hard-coded default value
    private int samplesPerBeat = 4;

    //Flags with initial values:
    private boolean countdownComplete = false;
    private boolean recording = false;
    private boolean drawCurrentNote = false;
    private boolean drawBarline = false;

    private SampleBeatPair currentPosition;
    private RecordedNote currentNote;

    private FrequencyAnalyzer fa;
    private RecordedFrequencies rf;
    private Drawer d;

    private Timer timer;
    private TimerTask task;

    //Indices to keep track of the place in the list of recorded frequencies
    private int start;
    private int end;
    private int count;

    private String status = "Not recording";

    private LinkedList<RecordedNote> recordedNotes;

    boolean redraw = false;

    public RecordingTask(int tempo, TimeSignature timeSignature, Drawer d){
        this.bpm = tempo;
        this.timeSignature = timeSignature;
        this.d = d;

        this.fa = new FrequencyAnalyzer();
        this.rf = new RecordedFrequencies();

        this.currentPosition = new SampleBeatPair(0, 0, 0);

        start = 0;
        count = 0;
        end = 0;

        this.recordedNotes = new LinkedList<>();
    }

    public void addFreq(Float freq){
        rf.addFrequency(freq);
        count++;
    }

    public TimerTask createTimerTask(){
        task = new TimerTask(){
            @Override
            public void run(){
                currentPosition.increment(samplesPerBeat, timeSignature.getTop());

                if (currentPosition.isNewBeat()){
                    SoundPlayer.playTick();

                    if (!countdownComplete){
                        int countDown = timeSignature.getTop() - currentPosition.getBeat();

                        status = "Starting recording in " + Integer.toString(countDown) + "...";

                        if (currentPosition.isNewMeasure()){
                            countdownComplete = true;
                            currentNote = new RecordedNote("R", 0);
                            currentPosition = new SampleBeatPair();
                        }
                    }
                }

                if (countdownComplete) {
                    status = "Recording";

                    end = count;

                    if (start != end) {

                        Log.i("composr-test", "start = " + Integer.toString(start) + ", end = " + Integer.toString(end));

                        // this line is the costliest line in the method
                        String pitch = fa.getNoteFromFreq(rf.getMedian(start, end));

                        // the same note is continuing
                        if (pitch.equals(currentNote.getPitch())) {
                            currentNote.incrementDuration();
                        }

                        // the note just ended
                        else {
                            Log.i("composr-test", "New note: " + pitch + ", old note: " + currentNote.getPitch());
                            recordedNotes.add(currentNote);
                            currentNote = new RecordedNote(pitch);
                            drawCurrentNote = true;
                            redraw = true;
                        }

                        start = end;
                    }

                    else {
                        currentNote.incrementDuration();
                    }

                    updateDisplay();

                    // reset current note
                    currentNote = new RecordedNote(currentNote.getPitch());
                }


            }
        };

        return task;
    }

    public void toggleRecordingTask(){
        this.recording = !this.recording;

        if (this.recording){
            resetDisplay();

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
            status = "Not recording";
        }

    }

    public void updateDisplay(){
        Log.i("composr-test", "update display called");
        if (recordedNotes.size() > 0) {
            if (drawCurrentNote) {
                d.addNote(recordedNotes.getLast());
                drawCurrentNote = false;
            }

        }
    }

    public void resetDisplay(){
        d.reset();
    }

    public void fillCurrentMeasureWithRests(){
        int distanceTilEndOfMeasure = currentPosition.getDistanceTilEndOfMeasure(samplesPerBeat,
                timeSignature.getTop());

        d.draw(new RecordedNote("R", distanceTilEndOfMeasure));
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

    public String generatePattern(){
        String p = "";
        for (RecordedNote recordedNote : recordedNotes){
           for (RecordedNote n : recordedNote.divideNotes()){
               p.concat(n.getPitch() + RecordedNote.getDurationString(n.getDuration()));
           }
        }
        return p;
    }

    public String getNoteFromFreq(float pitch){
        return fa.getNoteFromFreq(pitch);
    }

    public String getStatus(){
        return status;
    }

    public boolean isRecording(){
        return countdownComplete;
    }

    public void redraw(){
        d.redraw();
    }

    public boolean needsRedraw(){
        return redraw;
    }

}
