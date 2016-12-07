package ga2;

import javafx.util.Pair;
import jm.constants.Durations;
import jm.constants.Pitches;
import jm.constants.Scales;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Write;

import java.util.*;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class GATest {
    public static final int POPULATION_SIZE = 100;
    public static final int NUMBER_OF_RUNS = 1000;
    public static final int CATACLYSM_TIME = 25;
    public static final double MUTATION_PERCENT = 0.2;
    public static final double CROSSOVER_PERCENT = 0.5;
    public static final double SURVIVED_PERCENT = 0.3;


    public static final int FITNESS_WEIGHT_FIRST = 1;
    public static final int FITNESS_WEIGHT_SECOND = 1;

    protected Set<Chromosome> population = new TreeSet<Chromosome>(new Comparator<Chromosome>() {
        @Override
        public int compare(Chromosome o1, Chromosome o2) {
            Pair<Integer, Integer> firstFitness = o1.getFitness();
            Pair<Integer, Integer> secondFitness = o2.getFitness();
            int result = Integer.compare(FITNESS_WEIGHT_FIRST * firstFitness.getKey() /*+ FITNESS_WEIGHT_SECOND*firstFitness.getValue()*/,
                    FITNESS_WEIGHT_FIRST * secondFitness.getKey() /*+ FITNESS_WEIGHT_SECOND*secondFitness.getValue()*/);
            if (0 == result) {
                result = o1.hashCode() - o2.hashCode();
            }
            return -result;
        }
    });

    public static void main(String[] args) {
        new GATest().process();
    }

    public void process() {
        Score score = new Score();
        Part part = initPart();
        score.add(part);
        int[][] frequences = processPart(Scales.MAJOR_SCALE, part);
        Chromosome best = null;
        initPopulation(frequences);
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            if (0 == i % CATACLYSM_TIME) {
                operateCataclysm(frequences);
            }
            operateMutation();
            operateCrossover();
            initNextGeneration();
            if (null == best || best.getFitness().getKey() < ((Chromosome) population.toArray()[0]).fitnesses.getKey()) {
                best = new Chromosome(((Chromosome) population.toArray()[0]).measures);
                best.updateFitness();
            }
            System.out.println(i + ": best:" + ((Chromosome) population.toArray()[0]).fitnesses + ", worst: " + ((Chromosome) population.toArray()[49]).fitnesses);
        }
//        Chromosome best = population.iterator().next();
        Part accompaniment = processChromosome(Pitches.C4, best);
        score.add(accompaniment);
        Write.midi(score, "tutti.mid");
        System.out.println(best.getFitness() + " : " + best.toString());
    }


    public void operateCrossover() {
        Object[] oldPopulation = population.toArray();
        for (int i = 0; i < POPULATION_SIZE * CROSSOVER_PERCENT; i++) {
            population.add(Chromosome.crossover((Chromosome) oldPopulation[(int) (oldPopulation.length * Math.random())], (Chromosome) oldPopulation[(int) (oldPopulation.length * Math.random())]));
        }
    }

    public void operateMutation() {
        Object[] oldPopulation = population.toArray();
        for (int i = 0; i < POPULATION_SIZE * MUTATION_PERCENT; i++) {
            Chromosome chromosome = ((Chromosome) oldPopulation[(int) (oldPopulation.length * Math.random())]);
            chromosome.mutation();
        }
    }

    // roulette. The best takes size probability, the second - size-1, ..., the worst - 1
    public void initNextGeneration() {
        Iterator<Chromosome> iter;
        List<Chromosome> list = new ArrayList<>();
        Chromosome choosen = null;
        int rand;
        int index;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            iter = population.iterator();
            rand = (int) (Math.random() * (population.size() - 1) * population.size() / 2.);
            index = 0;
            while (rand >= 0) {
                rand -= population.size() - index;
                choosen = iter.next();
            }
            iter.remove();
            list.add(choosen);
        }

        population.clear();

        for (Chromosome c : list) {
            population.add(c);
        }
    }

    // survivors are randomly chosen
    public void operateCataclysm(int[][] frequences) {
        Chromosome[] oldPopulation = population.toArray(new Chromosome[1]);
        population.clear();
        while (population.size() < SURVIVED_PERCENT*POPULATION_SIZE) {
            population.add(oldPopulation[(int)(Math.random()*oldPopulation.length)]);
        }
        while (population.size() < POPULATION_SIZE) {
                population.add(new Chromosome(frequences));
        }
    }

    public Part initPart() {
        Part part = new Part();
        Phrase phrase = new Phrase();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.B4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.B4, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));

        part.add(phrase);

        return part;
    }

    public int[][] processPart(int[] scale, Part part) {
        int size = 0;
        Phrase[] phraseArray = part.getPhraseArray();
        for (int i = 0; i < phraseArray.length; i++) {
            size += phraseArray[i].size();
        }
        int[][] frequences = new int[size][7];
        Note[] noteArray;
        int indexPart = 0;
        for (int i = 0; i < phraseArray.length; i++) {
            noteArray = phraseArray[i].getNoteArray();
            for (int j = 0; j < noteArray.length; j++) {
                int index = Arrays.binarySearch(scale, noteArray[j].getPitch() % 12);
                if (-1 != index) {
                    frequences[indexPart][index]++;
                }
                indexPart++;
            }
        }
        return frequences;
    }

    public Part processChromosome(int tonic, Chromosome chromosome) {
        Part part = new Part();
        Phrase phrase = new Phrase();
        // TODO durations of accompaniment
        int[] chord;
        for (MeasureRepresentation measure : chromosome.measures) {
            chord = measure.getMeasure().clone();
            for (int j = 0; j < chord.length; j++) {
                chord[j] += tonic;
            }
            phrase.addChord(chord, Durations.QUARTER_NOTE);
        }
        part.add(phrase);
        return part;
    }

    public void initPopulation(int[][] frequences) {
        List<Chromosome> list = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(new Chromosome(frequences));
        }
    }
}
