package kenthacks.eric.composr;

import android.content.Context;

import org.jfugue.MusicStringParser;
import org.jfugue.MusicXmlRenderer;
import org.jfugue.Pattern;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import nu.xom.Serializer;

/**
 * Created by Vanya on 10/25/2014.
 */
public class PatternToMUSICXML {

    //private String givenName = "";


    public void write(Context c, Pattern pat, String givenName) throws IOException {

        String musicXMLTitle = givenName; //"givenName" from text field where it prompts user for name of output file. must be filled.

        FileOutputStream file = c.openFileOutput(musicXMLTitle + ".xml", 0);

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
