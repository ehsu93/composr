package eecs395.composr.io;

import android.content.Intent;
import android.net.Uri;

import eecs395.composr.Composr;

public class EmailSender {

    private static boolean pendingEmail;

    /**
     * Send the user an email with the file as an attachment (or whatever other app they use)
     */
    public static void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, "MusicXML Generated with Composr");
        i.putExtra(Intent.EXTRA_TEXT, "The MusicXML generated using Composr is attached");

        Uri uri = Uri.fromFile(FileWriter.getLastSavedFile());
        i.putExtra(Intent.EXTRA_STREAM, uri);

        Composr.getContext().startActivity(Intent.createChooser(i, "Send mail"));
    }

    public static boolean isPendingEmail(){
        return pendingEmail;
    }

    public static void setPendingEmail(boolean input){
        pendingEmail = input;
    }
}
