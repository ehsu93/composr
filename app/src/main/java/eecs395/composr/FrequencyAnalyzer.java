package eecs395.composr;

import android.content.Context;
import com.opencsv.CSVReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

/**
 * Contains all of the valid frequencies and their corresponding notes, performs operations that
 * have to do with matching frequencies to their closest note value
 */
public class FrequencyAnalyzer {
    /**
     * Stores frequency, note name pairs
     */
    private Hashtable<Float, String> frequencyNotePairs;

    /**
     * An array of exact frequencies that correspond with a note
     */
    private Float[] FREQUENCIES;

    /**
     * Application context, required to access assets
     */
    Context c;

    /**
     * Creates a FrequencyRecorder object given the application context
     *
     * @param c Application context, required to access assets
     */
    public FrequencyAnalyzer(Context c) {
        this.c = c;
        initializeMidiValues();
    }

    /**
     * Initializes the frequencyNotePairs HashTable from the pairs of frequencies and their
     * associated notes
     */
    public void initializeMidiValues() {
        try {

            // initialize fields
            frequencyNotePairs = new Hashtable<>();
            FREQUENCIES = new Float[88];

            // open note_frequencies file, create CSVReader
            InputStream is = c.getAssets().open("note_frequencies.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));

            // all the note, frequency pairs from the CSV
            List<String[]> pairs = reader.readAll();

            // populate frequencyNotePairs and FREQUENCIES
            int index = 0;
            for (String[] pair : pairs) {
                Float freq = Float.parseFloat(pair[1]);
                String note = pair[0];

                FREQUENCIES[index] = freq;
                frequencyNotePairs.put(freq, note);

                index++;
            }

        } catch (IOException e) {
            // this really should never happen tho
            // TODO: push error to application?
        }
    }

    /**
     * Returns the note closest to the given frequency
     *
     * @param recordedFreq the frequency recorded from user input
     * @return the name of the note most closely associated with the input frequency
     */
    public String getNoteFromFreq(Float recordedFreq) {
        Float nearest_freq = getNearestFreq(recordedFreq);
        if (nearest_freq == -1f) {
            return "R";
        }
        return frequencyNotePairs.get(nearest_freq);
    }

    /**
     * Returns the frequency closest the the recorded frequency value
     *
     * @param recordedFreq the frequency of the current note
     * @return the frequency closest to the input frequency that has an associated note
     */
    public Float getNearestFreq(Float recordedFreq) {
        // return invalid value if frequency is outside of valid range
        if (recordedFreq < 20 || recordedFreq > 4200){
            return -1f;
        }

        // return the result of performing a binary search to find the closest frequency
        return searchFreqs(recordedFreq, 0, FREQUENCIES.length - 1);
    }

    /**
     * Performs a binary search to find the closest frequency that corresponds with a note
     *
     * @param freq The frequency recorded from user input
     * @param lo                The lower bound of the search
     * @param hi                The upper bound of the search
     * @return The index of the closest frequency corresponding with a note
     */
    public Float searchFreqs(Float freq, int lo, int hi) {
        // midpoint between hi and low
        int mid = lo + (hi - lo) / 2;

        // when hi and lo have either met or crossed, return the value that's closer
        if (hi - lo < 2) {
            Float diff_hi = Math.abs(FREQUENCIES[hi] - freq);
            Float diff_lo = Math.abs(FREQUENCIES[lo] - freq);

            if (diff_lo < diff_hi){
                return FREQUENCIES[lo];
            } else {
                return FREQUENCIES[hi];
            }
        }

        // compare the recorded frequency to the frequency at the current median frequency
        int cmp = FREQUENCIES[mid].compareTo(freq);

        // recorded frequency is lower than the current midpoint
        if (cmp > 0) {
            return searchFreqs(freq, lo, mid);
        }

        // recorded frequency is higher than the current median frequency
        else if (cmp < 0) {

            // mid is not incremented because it could be the closest value
            return searchFreqs(freq, mid, hi);
        }

        // recorded frequency exactly equals the current median frequency, this won't happen often
        else return FREQUENCIES[mid];
    }
}