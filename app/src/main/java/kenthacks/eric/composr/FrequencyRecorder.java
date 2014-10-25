package kenthacks.eric.composr;

import java.util.Hashtable;
import java.util.Arrays;
/**
 * Created by Eric on 10/25/2014.
 */
public class FrequencyRecorder {

    private Hashtable<Float, String> midiValues = new Hashtable<Float, String>();

    final Float[] FREQUENCIES = {27.5f, 29.1352350949f, 30.8677063285f, 32.7031956626f, 34.6478288721f, 36.7080959897f, 38.8908729653f, 41.2034446141f, 43.6535289291f, 46.249302839f, 48.9994294977f, 51.9130871975f, 55.0f, 58.2704701898f, 61.735412657f, 65.4063913251f, 69.2956577442f, 73.4161919794f, 77.7817459305f, 82.4068892282f, 87.3070578583f, 92.4986056779f, 97.9988589954f, 103.826174395f, 110.0f, 116.54094038f, 123.470825314f, 130.81278265f, 138.591315488f, 146.832383959f, 155.563491861f, 164.813778456f, 174.614115717f, 184.997211356f, 195.997717991f, 207.65234879f, 220.0f, 233.081880759f, 246.941650628f, 261.625565301f, 277.182630977f, 293.664767917f, 311.126983722f, 329.627556913f, 349.228231433f, 369.994422712f, 391.995435982f, 415.30469758f, 440.0f, 466.163761518f, 493.883301256f, 523.251130601f, 554.365261954f, 587.329535835f, 622.253967444f, 659.255113826f, 698.456462866f, 739.988845423f, 783.990871963f, 830.60939516f, 880.0f, 932.327523036f, 987.766602512f, 1046.5022612f, 1108.73052391f, 1174.65907167f, 1244.50793489f, 1318.51022765f, 1396.91292573f, 1479.97769085f, 1567.98174393f, 1661.21879032f, 1760.0f, 1864.65504607f, 1975.53320502f, 2093.0045224f, 2217.46104781f, 2349.31814334f, 2489.01586978f, 2637.0204553f, 2793.82585146f, 2959.95538169f, 3135.96348785f, 3322.43758064f, 3520.0f, 3729.31009214f, 3951.06641005f, 4186.00904481f};
    final String[] NOTES = {"A0", "A0#", "B0", "C1", "C1#", "D1", "D1#", "E1", "F1", "F1#", "G1", "G1#", "A1", "A1#", "B1", "C2", "C2#", "D2", "D2#", "E2", "F2", "F2#", "G2", "G2#", "A2", "A2#", "B2", "C3", "C3#", "D3", "D3#", "E3", "F3", "F3#", "G3", "G3#", "A3", "A3#", "B3", "C4", "C4#", "D4", "D4#", "E4", "F4", "F4#", "G4", "G4#", "A4", "A4#", "B4", "C5", "C5#", "D5", "D5#", "E5", "F5", "F5#", "G5", "G5#", "A5", "A5#", "B5", "C6", "C6#", "D6", "D6#", "E6", "F6", "F6#", "G6", "G6#", "A6", "A6#", "B6", "C7", "C7#", "D7", "D7#", "E7", "F7", "F7#", "G7", "G7#", "A7", "A7#", "B7", "C8"};
    public FrequencyRecorder () {

    }

    public void initializeMidiValues() {
        int midiNo = 21;
        while (midiNo < 108) {
            midiValues.put(FREQUENCIES[midiNo-21], NOTES[midiNo-21]);
            midiNo++;
        }
    }

    //Binary search that takes recordedFrequency, and returns smaller/larger frequency closest to recordedFrequency
    public int findNearestFrequency(Float recordedFrequency) {
        return bin_search(recordedFrequency, 0, FREQUENCIES.length-1);
    }

    public Hashtable getMidiValues() {
        return midiValues;
    }

    public String getNoteFromFrequency(Float recordedFrequency) {
        Float frequency = getNearestFrequency(recordedFrequency);
        if(frequency == -1f) {
            return "NULL";
        }
        return midiValues.get(frequency);
    }

    public int bin_search(Float recordedFrequency, int lo, int hi) {
        int mid = lo + (hi - lo)/2;

        if(hi <= lo) {
            return mid;
        }
        int cmp = FREQUENCIES[mid].compareTo(recordedFrequency);
        if (cmp > 0) {
            return bin_search(recordedFrequency, lo, mid);
        }
        else if (cmp < 0) {
            return bin_search(recordedFrequency, mid+1, hi);
        }
        else return mid;
    }

    public Float getNearestFrequency(float recordedFrequency) {
        int index = findNearestFrequency(recordedFrequency);
        float closeFrequency1 = FREQUENCIES[index];
        float closeFrequency2;
        float diff1;
        float diff2;

        if(index <= 5) {
            return -1f;
        }
        if(index >=86) {
            return -1f;
        }

        if(closeFrequency1 < recordedFrequency) {
            closeFrequency2 = FREQUENCIES[index + 1];
            diff1 = recordedFrequency - closeFrequency1;
            diff2 = closeFrequency2 - recordedFrequency;
            if(diff1 < diff2) {
                return FREQUENCIES[index-1];
            }
            else return FREQUENCIES[index];
        }
        else if (closeFrequency1 > recordedFrequency) {
            closeFrequency2 = FREQUENCIES[index - 1];
            diff1 = recordedFrequency - closeFrequency1;
            diff2 = closeFrequency2 - recordedFrequency;
            if(diff1 < diff2) {
                return FREQUENCIES[index-1];
            }
            else return FREQUENCIES[index-2];
        }
        else return recordedFrequency;
    }

    public class FrequencyArray{

        int length;
        float[] frequencies;

        public FrequencyArray(int l) {
            this.length = l;
            frequencies = new float[this.length];
        }

        private void setLength(int l) {
            this.length = l;
        }

        private int addFrequency(float freq) {
            int index = 0;
            while(this.frequencies[index] != 0) {
                index++;
            }
            this.frequencies[index] = freq;
            return index;
        }

        private Boolean checkFull(int index) {
            if (index++ == frequencies.length) {
                return true;
            }
            else return false;
        }

        public float addAndGetFreq(float freq) {
            int index = addFrequency(freq);
            if(checkFull(index)) {
                Arrays.sort(this.frequencies);
                return getMedian();
            }
            return -1f;
        }

        private float getMedian() {
            int lo = 0;
            int hi = frequencies.length;
            int mid = lo + (hi - lo)/2;
            return frequencies[mid];
        }

    }

}
