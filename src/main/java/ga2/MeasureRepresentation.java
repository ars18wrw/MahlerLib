package ga2;

import javafx.util.Pair;
import jm.constants.Scales;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class MeasureRepresentation {
    public static final float ITEM_MUTATION_PROBABILITY = 0.2f;
    public static final float PITCH_MUTATION_PROBABILITY = 0.3f;
    public static final float REINITIALISE_MEASURE_PROBABILITY = 0.15f;
    public static final float COPY_PROBABILITY = 0.05f;
    public static final float INVERSE_PROBABILITY = 0.5f;

    // third status
    public static final int HAS_TRIAD = 1001;
    public static final int HAS_TRIAD_INVERSION_FIRST = 1002;
    public static final int HAS_TRIAD_INVERSION_SECOND = 1003;

    public static final int HAS_THIRD = 1004;
    public static final int NO_THIRD = 1005;

    // dissonance status
    public static final int MANY_SEMITONE_DISSONANCE = 2001;
    public static final int LEADING_SEMITONE_DISSONANCE = 2002;
    public static final int MANY_SEMITONE_DISSONANCE_WITH_ONE_LEADING = 2003;
    public static final int FEW_SEMITONE_DISSONANCE = 2004;

    public static final int INVALID_PITCH_FINE1 = -30;
    public static final int INVALID_PITCH_FINE2 = -30;

    public static final int TONIC_FIRST_FINE1 = 10;
    public static final int TONIC_FIRST_FINE2 = 3;

    public static final int TRIAD_ABSENCE_FINE1 = -40;
    public static final int TRIAD_ABSENCE_FINE2 = -40;

    public static final int FIFTH_ABSENCE_FINE1 = -15;
    public static final int FIFTH_ABSENCE_FINE2 = -5;

    public static final int DISSONANCE_FINE1 = -10;
    public static final int DISSONANCE_FINE2 = 10;

    public static final int SEMITONE_DISSONANCE_FINE1 = -20;
    public static final int SEMITONE_DISSONANCE_FINE2 = -20;

    public static final int NO_MEASURE_PITCHES_FINE1 = -80;
    public static final int NO_MEASURE_PITCHES_FINE2 = -80;

    public static final int UNISONS_FINE1 = -5;
    public static final int UNISONS_FINE2 = -5;





    protected int[] scale;
    protected int[] measure;
    protected int[] frequences;

    private static Random random = new Random();

    public MeasureRepresentation(int[] scale, int[] frequences) {
        // measure
        this.measure = new int[3];
        for (int i = 0; i < 3; i++) {
            measure[i] = getRandomNote();
        }

        // scale
        this.scale = scale;

        // frequences
        this.frequences = new int[7];
        for (int i = 0; i < scale.length; i++) {
            this.frequences[i] = frequences[i];
        }
    }

    public MeasureRepresentation(int[] frequences) {
        // measure
        this.measure = new int[3];
        for (int i = 0; i < 3; i++) {
            measure[i] = getRandomNote();
        }

        // scale
        scale = Scales.MAJOR_SCALE;

        // frequences
        this.frequences = new int[7];
        for (int i = 0; i < scale.length; i++) {
            this.frequences[i] = frequences[i];
        }
    }

    public int[] getMeasure() {
        return measure;
    }

    public void mutation(MeasureRepresentation nextMeasure) {
        if (random.nextFloat() < ITEM_MUTATION_PROBABILITY) {
            mutatePitch(PITCH_MUTATION_PROBABILITY);
            inverse(INVERSE_PROBABILITY);
            reinitialiseMeasure(REINITIALISE_MEASURE_PROBABILITY);
            copy(COPY_PROBABILITY, nextMeasure);
        }
    }

    public Pair<Integer, Integer> getFitness(MeasureRepresentation nextMeasure) {
        int fitness1 = 0;
        int fitness2 = 0;

        // chord
        int[] chord = new int[measure.length + 1];
        System.arraycopy(measure, 0, chord, 0, measure.length);
        chord[chord.length - 1] = getChordMelodyPitch();
        Arrays.sort(chord);

        // next chord
        int[] nextChord = null;
        if (null != nextMeasure) {
            nextChord = new int[nextMeasure.measure.length + 1];
            System.arraycopy(nextMeasure.measure, 0, nextChord, 0, nextMeasure.measure.length);
            nextChord[nextChord.length - 1] = nextMeasure.getChordMelodyPitch();
            Arrays.sort(nextChord);
        }

        int thirdStatus = getThirdStatus(chord);
        int dissonanceStatus = getDissonanceStatus(chord, nextChord);
        int dissonanceCount = getDissonancePitchesCount(chord);
        int invalidPithcesNum = getInvalidPitchesCount(chord);

        fitness1+=invalidPithcesNum*INVALID_PITCH_FINE1;
        fitness2+=invalidPithcesNum*INVALID_PITCH_FINE2;

        switch (thirdStatus) {
            case HAS_TRIAD:
                fitness1+=TONIC_FIRST_FINE1;
                fitness2+=TONIC_FIRST_FINE2;
            case HAS_THIRD:
                fitness1+=FIFTH_ABSENCE_FINE1;
                fitness2+=FIFTH_ABSENCE_FINE2;
            case NO_THIRD:
                fitness1+=TRIAD_ABSENCE_FINE1;
                fitness2+=TRIAD_ABSENCE_FINE2;
        }

        fitness1+=invalidPithcesNum*INVALID_PITCH_FINE1;
        fitness2+=invalidPithcesNum*INVALID_PITCH_FINE2;

        fitness1+=dissonanceCount*DISSONANCE_FINE1;
        fitness2+=dissonanceCount*DISSONANCE_FINE2;

        switch (dissonanceStatus) {
            case MANY_SEMITONE_DISSONANCE_WITH_ONE_LEADING:
            case MANY_SEMITONE_DISSONANCE:
                fitness1+=SEMITONE_DISSONANCE_FINE2;
                fitness2+=SEMITONE_DISSONANCE_FINE2;
        }

        return new Pair<>(fitness1, fitness2);
    }

    public void mutatePitch(float probability) {
        if (random.nextFloat() < probability) {
            int index = random.nextInt(3);
            boolean isUp = random.nextBoolean();
            int indexInScale = Arrays.binarySearch(scale, measure[index]);
            if (0 <= indexInScale) {
                measure[index] = scale[(isUp ? indexInScale + 1 : measure.length + indexInScale - 1) % measure.length];
            } else {
                measure[index] = (isUp ? measure[index] + 2 : 12 + measure[index] - 2) % 12;
            }
        }
    }

    public void inverse(float probability) {
        if (random.nextFloat() < probability) {
            int temp = measure[0];
            for (int i = 0; i < measure.length - 1; i++) {
                measure[i] = measure[i + 1];
            }
            measure[measure.length - 1] = temp;
        }
    }

    public void reinitialiseMeasure(float probability) {
        if (random.nextFloat() < probability) {
            int index = random.nextInt(7);
            while (0 == frequences[(index++) % 7]);
            index = (index - 1) % 7;
            for (int i = 0; i < measure.length; i++) {
                measure[i] = scale[(index + 2 * i) % 7];
            }
        }
    }

    public void copy(float probability, MeasureRepresentation nextMeasure) {
        if (random.nextFloat() < probability && null != nextMeasure) {
            this.measure = nextMeasure.getMeasure().clone();
        }
    }

    public int getThirdStatus(int[] chord) {
        // TODO UNISON
        boolean hasThird = false;

        boolean hasTempMinorThird;
        boolean hasTempMajorThird;
        boolean hasTempForth;

        for (int i = 0; i < chord.length - 1; i++) {
            hasTempMajorThird = false;
            hasTempMinorThird = false;
            hasTempForth = false;
            for (int j = i + 1; j < chord.length; j++) {
                if (3 == chord[j] - chord[i]) {
                    hasTempMinorThird = true;
                } else if (4 == chord[j] - chord[i]) {
                    hasTempMajorThird = true;
                } else if (5 == chord[j] - chord[i]) {
                    hasTempForth = true;
                } else if ((hasTempMajorThird || hasTempMinorThird) && (7 == chord[j] - chord[i])) {
                    return HAS_TRIAD;
                } else if (hasTempMajorThird && (9 == chord[j] - chord[i])) {
                    return HAS_TRIAD_INVERSION_FIRST;
                } else if (hasTempMinorThird && (8 == chord[j] - chord[i])) {
                    return HAS_TRIAD_INVERSION_FIRST;
                } else if (hasTempForth && (8 == chord[j] - chord[i] || 9 == chord[j] - chord[i])) {
                    return HAS_TRIAD_INVERSION_SECOND;
                }
            }
            if (hasTempMajorThird) {
                hasThird = true;
            }

        }
        return (hasThird ? HAS_THIRD : NO_THIRD);
    }

    public int getUnisonsCount(int[] chord) {
        int count = 0;
        for (int i = 0; i < chord.length-1; i++) {
            if (chord[i] == chord[i+1]) {
                count++;
            }
        }
        return count;
    }

    public boolean hasMeasurePitches(int[] chord) {
        boolean hasMeasurePitches = false;
        int pitch;
        int index;
        for (int i = 0; i < chord.length; i++) {
            pitch = chord[i];
            index = Arrays.binarySearch(scale, pitch);
            if (index >= 0 && frequences[index] > 0) {
                hasMeasurePitches = true;
            }
        }
        return hasMeasurePitches;
    }

    public int getInvalidPitchesCount(int[] chord) {
        int count = 0;
        for (int i = 0; i < chord.length; i++) {
            if (-1 == Arrays.binarySearch(scale, chord[i])) {
                ++count;
            }
        }
        return count;
    }

    public int getDissonancePitchesCount(int[] chord) {
        int count = 0;
        for (int i = 0; i < chord.length; i++) {
            int index = Arrays.binarySearch(scale, chord[i]);
            if (-1 == index || 2 == index || 4 == index || 6 == index || 7 == index) {
                ++count;
            }
        }
        return count;
    }

    public int getDissonanceStatus(int[] chord, int[] nextChord) {
        int countSemitone = 0;
        boolean hasLeadingSemitone = false;
        for (int i = 0; i < measure.length; i++) {
            for (int j = 0; j < scale.length; j++) {
                if (1 == Math.abs(measure[i] - scale[j])) {
                    ++countSemitone;
                }
                if (null != nextChord) {
                    if (j < nextChord.length && 1 == Math.abs((measure[i] - nextChord[j]))) {
                        hasLeadingSemitone = true;
                    }
                }
            }
        }
        if (hasLeadingSemitone) {
            return countSemitone > 1 ? MANY_SEMITONE_DISSONANCE_WITH_ONE_LEADING : LEADING_SEMITONE_DISSONANCE;
        } else {
            return countSemitone > 1 ? MANY_SEMITONE_DISSONANCE : FEW_SEMITONE_DISSONANCE;
        }
    }

    public static int getRandomNote() {
        return random.nextInt(12);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < measure.length; i++) {
            builder.append(measure[i] + " ");
        }
        return builder.toString();
    }

    protected int getChordMelodyPitch() {
        int pitch = 0;
        int maxFreq = 0;
        for (int i = 0; i < frequences.length; i++) {
            if (frequences[i] > maxFreq) {
                maxFreq = frequences[i];
                pitch = scale[i];
            }
        }
        return pitch;
    }
}
