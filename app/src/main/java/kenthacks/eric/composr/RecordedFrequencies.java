package kenthacks.eric.composr;

import java.util.LinkedList;
import java.util.Collections;

/**
 * Created by Meaghan on 10/26/14.
 */
public class RecordedFrequencies {

    LinkedList<Float> frequencies;

    public RecordedFrequencies() {
        frequencies = new LinkedList<Float>();
    }

    public void addFrequency(float freq) {
        frequencies.add(new Float(freq));
    }

    public void reset(){
        frequencies = null; // a strong hint that this can be deallocated
        frequencies = new LinkedList<Float>();
    }

    public float getMedian() {
        Collections.sort(frequencies);
        int mid = frequencies.size()/2;
        return frequencies.get(mid).floatValue();
    }
}
