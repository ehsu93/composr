package eecs395.composr;

import android.content.Context;
import android.media.MediaPlayer;

import eecs395.proj.composr.R;

/**
 * Created by Eric on 3/5/2015.
 */
public class PitchPipe {
    boolean playing;
    MediaPlayer tone;
    Context ctx;

    public PitchPipe(Context ctx, String pitch) {
        this.ctx = ctx;
        this.playing = false;
        switch(pitch) {
            case "A":
                tone.create(ctx, R.raw.a);
                break;
            case "A#":
                tone.create(ctx, R.raw.asharp);
                break;
            case "B":
                tone.create(ctx, R.raw.b);
                break;
            case "C":
                tone.create(ctx, R.raw.c);
                break;
            case "C#":
                tone.create(ctx, R.raw.csharp);
                break;
            case "D":
                tone.create(ctx, R.raw.d);
                break;
            case "D#":
                tone.create(ctx, R.raw.dsharp);
                break;
            case "E":
                tone.create(ctx, R.raw.e);
                break;
            case "F":
                tone.create(ctx, R.raw.f);
                break;
            case "F#":
                tone.create(ctx, R.raw.fsharp);
                break;
            case "G":
                tone.create(ctx, R.raw.g);
                break;
            case "G#":
                tone.create(ctx, R.raw.gsharp);
                break;
            default:
                break;
        }

    }

    public void togglePlayTest(MediaPlayer play) {

        play.setLooping(true);
        this.playing = !this.playing;
        if(this.playing) {
            play.start();
        }
        else {
            play.pause();
        }
    }

    public void togglePlay(String pitch) {
        this.playing = !this.playing;
//        if(getPitch(pitch) == -1) {
//            return 0;
//        }
//        if(pitch == "A") tone.create(ctx, R.raw.a);
        /*
        switch(pitch) {
            case "A":
                tone.create(ctx, R.raw.a);
                break;
            case "A#":
                tone.create(ctx, R.raw.asharp);
                break;
            case "B":
                tone.create(ctx, R.raw.b);
                break;
            case "C":
                tone.create(ctx, R.raw.c);
                break;
            case "C#":
                tone.create(ctx, R.raw.csharp);
                break;
            case "D":
                tone.create(ctx, R.raw.d);
                break;
            case "D#":
                tone.create(ctx, R.raw.dsharp);
                break;
            case "E":
                tone.create(ctx, R.raw.e);
                break;
            case "F":
                tone.create(ctx, R.raw.f);
                break;
            case "F#":
                tone.create(ctx, R.raw.fsharp);
                break;
            case "G":
                tone.create(ctx, R.raw.g);
                break;
            case "G#":
                tone.create(ctx, R.raw.gsharp);
                break;
            default:
                break;
        }
        */

        //tone.create(ctx, getPitch());

        if(this.playing) {
            tone.start();
        }
        else {
            tone.stop();
        }
//        return 1;
    }

    public int getPitch(String pitch) {
        switch(pitch) {
            case "A":   return R.raw.a;
            case "A#":  return R.raw.asharp;
            case "B":   return R.raw.b;
            case "C":   return R.raw.c;
            case "C#":  return R.raw.csharp;
            case "D":   return R.raw.d;
            case "D#":  return R.raw.dsharp;
            case "E":   return R.raw.e;
            case "F":   return R.raw.f;
            case "F#":  return R.raw.fsharp;
            case "G":   return R.raw.g;
            case "G#":  return R.raw.gsharp;
            default:    return -1;
        }
    }

}
