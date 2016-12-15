package ga2;

import javafx.util.Pair;
import jm.constants.Durations;
import jm.constants.Pitches;
import jm.constants.ProgramChanges;
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
    public static final int NUMBER_OF_RUNS = 2000;
    public static final int CATACLYSM_TIME = 40;
    public static final int PRINT_LOG_TIME = 25;

    public static final double MUTATION_PERCENT = 0.2;
    public static final double CROSSOVER_PERCENT = 0.5;
    public static final double SURVIVED_PERCENT = 0.3;


    public static final int FITNESS_WEIGHT_FIRST = 1;
    public static final int FITNESS_WEIGHT_SECOND = 1;

    protected static final Comparator<Chromosome> comparator = new Comparator<Chromosome>() {
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
    };

    protected List<Chromosome> population = new ArrayList<>();

    public static void main(String[] args) {
        new GATest().process();
    }

    public void process() {
        Score score = new Score();
        Part part = initPart();
        part.setInstrument(ProgramChanges.VIOLA);
        score.add(part);
        int[][] frequences = processPart(Scales.MAJOR_SCALE, part);
        Chromosome best = null;
        initPopulation(frequences);
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            if (0 == i % CATACLYSM_TIME) {
                best =  population.iterator().next();
                for (int j = 0; j < best.fines.length; j++) {
                    System.out.print(best.fines[j] + " ");
                }
                System.out.println();
                operateCataclysm(frequences);
            }
            operateMutation();
            operateCrossover();
            initNextGeneration();
//            if (null == best || best.getFitness().getKey() < ((Chromosome) population.toArray()[0]).fitnesses.getKey()) {
//                best = new Chromosome(((Chromosome) population.toArray()[0]).measures);
//                best.updateFitness();
//            }
//            System.out.println(i + ": best:" + ((Chromosome) population.toArray()[0]).fitnesses.getKey()
//                    + ", worst: " + ((Chromosome) population.toArray()[49]).fitnesses.getKey());
        }
        best = population.iterator().next();
        Part accompaniment = processChromosome(part, Pitches.C4, best);
        score.add(accompaniment);
        score.setTempo(120);
        Write.midi(score, "tutti.mid");
        best.updateFitness();
        System.out.println(best.getFitness() + " : " + best.toString());
    }

    public void updatePopulationFitnesses() {
        for (Chromosome chr : population) {
            chr.updateFitness();
        }
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
        updatePopulationFitnesses();
        Collections.sort(population, comparator);
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

    public void operateCataclysm(int[][] frequences) {
        Iterator<Chromosome> iter;
        Set<Chromosome> set = new HashSet<>();
        Chromosome choosen = null;
        int rand;
        int index;
        while(set.size() < SURVIVED_PERCENT*POPULATION_SIZE) {
            iter = population.iterator();
            rand = (int) (Math.random() * (population.size() - 1) * population.size() / 2.);
            index = 0;
            while (rand >= 0) {
                rand -= population.size() - index;
                choosen = iter.next();
            }
            iter.remove();
            set.add(choosen);
        }

        population.clear();
        population.addAll(set);
        while (population.size() < POPULATION_SIZE){
            population.add(new Chromosome(frequences));
        }
    }

    public Part initPart() {
//        Phrase resultPhrase = new Phrase();
//        Score temp = new Score();
//        Read.midi(temp, "C:\\Repo\\MahlerLib\\papageno.mid");
//        Note[] notes = temp.getPart(0).getPhrase(0).getNoteArray();
//        for (int i = 0; i < notes.length; i++) {
//            if (notes[i].getPitch() > 0) {
//                resultPhrase.addNote(notes[i]);
//            }
//        }
//        Part result = new Part();
//        result.add(resultPhrase);
//        return result;
        Part part = new Part();
        Phrase phrase = new Phrase();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.B4, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.B4, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE*3));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.G5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.G5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.G5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.G5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.F5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.E5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.D5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.B4, Durations.QUARTER_NOTE));
        part.add(phrase.copy());
        phrase.empty();

        phrase.add(new Note(Pitches.C5, Durations.QUARTER_NOTE*3));
        part.add(phrase.copy());
        phrase.empty();

        Write.midi(part, "britain.mid");
//        Score temp = new Score();
//        Read.midi(temp, "britain.mid");
        return part;
    }

    public int[][] processPart(int[] scale, Part part) {
        Phrase[] phraseArray = part.getPhraseArray();
        int[][] frequences = new int[phraseArray.length][7];
        Note[] noteArray;
        for (int i = 0; i < phraseArray.length; i++) {
            noteArray = phraseArray[i].getNoteArray();
            for (int j = 0; j < noteArray.length; j++) {
                int index = Arrays.binarySearch(scale, noteArray[j].getPitch() % 12);
                if (-1 != index) {
                    frequences[i][index]++;
                }
            }
        }
        return frequences;
    }

    public Part processChromosome(Part melody, int tonic, Chromosome chromosome) {
        Part part = new Part();
        Note[] notes = null;
        Double duration = null;
        List<Double> durations = new LinkedList<>();
        for (int j = 0; j < melody.size(); j++) {
            duration = 0d;
            notes = melody.getPhrase(j).getNoteArray();
            for (int i = 0; i < notes.length; i++) {
                if (notes[i].getPitch() > 0) {
                    duration += notes[i].getRhythmValue();
                }
            }
            durations.add(duration);
        }

        Phrase phrase = new Phrase();
        int[] chord;
        Iterator<Double> iter = durations.iterator();
        for (MeasureRepresentation measure : chromosome.measures) {
            chord = measure.getMeasure().clone();
            for (int j = 0; j < chord.length; j++) {
                chord[j] += tonic;
            }
            phrase.addChord(chord, iter.next());
        }
        part.add(phrase);
        return part;
    }

    public void initPopulation(int[][] frequences) {
        List<Chromosome> list = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            Chromosome c = new Chromosome(frequences);
            c.updateFitness();
            population.add(c);
        }
    }
}
