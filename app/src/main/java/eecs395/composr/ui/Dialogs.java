package eecs395.composr.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;

import eecs395.composr.Composr;
import eecs395.composr.R;
import eecs395.composr.io.EmailSender;
import eecs395.composr.io.FileWriter;
import eecs395.composr.io.SoundPlayer;
import eecs395.composr.musicXML.PatternHandler;

public class Dialogs {

    public static Dialog createTopLevelDialog(int layout, int title){
        Dialog dialog = new Dialog(Composr.getContext());
        dialog.setContentView(layout);
        dialog.setTitle(title);
        return dialog;
    }

    /**
     * Creates the pitch pitchPipe dialog
     * @return The pitch pitchPipe dialog
     */
    public static Dialog createPitchPipeDialog(){
        Dialog dialog = new Dialog(Composr.getContext());

        dialog.setContentView(R.layout.pitchpipe);
        dialog.setTitle(R.string.pitch_pipe); // "Pitch Pipe"

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                SoundPlayer.PitchPipe.stop();
            }
        });

        populatePitchPipeSpinner(dialog);
        return dialog;
    }

    /**
     * Create the spinner to display the pitch pitchPipe values
     *
     * @param dialog The dialog to add the spinner to
     * @return A spinner object with all the notes
     */
    public static void populatePitchPipeSpinner(Dialog dialog){
        // override onclick listener of positive button
        // initialize spinner with adapter
        Spinner spinner = (Spinner) dialog.findViewById(R.id.pitchpipe_spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Composr.getContext(),
                R.array.pitch_pipe_values, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                SoundPlayer.PitchPipe.play(pos);;
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Creates a simple dialog that has a list of options, from which the user chooses one and then
     * the dialog is dismissed
     *
     * @param title The string resource containing the title
     * @param options The string-array resource containing the list of options
     * @return The dialog with the list
     */
    public static Dialog createListOptionDialog(int title,
                                                int options,
                                                DialogInterface.OnClickListener d){
        AlertDialog.Builder b = new AlertDialog.Builder(Composr.getContext());
        b.setTitle(title).setSingleChoiceItems(options, -1, d);
        return b.create();
    }

    /**
     * Creates the dialog for the time signature input
     * @return The time signature dialog
     */
    public static Dialog createTimeSignatureDialog(final Dialog parentDialog){
        return createListOptionDialog(R.string.time_signature, R.array.time_signature_values,
                new Dialog.OnClickListener(){
                    public void onClick(DialogInterface dialog, int i){
                        Composr.updateTimeSignature(i);
                        Composr.redraw();
                        dialog.dismiss();
                        parentDialog.dismiss();
                    }
                });
    }

    /**
     * Creates the dialog for the key signature input
     * @return The key signature dialog
     */
    public static Dialog createKeySignatureDialog(final Dialog parentDialog){
        return createListOptionDialog(R.string.key_signature, R.array.key_signatures,
                new Dialog.OnClickListener(){
                    public void onClick(DialogInterface dialog, int i){
                        Composr.updateKeySignature(i);
                        Composr.redraw();
                        dialog.dismiss();
                        parentDialog.dismiss();
                    }
                });
    }

    public static Dialog createTempoDialog(final Dialog parentDialog){
        AlertDialog.Builder b = new AlertDialog.Builder(Composr.getContext());
        b.setTitle(R.string.tempo);
        final NumberPicker input = new NumberPicker(Composr.getContext());
        input.setMinValue(40);
        input.setMaxValue(200);
        input.setValue(120);
        b.setView(input);

        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Composr.updateTempo(input.getValue());
                dialog.dismiss();
                parentDialog.dismiss();
            }
        });
        b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return b.create();
    }

    public static void promptForExportFilename(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Composr.getContext());
        builder.setTitle("Export to MusicXML");

        final EditText input = new EditText(Composr.getContext());
        input.setHint("Enter file name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString();
                FileWriter.writeToFile(name, PatternHandler.getPattern());

                if (EmailSender.isPendingEmail()){
                    EmailSender.setPendingEmail(false);
                    dialog.cancel();

                    Toast.makeText(Composr.getContext(),
                            "Preparing file to send...",
                            Toast.LENGTH_LONG).show();

                    EmailSender.sendEmail();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public static void promptForOpenFilename(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Composr.getContext());
        builder.setTitle("Open sheet music");

        final EditText input = new EditText(Composr.getContext());
        input.setHint("Enter file name");
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fileName = input.getText().toString() + ".pdf";
                File file = new File(fileName);
                if (file.exists()) {
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "/Music/");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        Composr.getContext().startActivity(intent);
                    }
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(Composr.getContext(),
                                "No Application Available to View PDF",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
