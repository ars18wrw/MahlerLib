package melody2harmony;

import chords.ChordFunction;
import chords.SeventhChordType;
import chords.TriadType;
import jm.constants.Pitches;
import jm.music.data.CPhrase;
import jm.music.data.Note;
import jm.music.data.Phrase;
import jm.music.data.Rest;

import java.util.Set;

/**
 * Created by Уладзімір Асіпчук on 15.05.16.
 */
public class ChordUtility {
    protected static final int TONIC_DEC_ROOT = Pitches.CF4;
    protected static final int TONIC_ROOT = Pitches.C4;
    protected static final int FIRST_ROOT = Pitches.C4;
    protected static final int TONIC_INC_ROOT = Pitches.CS4;
    protected static final int SUPERTONIC_DEC_ROOT = Pitches.DF4;
    protected static final int SUPERTONIC_ROOT = Pitches.D4;
    protected static final int SECOND_ROOT = Pitches.D4;
    protected static final int SUPERTONIC_INC_ROOT = Pitches.DS4;
    protected static final int MEDIANT_DEC_ROOT = Pitches.EF4;
    protected static final int MEDIANT_ROOT = Pitches.E4;
    protected static final int THIRD_ROOT = Pitches.E4;
    protected static final int MEDIANT_INC_ROOT = Pitches.ES4;
    protected static final int SUBDOMINANT_DEC_ROOT = Pitches.FF4;
    protected static final int SUBDOMINANT_ROOT = Pitches.F4;
    protected static final int FOURTH_ROOT = Pitches.F4;
    protected static final int SUBDOMINANT_INC_ROOT = Pitches.FS4;
    protected static final int DOMINANT_DEC_ROOT = Pitches.GF4;
    protected static final int DOMINANT_ROOT = Pitches.G4;
    protected static final int FIFTH_ROOT = Pitches.G4;
    protected static final int DOMINANT_INC_ROOT = Pitches.GS4;
    protected static final int SUBMEDIANT_DEC_ROOT = Pitches.AF4;
    protected static final int SUBMEDIANT_ROOT = Pitches.A4;
    protected static final int SIXTH_ROOT = Pitches.A4;
    protected static final int SUBMEDIANT_INC_ROOT = Pitches.AF4;
    protected static final int LEADING_TONE_DEC_ROOT = Pitches.BS4;
    protected static final int SUBTONIC_DEC_ROOT = Pitches.BS4;
    protected static final int LEADING_TONE_ROOT = Pitches.B4;
    protected static final int SUBTONIC_ROOT = Pitches.B4;
    protected static final int SEVENTH_ROOT = Pitches.B4;
    protected static final int LEADING_TONE_INC_ROOT = Pitches.BS4;
    protected static final int SUBTONIC_INC_ROOT = Pitches.BS4;

    public static int getClosestPitchOfChord(Set<Integer> chordPitches, int pitch, boolean isUp) {
        int resultPitch = 0;
        int distance = Integer.MAX_VALUE;
        for (int elem : chordPitches) {
            if (Math.abs(pitch - elem) < distance && pitch < elem) {
                if (isUp) {
                    distance = Math.abs(pitch - elem);
                    resultPitch = elem;
                }
            }
            if (Math.abs(pitch - elem) < distance && pitch > elem) {
                if (!isUp) {
                    distance = Math.abs(pitch - elem);
                    resultPitch = elem;
                }
            }
        }
        return resultPitch;
    }

    public static CPhrase buildChordInMajor(Note rootNote, ChordFunction type) {
        switch (type) {
            case T:
                return buildTriad(rootNote, TONIC_ROOT, TriadType.MAJOR);
//            case IS:
//            case IIF:
//                return buildTriad(rootNote, TONIC_INC_ROOT, ChordType.DIMINISHED);
            case II:
                return buildTriad(rootNote, SECOND_ROOT, TriadType.MINOR);
//            case IIS:
//            case IIIF:
//                return buildTriad(rootNote, SUBTONIC_INC_ROOT);
            case III:
                return buildTriad(rootNote, THIRD_ROOT, TriadType.MINOR);
//            case IIIS:
//            case IVF:
//                return buildTriad(rootNote, MEDIANT_INC_ROOT);
            case S:
                return buildTriad(rootNote, SUBDOMINANT_ROOT, TriadType.MAJOR);
//            case IVS:
//            case VF:
//                return buildTriad(rootNote, SUBDOMINANT_INC_ROOT);
            case D:
                return buildTriad(rootNote, DOMINANT_ROOT, TriadType.MAJOR);
//            case VS:
//            case VIF:
//                return buildTriad(rootNote, DOMINANT_INC_ROOT);
            case VI:
                return buildTriad(rootNote, SIXTH_ROOT, TriadType.MINOR);
//            case VIS:
//            case VIIF:
//                return buildTriad(rootNote, SUBMEDIANT_INC_ROOT);
            case VII:
                return buildTriad(rootNote, SEVENTH_ROOT, TriadType.DIMINISHED);
//            case VIIS:
//            case IF:
//                return buildTriad(rootNote, LEADING_TONE_INC_ROOT);
            case D7:
                return build7InMajor(rootNote, type);
            case REST:
                return buildRest(rootNote.getRhythmValue());
            default:
                return null;
        }
    }

    public static CPhrase buildChordInMinor(Note rootNote, ChordFunction type) {
        switch (type) {
            case T:
                return buildTriad(rootNote, TONIC_ROOT, TriadType.MINOR);
//            case IS:
//            case IIF:
//                return buildTriad(rootNote, TONIC_INC_ROOT, ChordType.DIMINISHED);
            case II:
                return buildTriad(rootNote, SECOND_ROOT, TriadType.AUGMENTED);
//            case IIS:
            case IIIF:
                return buildTriad(rootNote, MEDIANT_DEC_ROOT, TriadType.MAJOR);
//            case III:
//                return buildTriad(rootNote, THIRD_ROOT, TriadType.MINOR);
//            case IIIS:
//            case IVF:
//                return buildTriad(rootNote, MEDIANT_INC_ROOT);
            case S:
                return buildTriad(rootNote, SUBDOMINANT_ROOT, TriadType.MINOR);
//            case IVS:
//            case VF:
//                return buildTriad(rootNote, SUBDOMINANT_INC_ROOT);
            case D:
                return buildTriad(rootNote, DOMINANT_ROOT, TriadType.MAJOR);
//            case VS:
            case VIF:
                return buildTriad(rootNote, SUBMEDIANT_DEC_ROOT, TriadType.MAJOR);
//            case VI:
//                return buildTriad(rootNote, SIXTH_ROOT, TriadType.MINOR);
//            case VIS:
            case VIIF:
                return buildTriad(rootNote, SUBMEDIANT_INC_ROOT, TriadType.MAJOR);
            case VII:
                return buildTriad(rootNote, SEVENTH_ROOT, TriadType.DIMINISHED);
//            case VIIS:
//            case IF:
//                return buildTriad(rootNote, LEADING_TONE_INC_ROOT);
            case D7:
            case VII7:
                return build7InMinor(rootNote, type);
            case REST:
                return buildRest(rootNote.getRhythmValue());
            default:
                return null;
        }
    }


    public static CPhrase buildTriad(Note rootNote, int chordRoot, TriadType type) {
        int pitch = rootNote.getPitch();
        while (pitch < chordRoot) {
            pitch +=12;
        }
        int distance = pitch - chordRoot;
        switch (distance % 12) {
            case 0:
                return build53(rootNote, type);
            // minor 3
            case 3:
            // major 3
            case 4:
                return build6(rootNote, type);
            case 5:
            case 6:
            case 7:
            case 8:
                return build64(rootNote, type);
            default:
                // TODO throw Exception
                return null;
        }
    }

    public static CPhrase build7InMajor(Note rootNote, ChordFunction type) {
        switch (type) {
            case D7:
                return build7Chord(rootNote, DOMINANT_ROOT, SeventhChordType.DOMINANT);
            default:
                // TODO throw Exception
                return null;
        }
    }

    public static CPhrase build7InMinor(Note rootNote, ChordFunction type) {
        switch (type) {
            case D7:
                return build7Chord(rootNote, DOMINANT_ROOT, SeventhChordType.DOMINANT);
            case VII7:
                return build7Chord(rootNote, LEADING_TONE_ROOT, SeventhChordType.DIMINISHED);
            default:
                // TODO throw Exception
                return null;
        }
    }



    public static CPhrase build7Chord(Note rootNote, int chordRoot, SeventhChordType type) {
        int pitch = rootNote.getPitch();
        while (pitch < chordRoot) {
            pitch +=12;
        }
        int distance = pitch - chordRoot;
        switch (distance % 12) {
            case 0:
                return build7(rootNote, type);
            case 3:
            case 4:
                return build65(rootNote, type);
            case 5:
            case 6:
            case 7:
                return build43(rootNote, type);
            case 9:
            case 10:
            case 11:
                return build2(rootNote, type);
            default:
                return null;
        }
    }

    private static CPhrase build53(Note rootNote, TriadType type) {
        int[] pitches = new int[3];
        pitches[0] = rootNote.getPitch();
        if (TriadType.MAJOR == type) {
            pitches[1] = rootNote.getPitch() + 4;
            pitches[2] = rootNote.getPitch() + 4 + 3;
        } else if (TriadType.MINOR == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 4;
        } else if (TriadType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 3;
        } else if (TriadType.AUGMENTED == type) {
            pitches[1] = rootNote.getPitch() + 4;
            pitches[2] = rootNote.getPitch() + 4 + 4;
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(53);
        return result;
    }

    private static CPhrase build6(Note rootNote, TriadType type) {
        int[] pitches = new int[3];
        pitches[0] = rootNote.getPitch();
        if (TriadType.MAJOR == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 5;
        } else if (TriadType.MINOR == type) {
            pitches[1] = rootNote.getPitch() + 4;
            pitches[2] = rootNote.getPitch() + 4 + 5;
        } else if (TriadType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 6;
        } else if (TriadType.AUGMENTED == type) {
            pitches[1] = rootNote.getPitch() + 4;
            pitches[2] = rootNote.getPitch() + 4 + 4;
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(6);
        return result;
    }

    private static CPhrase build64(Note rootNote, TriadType type) {
        int[] pitches = new int[3];
        pitches[0] = rootNote.getPitch();
        if (TriadType.MAJOR == type) {
            pitches[1] = rootNote.getPitch() + 5;
            pitches[2] = rootNote.getPitch() + 5 + 4;
        } else if (TriadType.MINOR == type) {
            pitches[1] = rootNote.getPitch() + 5;
            pitches[2] = rootNote.getPitch() + 5 + 3;
        } else if (TriadType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 6;
            pitches[2] = rootNote.getPitch() + 6 + 3;
        } else if (TriadType.AUGMENTED == type) {
            pitches[1] = rootNote.getPitch() + 4;
            pitches[2] = rootNote.getPitch() + 4 + 4;
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(64);
        return result;
    }
    private static CPhrase build7(Note rootNote, SeventhChordType type) {
        int[] pitches = new int[4];
        pitches[0] = rootNote.getPitch();
        if (SeventhChordType.DOMINANT == type) {
            pitches[1] = rootNote.getPitch() + 4;
            pitches[2] = rootNote.getPitch() + 4 + 3;
            pitches[3] = rootNote.getPitch() + 4 + 3 + 3;
        } else if (SeventhChordType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 3;
            pitches[3] = rootNote.getPitch() + 3 + 3 + 3;
        }
        else {
            // TODO Implement
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(7);
        return result;
    }

    private static CPhrase build65(Note rootNote, SeventhChordType type) {
        int[] pitches = new int[4];
        pitches[0] = rootNote.getPitch();
        if (SeventhChordType.DOMINANT == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 3;
            pitches[3] = rootNote.getPitch() + 3 + 3 + 2;
        } else if (SeventhChordType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 3;
            pitches[3] = rootNote.getPitch() + 3 + 3 + 3;
        }
        else {
            // TODO Implement
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(65);
        return result;
    }

    private static CPhrase build43(Note rootNote, SeventhChordType type) {
        int[] pitches = new int[4];
        pitches[0] = rootNote.getPitch();
        if (SeventhChordType.DOMINANT == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 2;
            pitches[3] = rootNote.getPitch() + 3 + 2 + 4;
        } else if (SeventhChordType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 3;
            pitches[3] = rootNote.getPitch() + 3 + 3 + 3;
        }
        else {
            // TODO Implement
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(43);
        return result;
    }

    private static CPhrase build2(Note rootNote, SeventhChordType type) {
        int[] pitches = new int[4];
        pitches[0] = rootNote.getPitch();
        if (SeventhChordType.DOMINANT == type) {
            pitches[1] = rootNote.getPitch() + 2;
            pitches[2] = rootNote.getPitch() + 2 + 4;
            pitches[3] = rootNote.getPitch() + 2 + 4 + 3;
        } else if (SeventhChordType.DIMINISHED == type) {
            pitches[1] = rootNote.getPitch() + 3;
            pitches[2] = rootNote.getPitch() + 3 + 3;
            pitches[3] = rootNote.getPitch() + 3 + 3 + 3;
        }
        else {
            // TODO Implement
        }
        CPhrase result = new CPhrase();
        result.addChord(pitches, rootNote.getRhythmValue());
        System.out.println(2);
        return result;
    }

    private static CPhrase buildRest(double rhytmValue) {
        CPhrase result = new CPhrase();
        Phrase phrase = new Phrase();
        phrase.addRest(new Rest(rhytmValue));
        result.addPhrase(phrase);
        System.out.println("rest");
        return result;
    }
}
