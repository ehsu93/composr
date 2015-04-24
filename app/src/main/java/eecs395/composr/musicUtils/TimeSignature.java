package eecs395.composr.musicUtils;

/**
 * Enum for the different possible time signatures
 */
public enum TimeSignature {

    FOUR_TWO(4, 2),
    THREE_TWO(3, 2),

    SIX_FOUR(6, 4),
    FIVE_FOUR(5, 4),
    FOUR_FOUR(4, 4),
    THREE_FOUR(3, 4),
    TWO_FOUR(2, 4),

    NINE_EIGHT(9, 8),
    SIX_EIGHT(6, 8),
    THREE_EIGHT(3, 8),
    TWO_EIGHT(2, 8);

    /** The top value of the time signature */
    int top;

    /** The bottom value of the time signature */
    int bottom;

    /** An array of all of the time signatures in the enumeration */
    private static TimeSignature[] timeSignatures = values();

    /**
     * Constructor for TimeSignature enum
     * @param top Top value of the time signature
     * @param bottom Bottom value of the time signature
     */
    TimeSignature(int top, int bottom){
        this.top = top;
        this.bottom = bottom;
    }

    /**
     * Get time signature from index in order to be able to read them from the menu
     *
     * @param i index of time signature in menu/enumeration
     * @return TimeSignature at that index
     */
    public static TimeSignature getTimeSignatureFromIndex(int i){
        return timeSignatures[i];
    }

    /**
     * Returns the top value of the time signature
     * @return the top value of the time signature
     */
    public int getTop(){
        return this.top;
    }

    /**
     * Returns the bottom value of the time signature
     * @return the bottom value of the time signature
     */
    public int getBottom(){
        return this.bottom;
    }
}