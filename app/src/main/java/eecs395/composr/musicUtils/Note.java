package eecs395.composr.musicUtils;

public class Note {

    private String name;
    private float position;
    private String symbol;

    public Note(String name, float position, String symbol){
        this.name = name;
        this.position = position;
        this.symbol = symbol;
    }

    public String getName(){
        return this.name;
    }

    public float getPosition(){
        return this.position;
    }

    public String getSymbol(){
        return this.symbol;
    }
}
