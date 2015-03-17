package kenthacks.eric.composr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jfugue.Player;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import java.io.IOException;

import static android.widget.Toast.makeText;

public class MyActivity extends Activity {

    private static final String DNAME = "/composr_files";
    RecordingTask rt;
    DrawNotes dn;
    Context mContext = this;
    String givenName = "";
    int beatsPerMeasure = 4;

    int HEIGHT;
    int WIDTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        File sdCard = Environment.getExternalStorageDirectory();
/*        File rootPath = new File(sdCard.getAbsolutePath() + DNAME);
        if(!rootPath.exists()) {
            rootPath.mkdir();
        }*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        LinearLayout noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WIDTH = size.x;
        HEIGHT = size.y;

        // Draw the notes
        dn = new DrawNotes(this);
        Bitmap result = Bitmap.createBitmap(WIDTH, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        dn.draw(canvas);
        dn.setLayoutParams(new LinearLayout.LayoutParams(WIDTH, 400));
        noteLayout.addView(dn);


        rt = new RecordingTask(60, 4, this);
        final PatternToMUSICXML pa = new PatternToMUSICXML(mContext);

        final Button beats =  (Button) findViewById(R.id.beats);
        beats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(beatsPerMeasure) {
                    case(4): beats.setText("2 beats");
                        beatsPerMeasure = 2;
                        dn.updateTimeSignature(2, 4);
                        dn.invalidate();
                        break;
                    case(3): beats.setText("4 beats");
                        beatsPerMeasure = 4;
                        dn.updateTimeSignature(4, 4);
                        dn.invalidate();
                        break;
                    case(2): beats.setText("3 beats");
                        beatsPerMeasure = 3;
                        dn.updateTimeSignature(3, 4);
                        dn.invalidate();
                        break;
                    default: break;
                }
            }
        });

        final Button metronomeButton = (Button) findViewById(R.id.Toggle);
        metronomeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String buttonText = metronomeButton.getText().toString();
                if (buttonText == "Stop Listening")
                    metronomeButton.setText("Listen");
                else {
                    metronomeButton.setText("Stop Listening");
                }
                rt.toggleRecordingTask();
            }
        });

        final Button musicButton = (Button) findViewById(R.id.makeMusic);
        musicButton.setOnClickListener(new View.OnClickListener() {
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
        });
//PITCH PIPE PART, NEED TO CLEAN UP LATER

        final PitchPipe pipe = new PitchPipe(this, "A");

        final Button aNote = (Button) findViewById(R.id.a);
        final Button aSharpNote = (Button) findViewById(R.id.aSharp);
        final Button bNote = (Button) findViewById(R.id.b);
        final Button cNote = (Button) findViewById(R.id.c);
        final Button cSharpNote = (Button) findViewById(R.id.cSharp);
        final Button dNote = (Button) findViewById(R.id.d);
        final Button dSharpNote = (Button) findViewById(R.id.dSharp);
        final Button eNote = (Button) findViewById(R.id.e);
        final Button fNote = (Button) findViewById(R.id.f);
        final Button fSharpNote = (Button) findViewById(R.id.fSharp);
        final Button gNote = (Button) findViewById(R.id.g);
        final Button gSharpNote = (Button) findViewById(R.id.gSharp);


        final MediaPlayer A = MediaPlayer.create(this, R.raw.a);
        final MediaPlayer ASHARP = MediaPlayer.create(this, R.raw.asharp);
        final MediaPlayer B = MediaPlayer.create(this, R.raw.b);
        final MediaPlayer C = MediaPlayer.create(this, R.raw.c);
        final MediaPlayer CSHARP = MediaPlayer.create(this, R.raw.csharp);
        final MediaPlayer D = MediaPlayer.create(this, R.raw.d);
        final MediaPlayer DSHARP = MediaPlayer.create(this, R.raw.dsharp);
        final MediaPlayer E = MediaPlayer.create(this, R.raw.e);
        final MediaPlayer F = MediaPlayer.create(this, R.raw.f);
        final MediaPlayer FSHARP = MediaPlayer.create(this, R.raw.fsharp);
        final MediaPlayer G = MediaPlayer.create(this, R.raw.g);
        final MediaPlayer GSHARP = MediaPlayer.create(this, R.raw.gsharp);
        boolean toggle = false;
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        aNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(A);
            }
        });
        aSharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(ASHARP);
            }
        });
        bNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(B);
            }
        });
        cNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(C);
            }
        });
        cSharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(CSHARP);
            }
        });
        dNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(D);
            }
        });
        dSharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(DSHARP);
            }
        });
        eNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(E);
            }
        });
        fNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(F);
            }
        });
        fSharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(FSHARP);
            }
        });
        gNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(G);
            }
        });
        gSharpNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(GSHARP);
            }
        });



//PITCH PIPE END
        dispatcher.addAudioProcessor(new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                final String note = rt.fr.getNoteFromFreq(pitchInHz);

                rt.addFreq(pitchInHz);

                runOnUiThread((Runnable) new Runnable() {
                    @Override
                    public void run() {
                        TextView text = (TextView) findViewById(R.id.Pitch);
                        text.setText("" + pitchInHz);
                        TextView text2 = (TextView) findViewById(R.id.Note);
                        text2.setText("" + note);

                        TextView text3 = (TextView) findViewById(R.id.frequencyArray);
                        String newtext = rt.displayPattern;
                        text3.setText(newtext);
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
