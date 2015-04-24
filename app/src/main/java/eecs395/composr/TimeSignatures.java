package eecs395.composr;

import eecs395.composr.TimeSignature;

public class TimeSignatures {

    static TimeSignature[] signatures = {
            new TimeSignature(4, 2),
            new TimeSignature(3, 2),
            new TimeSignature(6, 4),
            new TimeSignature(5, 4),
            new TimeSignature(4, 4),
            new TimeSignature(3, 4),
            new TimeSignature(2, 4),
            new TimeSignature(9, 8),
            new TimeSignature(6, 8),
            new TimeSignature(3, 8),
            new TimeSignature(2, 8)
    };

    public static TimeSignature getTimeSignatureFromIndex(int i){
        return signatures[i];
    }
}
