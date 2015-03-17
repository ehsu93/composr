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
    Paint staffPaint;
    Paint clefPaint;
    Paint timePaint;

    final int PADDING_TOP;
    //final int PADDING_BOTTOM;
    final int PADDING_LEFT;
    final int PADDING_RIGHT;
    final int SPACE_BETWEEN_LINES;
    final int HEIGHT;
    final int WIDTH;

    String timeSignature;

    public DrawNotes(Context ctx){
        super(ctx);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)ctx).getWindowManager().getDefaultDisplay().getMetrics(dm);

        HEIGHT = dm.heightPixels;
        WIDTH = dm.widthPixels;

        PADDING_TOP = 20;
        //final int PADDING_BOTTOM = 20;
        PADDING_LEFT = 20;
        PADDING_RIGHT = 20;
        SPACE_BETWEEN_LINES = 50;

        staffPaint = new Paint();
        staffPaint.setStyle(Paint.Style.FILL);
        staffPaint.setColor(Color.BLUE);
        staffPaint.setStrokeWidth(3f);

        // get musical font
        Typeface noteTypeFace = Typeface.createFromAsset(ctx.getAssets(), "fonts/MusiSync.ttf");

        clefPaint = new Paint();
        clefPaint.setStyle(Paint.Style.FILL);
        clefPaint.setColor(Color.RED);
        clefPaint.setTypeface(noteTypeFace);
        clefPaint.setTextSize(7.5f * SPACE_BETWEEN_LINES);

        timePaint = new Paint();
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setColor(Color.BLACK);
        timePaint.setTypeface(noteTypeFace);
        timePaint.setTextSize(5.75f * SPACE_BETWEEN_LINES);

        timeSignature = "$"; // default value of 4/4
    }

    public void onDraw(Canvas canvas){
        int line1Y = PADDING_TOP + 0 * SPACE_BETWEEN_LINES;
        int line2Y = PADDING_TOP + 1 * SPACE_BETWEEN_LINES;
        int line3Y = PADDING_TOP + 2 * SPACE_BETWEEN_LINES;
        int line4Y = PADDING_TOP + 3 * SPACE_BETWEEN_LINES;
        int line5Y = PADDING_TOP + 4 * SPACE_BETWEEN_LINES;

        // barlines
        canvas.drawLine(PADDING_LEFT, line1Y, WIDTH - PADDING_RIGHT, line1Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line2Y, WIDTH - PADDING_RIGHT, line2Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line3Y, WIDTH - PADDING_RIGHT, line3Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line4Y, WIDTH - PADDING_RIGHT, line4Y, staffPaint);
        canvas.drawLine(PADDING_LEFT, line5Y, WIDTH - PADDING_RIGHT, line5Y, staffPaint);

        // clef
        canvas.drawText("g", PADDING_LEFT, 5.65f * SPACE_BETWEEN_LINES, clefPaint);

        // time signature
        // K = 4/2, L = 3/2,
        // % = 5/4, $ = 4/4, # = 3/4, @ = 2/4,
        // k = 2/8, P = 6/8
        canvas.drawText(timeSignature, PADDING_LEFT + SPACE_BETWEEN_LINES * 2.5f, 4 * SPACE_BETWEEN_LINES + PADDING_TOP, timePaint);
    }

    public void updateTimeSignature(int top, int bottom){
        if (bottom == 2) {
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
}
