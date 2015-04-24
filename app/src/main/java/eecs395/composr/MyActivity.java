package eecs395.composr;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.File;
import java.io.IOException;

public class MyActivity extends Activity {

    /** RecordingTask instance */
    RecordingTask rt;

    /** Layout where the music will be displayed while recording */
    LinearLayout noteLayout;

    /** DrawNotes instance */
    Drawer d;

    /** Pitch pipe instance */
    PitchPipe pipe;

    /** Pattern object */
    PatternToMUSICXML pa;

    /** AudioDispatcher object */
    AudioDispatcher dispatcher;

    /** mContext */
    private static Context mContext;

    /** Default name of file */
    String givenName = "myMusic";

    /** Default number of beats per measure */
    int beatsPerMeasure = 4;

    /** Default value of beat duration */
    int beatDuration = 4;

    /** Store the bpm */
    int bpm = 120;

    int HEIGHT;
    int WIDTH;

    String previousPattern;

    /** Whether the program is currently listening to input */
    boolean listening;

    /** Indicates whether the last pattern recorded has been saved to the device */
    boolean isCurrentPatternSaved = false;

    /** Stores the last file that has been saved on the device */
    File lastSavedFile;

    /** Indicates whether the file has been named yet or not */
    boolean waitingOnFileName = false;

    @Override
    /**
     * Everything that happens when the application is first started
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState); // this must be the first line

        // set mContext so that other parts of the application can access the context
        mContext = this;

        setContentView(R.layout.activity_my); // defines the screen layout of the application

        noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);


        initializeHeightAndWidth(); // get height and width of screen
        initializeDrawer(); // Initialize drawer to draw anything necessary on the canvas

        // initialize RecordingTask object, default values
        // THE SECOND VALUE SHOULD STAY AT 4. THAT IS THE DEFAULT NUMBER OF BEATS IN A MEASURE.
        // IT CAN BE CHANGED WHEN THE NUMBER OF BEATS IS CHANGED, DO NOT CHANGE IT HERE WITHOUT
        // A GOOD REASON.
        rt = new RecordingTask(bpm, 4, d); // see comment before changing

        // initialize object that converts pattern to MusicXML
        pa = new PatternToMUSICXML();

        pipe = new PitchPipe();
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);

        final Dialog pitchPipeDialog = createPitchPipeDialog();
        createPitchPipeSpinner(pitchPipeDialog);

        final Dialog timeSignatureDialog = createTimeSignatureDialog();

        // create buttons. This is where all of the buttons go
        final Button tempoButton = (Button) findViewById(R.id.tempo);
        final Button listenButton = (Button) findViewById(R.id.Toggle);
        final Button generateMusicXMLButton = (Button) findViewById(R.id.makeMusic);
        final Button pitchPipeButton = (Button) findViewById(R.id.pitchPipeButton);
        final Button stopPitchPipeButton = (Button) pitchPipeDialog.findViewById(R.id.stop_pitchpipe);
        final Button sendEmailButton = (Button) findViewById(R.id.sendEmail);
        final Button openPdfButton = (Button) findViewById(R.id.pdfOpen);

        // set listeners. This is where all of the listeners are added.
        noteLayout.setOnTouchListener(getNoteLayoutListener(timeSignatureDialog));
        tempoButton.setOnClickListener(getTempoListener(tempoButton));
        listenButton.setOnClickListener(getListenListener(listenButton));
        generateMusicXMLButton.setOnClickListener(getMusicButtonListener());
        sendEmailButton.setOnClickListener(getSendEmailListener());
        pitchPipeButton.setOnClickListener(getPitchPipeButtonListener(pitchPipeDialog));
        stopPitchPipeButton.setOnClickListener(getStopPitchPipeButtonListener());
        openPdfButton.setOnClickListener(getOpenPdfListener());

        dispatcher.addAudioProcessor(getAudioProcessor());

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

    public View.OnTouchListener getNoteLayoutListener(final Dialog dialog){

        return new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent e){

                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = e.getX();
                    if (x > 170 && x < 320) {
                        dialog.show();
                    }
                }
                return true;
            }
        };
    }

    public View.OnClickListener getTempoListener(final Button tempo){
        return new View.OnClickListener(){
            public void onClick(View v){
                //TODO
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
                d.invalidate();
            }
        };
    }

    public View.OnClickListener getMusicButtonListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, rt.pattern, Toast.LENGTH_LONG).show();
                promptForExportFilename();
            }
        };
    }

    public View.OnClickListener getSendEmailListener(){
        return new View.OnClickListener(){
            public void onClick(View v){
                if (!isCurrentPatternSaved) {
                    waitingOnFileName = true;
                    promptForExportFilename();
                } else {
                    sendEmail();
                }
            }
        };
    }

    public View.OnClickListener getPitchPipeButtonListener(final Dialog pitchPipeDialog){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pitchPipeDialog.show();
            }
        };
    }

    public View.OnClickListener getStopPitchPipeButtonListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pipe.stop();
            }
        };
    }

    public View.OnClickListener getOpenPdfListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                promptForOpenFilename();
            }
        };
    }

    public void initializeHeightAndWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.WIDTH = size.x;
        this.HEIGHT = size.y;
    }

    public void initializeDrawer(){
        d = new Drawer(this);
        Bitmap result = Bitmap.createBitmap(WIDTH, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        d.draw(canvas);
        d.setLayoutParams(new LinearLayout.LayoutParams(WIDTH, 400));
        noteLayout.addView(d);
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

    public void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "MusicXML Generated with Composr");
        i.putExtra(Intent.EXTRA_TEXT, "The MusicXML generated using Composr is attached");

        Uri uri = Uri.fromFile(lastSavedFile);
        i.putExtra(Intent.EXTRA_STREAM, uri);

        startActivity(Intent.createChooser(i, "Send mail"));
    }

    /**
     * Creates the pitch pipe dialog
     * @return The pitch pipe dialog
     */
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

    public Dialog createTimeSignatureDialog(){

        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setTitle(R.string.time_signature)
                .setSingleChoiceItems(R.array.time_signature_values, -1,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int i){
                                rt.updateTimeSignature(i);
                                d.updateTimeSignature(i);
                                d.invalidate(); // must be called to update display
                                dialog.dismiss();
                            }
                        });

        return b.create();
    }

    public void promptForExportFilename(){
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
                    dialog.cancel();
                    Toast.makeText(mContext, "Preparing file to send...", Toast.LENGTH_LONG);
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

    public void promptForOpenFilename(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Open sheet music");

        final EditText input = new EditText(this);
        input.setHint("Enter file name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString() + ".pdf";
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
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
                                    d.drawNote(nextPiece);
                                }

                                previousPattern = rt.pattern;

                            }
                        }

                    }
                });
            }
        });
    }
}