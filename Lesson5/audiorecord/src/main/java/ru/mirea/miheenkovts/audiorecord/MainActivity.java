package ru.mirea.miheenkovts.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;

import ru.mirea.miheenkovts.audiorecord.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 200;
    private static final String TAG = "AudioRecord";

    private ActivityMainBinding binding;
    private boolean isWork = false;
    private String recordFilePath;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private boolean isStartRecording = true;
    private boolean isStartPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recordFilePath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),
                "audiorecordtest.3gp").getAbsolutePath();

        checkPermissions();
        setupButtons();
    }

    private void checkPermissions() {
        int audioPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (audioPermission == PackageManager.PERMISSION_GRANTED &&
                storagePermission == PackageManager.PERMISSION_GRANTED) {
            isWork = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, REQUEST_CODE_PERMISSION);
        }
    }

    private void setupButtons() {
        // Кнопка записи
        binding.recordButton.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(this, "Нет разрешений!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isStartRecording) {
                startRecording();
                binding.recordButton.setText("Остановить запись");
                binding.playButton.setEnabled(false);
            } else {
                stopRecording();
                binding.recordButton.setText("Начать запись. №16 студента, группа БСБО-08-23");
                binding.playButton.setEnabled(true);
            }
            isStartRecording = !isStartRecording;
        });

        binding.playButton.setOnClickListener(v -> {
            if (!isWork) {
                Toast.makeText(this, "Нет разрешений!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isStartPlaying) {
                startPlaying();
                binding.playButton.setText("Остановить воспроизведение");
                binding.recordButton.setEnabled(false);
            } else {
                stopPlaying();
                binding.playButton.setText("Воспроизвести");
                binding.recordButton.setEnabled(true);
            }
            isStartPlaying = !isStartPlaying;
        });

        binding.playButton.setEnabled(false);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(recordFilePath);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            Log.d(TAG, "Запись начата");
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
            Toast.makeText(this, "Ошибка записи!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Log.d(TAG, "Запись остановлена: " + recordFilePath);
            Toast.makeText(this, "Запись сохранена!", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(recordFilePath);
            player.prepare();
            player.setOnCompletionListener(mp -> {
                stopPlaying();
                resetPlayButton();
                Log.d(TAG, "Воспроизведение завершено автоматически");
            });
            player.start();
            Log.d(TAG, "Воспроизведение начато");
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed", e);
            Toast.makeText(this, "Ошибка воспроизведения!", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
            Log.d(TAG, "Воспроизведение остановлено");
        }
    }

    private void resetPlayButton() {
        runOnUiThread(() -> {
            binding.playButton.setText("Воспроизвести");
            binding.playButton.setEnabled(true);
            binding.recordButton.setEnabled(true);
            isStartPlaying = true;
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            isWork = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!isWork) {
                Toast.makeText(this, "Разрешения нужны для работы!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRecording();
        stopPlaying();
    }
}
