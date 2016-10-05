package probability;

import jm.JMC;
import jm.constants.Pitches;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Write;

import java.io.File;
import java.util.Random;

public final class SimpleDistribution implements JMC {
    private static final int COMPOSE_PIECE_SIZE = 30;

    private static final String FOLDER = "originals\\";
    private static final String NAME = "fantaisie";
    private static final String EXT = ".mid";

    private final String className;

    private Score s = new Score();

    public SimpleDistribution() {
        className = getClass().getName().toString();
    }

    public static void main(String[] args) {
        /* Prepare constant for creating folder */
        new SimpleDistribution().process();
    }

    private static final int[] distribution = new int[] {4, 1, 2, 2, 3, 1, 2, 4};

    private static final int[] c_major = new int[] {
            Pitches.C4, Pitches.D4, Pitches.E4, Pitches.F4, Pitches.G4, Pitches.B4,Pitches.C5
    };

    public void process() {
        new File("music/" + className).mkdir();
        new File("music/" + className + "/" + NAME).mkdir();
        Score score = new Score();
        Part part = new Part();
        Random random = new Random();
        int rouletteWheelSize = 0;
        for (int i = 0; i < distribution.length; i++) {
            rouletteWheelSize+=distribution[i];
        }
        for (int i = 0; i < COMPOSE_PIECE_SIZE; i++) {
            int randomInt = random.nextInt(rouletteWheelSize);
            int currentPitchIndex = 0;
            int oldRand = randomInt;
            while ((randomInt-=distribution[currentPitchIndex]) > 0) {
                currentPitchIndex++;
            }
            if (currentPitchIndex == 7) {
                currentPitchIndex = 6;
            }
            part.addNote(new Note(c_major[currentPitchIndex], QUARTER_NOTE), i);
        }
        score.add(part);
        score.setTempo(120);
        Write.midi(score, "music/" + className + "/" + NAME + "/simple_distribution.mid");
        return;
    }


}
