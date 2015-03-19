package eecs395.composr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Hashtable;

public class DrawNotes extends View {

    /** Paint object for the staff */
    Paint staffPaint;

    /** Paint object for the clef */
    Paint clefPaint;

    /** Paint object for the time signature */
    Paint timePaint;

    Paint notePaint;

    Hashtable<String, Note> notes = new Hashtable<>();

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

    Symbols symbols;

    /**
     * Constructor to initialize all of the default values and Paint objects.
     * Default time signature is 4/4 and default clef is treble.
     *
     * @param ctx The application context
     */
    public DrawNotes(Context ctx){
        super(ctx);

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

        symbols = new Symbols();
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

        createNoteObjects();
        drawTestNotes(canvas);
    }

    public void createNoteObjects(){
        notes.put("E5", new Note("E5", 4.55f, symbols.get("quarterNoteStemDown")));
        notes.put("C5", new Note("C5", 3.55f, symbols.get("quarterNoteStemDown")));
        notes.put("A4", new Note("A4", 3.95f, symbols.get("quarterNoteStemUp")));
        notes.put("F4", new Note("F4", 4.95f, symbols.get("quarterNoteStemUp")));
    }

    public void drawTestNotes(Canvas canvas){
        Note e5 = notes.get("E5");
        Note c5 = notes.get("C5");
        Note a4 = notes.get("A4");
        Note f4 = notes.get("F4");

        canvas.drawText(e5.getSymbol(), 300, e5.getPosition() * SPACE_BETWEEN_LINES + offset, notePaint);
        canvas.drawText(c5.getSymbol(), 400, c5.getPosition() * SPACE_BETWEEN_LINES + offset, notePaint);
        canvas.drawText(a4.getSymbol(), 500, a4.getPosition() * SPACE_BETWEEN_LINES + offset, notePaint);
        canvas.drawText(f4.getSymbol(), 600, f4.getPosition() * SPACE_BETWEEN_LINES + offset, notePaint);
    }

    /**
     * Draws the staff onto the canvas, called by onDraw. Uses the values set in the constructor for
     * padding and space between lines
     *
     * @param canvas The canvas object to be drawn on
     */
    public void drawStaff(Canvas canvas){

        // determine Y positions of each line of the clef
        float line1Y = PADDING_TOP + offset;
        float line2Y = PADDING_TOP + SPACE_BETWEEN_LINES + offset;
        float line3Y = PADDING_TOP + 2 * SPACE_BETWEEN_LINES + offset;
        float line4Y = PADDING_TOP + 3 * SPACE_BETWEEN_LINES + offset;
        float line5Y = PADDING_TOP + 4 * SPACE_BETWEEN_LINES + offset;

        // draw the barlines
        // inputs to drawLine: X0, Y0, X1, Y1, paint
        canvas.drawLine(PADDING_LEFT, line1Y, WIDTH - PADDING_RIGHT, line1Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line2Y, WIDTH - PADDING_RIGHT, line2Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line3Y, WIDTH - PADDING_RIGHT, line3Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line4Y, WIDTH - PADDING_RIGHT, line4Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line5Y, WIDTH - PADDING_RIGHT, line5Y, staffPaint);
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

    public static class Symbols{

        Hashtable<String, String> symbols;

        Symbols(){
            this.symbols = new Hashtable<String, String>() {{
                put("trebleClef", "g");
                put("bassClef", "?");

                // notes
                put("wholeNote", "");

                put("halfNoteStemDown", "");
                put("halfNoteStemUp", "");

                put("quarterNoteStemDown", "ö");
                put("quarterNoteStemUp", "");

                put("eigthNoteStemDown", "");
                put("eightNoteStemUp", "");

                put("sixteenthNoteStemDown", "");
                put("sixteenthNoteStemUp", "");

                // time signatures
                put("4/2", "K");
                put("3/2", "L");

                put("6/4", "^");
                put("5/4", "%");
                put("4/4", "$");
                put("3/4", "#");
                put("2/4", "@");

                put("9/8", "(");
                put("6/8", "P");
                put("3/8", ")");
                put("2/8", "k");

            }};
        }

        String get(String k){
            return symbols.get(k);
        }
    }
}
