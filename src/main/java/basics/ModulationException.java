package basics;

import chords.Degree;

/**
 * Created by Уладзімір Асіпчук on 11.05.16.
 */
public class ModulationException extends Exception {
    chords.Degree degreeInModulatedTonality;

    public ModulationException(chords.Degree degreeInModulatedTonality) {
        this.degreeInModulatedTonality = degreeInModulatedTonality;
    }

    public Degree getDegreeInModulatedTonality() {
        return degreeInModulatedTonality;
    }
}
