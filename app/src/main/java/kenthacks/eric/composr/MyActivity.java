package kenthacks.eric.composr;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
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
import java.util.Hashtable;

import static android.widget.Toast.makeText;

public class MyActivity extends Activity {

    private static final String DNAME = "/composr_files";
    int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        index = 0;

        File rootPath = new File(Environment.getExternalStorageDirectory() + DNAME);
        boolean success = true;
        if(!rootPath.exists()) {
            rootPath.mkdir();
        }


        final FrequencyRecorder FREQ = new FrequencyRecorder(this);
        FrequencyRecorder freq = new FrequencyRecorder(this);
        FREQ.initializeMidiValues();
        final Metronome m = new Metronome(70, this);      // start metronome
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        final RecordingTask rt = new RecordingTask(60, this);

        final Button metronomeButton = (Button) findViewById(R.id.Toggle);
        metronomeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rt.toggle();
            }
        });

        final Button recordButton = (Button) findViewById(R.id.frequencies);
        recordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //FREQ.logFrequencies();
            }
        });

        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        final Float[] result = new Float[1];

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                final float[] pitchInHz = {pitchDetectionResult.getPitch()};
                result[0] = pitchInHz[0];
                final String note = FREQ.getNoteFromFrequency(pitchInHz[0]);
                if(index < FREQ.fArray.frequencies.length) {
                    FREQ.fArray.frequencies[index] = pitchInHz[0];
                    index++;
                }
                final float[] median = new float[1];
                if(index >= FREQ.fArray.frequencies.length) median[0] = FREQ.getMedian();


                runOnUiThread((Runnable) new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.Pitch);
                        text.setText("" + result[0]);
                        TextView text2 = (TextView) findViewById(R.id.Note);
                        text2.setText("" + note);
                        TextView text3 = (TextView) findViewById(R.id.frequencyArray);
                        if(index != 0) {
                            text3.setText("" + FREQ.fArray.frequencies[index - 1] + "\n" + index);
                        }
                        if(index >= FREQ.fArray.frequencies.length) {
                            text3.setText("" + median[0] + "\n" + FREQ.getNoteFromFrequency(median[0]));
                        }

                    }
                });
           }
        }));

        //freq.addToFrequencyArray(result[0]);


        TextView text = (TextView) findViewById(R.id.Pitch);

//        freq.addToFrequencyArray(Float.parseFloat(text.getText().toString()));

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
