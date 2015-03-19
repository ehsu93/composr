package kenthacks.eric.composr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.View;

public class DrawNotes extends View {

    /** Paint object for the staff */
    Paint staffPaint;

    /** Paint object for the clef */
    Paint clefPaint;

    /** Paint object for the time signature */
    Paint timePaint;

    /** Distance between top of canvas and everything drawn */
    final int PADDING_TOP;

    /** Distance between bottom of canvas and everything drawn */
    final int PADDING_BOTTOM;

    /** Distance between left of canvas and everything drawn */
    final int PADDING_LEFT;

    /** Distance between right of canvas and everything drawn */
    final int PADDING_RIGHT;

    /** Space between the lines in the staff */
    final int SPACE_BETWEEN_LINES;

    /** Width of the canvas */
    final int WIDTH;

    /** The one-letter string that represents the current time signature */
    String timeSignature;

    /** The one-letter string that represents the current clef */
    String clef;

    /** The y-offset for the current clef */
    float clefYOffset;

    /**
     * Constructor to initialize all of the default values and Paint objects.
     * Default time signature is 4/4 and default clef is treble.
     *
     * @param ctx The application context
     */
    public DrawNotes(Context ctx){
        super(ctx);

        // get height and width from display metrics
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);
        WIDTH = dm.widthPixels;

        // set the padding values
        PADDING_TOP = 20;
        PADDING_BOTTOM = 20;
        PADDING_LEFT = 20;
        PADDING_RIGHT = 20;

        // determine space between lines
        SPACE_BETWEEN_LINES = 50;

        // initiatilize staff paint
        staffPaint = new Paint();
        staffPaint.setStyle(Paint.Style.FILL);
        staffPaint.setColor(Color.BLUE);
        staffPaint.setStrokeWidth(3f);

        // get musical font
        Typeface noteTypeFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/MusiSync.ttf");

        // initialize clef paint
        clefPaint = new Paint();
        clefPaint.setStyle(Paint.Style.FILL);
        clefPaint.setColor(Color.RED);
        clefPaint.setTypeface(noteTypeFace);
        clefPaint.setTextSize(7.5f * SPACE_BETWEEN_LINES);

        // initialize time signature paint
        timePaint = new Paint();
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setColor(Color.BLACK);
        timePaint.setTypeface(noteTypeFace);
        timePaint.setTextSize(5.75f * SPACE_BETWEEN_LINES);

        // set default time signature to 4/4 time
        timeSignature = "$";

        // set the default clef to treble
        clef = "g";

        // set the default clef offset to approriate treble value
        clefYOffset = 5.65f * SPACE_BETWEEN_LINES;
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
        canvas.drawText(clef, PADDING_LEFT, clefYOffset, clefPaint);

        // draw the time signature on the canvas
        canvas.drawText(timeSignature, PADDING_LEFT + SPACE_BETWEEN_LINES * 2.5f,
                4 * SPACE_BETWEEN_LINES + PADDING_TOP, timePaint);
    }

    /**
     * Draws the staff onto the canvas, called by onDraw. Uses the values set in the constructor for
     * padding and space between lines
     *
     * @param canvas The canvas object to be drawn on
     */
    public void drawStaff(Canvas canvas){

        // determine Y positions of each line of the clef
        int line1Y = PADDING_TOP;
        int line2Y = PADDING_TOP + SPACE_BETWEEN_LINES;
        int line3Y = PADDING_TOP + 2 * SPACE_BETWEEN_LINES;
        int line4Y = PADDING_TOP + 3 * SPACE_BETWEEN_LINES;
        int line5Y = PADDING_TOP + 4 * SPACE_BETWEEN_LINES;

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
        if (bottom == 2 ) {
            if (top == 4) {         // 4/2 time
                timeSignature = "K";
            } else if (top == 3) {  // 3/2 time
                timeSignature = "L";
            }
        } else if (bottom == 4) {
            if (top == 6) {         // 6/4 time
                timeSignature = "^";
            } else if (top == 5) {  // 5/4 time
                timeSignature = "%";
            } else if (top == 4) {  // 4/4 time
                timeSignature = "$";
            } else if (top == 3) {  // 3/4 time
                timeSignature = "#";
            } else if (top == 2) {  // 2/4 time
                timeSignature = "@";
            }
        } else if (bottom == 8) {
            if (top == 9) {         // 9/8 time
                timeSignature = "(";
            } else if (top == 6) {  // 6/8 time
                timeSignature = "P";
            } else if (top == 3) {  // 3/8 time
                timeSignature = ")";
            } else if (top == 2) {  // 2/8 time
                timeSignature = "k";
            }
        }
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
            clef = "?";
        } else {
            // increase font size
            clefPaint.setTextSize(7.5f * SPACE_BETWEEN_LINES);

            // move downwards
            clefYOffset = 5.65f * SPACE_BETWEEN_LINES;

            // change character
            clef = "g";
        }
    }
}
