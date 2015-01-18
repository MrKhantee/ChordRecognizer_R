package com.jzap.chordrecognizer_r;

import android.util.Log;
import java.util.Arrays;

public class ProcessPCP {

    private static String TAG = "ProcessPCP";

    private double[] PCP;

    // Constructor
	public ProcessPCP(double[] PCP){
		this.PCP = PCP;
		normalizeVector(PCP);
		//Log.i(TAG, "Normalized Vector Values: ");
		for(double d : PCP) {
			//Log.i(TAG, String.valueOf(d));
		}
	}
    // End Constructor
	
	public String determineChord() {
		double[] scores = assignScores();
		String chord = sort(scores);
		
		//Log.i(TAG, "SCORES: ");
		for(int i=0; i<numOfChords; i++) {
			//Log.i(TAG, ChordNames[i] + ": " + scores[i]);
		}

		return chord;
	}
	
	public double[] assignScores() {
		double[] scores = new double[numOfChords];
		for(int i=0; i<numOfChords; i++) {
			scores[i] = calculateScore(i);
		}
		return scores;
	}
	
	public double calculateScore(int chord) {
		double score = 0;
		for(int i=0; i<PCP.length; i++) {
			score += Math.pow((Chords[chord][i] - PCP[i]), 2);
		}
		return score;
	}
	
	public void normalizeVector(double myArray[]) {
		double vectorNorm = calcVectorNorm(myArray);
		for(int i=0; i<myArray.length; i++) {
			myArray[i] = myArray[i]/vectorNorm;
		}
	}

	public double calcVectorNorm(double[] myVector) {
		double vectorNorm = 0;
		double squareSum = 0;
		//Sum of vector elements squared
		for(double d : myVector) {
			squareSum += (d*d);
		}
		vectorNorm = Math.sqrt(squareSum);
		return vectorNorm;
	}

	//TODO : This is basically a copy from PCP.  A bit hacky.  Consider fixing.
	
	public static String sort(double[] myScores) {
 		//System.out.println(Arrays.toString(myArray));
 		double[] sortedArray = copyArray(myScores);
 		Arrays.sort(sortedArray);
 		//System.out.println(Arrays.toString(myArray));
 		String chord = indexToChord(indexOf(sortedArray[0], myScores));
 		
 		return chord;
 	}
 	
 	// Arrays.copyOf not supported on API 8 (my min?)
 	public static double[] copyArray(double[] myArray) {
 		double[] newArray = new double[myArray.length];
 		for(int i=0; i<myArray.length; i++) {
 			newArray[i] = myArray[i];
 		}
 		return newArray;
 	}
 	
 	public static int indexOf(double value, double[] array){
 		for(int i=0; i<array.length; i++) {
 			if(array[i]==value) return i;
 		}
 		return -1;
 	}

//----------------------------------------------------------------------------
//------------------------   CHORD TEMPLATES   -------------------------------
//----------------------------------------------------------------------------

    // C                                 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] CMaj = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    public static final double[] Cm   = {1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0};
    public static final double[] Cdim = {1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0};
    public static final double[] C7   = {1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};

    // C#                                 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] CsMaj = {0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0};
    public static final double[] Csm   = {0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0};
    public static final double[] Csdim = {0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0};
    public static final double[] Cs7   = {0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1};

    // D                                 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] DMaj = {0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0};
    public static final double[] Dm   = {0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0, 0};
    public static final double[] Ddim = {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0};
    public static final double[] D7   = {1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0, 0};

    // D#                                 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] DsMaj = {0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0};
    public static final double[] Dsm   = {0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1, 0};
    public static final double[] Dsdim = {0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0};
    public static final double[] Ds7   = {0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1, 0};

    // E								 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] EMaj = {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 1};
    public static final double[] Em   = {0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 1};
    public static final double[] Edim = {0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0};
    public static final double[] E7   = {0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1};

    // F								 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] FMaj = {1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0};
    public static final double[] Fm   = {1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0};
    public static final double[] Fdim = {0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1};
    public static final double[] F7   = {1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0};

    // F#   							  C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] FsMaj = {0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0};
    public static final double[] Fsm   = {0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0};
    public static final double[] Fsdim = {1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0};
    public static final double[] Fs7   = {0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0};

    // G								 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] GMaj = {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1};
    public static final double[] Gm   = {0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0};
    public static final double[] Gdim = {0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0};
    public static final double[] G7   = {0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1};

    // G#    							  C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] GsMaj = {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0};
    public static final double[] Gsm   = {0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 1};
    public static final double[] Gsdim = {0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1};
    public static final double[] Gs7   = {1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0};

    // A								 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] AMaj = {0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0};
    public static final double[] Am   = {1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0};
    public static final double[] Adim = {1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0};
    public static final double[] A7   = {0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0};

    // A#								  C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] AsMaj = {0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0};
    public static final double[] Asm   = {0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0};
    public static final double[] Asdim = {0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0};
    public static final double[] As7   = {0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1, 0};

    // B								 C  C# D  D# E  F F#  G  G# A  A# B
    public static final double[] BMaj = {0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1};
    public static final double[] Bm   = {0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1};
    public static final double[] Bdim = {0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 1};
    public static final double[] B7   = {0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 1};


    public static final double[][] Chords = {CMaj,  DMaj, EMaj, FMaj,  GMaj,  AMaj, BMaj,
            Cm,    Dm,   Em,   Fm,    Gm,    Am,   Bm,
            Cdim,  Ddim, Edim, Fdim,  Gdim,  Adim, Bdim,
            C7,    D7,   E7,   F7,    G7,    A7,   B7,
            CsMaj, DsMaj,      FsMaj, GsMaj, AsMaj,
            Csm,   Dsm,        Fsm,   Gsm,   Asm,
            Csdim, Dsdim,      Fsdim, Gsdim, Asdim,
            Cs7,   Ds7,        Fs7,   Gs7,   As7 };

    //Is this best practice?
    private static final int numOfChords = Chords.length;

    public static final String[] ChordNames = {"CMaj",  "DMaj", "EMaj", "FMaj",  "GMaj",  "AMaj", "BMaj",
            "Cm",    "Dm",   "Em",   "Fm",    "Gm",    "Am",   "Bm",
            "Cdim",  "Ddim", "Edim", "Fdim",  "Gdim",  "Adim", "Bdim",
            "C7",    "D7",   "E7",   "F7",    "G7",    "A7",   "B7",
            "CsMaj", "DsMaj",        "FsMaj", "GsMaj", "AsMaj",
            "Csm",   "Dsm",          "Fsm",   "Gsm",   "Asm",
            "Csdim", "Dsdim",        "Fsdim", "Gsdim", "Asdim",
            "Cs7",   "Ds7",          "Fs7",   "Gs7",   "As7",   };

 	public static String indexToChord(int i) {
 		String chord;
 		switch (i) {
 		
 			// Natural Major
 			case 0: chord = "CMaj";
 				break;
 			case 1: chord = "DMaj"; 
 				break;
 			case 2: chord = "EMaj";
				break;
 			case 3: chord = "FMaj";
 				break;
 			case 4: chord = "GMaj";
				break;
 			case 5: chord = "AMaj";
				break;
 			case 6: chord = "BMaj";
				break;
				
			// Natural Minor
 			case 7: chord = "Cm"; 
				break;
			case 8: chord = "Dm";
				break;
			case 9: chord = "Em";
				break;
			case 10: chord ="Fm";
				break;
			case 11: chord ="Gm";
				break;
			case 12: chord ="Am";
				break;
			case 13: chord ="Bm";
				break;
				
			// Natural Diminished
			case 14: chord = "Cdim";
				break;
			case 15: chord = "Ddim";
				break;
			case 16: chord = "Edim";
				break;
			case 17: chord = "Fdim";
				break;
			case 18: chord = "Gdim";
				break;
			case 19: chord = "Adim";
				break;
			case 20: chord = "Bdim";
				break;
				
			// Natural Dominant 7th
			case 21: chord = "C7";
				break;
			case 22: chord = "D7";
				break;
			case 23: chord = "E7";
				break;
			case 24: chord = "F7";
				break;
			case 25: chord = "G7";
				break;
			case 26: chord = "A7";
				break;
			case 27: chord = "B7";
				break;
				

			// # Major
 			case 28: chord = "C#Maj";
 				break;
 			case 29: chord = "D#Maj"; 
 				break;
 			case 30: chord = "F#Maj";
 				break;
 			case 31: chord = "G#Maj";
				break;
 			case 32: chord = "A#Maj";
				break;
				
			// # Minor
 			case 33: chord = "C#m"; 
				break;
			case 34: chord = "D#m";
				break;
			case 35: chord ="F#m";
				break;
			case 36: chord ="G#m";
				break;
			case 37: chord ="A#m";
				break;
				
			// # Diminished
			case 38: chord = "C#dim";
				break;
			case 39: chord = "D#dim";
				break;
			case 40: chord = "F#dim";
				break;
			case 41: chord = "G#dim";
				break;
			case 42: chord = "A#dim";
				break;
				
			// # Dominant 7th
			case 43: chord = "C#7";
				break;
			case 44: chord = "D#7";
				break;
			case 45: chord = "F#7";
				break;
			case 46: chord = "G#7";
				break;
			case 47: chord = "A#7";
				break;
				
			default: chord = "Chord Error";
 		}
 		return chord;
 	}
}
