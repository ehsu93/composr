package eecs395.composr.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import eecs395.composr.Composr;
import eecs395.composr.musicUtils.KeySignature;
import eecs395.composr.musicUtils.TimeSignature;
import eecs395.composr.process.RecordedNote;

public class Drawer extends View {

    /** Paint object for the staff */
    Paint staffPaint;

    /** Paint object for the clef */
    static Paint clefPaint;

    /** Paint object for the time signature */
    Paint timePaint;

    /** Paint object for the notes */
    Paint notePaint;

    Paint testPaint;

    /** Stores all of the Note objects */
    Hashtable<String, Note> notes;

    /** Stores all of the symbol/name pairs */
    static Symbols symbols;

    /** Distance between top of canvas and everything drawn */
    static final int PADDING_TOP = 75;

    /** Distance between bottom of canvas and everything drawn */
    //final int PADDING_BOTTOM = 20;

    /** Distance between left of canvas and everything drawn */
    final int PADDING_LEFT = 20;

    /** Distance between right of canvas and everything drawn */
    final int PADDING_RIGHT = 20;

    /** Space between the lines in the staff */
    static final int SPACE_BETWEEN_LINES = 50;

    /** Width of the canvas */
    final int WIDTH;

    /** The one-letter string that represents the current time signature */
    String timeSignature;

    KeySignature keySignature = KeySignature.C_MAJOR;

    /** The one-letter string that represents the current clef */
    static String clef;

    /** The y-offset for the current clef */
    static float clefYOffset;

    /** offset for everything, for ledger lines above the staff*/
    float offset;

    /** The amount to move the xCursor by after something is drawn on the canvas */
    float X_INCREMENT = 50;

    /** The current x location at which to draw an object on the canvas */
    float xCursor;

    /** The canvas */
    Canvas canvas;

    /** Indicates whether or not music is scrolling */
    //boolean scrolling;

    float xoffset = 0;

    float[] trebleSharpHeights = {50, 125, 25, 100, 175, 75, 150};
    float[] trebleFlatHeights = {150, 75, 175, 100, 200, 125, 225};
    float[] bassSharpHeights = {100, 175, 75, 150, 225, 125, 200};
    float[] bassFlatHeights = {200, 125, 225, 150, 250, 175, 275};

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

        xCursor = PADDING_LEFT;
        xoffset = 0;

        // get width from display metrics
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
        WIDTH = dm.widthPixels;

        // get musical font
        Typeface noteTypeFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/MusiSync.ttf");

        // initialize the Paint objects
        staffPaint = createPaint(Color.BLACK, 1, null);
        clefPaint = createPaint(Color.BLACK, 425, noteTypeFace);
        notePaint = createPaint(Color.BLACK, 250, noteTypeFace);
        timePaint = createPaint(Color.BLACK, 287.5f, noteTypeFace);
        testPaint = createPaint(Color.BLUE, 200, null);

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

        // this.scrolling = false; Not implemented yet

        Bitmap result = Bitmap.createBitmap(WIDTH, 400, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        this.draw(canvas);
        this.setLayoutParams(new LinearLayout.LayoutParams(WIDTH, 400));

    }

    /**
     * Create a paint object based on the given inputs
     *
     * @param color The color to paint with
     * @param textSize The text size of the Paint object
     * @param typeface The typeface of the Paint object, null for default
     * @return An instance of Paint with the given values
     */
    public Paint createPaint(int color, float textSize, Typeface typeface){

        Paint p = new Paint();

        p.setStyle(Paint.Style.FILL);
        p.setColor(color);
        p.setTextSize(textSize);

        if (typeface != null){
            p.setTypeface(typeface);
        }

        return p;
    }

    /**
     * Initializes the hash table of Notes that is used to place the notes on the staff
     */
    public void createNoteObjects(){
        try {

            // initialize field
            notes = new Hashtable<>();

            // open notes.csv file, create CSVReader
            InputStream is = Composr.getContext().getAssets().open("notes.csv");
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
            // TODO
            // Have never experienced the try block not working
        }

    }


    /**
     * Method called when the canvas is drawn on the screen, draws the staff, clef, and time
     * signature
     *
     * @param canvas The canvas object to be drawn on
     */
    public void onDraw(Canvas canvas){
        this.canvas = canvas;
        xCursor = PADDING_LEFT + xoffset;

        drawStaff();

        // draw the clef on the canvas
        canvas.drawText(clef, xCursor, clefYOffset + offset, clefPaint);
        moveXCursor(150);

        drawKeySignature();

        // draw the time signature on the canvas
        canvas.drawText(timeSignature, xCursor,
                4 * SPACE_BETWEEN_LINES + PADDING_TOP + offset, timePaint);
        moveXCursor(150);

        //test();
    }

    public void test(){
        canvas.drawText(symbols.get("eighthRest"), xCursor, 250, notePaint);
        moveXCursor(150);

        canvas.drawText(symbols.get("eighthNoteStemUp"), xCursor, 225, notePaint);
        moveXCursor(150);

        canvas.drawText(symbols.get("quarterNoteStemDown"), xCursor, 300, notePaint);
        moveXCursor(150);

        canvas.drawText(symbols.get("eighthNoteStemDown"), xCursor, 275, notePaint);
        moveXCursor(150);

        //canvas.drawText(symbols.get("eighthRest"), xCursor, 250, notePaint);
    }

    /**
     * Draws either a note or a rest, input comes from the generated pattern
     * Input will be in the form of "C#5i" or "D3e." if it is a note, "R" for a rest
     *
     * @param toDraw The s
     */
    /*
    public void draw(String toDraw){

        if (toDraw.contains("R")){
            drawRest("");
        } else {
            drawNote(toDraw);
        }
    }*/

    /**
     * Draw a note given a string from the pattern
     * Input will be in the form of "C#5i" or "D3e."
     *
    // * @param name The String taken from the pattern created in the Activity
     */
    /*
    public void drawNote(String name){

        String noteName;
        String duration;

        if (name.contains("#")){
            noteName = name.substring(0, 3);
            duration = name.substring(3);
        } else {
            noteName = name.substring(0, 2);
            duration = name.substring(2);
        }

        drawNote(noteName, duration);
    }*/

    public void drawNote(RecordedNote recordedNote){
        Note note = notes.get(recordedNote.getPitch());

        // get the symbol associated with a note of the given duration
        String symbol = note.getSymbol(clef, recordedNote.getDuration());

        // draw the note on the canvas
        canvas.drawText(symbol, xCursor,
                note.getPosition(clef, SPACE_BETWEEN_LINES) + offset + PADDING_TOP, notePaint);

        moveXCursor();
    }

    /**
     * Draws a rest on the canvas
     *
     * @param duration The duration of the rest to be drawn
     */
    public void drawRest(String duration) {
        canvas.drawText(symbols.get("quarterRest"), xCursor, 250, notePaint);
        moveXCursor();
    }

    /**
     * Draw a bar line on the canvas to separate different measures
     */
    public void drawBarLine(){
        canvas.drawLine(xCursor, PADDING_TOP, xCursor, PADDING_TOP + 4 * SPACE_BETWEEN_LINES,
                staffPaint);
        moveXCursor();
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
     * @param i The index of the time signature in the enum
     */
    public void updateTimeSignature(int i){
        TimeSignature t = TimeSignature.getTimeSignatureFromIndex(i);

        int top = t.getTop();
        int bottom = t.getBottom();

        timeSignature = symbols.get(Integer.toString(top) + "/" + Integer.toString(bottom));
    }

    /**
     * Update the key signature field
     *
     * @param i The index of the key signature in the enum
     */
    public void updateKeySignature(int i){
        keySignature = KeySignature.getKeySignatureFromIndex(i);
    }

    /**
     * Switch the clef displayed from treble to bass or vice versa. After using this method,
     * canvas.invalidate() must be called to update the display
     */
    public static void changeClef(){
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

    /**
     * Draw the key signature on the canvas
     */
    public void drawKeySignature(){
        int accidentals = keySignature.getAccidentals();
        boolean containsSharps = keySignature.getContainsSharps();
        float[] heights;
        String symbol;

        heights = getAccidentalHeights(containsSharps);

        if (containsSharps){
            symbol = symbols.get("sharp");
        } else {
            symbol = symbols.get("flat");
        }

        int i = 0;
        while (accidentals > i){
            canvas.drawText(symbol, xCursor - 20, heights[i] + PADDING_TOP - 25, notePaint);
            moveXCursor(60);
            i++;
        }
    }

    /**
     * Return the appropriate array of heights based on whether the key has sharps and the current
     * clef
     *
     * @param containsSharps Whether or not the key contains sharps
     */
    public float[] getAccidentalHeights(boolean containsSharps){
        if (containsSharps){
            if (isTrebleClef()) {
                return trebleSharpHeights;
            } else {
                return bassSharpHeights;
            }
        } else {
            if (isTrebleClef()) {
                return trebleFlatHeights;
            } else {
                return bassFlatHeights;
            }
        }
    }

    /**
     * Returns whether the current clef is the treble clef
     * @return Whether the current clef is the treble clef
     */
    public boolean isTrebleClef(){
        return clef == symbols.get("trebleClef");
    }

    /* Not implemented yet
    public void toggleScrolling(){
        this.scrolling = true;
    }

    public void scrollLeft(int left){
        this.xoffset -= left;
    }*/

    /**
     * Move the xCursor the default x increment
     */
    public void moveXCursor(){
        moveXCursor(X_INCREMENT);
    }

    /**
     * Move the xCursor along a custom increment
     * @param distance The distance to move the x cursor by
     */
    public void moveXCursor(float distance){
        xCursor += distance;
    }

    public void redraw(){
        invalidate();
    }
}