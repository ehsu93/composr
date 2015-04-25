package eecs395.composr.draw;
import java.util.Arrays;

public class Note {

    private String name;
    private String clefPref;
    private int trebleLedgers;
    private int bassLedgers;
    String spaceOrLine;
    int index;
    private Symbols symbols;

    /**
     * Initialize the note object
     *
     * @param name Name of the note
     * @param clefPref Preferred clef of the note, meaning the one whose staff it's closest to
     * @param trebleLedgers Number of ledger lines needed when using treble clef
     * @param bassLedgers Number of ledger lines needed when using bass clef
     * @param spaceOrLine Whether the note is on a space or a line
     * @param index The index of the note relative to middle C
     */
    public Note(String name, String clefPref, int trebleLedgers, int bassLedgers,
                String spaceOrLine, int index){
        this.name = name;
        this.clefPref = clefPref;
        this.trebleLedgers = trebleLedgers;
        this.bassLedgers = bassLedgers;
        this.spaceOrLine = spaceOrLine;
        this.index = index;
        this.symbols = new Symbols();
    }

    /**
     * Create a note with only a name. Only used internally.
     *
     * @param name The name of the note
     */
    private Note(String name){
        this.name = name;
    }

    /**
     * Get the full name of a note
     *
     * @return The name of the note
     */
    public String getName(){
        return this.name;
    }

    /**
     * Get just the letter name of the note (ex. A4 and A#4 are both A for our purposes)
     * TODO: Support for flat notes
     *
     * @return The letter name of the note without the accidental or octave
     */
    public char getNoteName(){
        return this.name.charAt(0);
    }

    /**
     * Return the octave of a note, determined from its name (ex. A#5 is octave 5)
     *
     * @return The octave number
     */
    public int getOctave(){
        return Character.getNumericValue(this.name.charAt(this.name.length() - 1));
    }

    /**
     * Uses the current clef and space between lines to determine where to place a note on the
     * canvas. Does not yet support ledger lines.
     *
     * TODO: Ledger lines
     *
     * @param clef The clef being drawn on
     * @param spaceBetweenLines The space between lines from DrawNotes
     * @return The float value of where to place the note on the canvas
     */
    public float getPosition(String clef, int spaceBetweenLines){

        // stem down notes are lower than stem up notes, calculate accordingly
        if (this.isStemDown(clef)){
            return (-0.5f * this.index + 9.45f) * spaceBetweenLines;
        } else {
            return (-0.5f * this.index + 6.85f) * spaceBetweenLines;
        }
    }

    public String getSymbol(String clef, String duration){

        String durationSubstring;

        // first part of the string, the length of the note plus the word note
        durationSubstring = "quarterNote";

        if (duration == "i"){
            durationSubstring = "eighthNote";
        } else if (duration == "s"){
            durationSubstring = "sixteenthNote";
        } else if (duration == "h"){
            durationSubstring = "halfNote";
        } else if (duration == "w"){
            durationSubstring = "wholeNote";
        }

        // second part of the string, whether the stem is up or down
        String stemSubstring;
        if (this.isStemDown(clef)){
            stemSubstring = "StemDown";
        } else {
            stemSubstring = "StemUp";
        }

        // in the form of quarterNoteStemUp, grab this from the symbols hashtable
        return symbols.get(durationSubstring + stemSubstring);
    }

    /**
     * Returns whether the current note is higher than an inputted note
     *
     * @param other The other note to test against
     * @return True if the current note is higher than the inputted note
     */
    public boolean higherThan(Note other){
        // if the notes are in different octaves, the one in the higher octave is higher
        if (this.getOctave() != other.getOctave()){
            return this.getOctave() > other.getOctave();
        }

        // this is the order that notes occur in each octave, with C being the lowest note of each octave
        // must be Character because Arrays.asList does not work with primitives
        Character[] noteOrder = {'C', 'D', 'E', 'F', 'G', 'A', 'B'};

        // use Arrays.asList to get the indices of the notes in the noteOrder array
        return Arrays.asList(noteOrder).indexOf(this.getNoteName()) > Arrays.asList(noteOrder).indexOf(other.getNoteName());
    }

    /**
     * Returns if the current stem is down based on the clef
     *
     * @param clef The clef being drawn on
     * @return Whether the stem is down or not
     */
    public boolean isStemDown(String clef){

        // treble clef: B4 and higher are stem down, A4 and lower are stem up
        if (clef.equals(symbols.get("trebleClef"))){
            return this.higherThan(new Note("A4"));
        }
        // bass clef: D4 and higher are stem down, C3 and lower are stem up
        else {
            return this.higherThan(new Note("C3"));
        }
    }

    public String toString(){
        return "Note at " + name;
    }
}
