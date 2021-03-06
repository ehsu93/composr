package eecs395.composr.process;

public class SampleBeatPair {
    private int sample;
    private int beat;
    private int measure;

    public SampleBeatPair(int sample, int beat, int measure){
        this.sample = sample;
        this.beat = beat;
        this.measure = measure;
    }

    public SampleBeatPair(SampleBeatPair s){
        this.sample = s.sample;
        this.beat = s.beat;
        this.measure = s.measure;
    }

    public SampleBeatPair(){
        this.sample = 0;
        this.beat = 0;
        this.measure = 0;
    }

    public int getSample(){
        return this.sample;
    }

    public int getBeat(){
        return this.beat;
    }

    public void incrementSample(){
        this.sample++;
    }

    public void incrementBeat(){
        this.beat++;
        this.sample = 0;
    }

    public void incrementMeasure(){
        this.measure++;
        this.beat = 0;
    }

    public boolean isNewBeat(){
        return this.sample == 0;
    }

    public boolean isNewMeasure(){
        return this.beat == 0;
    }

    public boolean isFirst(){
        return sample == 0 && measure == 0;
    }

    public void increment(int samplesPerBeat, int beatsPerMeasure){
        incrementSample();

        if (this.sample == samplesPerBeat) {
            incrementBeat();
        }

        if (this.beat == beatsPerMeasure) {
            incrementMeasure();
        }
    }

    public int getDistanceTilEndOfMeasure(int samplesPerBeat, int beatsPerMeasure){
        int distance = 0;

        distance += samplesPerBeat - sample;
        distance += (beatsPerMeasure - beat) * samplesPerBeat;

        return distance;
    }
}
