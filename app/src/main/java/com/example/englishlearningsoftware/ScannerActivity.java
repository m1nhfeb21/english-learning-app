package com.example.englishlearningsoftware;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Camera preview with on-device English object labels. */
public class ScannerActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 100;

    private PreviewView cameraPreview;
    private TextView tvResult;
    private ExecutorService cameraExecutor;
    private ImageLabeler labeler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        cameraPreview = findViewById(R.id.cameraPreview);
        tvResult = findViewById(R.id.tvResult);
        cameraExecutor = Executors.newSingleThreadExecutor();
        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> providerFuture =
                ProcessCameraProvider.getInstance(this);
        providerFuture.addListener(() -> {
            try {
                ProcessCameraProvider provider = providerFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

                ImageAnalysis analysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                analysis.setAnalyzer(cameraExecutor, this::analyzeImage);

                provider.unbindAll();
                provider.bindToLifecycle(
                        this,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis);
            } catch (Exception error) {
                showMessage("Cannot start camera");
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void analyzeImage(ImageProxy imageProxy) {
        if (imageProxy.getImage() == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                imageProxy.getImage(),
                imageProxy.getImageInfo().getRotationDegrees());

        labeler.process(image)
                .addOnSuccessListener(this::showBestLabel)
                .addOnFailureListener(error -> showMessage("No object detected"))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void showBestLabel(List<ImageLabel> labels) {
        if (labels.isEmpty()) {
            showMessage("No object detected");
            return;
        }

        ImageLabel best = labels.get(0);
        String result = best.getText()
                + "\nConfidence: "
                + Math.round(best.getConfidence() * 100)
                + "%";
        runOnUiThread(() -> tvResult.setText(result));
    }

    private void showMessage(String message) {
        runOnUiThread(() -> tvResult.setText(message));
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        if (labeler != null) {
            labeler.close();
        }
        super.onDestroy();
    }
}
