package ga2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class Chromosome {
    protected List<MeasureRepresentation> measures;
    private static Random random = new Random();
    private static List<int[]> freqList;

    // copy constructor
    public Chromosome(List<MeasureRepresentation> measures) {
        this.measures = measures;
        if (null == freqList || null == measures || freqList.size() != measures.size()) {
            throw new RuntimeException();
        }
    }

    // IMPORTANT to set
    public void setFreqList(List<int[]> freqList) {
        this.freqList = freqList;
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

    public int getFitness() {
        int fitness = 0;MeasureRepresentation previousMeasure = null;
        for (MeasureRepresentation measure : measures) {
            if (null != previousMeasure) {
                fitness += previousMeasure.getFitness(measure);
            } else {
                fitness += measure.getFitness(null);
            }
            previousMeasure = measure;
        }
        if (null != previousMeasure) {
            fitness += previousMeasure.getFitness(null);
        }
        return fitness;
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
