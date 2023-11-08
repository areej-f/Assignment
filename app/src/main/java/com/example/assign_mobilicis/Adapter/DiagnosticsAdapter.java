package com.example.assign_mobilicis.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assign_mobilicis.PdfDocumentCreator;
import com.example.assign_mobilicis.R;
import com.example.assign_mobilicis.Result;
import com.example.assign_mobilicis.Tests;
import com.example.assign_mobilicis.Utility.ToastUtils;

import java.io.File;
import java.util.ArrayList;

public class DiagnosticsAdapter extends RecyclerView.Adapter<DiagnosticsAdapter.ViewHolder> {
    Context context;
    ArrayList<Tests> testsArrayList;
    //    to check if card is clickable
    private PdfDocumentCreator pdfDocumentCreator;
    private boolean isClickable = false;
     private DiagnosticsAdapter diagnosticsAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);


    public void setClickable(boolean clickable) {
        isClickable = clickable;
    }

    public DiagnosticsAdapter(Context context, ArrayList<Tests> testsArrayList) {
        this.context = context;
        this.testsArrayList = testsArrayList;
        pdfDocumentCreator = new PdfDocumentCreator(context.getExternalFilesDir(null) + "/test_report.pdf");
    }

    @NonNull
    @Override
    public DiagnosticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diagnostics_test, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DiagnosticsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Tests currentItem = testsArrayList.get(position);
        holder.textTest.setText(currentItem.getTestName());
        holder.textDesc.setText(currentItem.getTestDesc());
        holder.icon.setImageResource(currentItem.getIcon());
        switch (position) {
            case 0:
                holder.btnStart.setOnClickListener(view -> checkDeviceRooted(currentItem, holder));
                break;

            case 1:
                holder.btnStart.setOnClickListener(view -> checkBluetoothFunctionality(context,currentItem,holder));
                break;

            case 2:
                holder.btnStart.setOnClickListener(view -> {
                    holder.llTestDetail.setVisibility(View.VISIBLE);
                    holder.llCompact.setVisibility(View.GONE);
                    testAccelerometerSensor(currentItem, holder);
                });
                break;

            case 3:
                holder.btnStart.setOnClickListener(view -> testGyroscopeSensor(currentItem, holder));
                break;

            case 4:
                holder.btnStart.setOnClickListener(view -> testProximitySensor(currentItem, holder));
                break;

            case 5:
                holder.btnStart.setOnClickListener(view -> testMicrophones(currentItem, holder));
                break;

            case 6:
                holder.btnStart.setOnClickListener(view -> {
                    Intent intentResult = new Intent(context, Result.class);
                    intentResult.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentResult);
                });
                break;

            default:
                // Handle any other positions here, if needed.
        }




        if (currentItem.isExpanded()) {
            holder.llExpanded.setVisibility(View.VISIBLE);
            if(position==7 ){
                holder.btnSkip.setEnabled(false);
                holder.btnSkip.setText("Report");
                holder.btnSkip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, Result.class);
                        intent.putExtra("pdfDocumentCreator", (CharSequence) diagnosticsAdapter.getPdfDocumentCreator());
                        context.startActivity(intent);
                    }
                });

            }
            String customizedText = getCustomizedTextForPosition(position);
            holder.textDesc.setText(customizedText);
            holder.btnSkip.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("ResourceAsColor")
                @Override
                public void onClick(View view) {
                    if (position < testsArrayList.size() - 1) {
                        Tests nextItem = testsArrayList.get(position + 1);
                        nextItem.setExpanded(true);
//Collapse the current card view
                        currentItem.setExpanded(false);

                        notifyDataSetChanged();

                    }
                    holder.cardTest.setBackgroundColor(R.color.yellow);
                }

            });
        } else {
            holder.llExpanded.setVisibility(View.GONE);
        }
    }

    private void testMicrophones(Tests currentItem, ViewHolder holder) {


        pdfDocumentCreator.addToPdfReport("Microphone Test","result");
        // Test the default microphone (e.g., primary microphone)
        boolean defaultMicResult = testMicrophone(MediaRecorder.AudioSource.MIC, "Default Microphone");

        // Test the secondary microphone (e.g., noise-canceling microphone)
        boolean secondaryMicResult = testMicrophone(MediaRecorder.AudioSource.CAMCORDER, "Secondary Microphone");

        // You can use the results to update your UI or perform further actions.
        if (defaultMicResult && secondaryMicResult) {
            // Both microphones are working.
            // Update your UI or perform additional actions.
            ToastUtils.showToastShort(context,"Both microphone are working properly");
        } else {
            // At least one microphone is not working.
            // Update your UI or take appropriate action.
            ToastUtils.showToastShort(context,"Microphones are not  working ");
        }
        updateRecyclerViewUI(currentItem,holder);
    }

    private boolean testMicrophone(int audioSource, String microphoneName) {
        @SuppressLint("MissingPermission") AudioRecord audioRecord = new AudioRecord(audioSource, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);

        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            try {
                Log.d("MicrophoneTest", "Testing " + microphoneName);

                byte[] buffer = new byte[BUFFER_SIZE];
                audioRecord.startRecording();
                int bytesRead = audioRecord.read(buffer, 0, BUFFER_SIZE);
                audioRecord.stop();
                audioRecord.release();

                // Check if sound is detected in the recorded audio (you can use the isSoundDetected function from a previous response).
                boolean soundDetected = isSoundDetected(buffer, bytesRead);

                if (soundDetected) {
                    ToastUtils.showToastShort(context,"Test successful");
                    Log.d("MicrophoneTest", microphoneName + " test successful.");
                } else {
                    ToastUtils.showToastShort(context,"No sound detected.Test failed");
                    Log.d("MicrophoneTest", microphoneName + " test failed (no sound detected).");
                }

                return soundDetected;

            } catch (Exception e) {
                Log.e("MicrophoneTest", "Error while testing " + microphoneName + ": " + e.getMessage());
                return false;
            }
        } else {
            Log.e("MicrophoneTest", microphoneName + " is not available on this device.");
            return false;
        }
    }

    private boolean isSoundDetected(byte[] audioData, int bytesRead) {
        // Calculate the amplitude of the audio data.
        double amplitude = calculateAmplitude(audioData);

        // Set a threshold for determining if sound is detected.
        double threshold = 1000; // Adjust this threshold as needed.

        return amplitude > threshold;
    }

    private double calculateAmplitude(byte[] audioData) {
        double sum = 0;
        for (byte value : audioData) {
            sum += Math.abs(value);
        }
        return sum / audioData.length;
    }

    private void testProximitySensor(Tests currentItem, ViewHolder holder) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (proximitySensor != null) {
            SensorEventListener proximitySensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float distance = event.values[0];
                    Toast.makeText(context, "distance"+distance, Toast.LENGTH_SHORT).show();
                    // You can use holder.textTest or holder.textDesc to display the data.
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Handle accuracy changes if needed
                }
            };

            sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

            // You can also update your UI or display a message here to indicate the test is running
        } else {
            // Proximity sensor not available on this device
            ToastUtils.showToastShort(context,"Proximity sensor is not available on your device");
        }
        updateRecyclerViewUI(currentItem,holder);
    }

    private void testGyroscopeSensor(Tests currentItem, ViewHolder holder) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // Check if the gyroscope sensor is available
        Sensor gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscopeSensor != null) {
            // Gyroscope sensor is available
            Toast.makeText(context, "Gyroscope sensor is available", Toast.LENGTH_SHORT).show();

            // Register a sensor listener to get gyroscope data
            SensorEventListener gyroscopeListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    // Get gyroscope data
                    float x = event.values[0]; // Angular speed around the x-axis (roll)
                    float y = event.values[1]; // Angular speed around the y-axis (pitch)
                    float z = event.values[2]; // Angular speed around the z-axis (yaw)

                    // You can perform tests or actions related to the gyroscope data here
                    // For example, you can detect device rotations or movements.

                    if (Math.abs(x) > 1.0 || Math.abs(y) > 1.0 || Math.abs(z) > 1.0) {
                        // Device is experiencing significant movement or rotation
                        Toast.makeText(context, "Device is moving/rotating.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Handle accuracy changes if needed
                }
            };

            // Register the gyroscope listener
            sensorManager.registerListener(gyroscopeListener, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

            // Provide instructions to the user to perform actions that will trigger gyroscope sensor data.

            // After the user has performed the required actions, you can signal the end of the test and unregister the listener.

            // When the test is complete, unregister the listener.
            // sensorManager.unregisterListener(gyroscopeListener);
        } else {
            // Gyroscope sensor is not available
            ToastUtils.showToastShort(context,"Gryoscope sensor is not available");
        }
        updateRecyclerViewUI(currentItem,holder);

    }

    private void testAccelerometerSensor(Tests currentItem, ViewHolder holder) {
        // Get the sensor service
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // Check if the accelerometer sensor is available
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometerSensor != null) {
            // Accelerometer sensor is available
            Toast.makeText(context, "Accelerometer sensor is available. Please change device orientation.", Toast.LENGTH_SHORT).show();

            // Register a sensor listener to get accelerometer data
            SensorEventListener accelerometerListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    // Get accelerometer data
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    // Use the accelerometer data to determine the device's orientation or perform tests
                    // For example, you can check the change in the z-axis value to detect orientation change.
                    if (Math.abs(z) > 7.0) {
                        // Device orientation has changed
                        // You can perform additional tests or actions related to the new orientation
ToastUtils.showToastShort(context,"Device orientation has changed");
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    // Handle accuracy changes if needed
                }
            };

            // Register the accelerometer listener
            sensorManager.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

            // You can provide instructions to the user to change the device's orientation.

            // After the user has changed the orientation, you can notify them that the test is complete and unregister the listener.
            // For example, you can use a button click or a timer to indicate the end of the test.

            // When the test is complete, unregister the listener.
            // sensorManager.unregisterListener(accelerometerListener);
        } else {
            // Accelerometer sensor is not available
            Toast.makeText(context, "Accelerometer sensor is not available on this device", Toast.LENGTH_SHORT).show();
        }
        updateRecyclerViewUI(currentItem,holder);

    }

    private void updateRecyclerViewUI(Tests currentItem, ViewHolder holder) {
        int position = holder.getAdapterPosition();
        // Check if there's a next item in the list
        if (position < testsArrayList.size() - 1) {
            Tests nextItem = testsArrayList.get(position + 1);
            nextItem.setExpanded(true);
            currentItem.setExpanded(false);
            notifyDataSetChanged();
        }
        else{}
    }

    private void checkBluetoothFunctionality(Context context, Tests currentItem, ViewHolder holder) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(context,"Your device doesn't support Bluetooth",Toast.LENGTH_SHORT).show();
        } else {
            // Bluetooth is supported
            if (!bluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled, you can prompt the user to enable it
                Toast.makeText(context,"Please enable bluetooth to check its functionality",Toast.LENGTH_SHORT).show();
                // Use an Intent to open the Bluetooth settings
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    // Request permissions here
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN},
                            REQUEST_ENABLE_BT);
                }
                else{
                }
            } else {
                // Bluetooth is both supported and enabled
              boolean isDiscoverable=  bluetoothAdapter.startDiscovery();
                if(isDiscoverable){
                    ToastUtils.showToastShort(context,"Your device is discoverable");
                }
                else{
                    ToastUtils.showToastShort(context,"Your device is not discoverable");
                }


            }
            testPassUI(holder);

        }


    }

    private void testPassUI(ViewHolder holder) {
        holder.iconRetry.setImageResource(R.drawable.check);
        holder.txtRetry.setVisibility(View.INVISIBLE);
        notifyItemChanged(holder.getAdapterPosition());
    }

    private void checkDeviceRooted(Tests currentItem, ViewHolder holder) {
        boolean isRooted = isDeviceRooted();
        String result = isRooted ? "Rooted" : "Not Rooted";

        pdfDocumentCreator.addToPdfReport("Rooted Test", result);
            if(isRooted){
                Toast.makeText(context,"rooted",Toast.LENGTH_LONG).show();
                holder.textDesc.setText("Your device is rooted");
                holder.iconRetry.setImageResource(R.drawable.check);
            }
            else{
                Toast.makeText(context,"not rooted",Toast.LENGTH_LONG).show();
            }
            updateRecyclerViewUI(currentItem, holder);

    }
    public PdfDocumentCreator getPdfDocumentCreator() {
        return pdfDocumentCreator;
    }
    private boolean isDeviceRooted() {
        String[] rootBinaries = {
                "/system/xbin/su",
                "/system/bin/su",
                "/system/sbin/su",
                "/sbin/su",
                "/system/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/data/local/su",
                "/su/bin/su",
                "/su/xbin/su",
                "/sbin/magisk"
        };

        for (String binaryPath : rootBinaries) {
            File file = new File(binaryPath);
            if (file.exists()) {
                return true;
            }
        }

        // Check for SuperUser app
        String[] superUserApps = {
                "com.noshufou.android.su",
                "eu.chainfire.supersu",
                "com.koushikdutta.superuser",
                "com.thirdparty.superuser"
        };

        for (String packageName : superUserApps) {
            if (isPackageInstalled(packageName)) {
                return true;
            }
        }

        // Check for Magisk app
        if (isPackageInstalled("com.topjohnwu.magisk")) {
            return true;
        }

        return false;
    }

    public boolean isPackageInstalled(String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();

            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private String getCustomizedTextForPosition(int position) {
        switch (position) {
            case 0:
                return "This test will detect if root access is properly installed on this device";
            case 1:
                return "Please connect your wired headphones.";
            default:
                return " Were you able to hear?";
        }
    }

    @Override
    public int getItemCount() {
        return testsArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llExpanded,llTestDetail,llCompact;
        ImageView icon,iconRetry;
        TextView textTest, textDesc,txtRetry;
        Button btnStart, btnSkip;
        CardView cardTest;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.imgIcon);
            iconRetry=itemView.findViewById(R.id.iconRetry);
            textTest = itemView.findViewById(R.id.txtTest);
            textDesc = itemView.findViewById(R.id.txtDesc);
            llExpanded = itemView.findViewById(R.id.llExpanded);
            btnStart = itemView.findViewById(R.id.btnStart);
            btnSkip = itemView.findViewById(R.id.btnSkip);
            cardTest=itemView.findViewById(R.id.cardTests);
            llTestDetail=itemView.findViewById(R.id.llTestDetail);
            llCompact=itemView.findViewById(R.id.llCompact);
            txtRetry=itemView.findViewById(R.id.txtRetry);
        }
    }
}
