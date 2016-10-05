package chords;

import jm.music.data.*;
import jm.util.Read;
import jm.util.Write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Уладзімір Асіпчук on 23.04.16.
 */
public final class DegreeHarmonization {
    // TODO Specify
    private static final String FOLDER = "melodies\\";
    private static final String NAME = "152194";
    private static final String EXT = ".mid";

    protected Map<ChordFunction, List<ChordFunction>> rules;
    protected Map<Degree, List<ChordFunction>> neighbourhood;

    private Score s = new Score();

    public DegreeHarmonization() {
        // rules
        rules = new HashMap<>();
        List<ChordFunction> tempSet;

        // I
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.II);
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.S);
        tempSet.add(ChordFunction.D);
        tempSet.add(ChordFunction.VI);
        tempSet.add(ChordFunction.VII);
        rules.put(ChordFunction.T, tempSet);

        // II
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.S);
        rules.put(ChordFunction.II, tempSet);

        // III
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.S);
        tempSet.add(ChordFunction.D);
        tempSet.add(ChordFunction.VI);
        tempSet.add(ChordFunction.VII);
        rules.put(ChordFunction.III, tempSet);

        // IV
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.II);
        tempSet.add(ChordFunction.S);
        rules.put(ChordFunction.S, tempSet);

        // V
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.D);
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.S);
        tempSet.add(ChordFunction.VII);
        rules.put(ChordFunction.D, tempSet);

        // VI
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.II);
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.S);
        rules.put(ChordFunction.VI, tempSet);

        // VII
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        rules.put(ChordFunction.VII, tempSet);

        // neighbourhood
        neighbourhood = new HashMap<>();

        // I
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.S);
        tempSet.add(ChordFunction.VI);
        neighbourhood.put(Degree.I, tempSet);

        // II
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.II);
        tempSet.add(ChordFunction.D);
        tempSet.add(ChordFunction.VII);
        neighbourhood.put(Degree.II, tempSet);

        // III
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.VI);
        neighbourhood.put(Degree.III, tempSet);

        // IV
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.II);
        tempSet.add(ChordFunction.S);
        tempSet.add(ChordFunction.VII);
        neighbourhood.put(Degree.IV, tempSet);

        // V
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.T);
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.D);
        neighbourhood.put(Degree.V, tempSet);

        // VI
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.II);
        tempSet.add(ChordFunction.S);
        tempSet.add(ChordFunction.VI);
        neighbourhood.put(Degree.VI, tempSet);

        // VII
        tempSet = new ArrayList<>();
        tempSet.add(ChordFunction.III);
        tempSet.add(ChordFunction.D);
        tempSet.add(ChordFunction.VII);

        neighbourhood.put(Degree.VII, tempSet);
    }

    public static void main(String[] args) {
        new DegreeHarmonization().process();
    }

    public void process() {
        List<Note> pitchesToHarmonize = new ArrayList<>();

        /* Read midi */
        Read.midi(s, FOLDER + NAME + EXT);

        Part part;
        for (int jj = 0; jj < 1 /* only the solo voice */; jj++) {
            part = s.getPart(jj);
            for (int i = 0; i < part.getSize(); i++) {
                Phrase phrase = part.getPhrase(i);
                if (phrase.getSize() < 1) {
                    continue;
                }
                // TODO Specify the tonality by transposition to C
//                Mod.transpose(phrase, -7 - 12);
                for (int j = 0; j < phrase.getSize(); j++) {
                    if (phrase.getNote(j).getPitch() != Note.REST) {
                        pitchesToHarmonize.add(new Note(phrase.getNote(j).getPitch(), phrase.getNote(j).getRhythmValue()));
                    }
                }
            }

        }

        List<ChordFunction> possibleChords = new ArrayList<>();
        possibleChords.add(ChordFunction.T);
        possibleChords.add(ChordFunction.II);
        possibleChords.add(ChordFunction.III);
        possibleChords.add(ChordFunction.S);
        possibleChords.add(ChordFunction.D);
        possibleChords.add(ChordFunction.VI);
        possibleChords.add(ChordFunction.VII);

        ChordFunction[] result = new ChordFunction[pitchesToHarmonize.size()];
        recursion(pitchesToHarmonize, possibleChords, 0, result);

        Score score = new Score();
        Part accompaniment = new Part();
        Part solo = new Part();
        for (int i = 0; i < result.length; i++) {
            CPhrase cPhraseAccompaniment = chords.ChordUtility.buildChordInMajor(pitchesToHarmonize.get(i), result[i]);
            accompaniment.addCPhrase(cPhraseAccompaniment);
            CPhrase cPhraseSolo = new CPhrase();
            cPhraseSolo.addPhrase(new Phrase(pitchesToHarmonize.get(i)));
            solo.addCPhrase(cPhraseSolo);
        }
        solo.setChannel(2);
        solo.setInstrument(Part.VIOLIN);
        score.add(solo);
        score.add(accompaniment);

        Write.midi(score, "Harmonization.mid");
    }


    public boolean recursion(List<Note> pitches, List<ChordFunction> chords, int currentPitchNumberInList, ChordFunction[] result) {
        if (pitches.size() == currentPitchNumberInList) {
            return true;
        }
        Degree degreeByPitch;
        try {
            degreeByPitch = getDegreeByPitch(pitches.get(currentPitchNumberInList).getPitch());
        } catch (ModulationException e) {
            degreeByPitch = e.getDegreeInModulatedTonality();
            // Make Modulation
        }
        chords.retainAll(neighbourhood.get(degreeByPitch));
        if (0 == chords.size()) {
            // should harmonize with other chord before
            return false;
        }

        // the last in most cases should be tonic
        if (currentPitchNumberInList-1 == pitches.size() && chords.contains(ChordFunction.T)) {
            result[currentPitchNumberInList] = ChordFunction.T;
            return true;
        }

        int numberOfHarmonyUsed = 0;
        while (numberOfHarmonyUsed < chords.size() && !recursion(pitches, new ArrayList<>(rules.get(chords.get(numberOfHarmonyUsed))), currentPitchNumberInList + 1, result)) {
            numberOfHarmonyUsed++;
        }
        if (numberOfHarmonyUsed == chords.size()) {
            // TODO : do not harmonize this
            if (currentPitchNumberInList == 0) {
                result[currentPitchNumberInList] = ChordFunction.REST;
            } else {
                result[currentPitchNumberInList] = result[currentPitchNumberInList - 1];
            }
//            return false;
        } else {
            result[currentPitchNumberInList] = chords.get(numberOfHarmonyUsed);
        }
        return true;
    }

    public Degree getDegreeByPitch(int pitch) throws ModulationException {
        int i = (pitch) % 12;
        switch (i) {
            case 0:
                return Degree.I;
            case 2:
                return Degree.II;
            case 4:
                return Degree.III;
            case 5:
                return Degree.IV;
            case 7:
                return Degree.V;
            case 9:
                return Degree.VI;
            case 11:
                return Degree.VII;
            case 6:
                // TODO Modulation to Dominant
                throw new ModulationException(Degree.VII);
                // TODO
            default:
                return Degree.I;
        }
    }
}

