package probability;

import jm.JMC;
import jm.constants.Durations;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Read;
import jm.util.Write;

import java.io.File;

/* This class differs from Markov1 and Markov2:
* Since the amount of data is limited,
* one should use not all 127 pitches,
* but a reduced number, for instance, 50. */
public final class Markov3 implements JMC {
    private static final int COMPOSE_PIECE_SIZE = 150;
    /// TODO Add some mechanism to choose the interval dynamically
    private static int BOTTOM_PITCH_BOUND = 60;
    private static int TOP_PITCH_BOUND = 110;
    private static final int PITCH_INTERVAL = 50;

    private static final String FOLDER = "originals\\";
    private static final String NAME = "fantaisie"; // Replace with your midi file
    private static final String EXT = ".mid";

    private final String className;

    private Score s = new Score();

    public Markov3() {
        /* Prepare constant for creating folder */
        className = getClass().getName().toString();
    }

    public static void main(String[] args) {
        new Markov3().process();
    }

    public void process() {
        /* Read midi */
        Read.midi(s, FOLDER + NAME + EXT);
        s.setTitle(NAME);
        /* Create folders */
        new File("music/" + className).mkdir();
        new File("music/" + className + "/" + NAME).mkdir();
        /* Fill probability matrix*/
        Score scoreToWrite = null;
        PartEx3[] partExes = new PartEx3[s.getSize()];
        for (int i = 0; i < s.getSize(); i++) {
            Part part = s.getPart(i);
//            BOTTOM_PITCH_BOUND = part.getLowestPitch();
//            TOP_PITCH_BOUND = part.getHighestPitch();
//            if (Math.abs(TOP_PITCH_BOUND - BOTTOM_PITCH_BOUND) > PITCH_INTERVAL) {
//                continue;
//            }
            partExes[i] = new PartEx3(s.getPart(i));
            scoreToWrite = new Score();
            scoreToWrite.add(s.getPart(i));
            scoreToWrite.setTempo(180);
            Write.midi(scoreToWrite, String.format("music/" + className + "/" + NAME + "/ori_voice" + "%d.mid", i));
        }
        /* Let's build music */
        Score scoreTogether = new Score();
        for (int ii = 0; ii < partExes.length; ii++) {
            Score score = new Score();
            Part part = new Part();
            Phrase phrase = new Phrase();
            int[] times = partExes[ii].getTimes();
            double[][][][] probabilities = partExes[ii].getProbabilities();
            int[] pitches = partExes[ii].getPart().getPhrase(0).getPitchArray();
            int jj = 0;
            while (REST == pitches[jj]) {
                jj++;
            }
            int lastlastlastIndex = pitches[jj];
            phrase.add(new Note(lastlastlastIndex, Durations.Q));
            jj++;
            while (REST == pitches[jj]) {
                jj++;
            }
            int lastlastIndex = pitches[jj];
            phrase.add(new Note(lastlastIndex, Durations.Q));
            jj++;
            while (REST == pitches[jj]) {
                jj++;
            }
            int lastIndex = pitches[jj];
            phrase.add(new Note(lastIndex, Durations.Q));
            int i = 0;
            while (COMPOSE_PIECE_SIZE >= i) {
                double randNum = Math.random();
                for (int j = BOTTOM_PITCH_BOUND; j < TOP_PITCH_BOUND; j++) {
                    // Get rid of playing the same pitch again and again
                    randNum -= probabilities[lastlastlastIndex - BOTTOM_PITCH_BOUND][lastlastIndex - BOTTOM_PITCH_BOUND][lastIndex - BOTTOM_PITCH_BOUND][j - BOTTOM_PITCH_BOUND];
                    if (randNum < 0) {
                        phrase.add(new Note(j, Durations.Q));
                        lastlastlastIndex = lastlastIndex;
                        lastlastIndex = lastIndex;
                        lastIndex = j;
                        i++;
                        break;
                    }
                }
            }
            // do not forget about
            part.add(phrase);
            scoreTogether.add(part);
            score.add(part);
            score.setTempo(180);
            Write.midi(score, String.format("music/" + className + "/" + NAME + "/com_voice" + "%d.mid", ii));
        }
        scoreTogether.setTempo(180);
        Write.midi(scoreTogether, "music/" + className + "/" + NAME + "/tutti.mid");
        return;
    }

    class PartEx3 {
        private Part part;
        private double[][][][] probabilities;
        private int[] times;

        PartEx3(Part part) {
            this.part = part.copy();
            probabilities = new double[PITCH_INTERVAL][PITCH_INTERVAL][PITCH_INTERVAL][PITCH_INTERVAL];
            for (int i = 0; i < part.getSize(); i++) {
                Phrase phrase = part.getPhrase(i);
                if (phrase.getSize() < 3) {
                    continue;
                }
                int lastlastlastIndex = 0;
                while (lastlastlastIndex < phrase.getSize()
                        && (phrase.getNote(lastlastlastIndex).getPitch() < BOTTOM_PITCH_BOUND
                            || phrase.getNote(lastlastlastIndex).getPitch() >= TOP_PITCH_BOUND)) {
                    lastlastlastIndex++;
                }
                int lastlastIndex = lastlastlastIndex + 1;
                while (lastlastIndex < phrase.getSize()
                        && (phrase.getNote(lastlastIndex).getPitch() < BOTTOM_PITCH_BOUND
                            || phrase.getNote(lastlastIndex).getPitch() >= TOP_PITCH_BOUND)) {
                    lastlastIndex++;
                }
                int lastIndex = lastlastIndex + 1;
                while (lastIndex < phrase.getSize()
                        && (phrase.getNote(lastIndex).getPitch() < BOTTOM_PITCH_BOUND
                            || phrase.getNote(lastIndex).getPitch() >= TOP_PITCH_BOUND)) {
                    lastIndex++;
                }
                if (lastIndex >= phrase.getSize()) {
                    continue;
                }

                for (int j = lastIndex + 1; j < phrase.getSize(); j++) {
                    if (phrase.getNote(j).getPitch() < BOTTOM_PITCH_BOUND
                            || phrase.getNote(j).getPitch() >= TOP_PITCH_BOUND) {
                        continue;
                    }
                    probabilities[phrase.getNote(lastlastlastIndex).getPitch() - BOTTOM_PITCH_BOUND][phrase.getNote(lastlastIndex).getPitch() - BOTTOM_PITCH_BOUND][phrase.getNote(lastIndex).getPitch() - BOTTOM_PITCH_BOUND][phrase.getNote(j).getPitch() - BOTTOM_PITCH_BOUND]++;
                    lastlastlastIndex = lastlastIndex;
                    lastlastIndex = lastIndex;
                    lastIndex = j;
                }
            }
            times = new int[PITCH_INTERVAL * PITCH_INTERVAL * PITCH_INTERVAL];
            for (int i = 0; i < PITCH_INTERVAL; i++) {
                for (int j = 0; j < PITCH_INTERVAL; j++) {
                    for (int k = 0; k < PITCH_INTERVAL; k++) {
                        for (int l = 0; l < PITCH_INTERVAL; l++) {
                            times[PITCH_INTERVAL * PITCH_INTERVAL * i + PITCH_INTERVAL * j + k] +=
                                    probabilities[i][j][k][l];
                        }
                        for (int l = 0; l < PITCH_INTERVAL; l++) {
                            if (0 != times[PITCH_INTERVAL * PITCH_INTERVAL * i + PITCH_INTERVAL * j + k]) {
                                probabilities[i][j][k][l] /=
                                        times[PITCH_INTERVAL * PITCH_INTERVAL * i + PITCH_INTERVAL * j + k];
                            }
                        }
                    }
                }
            }
        }

        public int[] getTimes() {
            return times;
        }

        public double[][][][] getProbabilities() {
            return probabilities;
        }

        public Part getPart() {
            return part;
        }
    }
}
