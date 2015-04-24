package eecs395.composr;

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

public class PatternToMUSICXML {
    final String PARENT_DIR = "/Music/";
    public PatternToMUSICXML() {
    }

    public File write(String pat, String givenName) throws IOException {
        try {

            File sdCard = Environment.getExternalStorageDirectory();
            String fullFileName = sdCard.getAbsolutePath() + PARENT_DIR + givenName + ".musicxml";
            FileOutputStream file = new FileOutputStream(fullFileName);
            MusicXmlRenderer renderer = new MusicXmlRenderer();
            MusicStringParser parser = new MusicStringParser();
            parser.addParserListener(renderer);

            Toast.makeText(MyActivity.getContext(), "Written to " +
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
}
