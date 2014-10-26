package kenthacks.eric.composr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jfugue.Pattern;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import java.io.File;
import java.io.IOException;

import static android.widget.Toast.makeText;

public class MyActivity extends Activity {

    private static final String DNAME = "/composr_files";
    RecordingTask rt;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        File sdCard = Environment.getExternalStorageDirectory();
        File rootPath = new File(sdCard.getAbsolutePath() + DNAME);
        if(!rootPath.exists()) {
            rootPath.mkdir();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        rt = new RecordingTask(60, this);
        final PatternToMUSICXML pa = new PatternToMUSICXML(mContext);

        final Button metronomeButton = (Button) findViewById(R.id.Toggle);
        metronomeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rt.toggle();
            }
        });
        final Button musicButton = (Button) findViewById(R.id.makeMusic);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, rt.pattern, Toast.LENGTH_LONG).show();
                try {
                    pa.write(rt.pattern, "example");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                final String note = rt.fr.getNoteFromFrequency(pitchInHz);

                rt.rf.addFrequency(pitchInHz);

                runOnUiThread((Runnable) new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.Pitch);
                        text.setText("" + pitchInHz);
                        TextView text2 = (TextView) findViewById(R.id.Note);
                        text2.setText("" + note);
                    }
                });
           }
        }));

        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
