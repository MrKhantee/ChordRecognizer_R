package com.jzap.chordrecognizer_r;

import java.util.Arrays;

import ca.uol.aig.fftpack.RealDoubleFFT;

public class ProcessAudio {

    private static final String TAG = "ProcessAudio";
    private static boolean mNewPCP = false;

    private AudioAnalysis mAudioAnalysis = new AudioAnalysis();
    private ProcessPCP mProcessPCP;

    private MainActivity mMainActivity;

    public ProcessAudio(MainActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    public AudioAnalysis detectChord(short[] myArray, boolean volumeThresholdMet, int greatestSample) {

        int N = myArray.length;
        double[] fft = new double[N];

        for (int i = 0; i < N; i++) {
            fft[i] = (double) myArray[i];
        }

        new RealDoubleFFT(N).ft(fft);

       // Log.i(TAG, "FFT Complete");
        int[] m = getM(N);
        //Log.i(TAG, "Length of m = " + m.length);

        double[] PCP = getPCP(m, fft);

        //
        for(int i=0; i<PCP.length; i++){
            //Log.i(TAG, "PCP " + i + " " + PCP[i] + ", ");
        }

        mProcessPCP = new ProcessPCP(PCP);

        if (volumeThresholdMet) {
            mAudioAnalysis.setmChord(mProcessPCP.determineChord());
        }

      // for(int i=0; i<PCP.length; i++){
           // Log.i(TAG, "PCP " + i + " " + PCP[i] + ", ");
      // }

        sort(mAudioAnalysis, PCP);

        mAudioAnalysis.setmPCP(PCP);
        mAudioAnalysis.setmVolumeThresholdMet(volumeThresholdMet);
        mAudioAnalysis.setmMaxVolume(greatestSample);
        mMainActivity.getmNgv_graph().setmAudioAnalysis(mAudioAnalysis);

        setmNewPCP(true);
        return mAudioAnalysis;
    }

    synchronized public static void setmNewPCP(boolean newPCP) {
        mNewPCP = newPCP;
    }

    synchronized public static boolean getmNewPCP() {
        return mNewPCP;
    }

    public static void sort(AudioAnalysis audioAnalysis, double[] myArray) {
        //System.out.println(Arrays.toString(myArray));
        double[] sortedArray = Arrays.copyOf(myArray, myArray.length);
        Arrays.sort(sortedArray);
        //Log.i(TAG, Arrays.toString(myArray));
        //Log.i(TAG, "Index of most intense note: " + indexOf(sortedArray[sortedArray.length-1], myArray));
        //Log.i(TAG, "Index of 2 most intense note: " + indexOf(sortedArray[sortedArray.length-2], myArray));
        //Log.i(TAG, "Index of 3 most intense note: " + indexOf(sortedArray[sortedArray.length-3], myArray));
        audioAnalysis.setmMostIntenseNote(indexToNote(indexOf(sortedArray[sortedArray.length - 1], myArray)));
        audioAnalysis.setmSeconMostIntenseNote(indexToNote(indexOf(sortedArray[sortedArray.length - 2], myArray)));
        audioAnalysis.setmThirdMostIntenseNote(indexToNote(indexOf(sortedArray[sortedArray.length - 3], myArray)));
    }

    public static int indexOf(double value, double[] array){
        for(int i=0; i<array.length; i++) {
            if(array[i]==value) return i;
        }
        return -1;
    }

    public static int[] getM(double N) {
        double refFreq = 523.25;
        int[] m = new int[(int) (N/2)];
        for(double i=0; i<=(N/2-1); i++) {
            if(i==0) {
                m[(int)i] = -1;
            }
            else {
                double tempElement = ((12*(log2(((double)AudioConfig.frequency)*(i/N)/refFreq)))) % 12;
                tempElement = (tempElement < 0) ? tempElement+12: tempElement;
                m[(int)i] = (int) Math.round(tempElement);
            }
        }
        return m;
    }

    public static double log2(double num) {
        return (Math.log(num)/Math.log(2));
    }

    public static double[] getPCP(int[] m, double x[]) { // was Complex x[]
        double[] PCP = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for(int i = 1, count = 1; i<m.length-1; i++, count++) { // Ignore  fft[0] and fft[N-1] as their imaginary parts are absent from the array
            if(m[i]<12 && m[i]>=0) {
                //PCP[m[i]] = PCP[m[i]] + (x[i].abs()*x[i].abs());
                PCP[m[i]] = PCP[m[i]] + Math.pow(abs(x, i + count),2);
            }
        }
        return PCP;
    }

    private static double abs(double x[], int i) {
        return Math.sqrt(x[i]*x[i]+x[i+1]*x[i+1]);
    }

    public static String indexToNote(int i) {
        String note;
        switch (i) {
            case 0: note = "C";
                break;
            case 1: note = "C#";
                break;
            case 2: note = "D";
                break;
            case 3: note = "D#";
                break;
            case 4: note = "E";
                break;
            case 5: note = "F";
                break;
            case 6: note = "F#";
                break;
            case 7: note = "G";
                break;
            case 8: note = "G#";
                break;
            case 9: note = "A";
                break;
            case 10: note = "A#";
                break;
            case 11: note = "B";
                break;
            default: note = "Note Error";
        }
        //Log.i(TAG, note);
        return note;
    }
}
