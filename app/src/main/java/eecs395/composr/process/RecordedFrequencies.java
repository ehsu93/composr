package eecs395.composr.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * The recorded frequencies that go along with a recording task
 * Wrapper for a LinkedList containing frequencies
 */
public class RecordedFrequencies {

    /** A list containing all the frequencies that have been recorded */
    LinkedList<Float> frequencies;

    /**
     * Initializes the LinkedList containing all the frequencies
     */
    public RecordedFrequencies() {
        frequencies = new LinkedList<>();
    }

    public boolean hasFrequencies(){
        return frequencies.size() > 0;
    }

    /**
     * Adds a frequency value to the list of frequencies
     *
     * @param freq the frequency to be added
     */
    public void addFrequency(float freq) {
        frequencies.add(freq);
    }

    /**
     * Gets the median value within a certain range of frequencies that have been recorded.
     *
     * @param start The start index
     * @param end The end index
     * @return Returns the median value in the range
     */
    public float getMedian(int start, int end){

        // create new ArrayList to hold the frequencies because subList() just references the
        // already created ArrayList
        ArrayList<Float> sub = new ArrayList<>();
        for (int i = start; i < end; i++){
            sub.add(frequencies.get(i));
        }

        // call the getMedian that takes an ArrayList, return result
        return getMedian(sub);
    }

    /**
     * Gives the median value of the frequencies in a sub-list. Do not try to call this on the
     * entire list because it uses Collections.sort()
     *
     * @return the median frequency over an interval
     */
    private float getMedian(ArrayList<Float> l) {
        Collections.sort(l);
        int mid = l.size()/2;
        return l.get(mid);
    }
}
