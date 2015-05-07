package eecs395.composr.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import eecs395.composr.Composr;
import eecs395.composr.R;
import eecs395.composr.draw.Drawer;
import eecs395.composr.io.EmailSender;
import eecs395.composr.io.FileWriter;
import eecs395.composr.io.SoundPlayer;

public class Listeners {

    /**
     * Returns the OnTouchListener for the note display area
     * The OnTouchListener performs different actions based on the area that is touched
     *
     * @param timeSignatureDialog The dialog used to select the time signature
     * @return The instance of OnTouchListener created for the NoteLayout
     */
    public static View.OnTouchListener getNoteLayoutListener(final Dialog timeSignatureDialog){
        return new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent e){
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = e.getX();

                    // the user touched the clef area
                    if (x < 170){
                        Drawer.changeClef();
                        Composr.redraw();
                    }

                    // the user touched the key signature area
                    if (x > 170 && x < 320) {
                        timeSignatureDialog.show();
                    }

                }
                return true;
            }
        };
    }


    /**
     * Returns an OnTouchListener that does nothing. This is used when the recording has started
     * and the user is no longer able to change the time signature or clef
     *
     * @return An OnTouchListener that does nothing
     */
    public static View.OnTouchListener getEmptyOnTouchListener(){
        return new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent e){
                return false;
            }
        };
    }

    /**
     * Returns the OnClickListener for the Listen button
     *
     * @param listenButton Indicates whether to stop or start listening
     * @return The OnClickListener for the Listen button
     */
    public static View.OnClickListener getListenListener(final Activity activity,
                                                  final Button [] buttonsToDisable,
                                                  final Button listenButton,
                                                  final Dialog timeSignatureDialog){
        return new View.OnClickListener() {
            public void onClick(View v) {

                // get the text from the button
                String buttonText = listenButton.getText().toString();

                // stop listening to the user
                if (buttonText.equals("Stop Listening")) {
                    listenButton.setText("Listen");
                    //Composr.setListening(false);
                    UserInterfaceController.setButtonsEnabled(buttonsToDisable, true);
                    Composr.getNoteLayout().setOnTouchListener(
                            getNoteLayoutListener(timeSignatureDialog));
                    Dialogs.promptForExportFilename();
                }

                // start listening to the user
                else {
                    listenButton.setText("Stop Listening");
                    //Composr.setHasListened(true);
                    //Composr.setListening(true);
                    UserInterfaceController.setButtonsEnabled(buttonsToDisable, false);
                    Composr.getNoteLayout().setOnTouchListener(getEmptyOnTouchListener());
                }

                Composr.reset();
            }
        };
    }

    public static View.OnClickListener showDialogListener(final Dialog dialog){
        return new View.OnClickListener(){
            public void onClick(View v){
                dialog.show();
            }
        };
    }

    /**
     * Returns the OnClickListener for the button that exports to MusicXML
     *
     * @return The OnClickListener for the button that exports to MusicXML
     */
    public static View.OnClickListener getMusicButtonListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // prompts the user to input a name for the file to export
                Dialogs.promptForExportFilename();
            }
        };
    }

    public static View.OnClickListener getClefListener(final Dialog parentDialog){
        return new View.OnClickListener(){
            public void onClick(View view){
                Drawer.changeClef();
                Composr.redraw();
                parentDialog.dismiss();
            }
        };
    }

    /**
     * Returns the OnClickListener for the send email button which will prompt for a file name and
     * save it if it hasn't already been done, otherwise it will go straight into the sendEmail
     * method
     *
     * @return the OnClickListener for the send email button
     */
    public static View.OnClickListener getSendEmailListener(){
        return new View.OnClickListener(){
            public void onClick(View v){

                // current pattern is not saved, prompt the user for a filename
                if (!FileWriter.hasBeenSaved()) {
                    EmailSender.setPendingEmail(true);
                    Dialogs.promptForExportFilename(); // this will call sendEmail() later
                } else {
                    EmailSender.sendEmail();
                }
            }
        };
    }

    /**
     * Return the OnClickListener for the pitch pipe button. Requires the dialog as input so that
     * it can show the dialog
     *
     * @param pitchPipeDialog The pitch pipe dialog
     * @return The OnClickListener for the pitch pipe button
     */
    public static View.OnClickListener getPitchPipeButtonListener(final Dialog pitchPipeDialog){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pitchPipeDialog.show();
            }
        };
    }

    /**
     * Return the OnClickListener for the stop button in the pitch pipe dialog. It turns the pitch
     * pipe sound off.
     *
     * @return the OnClickListener for the stop button in the pitch pipe dialog
     */
    public static View.OnClickListener getStopPitchPipeButtonListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SoundPlayer.PitchPipe.stop();
            }
        };
    }

    /**
     * Gets the OnClickListener for the OpenPDF button. Will prompt the user to select a filename to
     * open.
     *
     * @return the OnClickListener for the OpenPDF button
     */
    public static View.OnClickListener getOpenPdfListener(){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Dialogs.promptForOpenFilename();
            }
            /*TODO: decide how to handle a user trying to view the PDF of the most recently created
            file without having to go through this */
        };
    }
}
