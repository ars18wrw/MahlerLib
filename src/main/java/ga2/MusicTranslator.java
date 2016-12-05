package ga2;

import jm.constants.Durations;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;


/**
 * Created by Уладзімір Асіпчук on 26/11/2016.
 */
public class MusicTranslator {

    public static final int TONIC = 60;

    public static Part getScoreByText(Chromosome chromosome) {
        int length = chromosome.getMeasures().size()*12;
        int[] array = new int[length];
        for (int i = 0; i < chromosome.getMeasures().size(); i++) {
            int[] measure = chromosome.getMeasures().get(i).measure;
            for (int j = 0; j < 12; j++) {
                array[i*12+j] = measure[j];
            }
        }
        return getScoreByText(array);
    }

    public static Part getScoreByText(int[] array) {
        Part part = new Part();
        Phrase phrase = new Phrase();
        double duration = Durations.SIXTEENTH_NOTE;
        int pitch = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] == array[i-1] || array[i] == -2) {
                duration+=Durations.SIXTEENTH_NOTE;
            } else {
                phrase.add(new Note(pitch+TONIC, duration));
                pitch = array[i];
                duration=Durations.SIXTEENTH_NOTE;
            }
        }
        phrase.add(new Note(pitch+TONIC, duration));
        part.add(phrase);
        return part;
    }

}
