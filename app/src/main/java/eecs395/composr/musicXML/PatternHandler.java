package eecs395.composr.musicXML;

/**
 * Created by meaghan on 4/30/15.
 */
public class PatternHandler {
    /** Pattern object */
    private static PatternToMUSICXML pa;

    private static String patternString;

    private static String previousPatternString;

    /** Indicates whether the last pattern recorded has been saved to the device
     *  Default value is false because nothing has been recorded yet */
    private static boolean currentPatternSaved = false;

    public static PatternToMUSICXML getPattern(){
        return pa;
    }

    public static boolean isCurrentPatternSaved(){
        return currentPatternSaved;
    }

    public static void setCurrentPatternSaved(boolean input){
        currentPatternSaved = input;
    }

    public static void resetPreviousPattern() {
        previousPatternString = "";
    }

    public static void setPreviousPatternString(String s){
        previousPatternString = s;
    }

    public static String getPreviousPatternString(){
        return previousPatternString;
    }


}
