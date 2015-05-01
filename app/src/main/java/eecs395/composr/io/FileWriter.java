package eecs395.composr.io;

import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import eecs395.composr.Composr;
import eecs395.composr.musicXML.PatternHandler;
import eecs395.composr.musicXML.PatternToMUSICXML;

public class FileWriter {

    private static File lastSavedFile;

    public static File getLastSavedFile(){
        return lastSavedFile;
    }

    public static void setLastSavedFile(File f){
        lastSavedFile = f;
        PatternHandler.setCurrentPatternSaved(true);
    }


    /**
     * Write the currently generated pattern to a MusicXML file
     *
     */
    public static void writeToFile(String name, PatternToMUSICXML pa){

        try {
            File f = pa.write(Composr.getPatternString(), name);
            if (f != null){
                // inform the user where the file was written to
                Toast.makeText(Composr.getContext(), "Written to " + f.getAbsolutePath(),
                        Toast.LENGTH_LONG).show();
                setLastSavedFile(f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
