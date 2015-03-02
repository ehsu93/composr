package kenthacks.eric.composr;

import android.content.Context;

import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

public class FrequencyRecorder {
    /**
     * Stores frequency, note name pairs
     */
    private Hashtable<Float, String> midiValues;

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
    public FrequencyRecorder(Context c) {
        this.c = c;
        initializeMidiValues();
    }

    /**
     * Initializes the midiValues HashTable from the pairs of frequencies and their associated notes
     */
    public void initializeMidiValues() {
        try {

            // initialize fields
            midiValues = new Hashtable<Float, String>();
            FREQUENCIES = new Float[88];

            // open note_frequencies file, create CSVReader
            InputStream is = c.getAssets().open("note_frequencies.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(is));

            // all the note, frequency pairs from the CSV
            List<String[]> pairs = reader.readAll();

            // populate midiValues and FREQUENCIES
            int index = 0;
            for (String[] pair : pairs) {
                Float freq = Float.parseFloat(pair[1]);
                String note = pair[0];

                FREQUENCIES[index] = freq;
                midiValues.put(freq, note);

                index++;
            }

        } catch (IOException e) {
            // this really should never happen tho
        }
    }

    /**
     * Returns the note closest to the given frequency
     *
     * @param recordedFrequency the frequency recorded from user input
     * @return the name of the note most closely associated with the input frequency
     */
    public String getNoteFromFrequency(Float recordedFrequency) {
        Float nearest_frequency = getNearestFrequency(recordedFrequency);
        if (nearest_frequency == -1f) {
            return "R";
        }
        return midiValues.get(nearest_frequency);
    }

    /**
     * Returns the frequency closest the the recorded frequency value
     *
     * @param recordedFrequency the frequency of the current note
     * @return the frequency closest to the input frequency that has an associated note
     */
    public Float getNearestFrequency(Float recordedFrequency) {
        // finds closest frequency to recordedFrequency using a binary search
        if (recordedFrequency < 20 || recordedFrequency > 4200){
            return -1f;
        }
        int index = bin_search(recordedFrequency, 0, FREQUENCIES.length - 1);
        return FREQUENCIES[index];
    }

    /**
     * Performs a binary search to find the closest frequency that corresponds with a note
     *
     * @param recordedFrequency The frequency recorded from user input
     * @param lo                The lower bound of the search
     * @param hi                The upper bound of the search
     * @return The index of the closest frequency corresponding with a note
     */
    public int bin_search(Float recordedFrequency, int lo, int hi) {
        // midpoint between hi and low
        int mid = lo + (hi - lo) / 2;

        // when hi and lo have either met or crossed, return the midpoint
        if (hi - lo < 2) {
            // either hi or lo will be closer to the frequency
            Float diff_hi = Math.abs(FREQUENCIES[hi] - recordedFrequency);
            Float diff_lo = Math.abs(FREQUENCIES[lo] - recordedFrequency);

            if (diff_hi < diff_lo) {
                return hi;
            } else {
                return lo;
            }
        }

        // compare the recorded frequency to the frequency at the current median frequency
        int cmp = FREQUENCIES[mid].compareTo(recordedFrequency);

        // recorded frequency is lower than the current midpoint
        if (cmp > 0) {
            return bin_search(recordedFrequency, lo, mid);
        }

        // recorded frequency is higher than the current median frequency
        else if (cmp < 0) {

            // mid is not incremented because it could be the closest value
            return bin_search(recordedFrequency, mid, hi);
        }

        // recorded frequency exactly equals the current median frequency, this won't happen often
        else return mid;
    }
}