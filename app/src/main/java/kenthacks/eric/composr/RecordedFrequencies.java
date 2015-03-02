package kenthacks.eric.composr;

import java.util.LinkedList;
import java.util.Collections;

public class RecordedFrequencies {

    /** A list containing all the frequencies that have been recorded */
    LinkedList<Float> frequencies;

    /**
     * Initializes the LinkedList containing all the frequencies
     */
    public RecordedFrequencies() {
        frequencies = new LinkedList<Float>();
    }

    /**
     * Adds a frequency value to the list of frequencies
     *
     * @param freq the frequency to be added
     */
    public void addFrequency(float freq) {
        frequencies.add(new Float(freq));
    }

    /**
     * Empties the frequencies list
     */
    public void reset(){
        frequencies = null; // a strong hint that this can be deallocated
        frequencies = new LinkedList<Float>();
    }

    /**
     * Gives the median value of the frequencies in the list
     *
     * @return the median frequency over an interval
     */
    public float getMedian() {
        Collections.sort(frequencies);
        int mid = frequencies.size()/2;
        return frequencies.get(mid).floatValue();
    }
}
