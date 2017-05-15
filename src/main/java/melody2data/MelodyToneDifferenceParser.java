package melody2data;

import jm.music.data.Note;

import java.util.List;

/**
 * Created by Уладзімір Асіпчук on 03/04/2017.
 */
public class MelodyToneDifferenceParser implements IMelodyParser {

    @Override
    public String melodyToData(List<Note> noteList) {
        StringBuilder result = new StringBuilder();
        Note prevNote = null;
        for (Note note : noteList) {
            if (null != prevNote) {
                result.append(note.getPitch() - prevNote.getPitch());
            }
            prevNote = note;
        }
        return result.toString();
    }
}
