package eecs395.composr.draw;

import java.util.Hashtable;

public class Symbols {

    private Hashtable<String, String> symbols;

    public Symbols(){
        this.symbols = new Hashtable<String, String>() {{
            put("trebleClef", "g");
            put("bassClef", "?");

            // notes
            put("wholeNote", "");

            put("halfNoteStemDown", "");
            put("halfNoteStemUp", "");

            put("quarterNoteStemDown", "ö");
            put("quarterNoteStemUp", "ô");

            put("eigthNoteStemDown", "");
            put("eightNoteStemUp", "");

            put("sixteenthNoteStemDown", "");
            put("sixteenthNoteStemUp", "");

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
    }

    public String get(String key){
        return symbols.get(key);
    }
}
