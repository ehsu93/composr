package kenthacks.eric.composr;

import java.util.Hashtable;

/**
 * Created by Eric on 10/25/2014.
 */
public class FrequencyRecorder {
    private Hashtable<Float, String> midiValues = new Hashtable<Float, String>();
    final Float[] FREQUENCIES = {27.5f, 29.1352350949f, 30.8677063285f, 32.7031956626f, 34.6478288721f, 36.7080959897f, 38.8908729653f, 41.2034446141f, 43.6535289291f, 46.249302839f, 48.9994294977f, 51.9130871975f, 55.0f, 58.2704701898f, 61.735412657f, 65.4063913251f, 69.2956577442f, 73.4161919794f, 77.7817459305f, 82.4068892282f, 87.3070578583f, 92.4986056779f, 97.9988589954f, 103.826174395f, 110.0f, 116.54094038f, 123.470825314f, 130.81278265f, 138.591315488f, 146.832383959f, 155.563491861f, 164.813778456f, 174.614115717f, 184.997211356f, 195.997717991f, 207.65234879f, 220.0f, 233.081880759f, 246.941650628f, 261.625565301f, 277.182630977f, 293.664767917f, 311.126983722f, 329.627556913f, 349.228231433f, 369.994422712f, 391.995435982f, 415.30469758f, 440.0f, 466.163761518f, 493.883301256f, 523.251130601f, 554.365261954f, 587.329535835f, 622.253967444f, 659.255113826f, 698.456462866f, 739.988845423f, 783.990871963f, 830.60939516f, 880.0f, 932.327523036f, 987.766602512f, 1046.5022612f, 1108.73052391f, 1174.65907167f, 1244.50793489f, 1318.51022765f, 1396.91292573f, 1479.97769085f, 1567.98174393f, 1661.21879032f, 1760.0f, 1864.65504607f, 1975.53320502f, 2093.0045224f, 2217.46104781f, 2349.31814334f, 2489.01586978f, 2637.0204553f, 2793.82585146f, 2959.95538169f, 3135.96348785f, 3322.43758064f, 3520.0f, 3729.31009214f, 3951.06641005f, 4186.00904481f};
    final String[] NOTES = {"A0", "A#0", "B0", "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1", "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#", "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5", "G5", "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6", "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D7", "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B7", "C8"};

    public FrequencyRecorder () {

    }

    public void initializeMidiValues() {
        int midiNo = 21;
        while (midiNo < 108) {
            midiValues.put(FREQUENCIES[midiNo-21], NOTES[midiNo-21]);
//            midiValues.put(FREQUENCIES[midiNo-21], number.toString());
            midiNo++;
        }
    }

    public int findNearestFrequency(Float recordedFrequency) {
        //Binary search that takes recordedFrequency, and returns smaller/larger frequency closest to recordedFrequency
        return bin_search(recordedFrequency, 0, FREQUENCIES.length-1);
    }

    public String getNoteFromFrequency(Float recordedFrequency) {
        Float frequency = getNearestFrequency(recordedFrequency);
        if(frequency == -1f) {
            return "R";
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

        if(index <= 2) {
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
            diff1 = closeFrequency1 - recordedFrequency;
            diff2 = recordedFrequency - closeFrequency2;
            if(diff1 < diff2) {
                return FREQUENCIES[index];
            }
            else return FREQUENCIES[index-1];
        }
        else return recordedFrequency;
    }
}
