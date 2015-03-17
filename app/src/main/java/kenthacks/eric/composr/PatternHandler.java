package kenthacks.eric.composr;

import android.content.Context;

import org.jfugue.MusicStringParser;
import org.jfugue.MusicXmlRenderer;
import org.jfugue.Pattern;

import java.io.FileOutputStream;
import java.io.IOException;

import nu.xom.Serializer;

public class PatternHandler {

    //private String givenName = "";

    Pattern pat;

    public PatternHandler(){

    }


    public void writeToXML(Context c, String givenName) throws IOException {

        String musicXMLTitle = givenName; //"givenName" from text field where it prompts user for name of output file. must be filled.

        FileOutputStream file = c.openFileOutput(musicXMLTitle + ".xml", Context.MODE_PRIVATE);

        MusicXmlRenderer renderer = new MusicXmlRenderer();
        MusicStringParser parser = new MusicStringParser();
        parser.addParserListener(renderer);

        parser.parse(pat);

        Serializer serializer = new Serializer(file, "UTF-8"); //I guess.
        serializer.setIndent(4);
        serializer.write(renderer.getMusicXMLDoc());

        file.flush();
        file.close();
    }
}
