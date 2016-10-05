package grammar;

import jm.constants.Durations;
import jm.constants.Pitches;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.Read;
import jm.util.Write;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Grammar {
    private static boolean DEBUG = false;

    private static final String FOLDER = "originals\\";
    private static final String NAME = "fantaisie";
    private static final String EXT = ".mid";

    private static final String PRETEXT = "==========================================================";

    private final String className;

    private Score s = new Score();

    public Grammar() {
        className = getClass().getName().toString();
    }


    public static void main(String[] args) {
        new Grammar().process();
    }

    public void process() {
        /* Read midi */
        Read.midi(s, FOLDER + NAME + EXT);
        s.setTitle(NAME);
        /* Create folders */
        new File("music/" + className).mkdir();
        new File("music/" + className + "/" + NAME).mkdir();
        Write.midi(s, "music/" + className + "/" + NAME + "/original.mid");
        if (DEBUG) {
            System.out.println(PRETEXT);
            for (int i = 0; i < s.size(); i++) {
                for (int j = 0; j < s.getPart(i).size(); j++) {
                    for (int k = 0; k < s.getPart(i).getPhrase(j).size(); k++) {
                        if (Pitches.REST != s.getPart(i).getPhrase(j).getNote(k).getPitch()) {
                            System.out.print(s.getPart(i).getPhrase(j).getNote(k).getPitch() + " ");
                        }
                    }
                }
            }
            System.out.println(PRETEXT);
        }
        // for each part let's find rules
        Map<Long, Set<Integer>> rules = getRules();
        showRules(rules);

//        for (long key : rules.keySet()) {
//            System.out.println(key + "->" + rules.get(key));
//        }

        // Let's compose the music
        Phrase resultPhrase = new Phrase();
        // TODO Replace this "bad cases" solver
        for (int i = 0; i < 5; i++) {
            resultPhrase.add(new Note(s.getPart(0).getPhrase(0).getNote(i).getPitch(), Durations.Q));
        }
        int i = 5;
        while (i < 1000) {
            long key;
            // 5 letters
            key = (long) 128 * 128 * 128 * 128 * resultPhrase.getNote(i - 5).getPitch() +
                    (long) 128 * 128 * 128 * resultPhrase.getNote(i - 4).getPitch() +
                    (long) 128 * 128 * resultPhrase.getNote(i - 3).getPitch() +
                    (long) 128 * resultPhrase.getNote(i - 2).getPitch() +
                    (long) resultPhrase.getNote(i - 1).getPitch();
            if (rules.containsKey(key)) {
                resultPhrase.add(new Note(getRandFromSet(rules.get(key)), Durations.Q));
                i++;
                continue;
            }
            key = key % ((long) 128 * 128 * 128 * 128);
            if (rules.containsKey(key)) {
                resultPhrase.add(new Note(getRandFromSet(rules.get(key)), Durations.Q));
                i++;
                continue;
            }
            key = key % ((long) 128 * 128 * 128);
            if (rules.containsKey(key)) {
                resultPhrase.add(new Note(getRandFromSet(rules.get(key)), Durations.Q));
                i++;
                continue;
            }
            key = key % ((long) 128 * 128);
            if (rules.containsKey(key)) {
                resultPhrase.add(new Note(getRandFromSet(rules.get(key)), Durations.Q));
                i++;
                continue;
            }
            key = key % 128;
            if (rules.containsKey(key)) {
                resultPhrase.add(new Note(getRandFromSet(rules.get(key)), Durations.Q));
                i++;
                continue;
            }
            Score resultScore = new Score();
            Part resultPart = new Part();
            resultPart.add(resultPhrase);
            resultScore.add(resultPart);
            resultScore.setTempo(160);
            Write.midi(resultScore, "music/" + className + "/" + NAME + "/compose.mid");
            System.exit(-100);
        }
        Score resultScore = new Score();
        Part resultPart = new Part();
        resultPart.add(resultPhrase);
        resultScore.add(resultPart);
        resultScore.setTempo(160);
        Write.midi(resultScore, "music/" + className + "/" + NAME + "/compose.mid");

    }


    // coded pitch sequence -> {pitch}
    protected Map<Long, Set<Integer>> getRules() {
        Map<Long, Set<Integer>> realRules = new HashMap<>();
        for (int ii = 0; ii < s.size(); ii++) {
            // TODO Change List with Set because getting in List is very expensive operation
            Map<Long, Set<Long>> preRules = new HashMap<>();
            Part part = s.getPart(ii);
            // for each phrase
            for (int jj = 0; jj < part.getSize(); jj++) {
                int[] pitchesWithRests = part.getPhrase(jj).getPitchArray();
                long[] pitches = getNormalPitchArray(pitchesWithRests);
                long value = 0;
                /** Rules are like this: a->(bcd, e),
                 * while in a score this fact
                 * is rendered as ...bcdae...
                 * In this example:
                 * a - the current pitch
                 * bcd - previous pithes
                 * e - the result pitch*/
                for (int k = 0; k < pitches.length - 1; k++) {
                    if (k == 0) {
                        value = pitches[k + 1];
                    } else {
                        // Subtract the previous result pitch
                        value -= pitches[k];
                        // Drop the oldest pitch of "previous pitches"
                        value = (value % ((long) 128 * 128 * 128 * 128 * 128)) * 128;
                        // Add the first letter
                        value += 128 * pitches[k - 1];
                        // Add the result pitch
                        value += pitches[k + 1];
                    }
                    if (DEBUG) {
                        Set<Long> valueSet = new HashSet<>();
                        valueSet.add(value);
                        System.out.print(getFifthLetters(valueSet)[0]);
                        System.out.print(" ");
                        System.out.print(getFourthLetters(valueSet)[0]);
                        System.out.print(" ");
                        System.out.print(getThirdLetters(valueSet)[0]);
                        System.out.print(" ");
                        System.out.print(getSecondLetters(valueSet)[0]);
                        System.out.print(" ");
                        System.out.print(getFirstLetters(valueSet)[0]);
                        System.out.print(" ");
                        System.out.print(pitches[k]);
                        System.out.print("->");
                        System.out.println(getResultLetters(valueSet)[0]);
                    }

                    Set<Long> list;
                    // first rule or not
                    if (!preRules.containsKey(pitches[k])) {
                        list = new HashSet<>();
                    } else {
                        list = preRules.get(pitches[k]);
                    }
                    list.add(value);
                    preRules.put(pitches[k], list);
                }
            }

            Map<Long, Long> rules = new HashMap<>();

            Map<Long, Set<Long>> nextStepRules = new HashMap<>();
            // for each pitch let's build One Letter rules
            Set<Long> prefixes;
            for (long analyzedPitch : preRules.keySet()) {
                prefixes = preRules.get(analyzedPitch); // value, rules like XYZ -> analyzedPitch
                boolean isGood = true;
                Long[] firstLetters = getFirstLetters(prefixes);
                Long[] secondLetters = getSecondLetters(prefixes);
                Long[] thirdLetters = getThirdLetters(prefixes);
                Long[] fourthLetters = getFourthLetters(prefixes);
                Long[] fifthLetters = getFifthLetters(prefixes);
                Long[] resultLetters = getResultLetters(prefixes);
                for (int i = 0; i < prefixes.size(); i++) {
                    for (int jj = 0; jj < prefixes.size(); jj++) {
                        if (i != jj) {
                            // there is no one letter rule
                            if (0 != resultLetters[i].compareTo(resultLetters[jj])) {
                                long key = analyzedPitch + firstLetters[i] * 128;
                                long value = secondLetters[i] * 128 * 128 + thirdLetters[i] * 128 * 128 * 128 +
                                        (long) 128 * 128 * 128 * 128 * fourthLetters[i]
                                        + (long) 128 * 128 * 128 * 128 * 128 * fifthLetters[i] + resultLetters[i];
                                Set<Long> list;
                                // first rule or not
                                if (!nextStepRules.containsKey(key)) {
                                    list = new HashSet<>();
                                } else {
                                    list = nextStepRules.get(key);
                                }
                                list.add(value);
                                nextStepRules.put(key, list);
                                isGood = false;
                            }
                        }
                    }
                    if (isGood) {
                        rules.put(analyzedPitch, resultLetters[0]);
                    }
                    isGood = true;
                }
            }

            preRules = nextStepRules;
            nextStepRules = new HashMap<>();
            // for each pitch let's build Two Letter rules
            for (long analyzedPitch : preRules.keySet()) {
                prefixes = preRules.get(analyzedPitch); // value, rules like XYZ -> analyzedPitch
                boolean isGood = true;
                Long[] firstLetters = getFirstLetters(prefixes);
                Long[] secondLetters = getSecondLetters(prefixes);
                Long[] thirdLetters = getThirdLetters(prefixes);
                Long[] fourthLetters = getSecondLetters(prefixes);
                Long[] fifthLetters = getThirdLetters(prefixes);
                Long[] resultLetters = getResultLetters(prefixes);
                for (int i = 0; i < prefixes.size(); i++) {
                    for (int jj = 0; jj < prefixes.size(); jj++) {
                        if (i != jj) {
                            // there is no two letter rule
                            if (0 != (resultLetters[i].compareTo(resultLetters[jj]))) {
                                long key = analyzedPitch + secondLetters[i] * 128 * 128;
                                long value = thirdLetters[i] * 128 * 128 * 128 +
                                        (long) 128 * 128 * 128 * 128 * fourthLetters[i]
                                        + (long) 128 * 128 * 128 * 128 * 128 * fifthLetters[i] + resultLetters[i];
                                Set<Long> list;
                                // first rule or not
                                if (!nextStepRules.containsKey(key)) {
                                    list = new HashSet<>();
                                } else {
                                    list = nextStepRules.get(key);
                                }
                                list.add(value);
                                nextStepRules.put(key, list);
                                isGood = false;
                            }
                        }
                    }
                    // there is one lette rule
                    if (isGood) {
                        rules.put(analyzedPitch, resultLetters[0]);
                    }
                    isGood = true;
                }
            }


            preRules = nextStepRules;
            nextStepRules = new HashMap<>();
            // for each pitch let's build Two Letter rules
            for (long analyzedPitch : preRules.keySet()) {
                prefixes = preRules.get(analyzedPitch); // value, rules like XYZ -> analyzedPitch
                boolean isGood = true;
                Long[] firstLetters = getFirstLetters(prefixes);
                Long[] secondLetters = getSecondLetters(prefixes);
                Long[] thirdLetters = getThirdLetters(prefixes);
                Long[] fourthLetters = getSecondLetters(prefixes);
                Long[] fifthLetters = getThirdLetters(prefixes);

                Long[] resultLetters = getResultLetters(prefixes);
                for (int i = 0; i < prefixes.size(); i++) {
                    for (int jj = 0; jj < prefixes.size(); jj++) {
                        if (i != jj) {
                            // there is no two letter rule
                            if (0 != resultLetters[i].compareTo(resultLetters[jj])) {
                                long key = (long) analyzedPitch + thirdLetters[i] * 128 * 128 * 128;
                                long value = (long) 128 * 128 * 128 * 128 * fourthLetters[i]
                                        + (long) 128 * 128 * 128 * 128 * 128 * fifthLetters[i] + resultLetters[i];
                                Set<Long> list;
                                // first rule or not
                                if (!nextStepRules.containsKey(key)) {
                                    list = new HashSet<>();
                                } else {
                                    list = nextStepRules.get(key);
                                }
                                list.add(value);
                                nextStepRules.put(key, list);
                                isGood = false;
                                break;
                            }
                        }
                    }
                    // there is one lette rule
                    if (isGood) {
                        rules.put(analyzedPitch, resultLetters[0]);
                    }
                    isGood = true;
                }
            }

            preRules = nextStepRules;
            nextStepRules = new HashMap<>();
            // for each pitch let's build Two Letter rules
            for (long analyzedPitch : preRules.keySet()) {
                prefixes = preRules.get(analyzedPitch); // value, rules like XYZ -> analyzedPitch
                boolean isGood = true;
                Long[] firstLetters = getFirstLetters(prefixes);
                Long[] secondLetters = getSecondLetters(prefixes);
                Long[] thirdLetters = getThirdLetters(prefixes);
                Long[] fourthLetters = getSecondLetters(prefixes);
                Long[] fifthLetters = getThirdLetters(prefixes);

                Long[] resultLetters = getResultLetters(prefixes);
                for (int i = 0; i < prefixes.size(); i++) {
                    for (int jj = 0; jj < prefixes.size(); jj++) {
                        if (i != jj) {
                            // there is no two letter rule
                            if (0 != resultLetters[i].compareTo(resultLetters[jj])) {
                                long key = (long) analyzedPitch + fourthLetters[i] * 128 * 128 * 128 * 128;
                                long value = (long) 128 * 128 * 128 * 128 * 128 * fifthLetters[i] + resultLetters[i];
                                Set<Long> list;
                                // first rule or not
                                if (!nextStepRules.containsKey(key)) {
                                    list = new HashSet<>();
                                } else {
                                    list = nextStepRules.get(key);
                                }
                                list.add(value);
                                nextStepRules.put(key, list);
                                isGood = false;
                                break;
                            }
                        }
                    }
                    // there is one lette rule
                    if (isGood) {
                        rules.put(analyzedPitch, resultLetters[0]);
                    }
                    isGood = true;

                }
            }

            preRules = nextStepRules;
            nextStepRules = new HashMap<>();
            // for each pitch let's build Two Letter rules
            for (long analyzedPitch : preRules.keySet()) {
                prefixes = preRules.get(analyzedPitch); // value, rules like XYZ -> analyzedPitch
                boolean isGood = true;
                Long[] firstLetters = getFirstLetters(prefixes);
                Long[] secondLetters = getSecondLetters(prefixes);
                Long[] thirdLetters = getThirdLetters(prefixes);
                Long[] fourthLetters = getSecondLetters(prefixes);
                Long[] fifthLetters = getThirdLetters(prefixes);

                Long[] resultLetters = getResultLetters(prefixes);
                for (int i = 0; i < prefixes.size(); i++) {
                    for (int jj = 0; jj < prefixes.size(); jj++) {
                        if (i != jj) {
                            if (0 != resultLetters[i].compareTo(resultLetters[jj])) {
                                isGood = false;
                                // TODO Make this Long/Integer relations clearer
                                Set<Integer> tempSet = new HashSet<>();
                                for (long tempValue : prefixes) {
                                    tempSet.add(Math.toIntExact(tempValue));
                                }
                                realRules.put(analyzedPitch, tempSet);
                            }

                        }
                    }
                    // there is one lette rule
                    if (isGood) {
                        rules.put(analyzedPitch, resultLetters[0]);
                    }
                    isGood = true;

                }
            }
            for (long key : rules.keySet()) {
                Set<Integer> tempSet = new HashSet<>(1);
                tempSet.add(Math.toIntExact(rules.get(key)));
                realRules.put(key, tempSet);
            }

        }

        return realRules;
    }

    protected static Long[] getFirstLetters(Set<Long> set) {
        Long[] resultArray = new Long[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = (resultArray[i] / (long) 128) % 128;
        }
        return resultArray;
    }

    protected static Long[] getSecondLetters(Set<Long> set) {
        Long[] resultArray = new Long[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = (resultArray[i] / ((long) 128 * 128)) % 128;
        }
        return resultArray;

    }

    protected static Long[] getThirdLetters(Set<Long> set) {
        Long[] resultArray = new Long[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = resultArray[i] / ((long) 128 * 128 * 128) % 128;
        }
        return resultArray;
    }

    protected static Long[] getFourthLetters(Set<Long> set) {
        Long[] resultArray = new Long[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = resultArray[i] / ((long) 128 * 128 * 128 * 128) % 128;
        }
        return resultArray;
    }

    protected static Long[] getFifthLetters(Set<Long> set) {
        Long[] resultArray = new Long[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = resultArray[i] / ((long) 128 * 128 * 128 * 128 * 128);
        }
        return resultArray;
    }


    protected static Long[] getResultLetters(Set<Long> set) {
        Long[] resultArray = new Long[set.size()];
        resultArray = set.toArray(resultArray);
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] %= 128;
        }
        return resultArray;
    }

    protected static long[] getNormalPitchArray(int[] pitches) {
        int size = pitches.length;
        for (int i = 0; i < pitches.length; i++) {
            if (Pitches.REST == pitches[i]) {
                size--;
            }
        }
        long[] result = new long[size];
        int j = 0;
        for (int i = 0; i < pitches.length; i++) {
            if (Pitches.REST != pitches[i]) {
                result[j] = pitches[i];
                j++;
            }
        }
        return result;
    }

    protected static int getRandFromSet(Set<Integer> set) {
        int rand = (int) (Math.random() * set.size());
        for (int elem : set) {
            rand--;
            if (rand <= 0) {
                return elem;
            }
        }
        System.exit(-200);
        return 0;
    }

    protected static void showRules(Map<Long, Set<Integer>> map) {
        for (long key : map.keySet()) {
            Set<Long> set = new HashSet<>();
            set.add(key);
            Long[] firstLetters = getResultLetters(set);
            Long[] secondLetters = getFirstLetters(set);
            Long[] thirdLetters = getSecondLetters(set);
            Long[] fourthLetters = getThirdLetters(set);
            Long[] fifthLetters = getFourthLetters(set);
            for (int value : map.get(key)) {
                System.out.print(fifthLetters[0]);
                System.out.print(" ");
                System.out.print(fourthLetters[0]);
                System.out.print(" ");
                System.out.print(thirdLetters[0]);
                System.out.print(" ");
                System.out.print(secondLetters[0]);
                System.out.print(" ");
                System.out.print(firstLetters[0]);
                System.out.print("->");
                System.out.println(value);
            }
        }
    }

}
