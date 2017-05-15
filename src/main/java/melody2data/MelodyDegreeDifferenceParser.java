package melody2data;

import basics.ModulationException;
import jm.music.data.Note;
import melody2harmony.MajorHarmonization;

import java.util.List;

/**
 * Created by Уладзімір Асіпчук on 03/04/2017.
 */
public class MelodyDegreeDifferenceParser implements IMelodyParser {

    @Override
    public String melodyToData(List<Note> noteList) throws ModulationException {
        MajorHarmonization majorHarmonization = new MajorHarmonization(0); // TODO Custom tonic ?
        StringBuilder result = new StringBuilder();
        Note prevNote = null;
        for (Note note : noteList) {
            if (null != prevNote) {
                result.append(majorHarmonization.getDegreeByPitch(note.getPitch()).getNumVal()
                        - majorHarmonization.getDegreeByPitch(prevNote.getPitch()).getNumVal());
            }
            prevNote = note;
        }
        return result.toString();
    }
}
