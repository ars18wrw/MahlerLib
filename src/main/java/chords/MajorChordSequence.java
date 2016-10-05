package chords;

import jm.constants.Durations;
import jm.constants.Pitches;
import jm.constants.ProgramChanges;
import jm.constants.RhythmValues;
import jm.music.data.*;
import jm.util.Write;

import java.util.*;

/**
 * Created by Уладзімір Асіпчук on 08.11.15.
 */
public class MajorChordSequence {
    protected Set<Integer> tonicPitches;
    protected Set<Integer> subdominantPitches;
    protected Set<Integer> dominantPitches;
    protected Set<Integer> sixPitches;
    protected Set<Integer> dominant7Pitches;

    protected static final int NUMBER_OF_CHORDS = 30;

    protected Map<ChordFunction, LinkedList<ChordFunction>> rules;

    protected Score s = new Score();

    MajorChordSequence() {
        // root chord pitches
        tonicPitches = new TreeSet<Integer>();
        subdominantPitches = new TreeSet<Integer>();
        dominantPitches = new TreeSet<Integer>();
        sixPitches = new TreeSet<Integer>();
        dominant7Pitches = new TreeSet<Integer>();

        // by T53
        addPitches(tonicPitches, 0);
        addPitches(tonicPitches, 4);
        addPitches(tonicPitches, 4 + 3);
        // by S64
        addPitches(subdominantPitches, 0);
        addPitches(subdominantPitches, 5);
        addPitches(subdominantPitches, 5 + 4);
        // by D64
        addPitches(dominantPitches, 2);
        addPitches(dominantPitches, 2 + 5);
        addPitches(dominantPitches, 2 + 5 + 4);
//        // by VI6
        addPitches(sixPitches, 0);
        addPitches(sixPitches, 4);
        addPitches(sixPitches, 4 + 5);
        // by D43
        addPitches(dominant7Pitches, 2);
        addPitches(dominant7Pitches, 2 + 3);
        addPitches(dominant7Pitches, 2 + 5);
        addPitches(dominant7Pitches, 2 + 5 + 4);


        // rules
        rules = new HashMap<>();
        LinkedList<ChordFunction> tempList;
        // T
        tempList = new LinkedList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.S);
//        tempList.add(ChordFunction.VI);
        tempList.add(ChordFunction.D);
        rules.put(ChordFunction.T, tempList);

        // S
        tempList = new LinkedList<>();
        tempList.add(ChordFunction.S);
        tempList.add(ChordFunction.D);
        tempList.add(ChordFunction.T);
        rules.put(ChordFunction.S, tempList);

        // D
        // !!! D -> S is forbidden
        tempList = new LinkedList<>();
        tempList.add(ChordFunction.D7);
        tempList.add(ChordFunction.D);
//        tempList.add(ChordFunction.VI);
        rules.put(ChordFunction.D, tempList);

        // D7
        // !!! D -> S is forbidden
        tempList = new LinkedList<>();
        tempList.add(ChordFunction.T);
        tempList.add(ChordFunction.D7);
//        tempList.add(ChordFunction.VI);
        rules.put(ChordFunction.D7, tempList);

//        // VI
//        tempList = new LinkedList<>();
//        tempList.add(ChordFunction.S);
//        tempList.add(ChordFunction.VI);
//        tempList.add(ChordFunction.T);
//        rules.put(ChordFunction.VI, tempList);
    }

    public static void main(String[] args) {
        new MajorChordSequence().process();
    }

    public void process() {
        Note root = new Note(Pitches.C4, RhythmValues.EIGHTH_NOTE);
        Part part = new Part();
        Part basso = new Part();
        Part soprano = new Part();
        int chordsNum = 0;
        ChordFunction function = ChordFunction.T;
        Boolean isUp = null;
        System.out.print(function);
        Random randomGenerator = new Random();
        boolean isTheEndOnTheTonic = false;
        ChordFunction thePreviousFunction = null;
        ChordFunction thePreviousPreviousFunction = null;
        Boolean wasPreviousUp = null;
        do {
            System.out.println(root);
            if (root.getRhythmValue() == 1) {
                root.setRhythmValue(0.5);
            }
            // we temporary use only Dominant Seventh
            if (function != ChordFunction.D7) {
                if (thePreviousFunction != null && function == ChordFunction.T) {
                    root.setRhythmValue(root.getRhythmValue()*2);
                }
                CPhrase cPhrase = ChordUtility.buildChordInMajor(root, function);
                part.addCPhrase(cPhrase);
                basso.addCPhrase(getTheBasso(cPhrase.copy()));
                soprano.addCPhrase(getTheSoprano(cPhrase.copy()));
            } else {
                CPhrase cPhrase = ChordUtility.build7InMajor(root, function);
                part.addCPhrase(cPhrase);
                basso.addCPhrase(getTheBasso(cPhrase.copy()));
                soprano.addCPhrase(getTheSoprano(cPhrase.copy()));
            }

            if (null == thePreviousFunction) {
                thePreviousFunction = function;
            } else {
                thePreviousPreviousFunction = thePreviousFunction;
                thePreviousFunction = function;
            }
            LinkedList<ChordFunction> queue = rules.get(function);
            // next chord function (the harmony of the next chord)
            function = getNextChordFunction(queue, randomGenerator);
            System.out.print(function);


            if (null != isUp) {
                wasPreviousUp = isUp;
            }
            // whether the next chord should be higher or not
            isUp = randomGenerator.nextBoolean();
            // do not go very high
            if (isUp && root.getPitch() > Pitches.C5) {
                isUp = false;
            }
            // do not go very high
            if (!isUp && root.getPitch() < Pitches.B3) {
                isUp = true;
            }
            // Avoid duplication
            if (null != thePreviousPreviousFunction){
                if  (isUp != wasPreviousUp.booleanValue() && thePreviousPreviousFunction == function){
                    isUp = wasPreviousUp.booleanValue();
                }
            }
            switch (function) {
                case T:
                    root = new Note(ChordUtility.getClosestPitchOfChord(tonicPitches, root.getPitch(), isUp), root.getRhythmValue());
                    if (chordsNum >= NUMBER_OF_CHORDS) {
                        isTheEndOnTheTonic = true;
                        root.setRhythmValue(root.getRhythmValue()*4);
                    }
                    break;
                case S:
                    root = new Note(ChordUtility.getClosestPitchOfChord(subdominantPitches, root.getPitch(), isUp), root.getRhythmValue());
                    break;
                case D:
                    root = new Note(ChordUtility.getClosestPitchOfChord(dominantPitches, root.getPitch(), isUp), root.getRhythmValue());
                    break;
                case VI:
                    root = new Note(ChordUtility.getClosestPitchOfChord(sixPitches, root.getPitch(), isUp), root.getRhythmValue());
                    break;
                case D7:
                    root = new Note(ChordUtility.getClosestPitchOfChord(dominant7Pitches, root.getPitch(), isUp), root.getRhythmValue());
                    break;
            }
            chordsNum++;
        } while (!isTheEndOnTheTonic);
        CPhrase cPhrase = ChordUtility.buildChordInMajor(root, function);
        part.addCPhrase(cPhrase.copy());
        basso.addCPhrase(getTheBasso(cPhrase));
        soprano.addCPhrase(getTheSoprano(cPhrase.copy()));
        basso.setChannel(2);
        basso.setInstrument(ProgramChanges.VIOLIN);
        soprano.setChannel(3);
        soprano.setInstrument(ProgramChanges.VIOLIN);
        s.add(soprano);
        s.add(basso);
        s.add(part);

        Write.midi(s, "MajorChordSequence.mid");
    }


    public static CPhrase getAccompaniment(CPhrase chord) {
        Vector phraseList = chord.getPhraseList();
        int[] pitches = new int[phraseList.size() - 1];
        for (int i = 0; i < phraseList.size() - 1; i++) {
            pitches[i] = ((Phrase) phraseList.get(i)).getNote(0).getPitch();
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, Durations.Q);
        return result;
    }

    public static CPhrase getTheBasso(CPhrase chord) {
        Note basso = ((Phrase)chord.getPhraseList().get(0)).getNote(0);
        CPhrase result = new CPhrase();
        result.addChord(new Note[]{basso});
        return result;
    }

    public static CPhrase getTheSoprano(CPhrase chord) {
        Note soprano = ((Phrase)chord.getPhraseList().get(chord.getPhraseList().size()-1)).getNote(0);
        CPhrase result = new CPhrase();
        result.addChord(new Note[]{soprano});
        return result;
    }


    public ChordFunction getNextChordFunction(LinkedList<ChordFunction> queue, Random randomGenerator) {
        int rouletteWheelSize = queue.size() * (queue.size()+1) / 2;
        int randomInt = randomGenerator.nextInt(rouletteWheelSize);
        int sum = 0;
        int nextChordNumber = 1;
        while ((sum +=nextChordNumber) < randomInt) {
            nextChordNumber++;
        }
        ChordFunction result = queue.get(nextChordNumber-1);
        queue.remove(result);
        queue.add(result);
        return result;
    }

    public static void addPitches(Set<Integer> set, int root) {
        while (root < 128) {
            set.add(root);
            root += 12;
        }
    }
}
