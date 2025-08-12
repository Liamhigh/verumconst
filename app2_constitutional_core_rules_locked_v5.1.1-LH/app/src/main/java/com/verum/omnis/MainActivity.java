package com.verum.omnis;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.verum.omnis.core.AnalysisEngine;
import com.verum.omnis.core.HashUtil;
import com.verum.omnis.core.PDFSealer;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button verifyBtn = findViewById(R.id.verifyBtn);
        Button pdfBtn = findViewById(R.id.pdfBtn);
        ImageView logo = findViewById(R.id.logo);

        verifyBtn.setOnClickListener(v -> {
            // Demo: verify a placeholder file (self)
            try {
                File placeholder = new File(getFilesDir(), "placeholder.txt");
                if (!placeholder.exists()) {
                    FileOutputStream fos = new FileOutputStream(placeholder);
                    fos.write("Verum Omnis v5.2.6 demo".getBytes());
                    fos.close();
                }
                AnalysisEngine.ForensicReport report = AnalysisEngine.analyze(this, placeholder);
                Toast.makeText(this, "Risk: " + report.riskScore + "  Jurisdiction: " + report.jurisdiction, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "Verify error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        pdfBtn.setOnClickListener(v -> {
            try {
                // Generate sealed PDF with watermark, QR, and SHA-512
                File out = new File(getExternalFilesDir(null), "verum_report.pdf");
                PDFSealer.SealRequest req = new PDFSealer.SealRequest();
                req.title = "Verum Omnis Forensic Report";
                req.summary = "Local processing complete. No data stored. v5.2.6";
                req.includeQr = true;
                req.includeHash = true;
                PDFSealer.generateSealedPdf(this, req, out);
                Toast.makeText(this, "PDF saved: " + out.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(this, "PDF error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}