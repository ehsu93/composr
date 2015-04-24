package eecs395.composr;

import android.media.MediaPlayer;
import eecs395.composr.R;

public class PitchPipe {
    MediaPlayer currentPlayer;

    public PitchPipe() {
    }

    /**
     * Calls play based on the integer index of the note selected
     * @param noteIndex The index of the note to play when notes are in order
     */
    public void play(int noteIndex){
        // place all the different notes into an array in order
        int[] ids = {R.raw.a,
                R.raw.asharp,
                R.raw.b,
                R.raw.c,
                R.raw.csharp,
                R.raw.d,
                R.raw.dsharp,
                R.raw.e,
                R.raw.f,
                R.raw.fsharp,
                R.raw.g,
                R.raw.gsharp};

        play(MediaPlayer.create(MyActivity.getContext(), ids[noteIndex]));
    }

    /**
     * Starts the play test given a mediaplayer instance
     * @param player The MediaPlayer instance
     */
    public void play(MediaPlayer player) {
        stop();
        this.currentPlayer = player;
        currentPlayer.setLooping(true);
        currentPlayer.start();
    }

    /**
     * Stops the note that is currently being played by the pitch pipe
     */
    public void stop(){
        if (this.currentPlayer != null) {
            this.currentPlayer.pause();
        }
    }
}
