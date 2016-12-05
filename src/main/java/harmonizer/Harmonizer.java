package harmonizer;

import jm.constants.Scales;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Phrase;
import jm.music.data.Score;

import java.util.*;

public class Harmonizer {

	public static Score harmonize(Phrase melody, int key, boolean minor) {
		
		//get array of integers representing the pitches in this key (0 is C, 1 is C#, etc.)
		int[] scalePrim = minor ?  Scales.HARMONIC_MINOR_SCALE : Scales.MAJOR_SCALE;
		Integer[] scale = new Integer[7];
		for(int i=0; i<scalePrim.length; i++)
			scale[i] = (scalePrim[i] + key)%12;
		
		
		Score harmony = new Score();
		Note[] notes = melody.getNoteArray();
		
		
		LinkedList<ChordPHolder> chords = pickChordLocations(notes);
		chooseChords(chords, scale, minor);
		assignPitches(chords, scale);
		
		Phrase alto = new Phrase(0.0);
		Phrase tenor = new Phrase(0.0);
		Phrase bass = new Phrase(0.0);
		Iterator<ChordPHolder> iter = chords.listIterator();
		ChordPHolder c;
		while(iter.hasNext()) {
			c = iter.next();
			alto.add(c.alto);
			tenor.add(c.tenor);
			bass.add(c.bass);
		}
		
		harmony.add(new Part(melody,"Soprano"));
		harmony.add(new Part(alto,"Alto"));
		harmony.add(new Part(tenor,"Tenor"));
		harmony.add(new Part(bass,"Bass"));
		
		harmony.setKeyQuality(minor ? 1 : 0);
		
		switch(minor ? (key+3)%12 : key) {
			case 5:
				harmony.setKeySignature(-1);
				break;
			case 10:
				harmony.setKeySignature(-2);
				break;
			case 3:
				harmony.setKeySignature(-3);
				break;
			case 8:
				harmony.setKeySignature(-4);
				break;
			case 1:
				harmony.setKeySignature(-5);
				break;
			case 6:
				harmony.setKeySignature(-6);
				break;
			case 7:
				harmony.setKeySignature(1);
				break;
			case 2:
				harmony.setKeySignature(2);
				break;
			case 9:
				harmony.setKeySignature(3);
				break;
			case 4:
				harmony.setKeySignature(4);
				break;
			case 11:
				harmony.setKeySignature(5);
				break;
			case 0:
			default:
				harmony.setKeySignature(0);
		}
		return harmony;
	}
	
	
	private static LinkedList<ChordPHolder> pickChordLocations(Note[] notes) {
		LinkedList<ChordPHolder> chords = new LinkedList<ChordPHolder>();
		chords.add(new ChordPHolder(notes[0],notes[0].getDuration()));
		double beat = notes[0].getRhythmValue();
		double duration = notes[0].getRhythmValue();
		
		for(int i=1; i<notes.length; i++) {
			Note n = notes[i];
			
			if(!n.isRest() && i==notes.length-1 || chordOnBeat(beat, n.getDuration())) {
				chords.getLast().duration = duration;
				duration = 0;
				chords.add(new ChordPHolder(n));
			}
			duration += n.getRhythmValue();
			beat += n.getRhythmValue();
		}
		chords.getLast().duration = duration;
		return chords;
		
	}
	
	private static boolean chordOnBeat(double beat, double duration) {
		if(beat%2==0 || duration>=2)
			return true;
		else {
			if(beat%1==0) {
				if(Math.random()<0.50*duration)
					return true;
				return false;
			}
			if(Math.random()<0.2*duration)
				return true;
			return false;
		}
		
	}


	private static void chooseChords(LinkedList<ChordPHolder> chords, Integer[] scale, boolean minor) {
		ListIterator<ChordPHolder> iter = chords.listIterator();
		ChordPHolder c = iter.next();
		ChordPHolder previous = null;
		int[] possible = new int[3];
		int[] weights = new int[3];
		
		//always start on tonic chord
		c.root = 0;
		
		for(int i=0;i<scale.length;i++) {
			//System.out.print(scale[i]+"\t");
		}
		//System.out.print("\n");
		
		
		while(iter.hasNext()) {
			previous = c;
			c = iter.next();
			int pitch = c.soprano.getPitch() % 12;
			possible[0] = Arrays.asList(scale).indexOf(pitch);
			
			possible[1] = (possible[0] + 5) % 7;
			possible[2] = (possible[1] + 5) % 7;
			
			//System.out.println(pitch);
			//System.out.println(possible[0]+"\t"+possible[1]+"\t"+possible[2]+"\n");
			
			if(iter.hasNext()) {
				iter.next();
				if(iter.hasNext())
					iter.previous();
				else {
					//if second to last chord
					if(possible[0]==4 || possible[1]==4 || possible[2]==4) {
						c.root = 4;
						break;
					}
						
					else if(possible[0]==3 || possible[1]==3 || possible[2]==3) {
						c.root = 3;
						break;
					}
				}
			}

			weights[0] = weight(previous.root,possible[0]);
			weights[1] = weight(previous.root,possible[1]);
			weights[2] = weight(previous.root,possible[2]);
			do {
				int r = (int)(Math.random()*(weights[0]+weights[1]+weights[2]));
				//System.out.println(weights[0]+"\t"+weights[1]+"\t"+weights[2]);
				//System.out.println(r+"\n");
				if(r<weights[0]) 
					c.root = possible[0];
				else if (r<weights[0]+weights[1]) 
					c.root = possible[1];	
				else 
					c.root = possible[2];	
			} while (minor & c.root==2);
			//System.out.println((c.root+1)+"\n");
			
			
		}
		chords.getLast().root = 0;
	}
	
	
	
	private static void assignPitches(LinkedList<ChordPHolder> chords, Integer[] scale) {
		//TODO
		ListIterator<ChordPHolder> iter = chords.listIterator();
		ChordPHolder c = null;
		ChordPHolder previous = null;
		while(iter.hasNext()) {
			previous = c;
			c = iter.next();
			
			
			
			int root = scale[c.root];
			int third = scale[(c.root+2)%7];
			int fifth = scale[(c.root+4)%7];
			
			HashSet<Integer> pitches = new HashSet<Integer>();
			pitches.add(root);
			pitches.add(third);
			pitches.add(fifth);
			if(c.soprano.getPitch()%12!=root)
				pitches.remove(c.soprano.getPitch()%12);
			
			
			
			
			int nextBass = -5;
			int nextTenor = -5;
			int nextAlto = -5;
			
			//determine bass
			if(previous==null || !iter.hasNext()) {
				nextBass = root+48;
				
			}
			else {
				if(!pitches.contains(third)) {
					nextBass = closestPitch(root,previous.bass.getPitch());
				}
				else {
					int rootP = closestPitch(root,previous.bass.getPitch());
					int thirdP = closestPitch(third,previous.bass.getPitch());
					nextBass = (Math.abs(rootP-previous.bass.getPitch())<=Math.abs(thirdP-previous.bass.getPitch())
							? rootP : thirdP);
				}
			}
			
			if(pitches.size()==3)
				pitches.remove(nextBass%12+12);
			Integer[] remaining = Arrays.copyOf(pitches.toArray(),pitches.size(),Integer[].class);
			
			//determine tenor and alto
			if(previous==null) {
				if(Math.random()<0.5) {
					nextTenor = remaining[0]+48;
					nextAlto = remaining[1]+60;
					if(nextTenor<=nextBass)
						nextTenor += 12;
				}
				else {
					nextTenor = remaining[1]+48;
					nextAlto = remaining[0]+60;
					if(nextTenor<=nextBass)
						nextTenor += 12;
				}
				if(nextTenor<nextBass)
					nextTenor += 12;
				if(nextAlto>c.soprano.getPitch())
					nextAlto -= 12;
				if(nextAlto<nextTenor)
					nextAlto += 12;
				
					
			}
			else {
				int n0 = closestPitch(remaining[0],previous.tenor.getPitch());
				int n1 = closestPitch(remaining[1],previous.tenor.getPitch());
				if(Math.abs(n0-previous.tenor.getPitch())<Math.abs(n1-previous.tenor.getPitch())) {
					nextTenor = n0;
					nextAlto = closestPitch(remaining[1]-12,previous.alto.getPitch());
				}
				else {
					nextTenor = n1;
					nextAlto = closestPitch(remaining[0]-12,previous.alto.getPitch());
				}
				if(nextTenor<nextBass)
					nextTenor += 12;
				if(nextAlto>c.soprano.getPitch())
					nextAlto -= 12;
				if(nextAlto<nextTenor)
					nextAlto += 12;
			}
			
			
			c.bass = new Note(nextBass,c.duration);
			//System.out.println(c.bass.getPitch());
			c.tenor = new Note(nextTenor,c.duration);
			c.alto = new Note(nextAlto,c.duration);
			
		}
	}
	
	private static int closestPitch(int root, int previous) {
		int lower = root + 12*(previous/12);
		int upper = lower + 12;
		return (upper-previous < previous-lower ? upper : lower);
	}


	private static int weight(int from, int to) {
		switch((to-from+16)%8) {
		case 4:
			return 20;
		case 3:
			return 10;
		case 5:
			return 5;
		case 1:
			return 8;
		default:
			return 1;
		}
	}
}
