package com.example.assign_mobilicis;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.assign_mobilicis.Adapter.DiagnosticsAdapter;

public class Result extends AppCompatActivity {
    Button btnPdf,btnSendToServer;

    PdfDocumentCreator pdfDocumentCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);
        intitializeUI();

        pdfDocumentCreator = getIntent().getParcelableExtra("pdfDocumentCreator");
        btnPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pdfDocumentCreator.generatePdfReport();
            }
        });


    }

    private void intitializeUI() {
        btnPdf=findViewById(R.id.btnGeneratePDf);
        btnSendToServer=findViewById(R.id.btnSendToServer);
    }
}