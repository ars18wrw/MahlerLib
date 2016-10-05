package chords;

/**
 * Created by Уладзімір Асіпчук on 11.05.16.
 */
public class ModulationException extends Exception {
    Degree degreeInModulatedTonality;

    public ModulationException(Degree degreeInModulatedTonality) {
        this.degreeInModulatedTonality = degreeInModulatedTonality;
    }

    public Degree getDegreeInModulatedTonality() {
        return degreeInModulatedTonality;
    }
}
