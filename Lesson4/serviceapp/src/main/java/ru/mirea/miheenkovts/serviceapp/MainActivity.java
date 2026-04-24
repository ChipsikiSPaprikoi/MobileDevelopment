package ru.mirea.miheenkovts.serviceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import ru.mirea.miheenkovts.serviceapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final String TAG = "MainActivity";

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    isGranted -> {
                        if (Boolean.TRUE.equals(isGranted.get(Manifest.permission.POST_NOTIFICATIONS))) {
                            Log.d(TAG, "Разрешения получены");
                            binding.statusText.setText("Разрешения получены");
                        } else {
                            Log.d(TAG, "Нет разрешений!");
                            binding.statusText.setText("Требуются разрешения!");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkPermissions();

        binding.playButton.setOnClickListener(v -> startService());
        binding.stopButton.setOnClickListener(v -> stopService());
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.POST_NOTIFICATIONS});
        } else {
            binding.statusText.setText("Разрешения получены");
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, PlayerService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        binding.statusText.setText("Воспроизведение запущено");
    }

    private void stopService() {
        stopService(new Intent(this, PlayerService.class));
        binding.statusText.setText("Воспроизведение остановлено");
    }
}
