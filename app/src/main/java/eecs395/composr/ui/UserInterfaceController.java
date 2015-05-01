package eecs395.composr.ui;

import android.app.Dialog;
import android.view.View;
import android.widget.Button;

import java.util.Hashtable;
import java.util.Set;

import eecs395.composr.Composr;
import eecs395.composr.R;

public class UserInterfaceController {

    /**
     * Returns a button given its ID in R (the dynamically generated class which sets all of the
     * public constants from the XML files)
     *
     * @param id The id of the button
     * @return The button
     */
    public static Button getButton(int id){
        return (Button) Composr.getNoteLayout().findViewById(id);
    }

    /**
     * Returns a button given its ID in R (the dynamically generated class which sets all of the
     * public constants from the XML files), but in this case, the button is not in the main
     * layout
     *
     * @param parent The layout the button exists in
     * @param id The ID of the button
     * @return The button
     */
    public static Button getButton(Dialog parent, int id){
        return (Button) parent.findViewById(id);
    }

    public static void setButtonsEnabled(Button[] buttons, boolean enabled){
        for (Button b: buttons){
            b.setEnabled(enabled);
        }
    }

    public static void addOnClickListeners(){

        final Button [] buttonsOnMainScreen = new Button[]{
                getButton(R.id.settings),
                getButton(R.id.share_button),
                getButton(R.id.tools_button)};

        final Dialog settingsDialog = Dialogs.createTopLevelDialog(
                R.layout.settings,
                R.string.settings_title);

        final Dialog shareDialog = Dialogs.createTopLevelDialog(
                R.layout.share,
                R.string.share_title);

        final Dialog toolsDialog = Dialogs.createTopLevelDialog(
                R.layout.tools,
                R.string.tools_title);

        final Dialog pitchPipeDialog = Dialogs.createPitchPipeDialog();

        Hashtable<Button, View.OnClickListener> buttonsAndListeners =
                new Hashtable<Button, View.OnClickListener>() {{
                    put(getButton(R.id.settings), Listeners.showDialogListener(settingsDialog));

                    put(getButton(R.id.Toggle),
                            Listeners.getListenListener(buttonsOnMainScreen,
                                    getButton(R.id.Toggle),
                                    Dialogs.createTimeSignatureDialog(settingsDialog)));

                    put(getButton(R.id.share_button), Listeners.showDialogListener(shareDialog));

                    put(getButton(R.id.tools_button), Listeners.showDialogListener(toolsDialog));

                    put(getButton(settingsDialog, R.id.tempo),
                            Listeners.showDialogListener(Dialogs.createTempoDialog(settingsDialog)));

                    put(getButton(settingsDialog, R.id.changeKey),
                            Listeners.showDialogListener(Dialogs.createKeySignatureDialog(settingsDialog)));

                    put(getButton(settingsDialog, R.id.changeTime),
                            Listeners.showDialogListener(Dialogs.createTimeSignatureDialog(settingsDialog)));

                    put(getButton(settingsDialog, R.id.changeClef),
                            Listeners.getClefListener(settingsDialog));

                    put(getButton(shareDialog, R.id.makeMusic), Listeners.getMusicButtonListener());

                    put(getButton(shareDialog, R.id.sendEmail), Listeners.getSendEmailListener());

                    put(getButton(toolsDialog, R.id.pitchPipeButton),
                            Listeners.getPitchPipeButtonListener(pitchPipeDialog));

                    put(getButton(pitchPipeDialog, R.id.stop_pitchpipe),
                            Listeners.getStopPitchPipeButtonListener());

                    put(getButton(toolsDialog, R.id.pdfOpen), Listeners.getOpenPdfListener());
                }};

        Set<Button> buttons = buttonsAndListeners.keySet();

        for (Button button : buttons){
            button.setOnClickListener(buttonsAndListeners.get(button));
        }

        Composr.getNoteLayout().setOnTouchListener(Listeners.getNoteLayoutListener(
                Dialogs.createTimeSignatureDialog(settingsDialog)));

    }
}
