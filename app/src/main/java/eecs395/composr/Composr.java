package eecs395.composr;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

import eecs395.composr.io.FileWriter;
import eecs395.composr.draw.Drawer;
import eecs395.composr.musicUtils.TimeSignature;
import eecs395.composr.ui.UserInterfaceController;

public class Composr extends Activity {

    /** RecordingTask instance */
    private static RecordingTask rt;

    /** Layout where the music will be displayed while recording */
    private static LinearLayout noteLayout;

    /** DrawNotes instance */
    private static Drawer drawer;

    /** mContext */
    private static Context mContext;

    /** Default number of beats per measure */
    private TimeSignature timeSignature = TimeSignature.FOUR_FOUR;

    /** Whether the program is currently listening to input */
    //private static boolean listening;
    //private static boolean hasListened;

    /**
     * Everything that happens when the application is first started
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // this must be the first line

        // set mContext so that other parts of the application can access the context
        mContext = this;

        // defines the screen layout of the application
        setContentView(R.layout.activity_my);

        noteLayout = (LinearLayout) findViewById(R.id.NoteDisplay);

        initializeDrawer(); // Initialize drawer to draw anything necessary on the canvas

        // initialize RecordingTask object with default values
        rt = new RecordingTask(120, timeSignature, drawer);

        UserInterfaceController.addOnClickListeners(this);

        //hasListened = false;

        // initialize audio dispatcher
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050, 1024, 0);
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
     * Initializes the PitchProcessor object
     * @return The properly set up PitchProcessor
     */
    public PitchProcessor getAudioProcessor(){
        return new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent audioEvent) {
                final float pitchInHz = pitchDetectionResult.getPitch();
                final String note = rt.getNoteFromFreq(pitchInHz);

                if (rt.isRecording()){
                    rt.addFreq(pitchInHz);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (rt.needsRedraw()){
                            rt.redraw();
                        }

                        TextView text = (TextView) findViewById(R.id.Pitch);
                        text.setText("" + pitchInHz);
                        TextView text2 = (TextView) findViewById(R.id.Note);
                        text2.setText("" + note);

                        TextView text3 = (TextView) findViewById(R.id.Countdown);
                        text3.setText("" + rt.getStatus());
                    }
                });
            }
        });
    }

    /**
     * Initialize the Drawer object
     */
    public void initializeDrawer(){
        drawer = new Drawer(this);
        noteLayout.addView(drawer);
    }




    // STATIC METHODS

    /**
     * Gives the context of the application so that it is accessible from outside the activity
     * @return The application context
     */
    public static Context getContext(){
        return mContext;
    }

    public static void reset(){

        // there is no last saved file at this time
        FileWriter.setLastSavedFile(null);


        // start or stop the recording task
        rt.toggleRecordingTask();

        // invalidate the drawer object so that the display updates
        drawer.redraw();
    }

    public static void updateTimeSignature(int i){
        rt.updateTimeSignature(i);
        drawer.updateKeySignature(i);
    }

    public static void updateKeySignature(int i){
        drawer.updateKeySignature(i);
    }

    public static void updateTempo(int tempo){
        rt.updateTempo(tempo);
    }

    public static LinearLayout getNoteLayout(){
        return noteLayout;
    }

    //public static void setListening(boolean input) { listening = input; }

    //public static void setHasListened(boolean input) {  hasListened = input; }

    public static void redraw(){
        drawer.redraw();
    }

    public static String getPattern(){
        return rt.generatePattern();
    }

}