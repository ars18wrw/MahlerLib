package grammar;

import jm.constants.Pitches;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.util.Read;
import jm.util.Write;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleGrammar {
    public static void main(String[] args) {
        new SimpleGrammar().process();
    }

    public void process() {
        /* Open and read MIDI file */
        FileDialog fd;
        Frame f = new Frame();
        fd = new FileDialog(f,
                "Open MIDI file or choose cancel to"
                        + " finish.",
                FileDialog.LOAD);
        fd.show();
        if (fd.getFile() == null) {
            return;
        }
        Score s = new Score();
        Read.midi(s, fd.getDirectory() + fd.getFile());
        s.setTitle(fd.getFile());

        // Create directory for result files
        new File("music\\" + fd.getFile()).mkdir();
        Write.midi(s, "music/" + fd.getFile() + "/original.mid");

        // for each part let's find rules
        for (int ii = 0; ii < 1; ii++) { // s.getSize(); ii++) {
            // TODO Change List with Set because getting in List is very expensive operation
            Map<Integer, Set<Integer>> preRules = new HashMap<>();
            Part part = s.getPart(ii);
            // for each phrase
            for (int jj = 0; jj < part.getSize(); jj++) {
                int[] pitches = part.getPhrase(jj).getPitchArray();
                int value = 0;
                for (int k = 0; k < pitches.length-1; k++) {
                    if (Pitches.REST == pitches[k]) {
                        continue;
                    }
                    value = (value % (128 * 128)) * 128 + pitches[k];
                    // the maximum left size of the rule is 3: ABC -> D
                    Set<Integer> list;
                    // first rule or not
                    if (!preRules.containsKey(pitches[k])) {
                        list = new HashSet<>();
                    } else {
                        list = preRules.get(pitches[k]);
                    }
                    list.add(value);
                    while (Pitches.REST == pitches[++k]) {
                    }
                    preRules.put(pitches[k], list);
                }
            }

            // for each pitch let's build real rules
            // TODO Make List from key, because rules can be not so definite
            Map<Integer, Integer> rules = new HashMap<>();
            Set<Integer> prefixes;
            for (int analyzedPitch : preRules.keySet()) {
                prefixes = preRules.get(analyzedPitch); // value, rules like XYZ -> analyzedPitch
                boolean isGood = true;
                Integer[] firstLetters = getFirstLetters(prefixes);
                Integer[] secondLetters = getSecondLetters(prefixes);
                Integer[] thirdLetters = getThirdLetters(prefixes);

                // add one left letter rules
                for (int i = 1; i < prefixes.size(); i++) {
                    if (firstLetters[i] != firstLetters[0]) {
                        isGood = false;
                        break; // this one-letter-rule is bad
                    }
                }
                if (isGood) {
                    rules.put(firstLetters[0], analyzedPitch);
                    continue; // all rules for this pitch are the same and "one lettered", go to next analyzedPitch
                }

                Integer[] prefixesArr = new Integer[prefixes.size()];
                prefixesArr = prefixes.toArray(prefixesArr);

                // add two left letters rules
                for (int i = 0; i < prefixesArr.length; i++) {
                    isGood = true;
                    for (int j = i + 1; j < prefixesArr.length; j++) {
                        if (secondLetters[i] != secondLetters[j]) {
                            isGood = false;
                            break; // this two-letter-rule is not single
                        }
                    }
                    if (isGood) {
                        rules.put(prefixesArr[i] % 128 * 128, analyzedPitch);
                    }
                }

                // add three left letters rules
                for (int i = 0; i < prefixesArr.length; i++) {
                    isGood = true;
                    for (int j = i + 1; j < prefixesArr.length; j++) {
                        if (thirdLetters[i] != thirdLetters[j]) {
                            isGood = false;
                            break; // this two-letter-rule is not single
                        }
                    }
                    if (isGood) {
                        rules.put(prefixesArr[i], analyzedPitch);
                    }
                }
            }
            // we should add "begin"- and "end"- rules
            System.out.println("d");
            // here we have rules. Let's build music!


        }


    }

    protected Integer[] getFirstLetters(Set<Integer> set) {
        Integer[] resultArray = new Integer[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] %= 128;
        }
        return resultArray;
    }

    protected Integer[] getSecondLetters(Set<Integer> set) {
        Integer[] resultArray = new Integer[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = (resultArray[i] % (128 * 128)) / 128;
        }
        return resultArray;

    }

    protected Integer[] getThirdLetters(Set<Integer> set) {
        Integer[] resultArray = new Integer[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] /= 128 * 128;
        }
        return resultArray;
    }

}
