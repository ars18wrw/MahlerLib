package ga2;

import jm.music.data.Score;
import jm.util.Write;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Уладзімір Асіпчук on 20/11/2016.
 */
public class GATest {
    public static final int POPULATION_SIZE = 100;
    public static final int NUMBER_OF_RUNS = 100;
    public static final int MUTATIONS_MAX_NUMBER = 20;


    protected Chromosome chromosome;
    protected Set<Chromosome> population;

    public static void main(String[] args){
        new GATest().process();
    }

    public void process() {
        Score score = new Score();
        initChromosome();
        score.add(MusicTranslator.getScoreByText(chromosome));
        initPopulation();
        for (int i = 0; i < NUMBER_OF_RUNS; i++) {
            operateCrossover();
            operateMutation();
        }
        Chromosome best = population.iterator().next();
        score.add(MusicTranslator.getScoreByText(best));
        Write.midi(score, "tutti.mid");
        System.out.println(best.toString());
    }

    public void initChromosome() {
        File file = new File("./src/main/resources/ga_patterns/god_save_the_queen.txt");
        int[] melody = new int[12];

        List<MeasureRepresentation> list = new ArrayList<>();
        try {
            Scanner sc = new Scanner(file);
            int i = 0;
            while (sc.hasNextLine()) {
                melody[i++] = sc.nextInt();
                if (i == 12) {
                    list.add(new MeasureRepresentation(melody.clone()));
                    Arrays.fill(melody, 0);
                    i = 0;
                }
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        chromosome = new Chromosome(list);

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
        initPopulation(list);
    }

    public void operateMutation() {
        Object[] oldPopulation = population.toArray();
        int numberOfMutations = (int) (MUTATIONS_MAX_NUMBER*Math.random());
        for (int i = 0; i < numberOfMutations; i++) {
            ((Chromosome)oldPopulation[(int)(oldPopulation.length*Math.random())]).mutation();
        }
    }

    public void initPopulation(List<Chromosome> list) {
        population = new TreeSet<Chromosome>(new Comparator<Chromosome>() {
            @Override
            public int compare(Chromosome o1, Chromosome o2) {
                int result = Integer.compare(o1.getFitness(chromosome), o2.getFitness(chromosome));
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


    public void initPopulation() {
        List<Chromosome> list = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            list.add(new Chromosome(5));
        }
        initPopulation(list);
    }
}
