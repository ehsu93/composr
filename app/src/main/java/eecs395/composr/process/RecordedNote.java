package eecs395.composr.process;

/**
 * Created by meaghan on 4/30/15.
 */
public class RecordedNote {

    private String pitch;
    private String duration;

    public RecordedNote(String pitch, String duration){
        this.pitch = pitch;
        this.duration = duration;
    }

    public String getPitch(){
        return this.pitch;
    }

    public String getDuration(){
        return this.duration;
    }
}
