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

        canvas.drawText("$", PADDING_LEFT + SPACE_BETWEEN_LINES * 2.5f, 4 * SPACE_BETWEEN_LINES + PADDING_TOP, timePaint);
    }

    public void updateTimeSignature(int top, int bottom){

        // draw top number

        // draw bottom number
    }
}
