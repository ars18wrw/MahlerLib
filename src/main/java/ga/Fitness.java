package ga;

/**
 * Created by Уладзімір Асіпчук on 22/11/2016.
 */
public class Fitness {

    // perfect consonances
    public static final int UNISON = 8;
    public static final int OCTAVE = 8;
    public static final int PERFECT_FORTH = 7;
    public static final int PERFECT_FIFTH = 7;

    // imperfect consonances
    public static final int MINOR_THIRD = 8;
    public static final int MAJOR_THIRD = 8;
    public static final int MINOR_SIXTH = 8;
    public static final int MAJOR_SIXTH = 8;

    // dissonances
    public static final int MINOR_SECOND = -20;
    public static final int MAJOR_SECOND = -20;
    public static final int MINOR_SEVENTH = -20;
    public static final int MAJOR_SEVENTH = -20;
    public static final int AUGMENTED_INTERVAL = -30;
    public static final int DIMINISHED_INTERVAL = -30;



    public static int getIntervalFitness(int melodyNote, int accompanimentNote) {
        int difference = (melodyNote - accompanimentNote) % 12;
        if (difference < 0) {
            difference += 12;
        }
        switch (difference) {
            case 0:
                return UNISON;
            case 1:
                return MINOR_SECOND;
            case 2:
                return MAJOR_SECOND;
            case 3:
                return MINOR_THIRD;
            case 4:
                return MAJOR_THIRD;
            case 5:
                return PERFECT_FORTH;
            case 6:
                return AUGMENTED_INTERVAL;
            case 7:
                return PERFECT_FIFTH;
            case 8:
                return MINOR_SIXTH;
            case 9:
                return MAJOR_SIXTH;
            case 10:
                return MINOR_SEVENTH;
            case 11:
                return MAJOR_SEVENTH;
            case 12:
                return OCTAVE;
            default:
                return 0;
        }
    }
}
