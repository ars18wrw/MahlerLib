package chords;

import jm.music.data.*;
import jm.music.tools.Mod;
import jm.util.Read;
import jm.util.Write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Уладзімір Асіпчук on 23.04.16.
 */
public final class MinorHarmonization {
    // TODO Specify
    private static final String FOLDER = "melodies\\";
    private static final String NAME = "121";
    private static final String EXT = ".mid";

    protected Map<ChordFunction, List<ChordFunction>> rules;
    protected Map<Degree, List<ChordFunction>> neighbourhood;

    private Score s = new Score();

    public MinorHarmonization() {
        // rules
        rules = new HashMap<>();
        List<ChordFunction> tempList;

        // T
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.S);
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.VIIS);
        rules.put(ChordFunction.T, tempList);

        // S
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.S);
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.VII7);
        tempList.add(ChordFunction.VIIS);
        rules.put(ChordFunction.S, tempList);

        // D
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.VII7);
        tempList.add(ChordFunction.VIIS);
        // The last, cause D->S is not commonly used
        tempList.add(ChordFunction.II);
        tempList.add(ChordFunction.S);
        rules.put(ChordFunction.D, tempList);

        // D7
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.VII7);
        tempList.add(ChordFunction.D);
        rules.put(ChordFunction.D7, tempList);

        // II
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.D);
        rules.put(ChordFunction.II, tempList);

        // VIIS
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.VII7);
        tempList.add(ChordFunction.VIIS);
        rules.put(ChordFunction.VIIS, tempList);

        // VII7
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.VII7);
        tempList.add(ChordFunction.VIIS);
        rules.put(ChordFunction.VII7, tempList);


        // neighbourhood
        neighbourhood = new HashMap<>();

        // I
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.S);
        neighbourhood.put(Degree.I, tempList);

        // II
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.II);
        tempList.add(ChordFunction.VIIS);
        tempList.add(ChordFunction.VII7);
        neighbourhood.put(Degree.II, tempList);

        // III
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        neighbourhood.put(Degree.III, tempList);

        // IV
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.S);
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.II);
        tempList.add(ChordFunction.VIIS);
        tempList.add(ChordFunction.VII7);
        neighbourhood.put(Degree.IV, tempList);

        // V
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.D7);
        neighbourhood.put(Degree.V, tempList);

        // VI
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.S);
        tempList.add(ChordFunction.II);
        tempList.add(ChordFunction.VII7);
        neighbourhood.put(Degree.VI, tempList);

        // VIIS
        tempList = new ArrayList<>();
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.VII7);
        tempList.add(ChordFunction.VIIS);
        neighbourhood.put(Degree.VIIS, tempList);


    }

    public static void main(String[] args) {
        new MinorHarmonization().process();
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
                Mod.transpose(phrase, 3);
                for (int j = 0; j < phrase.getSize(); j++) {
                    if (phrase.getNote(j).getPitch() != Note.REST) {
                        pitchesToHarmonize.add(new Note(phrase.getNote(j).getPitch(), phrase.getNote(j).getRhythmValue()));
                    }
                }
            }

        }

        List<ChordFunction> possibleChords = new ArrayList<>();
        possibleChords.add(ChordFunction.T);
        possibleChords.add(ChordFunction.S);
        possibleChords.add(ChordFunction.D);
        possibleChords.add(ChordFunction.VIIS);
        possibleChords.add(ChordFunction.VII7);
        possibleChords.add(ChordFunction.II);


        ChordFunction[] result = new ChordFunction[pitchesToHarmonize.size()];
        recursion(pitchesToHarmonize, possibleChords, 0, result);

        Score score = new Score();
        Part accompaniment = new Part();
        Part solo = new Part();

        for (int i = 0; i < result.length; i++) {
            System.out.print(result[i]);
            CPhrase cPhraseAccompaniment = ChordUtility.buildChordInMinor(pitchesToHarmonize.get(i), result[i]);
            accompaniment.addCPhrase(cPhraseAccompaniment);
            CPhrase cPhraseSolo = new CPhrase();
            cPhraseSolo.addPhrase(new Phrase(pitchesToHarmonize.get(i)));
            solo.addCPhrase(cPhraseSolo);
        }
        solo.setChannel(2);
        solo.setInstrument(Part.VIOLIN);
        score.add(solo);
        score.add(accompaniment);
        score.setTempo(120);
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
            return false;
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
            case 3:
                return Degree.III;
            case 5:
                return Degree.IV;
            case 7:
                return Degree.V;
            case 8:
                return Degree.VI;
            case 10:
                return Degree.VII;
            case 11:
                return Degree.VIIS;
            default:
                return Degree.I;
        }
    }
}

