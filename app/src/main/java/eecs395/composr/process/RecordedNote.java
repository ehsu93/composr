package eecs395.composr.process;

import java.util.LinkedList;

import eecs395.composr.draw.Note;

/**
 * Created by meaghan on 4/30/15.
 */
public class RecordedNote {

    private String pitch;
    private int duration;


    public RecordedNote(String pitch){
        this.pitch = pitch;
        this.duration = 1;
    }

    public RecordedNote(String pitch, int duration){
        this.pitch = pitch;
        this.duration = duration;
    }

    public String getPitch(){
        return this.pitch;
    }

    public int getDuration(){
        return this.duration;
    }


    public void incrementDuration(){
        this.duration += 1;
    }

    public static String getDurationString(int duration){

        /**
         * get the corresponding string based on the duration
         */

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

    public int getFirstDuration(){
        // a power of two will have a
        double logValue =  Math.log(duration) / Math.log(2);
        double floor = Math.floor(logValue);

        // Case 1: the duration is a power of 2. Return the duration because this can be represented
        //         with one note.
        if (logValue == floor){
            return duration;
        }

        int combinationValue = (int)(Math.pow(2, floor) + Math.pow(2, floor - 1));

        // Case 2: The duration indicates that this is a dotted note, return the duration because
        //         this can be represented with one note.
        if (duration == combinationValue){
            return duration;
        }

        // Case 3: The longest single note that is shorter than this note is a dotted note
        if (combinationValue < duration){
            return combinationValue;
        }

        // Case 4: The longest single note shorter than this one is a note without a dot
        return (int) Math.pow(2, floor);
    }

    public LinkedList<RecordedNote> divideNotes(){
        LinkedList<RecordedNote> dividedNotes = new LinkedList<>();

        int firstDuration = getFirstDuration();
        int secondDuration = duration - getFirstDuration();

        dividedNotes.add(new RecordedNote(pitch, firstDuration));
        if (secondDuration != 0){
            dividedNotes.add(new RecordedNote(pitch, secondDuration));
        }

        return dividedNotes;
    }
}
