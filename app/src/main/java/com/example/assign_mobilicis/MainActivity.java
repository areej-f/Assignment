package com.example.assign_mobilicis;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.assign_mobilicis.Adapter.DiagnosticsAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnOK, btnReport;
    TextView txtInfo;
    RecyclerView recyclerViewCards;
    DiagnosticsAdapter diagnosticsAdapter;
    ArrayList<Tests> testsArrayList;
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeElements();
// check if user's device is in silent mode
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentMode = audioManager.getRingerMode();

        if (currentMode == AudioManager.RINGER_MODE_SILENT) {
            showSoundModeDialog();
        }

//     btn Ok
        btnOK.setOnClickListener(view -> startTests());


    }



    private void startTests(){
        txtInfo.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);
        Tests itemToExpand = testsArrayList.get(0);
        itemToExpand.setExpanded(true);
        // Notify the adapter to update the specific card view
        recyclerViewCards.getAdapter().notifyItemChanged(0);

    }

    private void initializeElements() {
        btnOK = findViewById(R.id.btnOK);
        txtInfo = findViewById(R.id.txtInfo);
        recyclerViewCards = findViewById(R.id.recyclerViewCards);
        testsArrayList = getData();
        diagnosticsAdapter = new DiagnosticsAdapter(getApplicationContext(), testsArrayList);
        recyclerViewCards.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCards.setAdapter(diagnosticsAdapter);
    }

    private ArrayList<Tests> getData() {
        testsArrayList = new ArrayList<>();
        testsArrayList.add(new Tests(R.drawable.baseline_phone_android_24, R.drawable.ic_launcher_background, "Rooted Test", "Check if your device is rooted"));
        testsArrayList.add(new Tests(R.drawable.bluetooth, R.drawable.ic_launcher_foreground, "Bluetooth Test", "Test Bluetooth functionality"));
        testsArrayList.add(new Tests(R.drawable.ic_launcher_background, R.drawable.ic_launcher_background, "Accelerometer", "Check rotation"));
        testsArrayList.add(new Tests(R.drawable.ic_launcher_background, R.drawable.ic_launcher_foreground, "Gyroscope Test", "Angular functionality"));
        testsArrayList.add(new Tests(R.drawable.sensor, R.drawable.ic_launcher_foreground, "Proximity Test", "Test distance"));
        testsArrayList.add(new Tests(R.drawable.mic, R.drawable.ic_launcher_foreground, "Microphone Test", "Check if both works"));
        testsArrayList.add(new Tests(R.drawable.baseline_camera_rear_24, R.drawable.ic_launcher_foreground, "Front Camera Test", "Can you click selfie"));
        testsArrayList.add(new Tests(R.drawable.baseline_linked_camera_24, R.drawable.ic_launcher_foreground, "Rear Camera Test", "Can you click pics"));

        return testsArrayList;

    }

    private void showSoundModeDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device is not in General or Vibrate Mode. Please enable to continue further.")
                .setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentSettingsDialog = new Intent(Settings.ACTION_SOUND_SETTINGS);
                        startActivity(intentSettingsDialog);

                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


}