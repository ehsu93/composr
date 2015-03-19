package eecs395.composr;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import org.jfugue.MusicStringParser;
import org.jfugue.MusicXmlRenderer;
import org.jfugue.Pattern;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import nu.xom.Serializer;

/**
 * Created by Vanya on 10/25/2014.
 */
public class PatternToMUSICXML {
    Context context;
    final String PARENT_DIR = "/Music/";
    //private String givenName = "";
    public PatternToMUSICXML(Context con) {
       context = con;
    }

    public void write(String pat, String givenName) throws IOException {
        if(givenName.length() < 1) {
            givenName = "myMusic";
        }
        try {

           File sdCard = Environment.getExternalStorageDirectory();
           FileOutputStream file = new FileOutputStream(sdCard.getAbsolutePath() +  PARENT_DIR + givenName + ".musicxml");
           MusicXmlRenderer renderer = new MusicXmlRenderer();
           MusicStringParser parser = new MusicStringParser();
           parser.addParserListener(renderer);

           Toast.makeText(context, "Written to " + Environment.getExternalStorageDirectory() + PARENT_DIR + givenName + ".musicxml", Toast.LENGTH_LONG).show();

           Pattern pattern = new Pattern(pat);

           parser.parse(pattern);

           Serializer serializer = new Serializer(file, "UTF-8");
           serializer.setIndent(4);
           serializer.write(renderer.getMusicXMLDoc());

           file.flush();
           file.close();
       }
       catch(UnsupportedEncodingException e) {
           e.printStackTrace();
       }
       catch(IOException e) {
           e.printStackTrace();
       }

    }
/*
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

   */
}
