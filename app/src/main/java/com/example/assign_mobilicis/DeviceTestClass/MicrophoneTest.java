package com.example.assign_mobilicis.DeviceTestClass;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class MicrophoneTest {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

    public static void testMicrophone(Context context) {
        // Record audio from the microphone
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            Log.d("MicrophoneTest", "Recording audio...");
            byte[] audioData = new byte[BUFFER_SIZE];
            audioRecord.startRecording();
            int bytesRead = audioRecord.read(audioData, 0, BUFFER_SIZE);
            audioRecord.stop();
            audioRecord.release();
            Log.d("MicrophoneTest", "Audio recorded successfully.");

            // Play back the recorded audio
            AudioTrack audioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AUDIO_FORMAT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build())
                    .setBufferSizeInBytes(BUFFER_SIZE)
                    .build();

            audioTrack.play();
            audioTrack.write(audioData, 0, bytesRead);
            audioTrack.stop();
            audioTrack.release();
            Log.d("MicrophoneTest", "Audio playback successful.");
        } else {
            Log.e("MicrophoneTest", "Microphone is not available on this device.");
        }
    }
}
