package eecs395.composr;

import android.app.Activity;
<<<<<<< HEAD
import android.content.ActivityNotFoundException;
import android.content.Context;
=======
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
<<<<<<< HEAD
import android.media.MediaPlayer;
=======
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import eecs395.proj.composr.R;

import java.io.File;
import java.io.IOException;

public class MyActivity extends Activity {

    //private static final String DNAME = "/composr_files";

    /** RecordingTask instance */
    RecordingTask rt;

    /** Layout where the music will be displayed while recording */
    LinearLayout noteLayout;

    /** DrawNotes instance */
    DrawNotes dn;

    /** AudioDispatcher object */
    AudioDispatcher dispatcher;

    /** mContext */
    Context mContext = this;

    /** Default name of file */
    String givenName = "myMusic";

    /** Default number of beats per measure */
    int beatsPerMeasure = 4;

    /** Default value of beat duration */
    int beatDuration = 4;

    /** Store the bpm */
<<<<<<< HEAD
    int bpm = 60;
=======
    int bpm = 120;
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353

    int HEIGHT;
    int WIDTH;

    String previousPattern;
    boolean listening;

    boolean isCurrentPatternSaved = false;
    File lastSavedFile;
    boolean waitingOnFileName = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
<<<<<<< HEAD
        LinearLayout noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);

        // get height and width of screen
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
=======

        noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);

        mContext = this;

        initializeHeightAndWidth(); // get height and width of screen
        initializeDrawer(); // Initialize drawer to draw anything necessary on the canvas
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353

        rt = new RecordingTask(60, beatsPerMeasure, this);
        // initialize RecordingTask object, default
        rt = new RecordingTask(bpm, 4, this);
        final PatternToMUSICXML pa = new PatternToMUSICXML(mContext);

        final Button openPdf = (Button) findViewById(R.id.pdfOpen);
        openPdf.setOnClickListener(new View.OnClickListener() {
            TextView name = (TextView) findViewById(R.id.SheetMusicName);
            String fileName = name.getText().toString() + ".pdf";
            @Override
            public void onClick(View v) {
                File file = new File(fileName);
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "/Music/");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(MyActivity.this,
                                "No Application Available to View PDF",
                                Toast.LENGTH_SHORT).show();
                    }
            }

        }
        });

<<<<<<< HEAD
        final Button beats =  (Button) findViewById(R.id.beats);
        beats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(rt.getBeats()) {
                    case(4): beats.setText("2 beats"); rt.setBeats(2); break;
                    case(3): beats.setText("4 beats"); rt.setBeats(4); break;
                    case(2): beats.setText("3 beats"); rt.setBeats(3); break;
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
                    default: break;
                }
                dn.updateTimeSignature(beatsPerMeasure, beatDuration);
                dn.changeClef();
                dn.invalidate();
            }
        });

        final Button metronomeButton = (Button) findViewById(R.id.Toggle);
        metronomeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String buttonText = metronomeButton.getText().toString();
                if (buttonText.equals("Stop Listening"))
                    metronomeButton.setText("Listen");
                else {
                    metronomeButton.setText("Stop Listening");
                }
                rt.bpm = Integer.parseInt(beats.getText().toString());
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

        //PITCH PIPE PART, TODO: NEED TO CLEAN UP LATER

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
=======
        pipe = new PitchPipe();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        final Dialog dialog = createPitchPipeDialog();
        createPitchPipeSpinner(dialog);

        // create buttons
        final Button beats =  (Button) findViewById(R.id.beats);
        final Button listenButton = (Button) findViewById(R.id.Toggle);
        final Button musicButton = (Button) findViewById(R.id.makeMusic);
        final Button pitchPipeButton = (Button) findViewById(R.id.pitchPipeButton);
        final Button stopPitchPipe = (Button) dialog.findViewById(R.id.stop_pitchpipe);
        final Button sendEmail = (Button) findViewById(R.id.sendEmail);


        // set button listeners
        beats.setOnClickListener(getBeatsListener(beats));
        listenButton.setOnClickListener(getListenListener(listenButton));
        musicButton.setOnClickListener(getMusicButtonListener(musicButton));
        sendEmail.setOnClickListener(sendEmailListener(sendEmail));

        pitchPipeButton.setOnClickListener(new View.OnClickListener(){
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353
            @Override
            public void onClick(View v) {
                pipe.togglePlayTest(C);
            }
        });
<<<<<<< HEAD
        cSharpNote.setOnClickListener(new View.OnClickListener() {
=======

        stopPitchPipe.setOnClickListener(new View.OnClickListener(){
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353
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

<<<<<<< HEAD
        //PITCH PIPE END

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

                        TextView text3 = (TextView) findViewById(R.id.frequencyArray);
                        String newtext = rt.displayPattern;
                        text3.setText(newtext);
                    }
                });
            }
        }));
=======
        dispatcher.addAudioProcessor(getAudioProcessor());
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353


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
<<<<<<< HEAD
}
=======

    public void initializeHeightAndWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.WIDTH = size.x;
        this.HEIGHT = size.y;
    }

    public void initializeDrawer(){
        dn = new Drawer(this);
        Bitmap result = Bitmap.createBitmap(WIDTH, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        dn.draw(canvas);
        dn.setLayoutParams(new LinearLayout.LayoutParams(WIDTH, 400));
        noteLayout.addView(dn);
    }

    public View.OnClickListener sendEmailListener(final Button sendEmail){
        return new View.OnClickListener(){
            public void onClick(View v){
                if (!isCurrentPatternSaved) {
                    waitingOnFileName = true;
                    promptForFilename();
                } else {
                    sendEmail();
                }
            }
        };
    }

    public void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "MusicXML Generated with Composr");
        i.putExtra(Intent.EXTRA_TEXT, "The MusicXML generated using Composr is attached");

        Uri uri = Uri.fromFile(lastSavedFile);
        i.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(i, "Send mail"));
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
                if (buttonText.equals("Stop Listening")) {
                    listenButton.setText("Listen");
                    listening = false;
                }
                else {
                    listenButton.setText("Stop Listening");
                    listening = true;
                }

                isCurrentPatternSaved = false;
                lastSavedFile = null;
                previousPattern = "";
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
                promptForFilename();
            }
        };
    }

    /**
     * Write the currently generated pattern to a MusicXML file
     *
     */
    public void writeToFile(){

        try {
            File f = pa.write(rt.pattern, givenName);
            if (f != null){
                // inform the user where the file was written to
                Toast.makeText(MyActivity.getContext(), "Written to " + f.getAbsolutePath(),
                        Toast.LENGTH_LONG).show();

                isCurrentPatternSaved = true;
                lastSavedFile = f;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Dialog createPitchPipeDialog(){
        Dialog dialog = new Dialog(MyActivity.this);
        dialog.setContentView(R.layout.pitchpipe);
        dialog.setTitle(R.string.pitch_pipe);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                pipe.stop();
            }
        });
        return dialog;
    }

    public void promptForFilename(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Export to MusicXML");

        final EditText input = new EditText(this);
        input.setHint("Enter file name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                givenName = input.getText().toString();
                writeToFile();

                if (waitingOnFileName){
                    waitingOnFileName = false;
                    sendEmail();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    /**
     * Create the spinner to display the pitch pipe values
     *
     * @param dialog The dialog to add the spinner to
     * @return A spinner object with all the notes
     */
    public void createPitchPipeSpinner(Dialog dialog){
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
    }

    public PitchProcessor getAudioProcessor(){
        return new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
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

                        if (listening) {
                            // check with current pattern in recording task
                            if (!previousPattern.equals(rt.pattern)) {

                                // update displayed pattern
                                int i = previousPattern.length();
                                boolean notDone = true;
                                while (notDone) {
                                    int space = rt.pattern.indexOf(" ", i);
                                    int nextSpace = rt.pattern.indexOf(" ", space + 1);

                                    if (nextSpace == -1) {
                                        nextSpace = rt.pattern.length();
                                        notDone = false;
                                    }

                                    String nextPiece = rt.pattern.substring(space + 1, nextSpace);
                                    i = nextSpace;
                                    Log.i("drawnotetest", "next note to draw = [" + nextPiece + "]");
                                    dn.drawNote(nextPiece);
                                }

                                previousPattern = rt.pattern;

                                //}

                                // scroll
                                // dn.scrollBy(150, 0);
                            }
                        }

                    }
                });
            }
        });
    }
}
>>>>>>> 47fd3f8f9a179c202b8387245dc544688832c353
