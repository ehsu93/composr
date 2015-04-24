package eecs395.composr.musicUtils;

public class TimeSignature {

    private int top;
    private int bottom;

    public TimeSignature(int top, int bottom){
        this.top = top;
        this.bottom = bottom;
    }

    public int getTop(){
        return this.top;
    }

    public int getBottom(){
        return this.bottom;
    }
}
