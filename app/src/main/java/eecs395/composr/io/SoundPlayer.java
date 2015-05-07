package eecs395.composr.io;

import android.media.MediaPlayer;

import eecs395.composr.Composr;
import eecs395.composr.R;

public class SoundPlayer {

    public static class PitchPipe {
        private static MediaPlayer player;

        /**
         * Calls play based on the integer index of the note selected
         * @param noteIndex The index of the note to play when notes are in order
         */
        public static void play(int noteIndex){
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

            play(MediaPlayer.create(Composr.getContext(), ids[noteIndex]));
        }

        /**
         * Starts the play given a MediaPlayer instance
         * @param player The MediaPlayer instance
         */
        public static void play(MediaPlayer player) {
            stop();
            PitchPipe.player = player;
            PitchPipe.player.setLooping(true);
            PitchPipe.player.start();
        }

        /**
         * Stops the note that is currently being played by the pitch pitchPipe
         */
        public static void stop(){
            if (player != null) {
                player.pause();
            }
        }
    }

    public static void playTick(){
        MediaPlayer m =  MediaPlayer.create(Composr.getContext(), R.raw.tick);
        m.start();
    }

}
