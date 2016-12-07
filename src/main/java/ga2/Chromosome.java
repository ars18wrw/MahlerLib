package ga2;

import javafx.util.Pair;

import java.util.*;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class Chromosome {
    protected List<MeasureRepresentation> measures;
    private static Random random = new Random();

    // copy constructor
    public Chromosome(List<MeasureRepresentation> measures) {
        this.measures = measures;
    }

    public Chromosome(int[][] frequences) {
        measures = new ArrayList<>();
        for (int i = 0; i < frequences.length; i++) {
            measures.add(new MeasureRepresentation(frequences[i]));
        }
    }

    public static Chromosome crossover(Chromosome parent1, Chromosome parent2) {
        Iterator<MeasureRepresentation> iterator1 = parent1.getMeasures().iterator();
        Iterator<MeasureRepresentation> iterator2 = parent2.getMeasures().iterator();

        ArrayList<MeasureRepresentation> childChromosome = new ArrayList<>();
        int crossoverPoint = random.nextInt(parent1.getMeasures().size());

        MeasureRepresentation measure1;
        MeasureRepresentation measure2;
        int i = 0;
        boolean isFirstParent = random.nextBoolean();
        while (iterator1.hasNext() && iterator2.hasNext()) {
            measure1 = iterator1.next();
            measure2 = iterator2.next();
            childChromosome.add(isFirstParent ? measure1 : measure2);
            if (++i >= crossoverPoint) {
                isFirstParent = !isFirstParent;
            }
        }
        return new Chromosome(childChromosome);
    }

    public void mutation() {
        MeasureRepresentation previousMeasure = null;
        for (MeasureRepresentation measure : measures) {
            if (null != previousMeasure) {
                previousMeasure.mutation(measure);
            } else {
                measure.mutation(null);
            }
            previousMeasure = measure;
        }
        if (null != previousMeasure) {
            previousMeasure.mutation(null);
        }
    }

    public Pair<Integer, Integer> getFitness() {
        int fitness1 = 0;
        int fitness2 = 0;
        Pair<Integer, Integer> fitness ;

        MeasureRepresentation previousMeasure = null;
        for (MeasureRepresentation measure : measures) {
            if (null != previousMeasure) {
                fitness = previousMeasure.getFitness(measure);
            } else {
                fitness = measure.getFitness(null);
            }
            fitness1 += fitness.getKey();
            fitness2 += fitness.getValue();
            previousMeasure = measure;
        }
        if (null != previousMeasure) {
            fitness = previousMeasure.getFitness(null);
            fitness1 += fitness.getKey();
            fitness2 += fitness.getValue();
        }
        return new Pair<Integer, Integer>(fitness1, fitness2);
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
