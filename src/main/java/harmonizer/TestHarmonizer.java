package harmonizer;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;
import jm.util.View;

public class TestHarmonizer {


	final static int MELODY_NUM = 3;

	final static int keys[] = {0,4,5};
	final static boolean minor[] = {false,false,true};

	public static void main(String[] args) {
		for(int i=MELODY_NUM-1;i>=0;i--) {
			System.out.println("Harmonizing melody #"+i);
			Score score = Harmonizer.harmonize(getMelody(i).getPart(0).getPhrase(0),keys[i],minor[i]);
			View.notate(score,200+400*i,400);
			View.notate(getMelody(i),200+400*i,250);
		}
	}


	public static Score getMelody(int num) {
		Score mScore = new Score();
		Part p = new Part();
		Phrase melody = new Phrase("Melody");

		if(num==0) {
			melody.add(new Note(72, 0.5));
			melody.add(new Note(76, 0.5));
			melody.add(new Note(74, 0.5));
			melody.add(new Note(77, 0.5));
			melody.add(new Note(76, 0.5));
			melody.add(new Note(79, 0.5));
			melody.add(new Note(77, 1.0));
			melody.add(new Note(77, 0.5));
			melody.add(new Note(74, 0.5));
			melody.add(new Note(76, 0.5));
			melody.add(new Note(72, 0.5));
			melody.add(new Note(74, 0.5));
			melody.add(new Note(71, 0.5));
			melody.add(new Note(72, 1.0));
		}
		
		if(num==1) {
			melody.add(new Note(72+4, 1));
			melody.add(new Note(79+4, 1));
			melody.add(new Note(77+4, 2));
			melody.add(new Note(76+4, 1));
			melody.add(new Note(74+4, 1));
			melody.add(new Note(76+4, 2));
			melody.add(new Note(77+4, 1));
			melody.add(new Note(81+4, 1));
			melody.add(new Note(79+4, 1));
			melody.add(new Note(77+4, 1));
			melody.add(new Note(76+4, 1));
			melody.add(new Note(74+4, 1));
			melody.add(new Note(72+4, 2));
		}
		
		if(num==2) {
			melody.add(new Note(80,1));
			melody.add(new Note(77,0.5));
			melody.add(new Note(80,0.5));
			melody.add(new Note(84,1.5));
			melody.add(new Note(82,0.5));
			melody.add(new Note(80,1));
			melody.add(new Note(77,0.5));
			melody.add(new Note(80,0.5));
			melody.add(new Note(84,1.5));
			melody.add(new Note(82,0.5));
			melody.add(new Note(79,1));
			melody.add(new Note(80,1));
			melody.add(new Note(77,0.5));
			melody.add(new Note(79,0.5));
			melody.add(new Note(73,0.5));
			melody.add(new Note(76,0.5));
			melody.add(new Note(77,2));
		}

		p.add(melody);
		mScore.add(p);
		return mScore;
	}

}
