package eecs395.composr.musicUtils;

public enum KeySignature {

    C_MAJOR(false, 0),

    // majors with sharps
    G_MAJOR(true, 1),
    D_MAJOR(true, 2),
    A_MAJOR(true, 3),
    E_MAJOR(true, 4),
    B_MAJOR(true, 5),
    F_SHARP_MAJOR(true, 6),
    C_SHARP_MAJOR(true, 7),

    // majors without sharps
    F_MAJOR(false, 1),
    B_FLAT_MAJOR(false, 2),
    E_FLAT_MAJOR(false, 3),
    A_FLAT_MAJOR(false, 4),
    D_FLAT_MAJOR(false, 5),
    G_FLAT_MAJOR(false, 6),
    C_FLAT_MAJOR(false, 7),

    // minors with sharps
    E_MINOR(G_MAJOR),
    B_MINOR(D_MAJOR),
    F_SHARP_MINOR(A_MAJOR),
    C_SHARP_MINOR(E_MAJOR),
    G_SHARP_MINOR(B_MAJOR),
    D_SHARP_MINOR(F_SHARP_MAJOR),
    A_SHARP_MINOR(C_SHARP_MAJOR),

    // minors without sharps
    A_MINOR(F_MAJOR),
    D_MINOR(B_FLAT_MAJOR),
    G_MINOR(E_FLAT_MAJOR),
    C_MINOR(E_FLAT_MAJOR),
    F_MINOR(A_FLAT_MAJOR),
    B_FLAT_MINOR(D_FLAT_MAJOR),
    E_FLAT_MINOR(G_FLAT_MAJOR),
    A_FLAT_MINOR(C_FLAT_MAJOR),
    ;

    KeySignature complement;
    boolean containsSharps;
    boolean isMajor;
    int accidentals;

    private static KeySignature[] keySignatures = values();

    KeySignature(boolean containsSharps, int accidentals){
        this.containsSharps = containsSharps;
        this.accidentals = accidentals;
        this.isMajor = true;
    }

    KeySignature(KeySignature complementKey){
        this.complement = complementKey;
        complement.setComplement(this);

        this.isMajor = false;
        this.accidentals = complement.getAccidentals();
        this.containsSharps = complement.getContainsSharps();
    }

    public int getAccidentals(){
        return this.accidentals;
    }

    public boolean getContainsSharps(){
        return this.containsSharps;
    }

    void setComplement(KeySignature complement){
        this.complement = complement;
    }

    public static KeySignature getKeySignatureFromIndex(int i){
        return keySignatures[i];
    }
}