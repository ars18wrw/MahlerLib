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

public final class Markov2 implements JMC {
    private static final int COMPOSE_PIECE_SIZE = 150;

    private static final String FOLDER = "originals\\";
    private static final String NAME = "fantaisie";
    private static final String EXT = ".mid";

    private final String className;

    private Score s = new Score();

    public Markov2() {
        /* Prepare constant for creating folder */
        className = getClass().getName().toString();
    }

    public static void main(String[] args) {
        new Markov2().process();
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
        PartEx2[] partExes = new PartEx2[s.getSize()];
        for (int i = 0; i < s.getSize(); i++) {
            partExes[i] = new PartEx2(s.getPart(i));
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
            double[][][] probabilities = partExes[ii].getProbabilities();
            int[] pitches = partExes[ii].getPart().getPhrase(0).getPitchArray();
            int jj = 0;
            while (REST == pitches[jj]) {
                jj++;
            }
            int lastlastIndex = pitches[jj+1];
            phrase.add(new Note(lastlastIndex, Durations.Q));
            while (REST == pitches[jj]) {
                jj++;
            }
            int lastIndex = pitches[jj];
            phrase.add(new Note(lastIndex, Durations.Q));
            int i  = 0;
            while (COMPOSE_PIECE_SIZE >= i) {
                double randNum = Math.random();
                for (int j = 0; j < 127; j++) {
                    // Get rid of playing the same pitch again and again
                    randNum -= probabilities[lastlastIndex][lastIndex][j];
                    if (randNum <= 0) {
                        phrase.add(new Note(j, Durations.Q));
                        lastlastIndex = lastIndex;
                        lastIndex = j;
                        i++;
                        break;
                    }
                }
            }
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

    class PartEx2 {
        private static final String PRE_TEXT =
                "========================================================\n";
        private Part part;
        private double[][][] probabilities;
        private int[] times;

        PartEx2(Part part) {
            this.part = part.copy();
            probabilities = new double[127][127][127];
            for (int i = 0; i < part.getSize(); i++) {
                /* Find first notes */
                Phrase phrase = part.getPhrase(i);
                if (phrase.getSize() < 2) {
                    continue;
                }
                int lastlastIndex = 0;
                while (lastlastIndex < phrase.getSize() && phrase.getNote(lastlastIndex).getPitch() < 0) {
                    lastlastIndex++;
                }
                int lastIndex = lastlastIndex + 1;
                while (lastIndex < phrase.getSize() && phrase.getNote(lastIndex).getPitch() < 0) {
                    lastIndex++;
                }
                /* Add all 2nd order sequences */
                for (int j = lastlastIndex + 1; j < phrase.getSize(); j++) {
                    if (phrase.getNote(j).getPitch() < 0) {
                        continue;
                    }
                    probabilities[phrase.getNote(lastlastIndex).getPitch()][phrase.getNote(lastIndex).getPitch()][phrase.getNote(j).getPitch()]++;
                    lastlastIndex = lastIndex;
                    lastIndex = j;
                }
            }
            /* Normalize probability table */
            times = new int[127 * 127];
            for (int i = 0; i < 127; i++) {
                for (int j = 0; j < 127; j++) {
                    for (int k = 0; k < 127; k++) {
                        times[127 * i + j] += probabilities[i][j][k];
                    }
                    for (int k = 0; k < 127; k++) {
                        if(0 != times[127*i+k]) {
                            probabilities[i][j][k] /= times[127 * i + j];
                        }
                    }
                }
            }
        }

        public void printProbabilities() {
            System.out.println(PRE_TEXT);
            StringBuffer buffer;
            for (int i = 0; i < 127; i++) {
                for (int j = 0; j < 127; j++) {
                    buffer = new StringBuffer();
                    for (int ii = 0; ii < 127; ii++) {
                        buffer.append(probabilities[i][j][ii] + " ");
                    }
                    System.out.println(buffer);
                }
            }
        }

        public int[] getTimes() {
            return times;
        }

        public double[][][] getProbabilities() {
            return probabilities;
        }

        public Part getPart() {
            return part;
        }
    }
}
