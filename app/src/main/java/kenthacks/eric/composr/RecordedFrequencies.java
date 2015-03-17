package kenthacks.eric.composr;

import java.util.ArrayList;
import java.util.Collections;

public class RecordedFrequencies {

    /** A list containing all the frequencies that have been recorded */
    ArrayList<Float> frequencies;

    /**
     * Initializes the LinkedList containing all the frequencies
     */
    public RecordedFrequencies() {
        frequencies = new ArrayList<Float>();
    }

    public RecordedFrequencies(ArrayList<Float> frequencies) {
        this.frequencies = frequencies;
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
        frequencies = new ArrayList<Float>();
    }

    /**
     * Gives the median value of the frequencies in the list
     *
     * @return the median frequency over an interval
     */
    public float getMedian(ArrayList<Float> l) {
        Collections.sort(l);
        int mid = l.size()/2;
        return l.get(mid).floatValue();
    }

    public float getMedian(int start, int end){
        ArrayList<Float> sub = new ArrayList<Float>();

        for (int i = start; i < end; i++){
            sub.add(frequencies.get(i));
        }
        return getMedian(sub);
    }
}
