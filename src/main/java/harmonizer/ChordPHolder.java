package harmonizer;

import jm.music.data.Note;

public class ChordPHolder {

	public int root;
	public double duration;
	public Note soprano;
	public Note alto;
	public Note tenor;
	public Note bass;
	
	
	public ChordPHolder(Note s) {
		soprano = s;
	}
	
	public ChordPHolder(Note s, double d) {
		soprano = s;
		duration = d;
	}
	
	
	public int[] makeChord() {
		//TODO
		return null;
		
	}
}
