package eecs395.composr.io;

import android.os.Environment;
import android.widget.Toast;

import org.jfugue.MusicStringParser;
import org.jfugue.MusicXmlRenderer;
import org.jfugue.Pattern;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import eecs395.composr.Composr;
import nu.xom.Serializer;

public class FileWriter {

    private static File lastSavedFile;
    final static String PARENT_DIR = "/Music/";

    public static File getLastSavedFile(){
        return lastSavedFile;
    }

    public static void setLastSavedFile(File f){
        lastSavedFile = f;
    }


    /**
     * Write the currently generated pattern to a MusicXML file
     *
     */
    public static void writeToFile(String name){

        try {
            File f = createMusicXML(Composr.getPattern(), name);
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

    public static File createMusicXML(String pat, String givenName) throws IOException {
        try {

            File sdCard = Environment.getExternalStorageDirectory();
            String fullFileName = sdCard.getAbsolutePath() + PARENT_DIR + givenName + ".musicxml";
            FileOutputStream file = new FileOutputStream(fullFileName);
            MusicXmlRenderer renderer = new MusicXmlRenderer();
            MusicStringParser parser = new MusicStringParser();
            parser.addParserListener(renderer);

            Toast.makeText(Composr.getContext(), "Written to " +
                            Environment.getExternalStorageDirectory() + PARENT_DIR + givenName + ".musicxml",
                    Toast.LENGTH_LONG).show();

            Pattern pattern = new Pattern(pat);

            parser.parse(pattern);

            Serializer serializer = new Serializer(file, "UTF-8");
            serializer.setIndent(4);
            serializer.write(renderer.getMusicXMLDoc());

            file.flush();
            file.close();
            return new File(fullFileName);

        }
        catch(UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean hasBeenSaved(){
        return lastSavedFile == null;
    }
}
