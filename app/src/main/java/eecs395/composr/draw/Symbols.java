package eecs395.composr.draw;

import java.util.Hashtable;

public class Symbols {

    private static Hashtable<String, String> symbols = new Hashtable<String, String>() {{
            put("trebleClef", "g");
            put("bassClef", "?");

            put("flat", "b");
            put("sharp", "B");

            // notes
            put("wholeNote", "");

            put("halfNoteStemDown", "");
            put("halfNoteStemUp", "");

            put("quarterNoteStemDown", "ö");
            put("quarterNoteStemUp", "ô");

            put("eighthNoteStemDown", "Ê");
            put("eighthNoteStemUp", "É");

            put("sixteenthNoteStemDown", "");
            put("sixteenthNoteStemUp", "");

            // rests
            put("wholeRest", "W");      // 16
            put("halfRest", "D");       // 8
            put("halfRestDot", "H");    // 12
            put("quarterRest", "Q");    // 4
            put("quarterRestDot", "J"); // 6
            put("eighthRest", "E");     // 2
            put("eighthRestDot", "P");  // 3
            put("sixteenthRest", "Z");  // 1

            // time signatures
            put("4/2", "K");
            put("3/2", "L");

            put("6/4", "^");
            put("5/4", "%");
            put("4/4", "$");
            put("3/4", "#");
            put("2/4", "@");

            put("9/8", "(");
            put("6/8", "P");
            put("3/8", ")");
            put("2/8", "k");

        }};

    public static String get(String key){
        return symbols.get(key);
    }
}
