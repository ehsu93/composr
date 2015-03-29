package eecs395.composr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import eecs395.composr.draw.Drawer;
import eecs395.proj.composr.R;

import java.io.IOException;

public class MyActivity extends Activity {
    /** RecordingTask instance */
    RecordingTask rt;

    /** DrawNotes instance */
    Drawer dn;

    /** Pitch pipe instance */
    PitchPipe pipe;

    /** Pattern object */
    PatternToMUSICXML pa;

    /** mContext */
    private static Context mContext;

    /** Default name of file */
    String givenName = "";

    /** Default number of beats per measure */
    int beatsPerMeasure = 4;

    /** Default value of beat duration */
    int beatDuration = 4;

    /** Store the bpm */
    int bpm = 100;

    /** Store the height and width */
    int HEIGHT;
    int WIDTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);

        LinearLayout noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);

        mContext = this;

        // get height and width of screen
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WIDTH = size.x;
        HEIGHT = size.y;

        // Initialize drawer to draw anything necessary on the canvas
        dn = new Drawer(this);
        Bitmap result = Bitmap.createBitmap(WIDTH, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        dn.draw(canvas);
        dn.setLayoutParams(new LinearLayout.LayoutParams(WIDTH, 400));
        noteLayout.addView(dn);

        // initialize RecordingTask object, default values
        // THE SECOND VALUE SHOULD STAY AT 4. THAT IS THE DEFAULT NUMBER OF BEATS IN A MEASURE.
        // IT CAN BE CHANGED WHEN THE NUMBER OF BEATS IS CHANGED, DO NOT CHANGE IT HERE WITHOUT
        // A GOOD REASON.
        rt = new RecordingTask(bpm, 4, dn); // see comment before changing

        // initialize object that converts pattern to MusicXML
        pa = new PatternToMUSICXML();

        final Button beats =  (Button) findViewById(R.id.beats);
        beats.setOnClickListener(getBeatsListener(beats));

        final Button listenButton = (Button) findViewById(R.id.Toggle);
        listenButton.setOnClickListener(getListenListener(listenButton));

        final Button musicButton = (Button) findViewById(R.id.makeMusic);
        musicButton.setOnClickListener(getMusicButtonListener(musicButton));

        pipe = new PitchPipe();
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        final Dialog dialog = new Dialog(MyActivity.this);
        dialog.setContentView(R.layout.pitchpipe);
        dialog.setTitle(R.string.pitch_pipe);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                pipe.stop();
            }
        });

        // override onclick listener of positive button
        // initialize spinner with adapter
        Spinner spinner = (Spinner) dialog.findViewById(R.id.pitchpipe_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pitch_pipe_values, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                pipe.play(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        final Button pitchPipeButton = (Button) findViewById(R.id.pitchPipeButton);
        pitchPipeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });

        final Button stopPitchPipe = (Button) dialog.findViewById(R.id.stop_pitchpipe);
        stopPitchPipe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pipe.stop();
            }
        });

        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                final String note = rt.fr.getNoteFromFreq(pitchInHz);

                rt.addFreq(pitchInHz);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.Pitch);
                        text.setText("" + pitchInHz);
                        TextView text2 = (TextView) findViewById(R.id.Note);
                        text2.setText("" + note);

                        //TextView text3 = (TextView) findViewById(R.id.frequencyArray);
                        //String newtext = rt.displayPattern;
                        //text3.setText(newtext);

                        dn.scrollBy(-5, 0);
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
        // Handle action bar item clicks here. The action bar will automatically handle clicks on
        // the Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public static Context getContext(){
        return mContext;
    }

    public View.OnClickListener getBeatsListener(final Button beats){

        // create and return a new OnClickListener object
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch(beatsPerMeasure) {
                    case(4): beats.setText("2 beats");
                        beatsPerMeasure = 2;
                        break;
                    case(3): beats.setText("4 beats");
                        beatsPerMeasure = 4;
                        break;
                    case(2): beats.setText("3 beats");
                        beatsPerMeasure = 3;
                        break;
                    default:
                        break;
                }

                rt.updateTimeSignature(beatsPerMeasure, beatDuration);
                dn.updateTimeSignature(beatsPerMeasure, beatDuration);

                dn.invalidate(); // redraws the canvas
            }
        };
    }

    public View.OnClickListener getListenListener(final Button listenButton){
        return new View.OnClickListener() {
            public void onClick(View v) {
                String buttonText = listenButton.getText().toString();
                if (buttonText.equals("Stop Listening"))
                    listenButton.setText("Listen");

                else {
                    listenButton.setText("Stop Listening");
                }

                dn.scrollLeft(50);
                rt.toggleRecordingTask();
                dn.invalidate();
            }
        };
    }

    public View.OnClickListener getMusicButtonListener(final Button musicButton){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, rt.pattern, Toast.LENGTH_LONG).show();
                TextView name = (TextView) findViewById(R.id.musicName);
                givenName = name.getText().toString();
                try {
                    pa.write(rt.pattern, givenName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}