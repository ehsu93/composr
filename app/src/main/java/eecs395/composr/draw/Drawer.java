package eecs395.composr.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import eecs395.composr.Composr;
import eecs395.composr.musicUtils.KeySignature;
import eecs395.composr.musicUtils.TimeSignature;
import eecs395.composr.process.RecordedNote;

public class Drawer extends View {

    static Typeface noteTypeFace = Typeface.createFromAsset(Composr.getContext().getAssets(),
            "fonts/MusiSync.ttf");

    static Paint staffPaint = createPaint(Color.BLACK, 1, null);
    static Paint clefPaint = createPaint(Color.BLACK, 425, noteTypeFace);
    static Paint notePaint = createPaint(Color.BLACK, 250, noteTypeFace);
    static Paint timePaint = createPaint(Color.BLACK, 287.5f, noteTypeFace);
    // static Paint testPaint = createPaint(Color.BLUE, 200, null);

    /** Stores all of the Note objects */
    Hashtable<String, Note> notes;

    /** Distance between top of canvas and everything drawn */
    static final int PADDING_TOP = 75;

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
    float X_INCREMENT = 100;

    /** The current x location at which to draw an object on the canvas */
    float xCursor;

    /** The canvas */
    Canvas canvas;

    /** Indicates whether or not music is scrolling */
    //boolean scrolling;

    float xoffset = 0;
    int noteIndex;

    ArrayList<RecordedNote> notesToDraw;

    boolean redraw = false;

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

        xCursor = PADDING_LEFT;

        // get width from display metrics
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
        WIDTH = dm.widthPixels;

        // set default time signature to 4/4 time
        timeSignature = Symbols.get("4/4");

        // set the default clef to treble
        clef = Symbols.get("trebleClef");

        // set the default clef offset to appropriate treble value
        clefYOffset = 275 + PADDING_TOP;

        // offset for ledger lines. Default is no ledger lines, hence offset = 0
        offset = 0;

        // populates the notes hashtable
        notes = Note.getAllNotes();

        notesToDraw = new ArrayList<>();

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
    public static Paint createPaint(int color, float textSize, Typeface typeface){

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
     * Method called when the canvas is drawn on the screen, draws the staff, clef, and time
     * signature
     *
     * @param canvas The canvas object to be drawn on
     */
    public void onDraw(Canvas canvas){
        this.canvas = canvas;

        drawInitial();


        int drawnCount = 0;
        for (int i = 0; i < noteIndex; i++){
            RecordedNote n = notesToDraw.get(i);
            draw(n);
            drawnCount += n.getDuration();
            if (drawnCount % 64 == 0){
                drawBarLine();
            }
        }
        //test();
    }

    public void test(){
        //canvas.drawText("s", xCursor, 400, notePaint);
        //drawNote(new RecordedNote("A5", 4));
        //drawNote(new RecordedNote("A4", 2));
        //drawNote(new RecordedNote("E4", 1));

        //drawNote(new RecordedNote("E5", 1));
        //drawNote(new RecordedNote("B4", 1));

        //canvas.drawText("s", 200, 100, notePaint);
        //canvas.drawText("s", 300, 125, notePaint); // b5
        //canvas.drawText("s", 400, 150, notePaint); //
        //canvas.drawText("s", 500, 175, notePaint); //
    }

    public void drawInitial(){
        xCursor = PADDING_LEFT - xoffset;

        drawStaff();

        // draw the clef on the canvas
        canvas.drawText(clef, xCursor, clefYOffset + offset, clefPaint);
        moveXCursor(150);

        drawKeySignature();

        // draw the time signature on the canvas
        canvas.drawText(timeSignature, xCursor,
                4 * SPACE_BETWEEN_LINES + PADDING_TOP + offset, timePaint);
        moveXCursor(150);
    }

    /**
     * Draws either a note or a rest, input comes from the generated pattern
     * Input will be in the form of "C#5i" or "D3e." if it is a note, "R" for a rest
     *
     * @param toDraw The s
     */
    public void draw(RecordedNote toDraw){


        if (toDraw.getPitch().equals("R")){
            drawRest(RecordedNote.getDurationString(toDraw.getDuration()));
        } else {
            drawNotes(toDraw);
        }
    }


    public void addNote(RecordedNote n){
        notesToDraw.add(n);
    }

    public void drawNotes(RecordedNote recordedNote){

        LinkedList<RecordedNote> dividedNote = recordedNote.divideNotes();

        for (RecordedNote n : dividedNote) {
            drawNote(n);
        }
    }

    private void drawNote(RecordedNote recordedNote){
        Note note = notes.get(recordedNote.getPitch());

        Log.i("composr-draw-test", RecordedNote.getDurationString(recordedNote.getDuration()));

        // get the symbol associated with a note of the given duration
        int x = recordedNote.getDuration();
        String z =  RecordedNote.getDurationString(x);
        String s = note.getSymbol(clef, z);

        Log.i("composr-draw-test", s);
        Log.i("composr-draw-test", "" + xCursor);
        redraw = true;

        //canvas.drawText("s", 400, 350, notePaint);

        Log.i("composr-note-test", "name: " + note.getName() + ", pos: " + note.getPosition(clef, 0));

        float y = note.getPosition(clef, SPACE_BETWEEN_LINES) + offset + PADDING_TOP;

        if (recordedNote.getPitch().contains("#")){
            canvas.drawText("B", xCursor, y, notePaint);
            moveXCursor(60);
        }

        // draw the note on the canvas
        canvas.drawText(s, xCursor, y, notePaint);

        moveXCursor();
    }


    /**
     * Draws a rest on the canvas
     *
     * @param duration The duration of the rest to be drawn
     */
    public void drawRest(String duration) {

        String key = Symbols.durationToLongString(duration) + "Rest";
        canvas.drawText(Symbols.get(key), xCursor , 250, notePaint);
        moveXCursor();
    }

    /**
     * Draw a bar line on the canvas to separate different measures
     */
    public void drawBarLine(){
        canvas.drawLine(xCursor , PADDING_TOP,
                xCursor , PADDING_TOP + 4 * SPACE_BETWEEN_LINES,
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

        timeSignature = Symbols.get(Integer.toString(top) + "/" + Integer.toString(bottom));
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
        if (clef.equals(Symbols.get("trebleClef"))){
            // decrease font size
            clefPaint.setTextSize(4.8f * SPACE_BETWEEN_LINES);

            // move upwards
            clefYOffset = 165 + PADDING_TOP;

            // change character
            clef = Symbols.get("bassClef");
        } else {
            // increase font size
            clefPaint.setTextSize(8.5f * SPACE_BETWEEN_LINES);

            // move downwards
            clefYOffset = 275 + PADDING_TOP;

            // change character
            clef = Symbols.get("trebleClef");
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
            symbol = Symbols.get("sharp");
        } else {
            symbol = Symbols.get("flat");
        }

        int i = 0;
        while (accidentals > i){

            canvas.drawText(symbol,
                    xCursor - 20 ,
                    heights[i] + PADDING_TOP - 25,
                    notePaint);

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
        return clef == Symbols.get("trebleClef");
    }

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
        if (xCursor >= 600){
            xoffset += 20;
        } else {
            xCursor += distance;
        }
    }

    public void redraw(){
        noteIndex = notesToDraw.size();
        invalidate();
    }

    public void reset(){
        Path path = new Path();
        Paint clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(0, 0, 0, 0, clearPaint);
        xCursor = PADDING_LEFT;
        notesToDraw = new ArrayList<>();
        xoffset = 0;

        drawInitial();
    }

}