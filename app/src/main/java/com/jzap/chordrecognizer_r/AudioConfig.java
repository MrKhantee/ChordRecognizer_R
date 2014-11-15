package com.jzap.chordrecognizer_r;

import android.media.AudioFormat;
import android.media.MediaRecorder;

/**
 * Created by Justin on 10/14/2014.
 */
public class AudioConfig {
    static int audioSrc = MediaRecorder.AudioSource.MIC ;
    static int frequency = 8000;
    static int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    static int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //final int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
    static final int bufferSize = 32000; //in bytes
}
