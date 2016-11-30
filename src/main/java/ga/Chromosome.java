package ga;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class Chromosome {
    public static final float MEASURE_MUTATION_PROBABILITY = 0.1f;
    protected List<MeasureRepresentation> measures;
    private static Random random = new Random();

    public Chromosome(List<MeasureRepresentation> measures) {
        this.measures = measures;
    }

    public Chromosome(int size) {
        this.measures = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            measures.add(new MeasureRepresentation());
        }
    }

    public static Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        Iterator<MeasureRepresentation> iterator1 = parent1.getMeasures().iterator();
        Iterator<MeasureRepresentation> iterator2 = parent2.getMeasures().iterator();

        ArrayList<MeasureRepresentation> childChromosome = new ArrayList<>();

        int[] measure1;
        int[] measure2;
        int[] childMeasure = new int[MeasureRepresentation.BEATS_IN_MEASURE*MeasureRepresentation.ATOMIC_ITEMS_IN_BEAT];
        int crossoverStart;
        int crossoverEnd;
        boolean isFirstParent;
        while (iterator1.hasNext() && iterator2.hasNext()) {
            measure1 = iterator1.next().getMeasure();
            measure2 = iterator2.next().getMeasure();
            crossoverStart = getRandomCrossoverStart(measure1.length - 1);
            crossoverEnd = crossoverStart + getRandomCrossoverEnd(measure1.length-crossoverStart-1);
            isFirstParent = getRandomFirstCrossoverParent();
            for (int i = 0; i < crossoverStart; i++) {
                childMeasure[i] = isFirstParent ? measure1[i] : measure2[i];
            }
            for (int i = crossoverStart; i < crossoverEnd; i++) {
                childMeasure[i] = isFirstParent ? measure2[i] : measure1[i];
            }
            for (int i = crossoverEnd; i < measure1.length; i++) {
                childMeasure[i] = isFirstParent ? measure1[i] : measure2[i];
            }
            childChromosome.add(new MeasureRepresentation(childMeasure));
        }
        return new Chromosome(childChromosome);
    }

    public void mutation() {
        for (MeasureRepresentation measure : measures) {
            if (getRandomProbability() < MEASURE_MUTATION_PROBABILITY) {
                measure.mutation();
            }
        }
    }

    public int getFitnessValue(Chromosome melody) {
        int fitness = 0;

        Iterator<MeasureRepresentation> melodyIterator = melody.getMeasures().iterator();
        Iterator<MeasureRepresentation> thisIterator = this.getMeasures().iterator();


        while (melodyIterator.hasNext() && thisIterator.hasNext()) {
            fitness += thisIterator.next().getFitness(melodyIterator.next());
        }
        return fitness;
    }

    public static float getRandomProbability() {
        return random.nextFloat();
    }

    public static int getRandomCrossoverStart(int size) {
        return random.nextInt(size);
    }
    public static int getRandomCrossoverEnd(int size) {
        return random.nextInt(size);
    }

    public static boolean getRandomFirstCrossoverParent() {
        return random.nextBoolean();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (MeasureRepresentation measure : measures) {
            result.append(measure.toString());
        }
        return result.toString();
    }

    public List<MeasureRepresentation> getMeasures() {
        return measures;
    }
}
