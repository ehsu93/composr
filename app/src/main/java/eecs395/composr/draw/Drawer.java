package eecs395.composr.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import eecs395.composr.MyActivity;
import eecs395.composr.musicUtils.KeySignature;
import eecs395.composr.musicUtils.TimeSignature;

public class Drawer extends View {

    /** Paint object for the staff */
    Paint staffPaint;

    /** Paint object for the clef */
    Paint clefPaint;

    /** Paint object for the time signature */
    Paint timePaint;

    /** Paint object for the notes */
    Paint notePaint;

    Paint testPaint;

    /** Stores all of the Note objects */
    Hashtable<String, Note> notes;

    /** Stores all of the symbol/name pairs */
    Symbols symbols;

    /** Distance between top of canvas and everything drawn */
    final int PADDING_TOP = 50;

    /** Distance between bottom of canvas and everything drawn */
    //final int PADDING_BOTTOM = 20;

    /** Distance between left of canvas and everything drawn */
    final int PADDING_LEFT = 20;

    /** Distance between right of canvas and everything drawn */
    final int PADDING_RIGHT = 20;

    /** Space between the lines in the staff */
    final int SPACE_BETWEEN_LINES = 50;

    /** Width of the canvas */
    final int WIDTH;

    /** The one-letter string that represents the current time signature */
    String timeSignature;

    KeySignature keySignature = KeySignature.C_MAJOR;

    /** The one-letter string that represents the current clef */
    String clef;

    /** The y-offset for the current clef */
    float clefYOffset;

    /** offset for everything, for ledger lines above the staff*/
    float offset;

    float currentX;

    /** The canvas */
    Canvas canvas;

    /** Indicates whether or not music is scrolling */
    boolean scrolling;

    float xoffset = 0;

    float[] sharpHeights = {50, 125, 25, 100, 175, 75, 150};
    float[] flatHeights = {150, 75, 175, 100, 200, 125, 225};

    /**
     * Constructor to initialize all of the default values and Paint objects.
     * Default time signature is 4/4 and default clef is treble.
     *
     * @param ctx The application context
     */
    public Drawer(Context ctx){
        super(ctx);

        // initialize Symbols object
        symbols = new Symbols();

        currentX = PADDING_LEFT;
        xoffset = 0;

        // get width from display metrics
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
        WIDTH = dm.widthPixels;

        // initialize staff paint
        staffPaint = new Paint();
        staffPaint.setStyle(Paint.Style.FILL);
        staffPaint.setColor(Color.BLACK);
        staffPaint.setStrokeWidth(3f);

        // get musical font
        Typeface noteTypeFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/MusiSync.ttf");

        // initialize clef paint
        clefPaint = new Paint();
        clefPaint.setStyle(Paint.Style.FILL);
        clefPaint.setColor(Color.BLACK);
        clefPaint.setTypeface(noteTypeFace);
        clefPaint.setTextSize(8.5f * SPACE_BETWEEN_LINES);

        // initialize note paint
        notePaint = new Paint();
        notePaint.setStyle(Paint.Style.FILL);
        notePaint.setColor(Color.BLACK);
        notePaint.setTypeface(noteTypeFace);
        notePaint.setTextSize(5 * SPACE_BETWEEN_LINES);

        // initialize time signature paint
        timePaint = new Paint();
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setColor(Color.BLACK);
        timePaint.setTypeface(noteTypeFace);
        timePaint.setTextSize(5.75f * SPACE_BETWEEN_LINES);

        testPaint = new Paint();
        testPaint.setStyle(Paint.Style.FILL);
        testPaint.setColor(Color.BLUE);
        testPaint.setTextSize(200);

        // set default time signature to 4/4 time
        timeSignature = symbols.get("4/4");

        // set the default clef to treble
        clef = symbols.get("trebleClef");

        // set the default clef offset to appropriate treble value
        clefYOffset = 275 + PADDING_TOP;

        // offset for ledger lines. Default is no ledger lines, hence offset = 0
        offset = 0;

        // populates the notes hashtable
        createNoteObjects();

        this.scrolling = false;
    }

    /**
     * Method called when the canvas is drawn on the screen, draws the staff, clef, and time
     * signature
     *
     * @param canvas The canvas object to be drawn on
     */
    public void onDraw(Canvas canvas){
        this.canvas = canvas;

        currentX = PADDING_LEFT + xoffset;

        drawStaff();

        // draw the clef on the canvas
        canvas.drawText(clef, currentX, clefYOffset + offset, clefPaint);
        currentX += 150;

        drawKeySignature();

        // draw the time signature on the canvas
        canvas.drawText(timeSignature, currentX,
                4 * SPACE_BETWEEN_LINES + PADDING_TOP + offset, timePaint);
        currentX += 150;


    }

    public void createNoteObjects(){
        try {

            // initialize field
            notes = new Hashtable<>();

            // open notes.csv file, create CSVReader
            InputStream is = MyActivity.getContext().getAssets().open("notes.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));

            // creates a list of arrays representing each row in the CSV
            List<String[]> notesList = reader.readAll();

            // populate notes hashtable
            for (String[] note : notesList) {
                String name = note[0];
                String clefPref = note[1];
                int trebleLedgers = Integer.parseInt(note[2]);
                int bassLedgers = Integer.parseInt(note[3]);
                String spaceOrLine = note[4];
                int index = Integer.parseInt(note[5]);

                notes.put(name, new Note(name, clefPref, trebleLedgers, bassLedgers, spaceOrLine,
                        index));

            }

        } catch (IOException e) {
            // this really should never happen tho
            // TODO: push error to application?
        }

    }

    public void drawNote(Note note, String duration){
        String symbol = note.getSymbol(clef, duration);

        canvas.drawText(symbol, currentX, note.getPosition(clef, SPACE_BETWEEN_LINES) + offset,
                notePaint);

        currentX += 150;
    }

    public void drawNote(String name, String duration){
        Note note = notes.get(name);
        drawNote(note, duration);
        Log.i("testDraw", "note: " + name);
        Log.i("testDraw", "Note: " + note.toString());
        Log.i("testDraw", "dur: " + duration);
    }

    public void drawNote(String name){
        String noteName;
        String duration;
        if (name.indexOf("#") != -1){
            noteName = name.substring(0, 3);
            duration = name.substring(3);
        } else {
            noteName = name.substring(0, 2);
            duration = name.substring(2);
        }

        drawNote(noteName, duration);
    }

    public void draw(String toDraw){
        if (toDraw.indexOf("R") != -1){
            drawRest("");
        } else {
            drawNote(toDraw);
        }
    }

    public void drawRest(String duration) {
        float y = 250;
        canvas.drawText(symbols.get("quarterRest"), currentX, y, notePaint);
        currentX += 150;
    }

    public void drawBarLine(){
        canvas.drawLine(currentX, PADDING_TOP, currentX, PADDING_TOP + 4 * SPACE_BETWEEN_LINES, staffPaint);
    }

    /**
     * Draws the staff onto the canvas, called by onDraw. Uses the values set in the constructor for
     * padding and space between lines
     *
     */
    public void drawStaff(){

        for (int i = 0; i < 5; i++){
            // determine the y position of the line
            float y = PADDING_TOP + i * SPACE_BETWEEN_LINES + offset;

            // inputs to drawLine: X0, Y0, X1, Y1, paint
            canvas.drawLine(PADDING_LEFT, y, WIDTH - PADDING_RIGHT, y, staffPaint);
        }
    }

    /**
     * Given the top and bottom number of a time signature, sets the timeSignature field to the
     * appropriate character. If there is no case for the inputted time signature, the field does
     * not change, but the user will only be able to select from available options so this shouldn't
     * happen.
     *
     * Uses the MusiSync font to display time signature.
     *
     * When using this method, there needs to be a canvas.invalidate() call to reset the canvas.
     *
     * @param t The new time signature
     */
    public void updateTimeSignature(TimeSignature t){
        int top = t.getTop();
        int bottom = t.getBottom();

        timeSignature = symbols.get(Integer.toString(top) + "/" + Integer.toString(bottom));
    }

    public void updateTimeSignature(int i){
        updateTimeSignature(TimeSignature.getTimeSignatureFromIndex(i));
    }

    public void updateKeySignature(int i){
        updateKeySignature(KeySignature.getKeySignatureFromIndex(i));
    }

    public void updateKeySignature(KeySignature k){
        keySignature = k;
    }

    /**
     * Switch the clef displayed from treble to bass or vice versa. After using this method,
     * canvas.invalidate() must be called to update the display
     */
    public void changeClef(){
        if (clef.equals(symbols.get("trebleClef"))){
            // decrease font size
            clefPaint.setTextSize(4.8f * SPACE_BETWEEN_LINES);

            // move upwards
            clefYOffset = 165 + PADDING_TOP;

            // change character
            clef = symbols.get("bassClef");
        } else {
            // increase font size
            clefPaint.setTextSize(8.5f * SPACE_BETWEEN_LINES);

            // move downwards
            clefYOffset = 275 + PADDING_TOP;

            // change character
            clef = symbols.get("trebleClef");
        }

    }

    public void drawKeySignature(){
        int accidentals = keySignature.getAccidentals();
        boolean containsSharps = keySignature.getContainsSharps();
        String symbol;
        float[] heights;

        if (containsSharps){
            symbol = symbols.get("sharp");
            heights = sharpHeights;
        } else {
            symbol = symbols.get("flat");
            heights = flatHeights;
        }

        int i = 0;
        while (accidentals > i){
            canvas.drawText(symbol, currentX - 20, heights[i] + PADDING_TOP, notePaint);
            currentX += 60;
            i++;
        }
    }

    public void toggleScrolling(){
        this.scrolling = true;
    }

    public void scrollLeft(int left){
        this.xoffset -= left;
    }


}