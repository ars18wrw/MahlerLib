package ga2;

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
        for (int i = 0; i < scale.length-3; i++) {
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
        for (int i = 0; i < scale.length-3; i++) {
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

    public int getFitness(MeasureRepresentation nextMeasure) {
        int result = 0;
        return result;
    }

    public void mutatePitch(float probability) {
        if (random.nextFloat() < probability) {
            int index = random.nextInt(3);
            boolean isUp = random.nextBoolean();
            int indexInScale = Arrays.binarySearch(scale, measure[index]);
            if (-1 != indexInScale) {
                measure[index] = scale[(isUp ? indexInScale+1 : measure.length+indexInScale-1)%measure.length];
            } else {
                measure[index] = (isUp ? measure[index]+2 : 12+measure[index]-2) % 12;
            }
        }
    }

    public void inverse(float probability) {
        if (random.nextFloat() < probability) {
            int temp = measure[0];
            for (int i = 0; i < measure.length-1; i++) {
                measure[i] = measure[i+1];
            }
            measure[measure.length-1] = temp;
        }
    }

    public void reinitialiseMeasure(float probability) {
        if (random.nextFloat() < probability) {
            int index = random.nextInt(7);
            while (0 == frequences[(index++)%7]);
            index = (index-1)%7;
            for (int i = 0; i < measure.length; i++) {
                measure[i] = scale[(index+2*i)%7];
            }
        }
    }

    public void copy(float probability, MeasureRepresentation nextMeasure) {
        if (random.nextFloat() < probability && null != nextMeasure) {
            this.measure = nextMeasure.getMeasure().clone();
        }
    }


    public static int getRandomNote() {
        return random.nextInt(12);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < measure.length; i++) {
            builder.append(measure[i]+" ");
        }
        return builder.toString();
    }
}
