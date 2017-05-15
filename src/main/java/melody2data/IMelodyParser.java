package melody2data;

import basics.ModulationException;
import jm.music.data.Note;

import java.util.List;

/**
 * Created by Уладзімір Асіпчук on 03/04/2017.
 */
public interface IMelodyParser {

    public String melodyToData(List<Note> noteList) throws ModulationException;
}
