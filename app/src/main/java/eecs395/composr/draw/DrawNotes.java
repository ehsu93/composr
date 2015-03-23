package eecs395.composr.draw;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

public class DrawNotes extends View {

    /** Paint object for the staff */
    Paint staffPaint;

    /** Paint object for the clef */
    Paint clefPaint;

    /** Paint object for the time signature */
    Paint timePaint;

    /** Paint object for the notes */
    Paint notePaint;

    /** Stores all of the Note objects */
    Hashtable<String, Note> notes;

    /** Stores all of the symbol/name pairs */
    Symbols symbols;

    /** Distance between top of canvas and everything drawn */
    final int PADDING_TOP = 20;

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

    /** The one-letter string that represents the current clef */
    String clef;

    /** The y-offset for the current clef */
    float clefYOffset;

    /** offset for everything, for ledger lines above the staff*/
    float offset;

    /** The context of the application */
    Context ctx;

    /**
     * Constructor to initialize all of the default values and Paint objects.
     * Default time signature is 4/4 and default clef is treble.
     *
     * @param ctx The application context
     */
    public DrawNotes(Context ctx){
        super(ctx);

        // initialize Symbols object
        Symbols symbols = new Symbols();

        // get width from display metrics
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
        WIDTH = dm.widthPixels;

        // initiatilize staff paint
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
        clefPaint.setTextSize(7.5f * SPACE_BETWEEN_LINES);

        // initialize note paint
        notePaint = new Paint();
        notePaint.setStyle(Paint.Style.FILL);
        notePaint.setColor(Color.BLUE);
        notePaint.setTypeface(noteTypeFace);
        notePaint.setTextSize(5 * SPACE_BETWEEN_LINES);

        // initialize time signature paint
        timePaint = new Paint();
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setColor(Color.BLACK);
        timePaint.setTypeface(noteTypeFace);
        timePaint.setTextSize(5.75f * SPACE_BETWEEN_LINES);

        // set default time signature to 4/4 time
        timeSignature = symbols.get("4/4");

        // set the default clef to treble
        clef = symbols.get("trebleClef");

        // set the default clef offset to approriate treble value
        clefYOffset = 5.65f * SPACE_BETWEEN_LINES;

        // offset for ledger lines. Default is no ledger lines, hence offset = 0
        offset = 0;

        // save the context as a field
        this.ctx = ctx;
    }

    /**
     * Method called when the canvas is drawn on the screen, draws the staff, clef, and time
     * signature
     *
     * @param canvas The canvas object to be drawn on
     */
    public void onDraw(Canvas canvas){
        drawStaff(canvas);

        // draw the clef on the canvas
        canvas.drawText(clef, PADDING_LEFT, clefYOffset + offset, clefPaint);

        // draw the time signature on the canvas
        canvas.drawText(timeSignature, PADDING_LEFT + SPACE_BETWEEN_LINES * 2.5f,
                4 * SPACE_BETWEEN_LINES + PADDING_TOP + offset, timePaint);

        // populates the notes hashtable
        createNoteObjects(ctx);

        // to test that drawing a note will work
        drawTestNotes(canvas);
    }

    public void createNoteObjects(Context c){
        try {

            // initialize field
            notes = new Hashtable<>();

            // open notes.csv file, create CSVReader
            InputStream is = c.getAssets().open("notes.csv");
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

    public void drawTestNotes(Canvas canvas){
        Note d5 = notes.get("D5");
        canvas.drawText(d5.getSymbol(clef), 300, d5.getPosition(clef, SPACE_BETWEEN_LINES)+ offset,
                notePaint);
    }

    /**
     * Draws the staff onto the canvas, called by onDraw. Uses the values set in the constructor for
     * padding and space between lines
     *
     * @param canvas The canvas object to be drawn on
     */
    public void drawStaff(Canvas canvas){

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
     * @param top The top number of the time signature
     * @param bottom The bottom number of the time signature
     */
    public void updateTimeSignature(int top, int bottom){
        timeSignature = symbols.get(Integer.toString(top) + "/" + Integer.toString(bottom));
    }

    /**
     * Switch the clef displayed from treble to bass or vice versa. After using this method,
     * canvas.invalidate() must be called to update the display
     */
    public void changeClef(){
        if (clef.equals("g")){
            // decrease font size
            clefPaint.setTextSize(4.5f * SPACE_BETWEEN_LINES);

            // move upwards
            clefYOffset = 3.7f * SPACE_BETWEEN_LINES;

            // change character
            clef = symbols.get("bassClef");
        } else {
            // increase font size
            clefPaint.setTextSize(7.5f * SPACE_BETWEEN_LINES);

            // move downwards
            clefYOffset = 5.65f * SPACE_BETWEEN_LINES;

            // change character
            clef = symbols.get("trebleClef");
        }
    }

}
