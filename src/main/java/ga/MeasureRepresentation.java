package ga;

import java.util.Random;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class MeasureRepresentation {
    public static final int BEATS_IN_MEASURE = 3;
    public static final int ATOMIC_ITEMS_IN_BEAT = 4;
    public static final float ITEM_MUTATION_PROBABILITY = 0.2f;


    public static final int TENUTO = -2;
    public static final int REST = -1;


    protected int[] measure;
    private static Random random = new Random();


    public MeasureRepresentation(int[] measure) {
        this.measure = measure;
    }

    public MeasureRepresentation() {
        this.measure = new int[BEATS_IN_MEASURE*ATOMIC_ITEMS_IN_BEAT];
        for (int i = 0; i < BEATS_IN_MEASURE*ATOMIC_ITEMS_IN_BEAT; i++) {
            measure[i] = getRandomNote();
        }
//        while (measure[0] == -2 /*TODO*/ || measure[0] == -1){
//            measure[0] = getRandomNote();
//        }
    }

    public int[] getMeasure() {
        return measure;
    }

    public void mutation() {
        for (int i = 0; i < measure.length; i++) {
            if (getRandomProbability() < ITEM_MUTATION_PROBABILITY) {
                measure[i] = getRandomNote();
            }
        }
    }

    public int getFitness(MeasureRepresentation melodyMeasure) {
        int result = 0;
        int[] melody = melodyMeasure.getMeasure();
        int lastMelodyNote = 0;
        int lastAccompanimentNote = 0;
        for (int i = 0; i < measure.length; i++) {
            if (melody[i] >= 0) {
                lastMelodyNote = melody[i];
            }
            if (measure[i] >= 0) {
                lastAccompanimentNote = measure[i];
            }
            result += Fitness.getIntervalFitness(lastMelodyNote, lastAccompanimentNote);
        }
        return result;
    }


    public int getMeasureSize() {
        return measure.length;
    }

    public static float getRandomProbability() {
        return random.nextFloat();
    }


    public static int getRandomNote() {
        return random.nextInt(13);
    }

//    public static int getRandomNote(int previous) {
//        int randomNote = random.nextInt(13);
//        return randomNote;
//    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < measure.length; i++) {
            builder.append(measure[i]+" ");
        }
        return builder.toString();
    }
}
