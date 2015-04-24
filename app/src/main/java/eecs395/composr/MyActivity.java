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
import android.widget.NumberPicker;
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
import eecs395.composr.musicUtils.TimeSignature;

import java.io.File;
import java.io.IOException;

public class MyActivity extends Activity {

    /** RecordingTask instance */
    private RecordingTask rt;

    /** Layout where the music will be displayed while recording */
    private LinearLayout noteLayout;

    /** DrawNotes instance */
    private Drawer drawer;

    /** PitchPipe instance */
    private PitchPipe pitchPipe;

    /** Pattern object */
    private PatternToMUSICXML pa;

    /** AudioDispatcher object */
    private AudioDispatcher dispatcher;

    /** mContext */
    private static Context mContext;

    /** Default name of file */
    private String givenName = "myMusic";

    /** Default number of beats per measure */
    private TimeSignature timeSignature = TimeSignature.FOUR_FOUR;

    /** Store the bpm */
    private int bpm = 120;

    private int HEIGHT;
    private int WIDTH;

    private String previousPattern;

    /** Whether the program is currently listening to input */
    private boolean listening;

    /** Indicates whether the last pattern recorded has been saved to the device
     *  Default value is false because nothing has been recorded yet */
    private boolean isCurrentPatternSaved = false;

    /** Indicates whether the file has been named yet or not
     *  Necessary because there needs to be a file name for the email functionality
     *  Default value is false because there is no pattern to write to a file yet */
    private boolean toSendEmail = false;

    /** Stores the last file that has been saved on the device for emailing purposes */
    private File lastSavedFile;

    /**
     * Everything that happens when the application is first started
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // this must be the first line

        // set mContext so that other parts of the application can access the context
        mContext = this;

        setContentView(R.layout.activity_my); // defines the screen layout of the application

        noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);

        initializeHeightAndWidth(); // get height and width of screen
        initializeDrawer(); // Initialize drawer to draw anything necessary on the canvas

        // initialize RecordingTask object with default values
        rt = new RecordingTask(bpm, timeSignature, drawer);

        // initialize object that converts pattern to MusicXML
        pa = new PatternToMUSICXML();

        // initialize pitchPipe
        pitchPipe = new PitchPipe();

        // create dialogs
        final Dialog pitchPipeDialog = createPitchPipeDialog();
        final Dialog settingsDialog = createParentDialog(R.layout.settings,
                R.string.settings_title);
        final Dialog toolsDialog = createParentDialog(R.layout.tools, R.string.tools_title);
        final Dialog shareDialog = createParentDialog(R.layout.share, R.string.share_title);
        final Dialog timeSignatureDialog = createTimeSignatureDialog(settingsDialog);
        final Dialog keySignatureDialog = createKeySignatureDialog(settingsDialog);
        final Dialog tempoDialog = createTempoDialog(settingsDialog);

        // buttons on the main screen
        final Button settingsButton = (Button) findViewById(R.id.settings);
        final Button listenButton = (Button) findViewById(R.id.Toggle);
        final Button shareButton = (Button) findViewById(R.id.share_button);
        final Button toolsButton = (Button) findViewById(R.id.tools_button);

        // settings dialog buttons
        final Button tempoButton = (Button) settingsDialog.findViewById(R.id.tempo);
        final Button keyButton = (Button) settingsDialog.findViewById(R.id.changeKey);
        final Button timeButton = (Button) settingsDialog.findViewById(R.id.changeTime);

        // save/share dialog buttons
        final Button generateMusicXMLButton = (Button) shareDialog.findViewById(R.id.makeMusic);
        final Button sendEmailButton = (Button) shareDialog.findViewById(R.id.sendEmail);

        // tools dialog buttons
        final Button pitchPipeButton = (Button) toolsDialog.findViewById(R.id.pitchPipeButton);
        final Button stopPitchPipeButton =
                (Button) pitchPipeDialog.findViewById(R.id.stop_pitchpipe);
        final Button openPdfButton = (Button) toolsDialog.findViewById(R.id.pdfOpen);

        // set listeners. This is where all of the listeners are added.
        noteLayout.setOnTouchListener(getNoteLayoutListener(timeSignatureDialog));

        // buttons on the main screen
        settingsButton.setOnClickListener(showDialogListener(settingsDialog));
        listenButton.setOnClickListener(getListenListener(listenButton));
        shareButton.setOnClickListener(showDialogListener(shareDialog));
        toolsButton.setOnClickListener(showDialogListener(toolsDialog));

        // buttons in the settings dialog
        tempoButton.setOnClickListener(showDialogListener(tempoDialog));
        keyButton.setOnClickListener(showDialogListener(keySignatureDialog));
        timeButton.setOnClickListener(showDialogListener(timeSignatureDialog));

        // buttons in the share dialog
        generateMusicXMLButton.setOnClickListener(getMusicButtonListener());
        sendEmailButton.setOnClickListener(getSendEmailListener());

        // buttons in the tools dialog
        pitchPipeButton.setOnClickListener(getPitchPipeButtonListener(pitchPipeDialog));
        stopPitchPipeButton.setOnClickListener(getStopPitchPipeButtonListener());
        openPdfButton.setOnClickListener(getOpenPdfListener());

        keyButton.setEnabled(true);

        // initialize audio dispatcher
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
        dispatcher.addAudioProcessor(getAudioProcessor());
        new Thread(dispatcher, "Audio Dispatcher").start();
    }

    /**
     * Needs to be overridden, not used in the application yet
     * @param menu The application menu
     * @return Returns true to display the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    /**
     * Must be overridden, not used in the application
     * @param item The selected item from the menu
     * @return Returns true to display the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * Gives the context of the application so that it is accessible from outside the activity
     * @return The application context
     */
    public static Context getContext(){
        return mContext;
    }

    /**
     * Returns the OnTouchListener for the note display area
     * The OnTouchListener performs different actions based on the area that is touched
     *
     * @param timeSignatureDialog The dialog used to select the time signature
     * @return The instance of OnTouchListener created for the NoteLayout
     */
    public View.OnTouchListener getNoteLayoutListener(final Dialog timeSignatureDialog){
        return new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent e){
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = e.getX();

                    // the user touched the clef area
                    if (x < 170){
                        drawer.changeClef();
                    }

                    // the user touched the key signature area
                    if (x > 170 && x < 320) {
                        timeSignatureDialog.show();
                    }

                }
                return true;
            }
        };
    }

    /**
     * Returns an OnTouchListener that does nothing. This is used when the recording has started
     * and the user is no longer able to change the time signature or clef
     *
     * @return An OnTouchListener that does nothing
     */
    public View.OnTouchListener getEmptyOnTouchListener(){
        return new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent e){
                return false;
            }
        };
    }

    public View.OnClickListener showDialogListener(final Dialog dialog){
        return new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
            }
        };
    }

    /**
     * Returns the OnClickListener for the Listen button
     *
     * @param listenButton Indicates whether to stop or start listening
     * @return The OnClickListener for the Listen button
     */
    public View.OnClickListener getListenListener(final Button listenButton){
        return new View.OnClickListener() {
            public void onClick(View v) {

                // get the text from the button
                String buttonText = listenButton.getText().toString();

                // stop listening to the user
                if (buttonText.equals("Stop Listening")) {
                    listenButton.setText("Listen");
                    listening = false;
                }

                // start listening to the user
                else {
                    listenButton.setText("Stop Listening");
                    listening = true;
                }

                /* Whether the user just started or just stopped recording, the current pattern has
                not yet been saved, set isCurrentPatternSaved to false*/
                isCurrentPatternSaved = false;

                // there is no last saved file at this time
                lastSavedFile = null;

                // reset previous pattern
                previousPattern = "";

                // start or stop the recording task
                rt.toggleRecordingTask();

                // invalidate the drawer object so that the display updates
                drawer.invalidate();
            }
        };
    }

    /**
     * Returns the OnClickListener for the button that exports to MusicXML
     *
     * @return The OnClickListener for the button that exports to MusicXML
     */
    public View.OnClickListener getMusicButtonListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prompts the user to input a name for the file to export
                promptForExportFilename();
            }
        };
    }

    /**
     * Returns the OnClickListener for the send email button which will prompt for a file name and
     * save it if it hasn't already been done, otherwise it will go straight into the sendEmail
     * method
     *
     * @return the OnClickListener for the send email button
     */
    public View.OnClickListener getSendEmailListener(){
        return new View.OnClickListener(){
            public void onClick(View v){

                // current pattern is not saved, prompt the user for a filename
                if (!isCurrentPatternSaved) {
                    toSendEmail = true;
                    promptForExportFilename(); // this will call sendEmail() later
                } else {
                    sendEmail();
                }
            }
        };
    }

    /**
     * Return the OnClickListener for the pitch pipe button. Requires the dialog as input so that
     * it can show the dialog
     *
     * @param pitchPipeDialog The pitch pipe dialog
     * @return The OnClickListener for the pitch pipe button
     */
    public View.OnClickListener getPitchPipeButtonListener(final Dialog pitchPipeDialog){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pitchPipeDialog.show();
            }
        };
    }

    /**
     * Return the OnClickListener for the stop button in the pitch pipe dialog. It turns the pitch
     * pipe sound off.
     *
     * @return the OnClickListener for the stop button in the pitch pipe dialog
     */
    public View.OnClickListener getStopPitchPipeButtonListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                pitchPipe.stop();
            }
        };
    }

    /**
     * Gets the OnClickListener for the OpenPDF button. Will prompt the user to select a filename to
     * open.
     *
     * @return the OnClickListener for the OpenPDF button
     */
    public View.OnClickListener getOpenPdfListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                promptForOpenFilename();
            }
            /*TODO: decide how to handle a user trying to view the PDF of the most recently created
            file without having to go through this */
        };
    }

    /**
     * Set the HEIGHT and WIDTH attributes for the activity based on the phone size
     */
    public void initializeHeightAndWidth(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.WIDTH = size.x;
        this.HEIGHT = size.y;
    }

    /**
     * Initliaze the Drawer object
     */
    public void initializeDrawer(){
        drawer = new Drawer(this);
        Bitmap result = Bitmap.createBitmap(WIDTH, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        drawer.draw(canvas);
        drawer.setLayoutParams(new LinearLayout.LayoutParams(WIDTH, 400));
        noteLayout.addView(drawer);
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

    /**
     * Send the user an email with the file as an attachment (or whatever other app they use)
     */
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
     * Creates the pitch pitchPipe dialog
     * @return The pitch pitchPipe dialog
     */
    public Dialog createPitchPipeDialog(){
        Dialog dialog = new Dialog(MyActivity.this);
        dialog.setContentView(R.layout.pitchpipe);
        dialog.setTitle(R.string.pitch_pipe);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                pitchPipe.stop();
            }
        });
        createPitchPipeSpinner(dialog);
        return dialog;
    }

    public Dialog createParentDialog(int layout, int title){
        Dialog dialog = new Dialog(mContext);
        dialog.setContentView(layout);
        dialog.setTitle(title);
        return dialog;
    }

    /**
     * Create the spinner to display the pitch pitchPipe values
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
                pitchPipe.play(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Creates a simple dialog that has a list of options, from which the user chooses one and then
     * the dialog is dismissed
     *
     * @param title The string resource containing the title
     * @param options The string-array resource containing the list of options
     * @return The dialog with the list
     */
    public Dialog createListOptionDialog(int title, int options, DialogInterface.OnClickListener d){
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setTitle(title).setSingleChoiceItems(options, -1, d);
        return b.create();
    }

    /**
     * Creates the dialog for the time signature input
     * @return The time signature dialog
     */
    public Dialog createTimeSignatureDialog(final Dialog parentDialog){
        return createListOptionDialog(R.string.time_signature, R.array.time_signature_values,
                new Dialog.OnClickListener(){
                    public void onClick(DialogInterface dialog, int i){
                        rt.updateTimeSignature(i);
                        drawer.updateTimeSignature(i);
                        drawer.invalidate(); // must be called to update display
                        dialog.dismiss();
                        parentDialog.dismiss();
                    }
                });
    }

    /**
     * Creates the dialog for the key signature input
     * @return The key signature dialog
     */
    public Dialog createKeySignatureDialog(final Dialog parentDialog){
        return createListOptionDialog(R.string.key_signature, R.array.key_signatures,
                new Dialog.OnClickListener(){
                    public void onClick(DialogInterface dialog, int i){
                        drawer.updateKeySignature(i);
                        drawer.invalidate();
                        dialog.dismiss();
                        parentDialog.dismiss();
                    }
                });
    }

    public Dialog createTempoDialog(final Dialog parentDialog){
        AlertDialog.Builder b = new AlertDialog.Builder(mContext);
        b.setTitle(R.string.tempo);
        final NumberPicker input = new NumberPicker(mContext);
        input.setMinValue(40);
        input.setMaxValue(200);
        input.setValue(120);
        b.setView(input);

        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bpm = input.getValue();
                rt.updateTempo(bpm);
                dialog.dismiss();
                parentDialog.dismiss();

            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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

                if (toSendEmail){
                    toSendEmail = false;
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
                                    drawer.drawNote(nextPiece);
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