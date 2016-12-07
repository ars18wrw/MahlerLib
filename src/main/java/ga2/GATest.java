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
    public static final int NUMBER_OF_RUNS = 300;
    public static final int MUTATIONS_MAX_NUMBER = 20;

    public static final int FITNESS_WEIGHT_FIRST = 3;
    public static final int FITNESS_WEIGHT_SECOND = 1;

    protected Set<Chromosome> population;

    public static void main(String[] args){
        new GATest().process();
    }

    public void process() {
        Score score = new Score();
        Part part = initPart();
        score.add(part);
        int[][] frequences = processPart(Scales.MAJOR_SCALE, part);
        initPopulation(frequences);
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            operateCrossover();
            operateMutation();
            System.out.println(i);
        }
        Chromosome best = population.iterator().next();
        Part accompaniment = processChromosome(Pitches.C4, best);
        score.add(accompaniment);
        Write.midi(score, "tutti.mid");
        System.out.println(best.toString());
    }


    public void operateCrossover() {
        Object[] oldPopulation = population.toArray();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(Chromosome.crossover((Chromosome) oldPopulation[(int) (oldPopulation.length * Math.random())], (Chromosome) oldPopulation[(int) (oldPopulation.length * Math.random())]));
        }
        Iterator<Chromosome> iter = population.iterator();
        List<Chromosome> list = new ArrayList<>();
        int i = 0;
        while (iter.hasNext() && i < POPULATION_SIZE) {
            i++;
            list.add(iter.next());
        }
        initNextGeneration(list);
    }

    public void operateMutation() {
        Object[] oldPopulation = population.toArray();
        int numberOfMutations = (int) (MUTATIONS_MAX_NUMBER*Math.random());
        for (int i = 0; i < numberOfMutations; i++) {
            ((Chromosome)oldPopulation[(int)(oldPopulation.length*Math.random())]).mutation();
        }
    }

    public void initNextGeneration(List<Chromosome> list) {
        population = new TreeSet<Chromosome>(new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome o1, Chromosome o2) {
                Pair<Integer, Integer> firstFitness = o1.getFitness();
                Pair<Integer, Integer> secondFitness = o2.getFitness();
                int result = Integer.compare(FITNESS_WEIGHT_FIRST*firstFitness.getKey() + FITNESS_WEIGHT_SECOND*firstFitness.getValue(),
                        FITNESS_WEIGHT_FIRST*secondFitness.getKey() + FITNESS_WEIGHT_SECOND*secondFitness.getValue());
                if (0 == result) {
                    result = o1.hashCode() - o2.hashCode();
                }
                return -result;
            }
        });

        for (Chromosome c : list) {
            population.add(c);
        }
    }

    public Part initPart() {
        Part part = new Part();
        Phrase phrase = new Phrase();

        phrase.add(new Note(Pitches.C4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.D4, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.B3, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.D4, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.E4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.E4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.F4, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.E4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.D4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C4, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.D4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.C4, Durations.QUARTER_NOTE));
        phrase.add(new Note(Pitches.B3, Durations.QUARTER_NOTE));

        phrase.add(new Note(Pitches.C4, Durations.QUARTER_NOTE));

        part.add(phrase);

        return part;
    }

    public int[][] processPart(int[] scale, Part part) {
        int size = 0;
        Phrase[] phraseArray = part.getPhraseArray();
        for (int i = 0; i < phraseArray.length; i++) {
            size+=phraseArray[i].size();
        }
        int[][] frequences = new int[size][7];
        Note[] noteArray;
        int indexPart = 0;
        for (int i = 0; i < phraseArray.length; i++) {
            noteArray = phraseArray[i].getNoteArray();
            for (int j = 0; j < noteArray.length; j++) {
                int index = Arrays.binarySearch(scale, noteArray[j].getPitch()%12);
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
                chord[j]+=tonic;
            }
            phrase.addChord(chord, Durations.QUARTER_NOTE);
        }
        part.add(phrase);
        return part;
    }

    public void initPopulation(int[][] frequences) {
        List<Chromosome> list = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            list.add(new Chromosome(frequences));
        }
        initNextGeneration(list);
    }
}
