package ru.mirea.miheenkovts.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

import ru.mirea.miheenkovts.mireaproject.R;
import ru.mirea.miheenkovts.mireaproject.databinding.FragmentAudioRecorderBinding;

public class AudioRecorder extends Fragment {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private FragmentAudioRecorderBinding binding;

    private Button btnRecord, btnPlay;
    private TextView tvStatus;
    private MediaRecorder recorder;
    private MediaPlayer player;
    private File audioFile;
    private boolean isRecording = false;

    // Разрешения
    private final String[] permissions = { Manifest.permission.RECORD_AUDIO };
    private boolean permissionToRecordAccepted = false;

    public AudioRecorder() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAudioRecorderBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnRecord = binding.btnRecord;
        btnPlay = binding.btnPlay;
        tvStatus = binding.tvStatus;

        permissionToRecordAccepted = ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!permissionToRecordAccepted) {
            requestPermissions(permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        } else {
            setupButtons();
        }

        return root;
    }

    private void setupButtons() {
        btnRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
            } else {
                startRecording();
            }
        });

        btnPlay.setOnClickListener(v -> {
            if (audioFile == null || !audioFile.exists()) {
                tvStatus.setText("Файл записи не найден");
                return;
            }
            startPlaying();
        });
    }

    private void startRecording() {
        File audioDir = new File(requireContext().getFilesDir(), "audio_recordings");
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());
        audioFile = new File(audioDir, "audio_" + timeStamp + ".3gp");

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audioFile.getAbsolutePath());

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            btnRecord.setText("Остановить");
            tvStatus.setText("Идёт запись...");
        } catch (IOException e) {
            tvStatus.setText("Ошибка записи: " + e.getMessage());
            stopRecording();
        }
    }

    private void stopRecording() {
        if (recorder == null) return;

        try {
            recorder.stop();
            recorder.release();
        } catch (Exception e) {
            // ignore
        } finally {
            recorder = null;
        }

        isRecording = false;
        btnRecord.setText("Записать");
        tvStatus.setText("Запись сохранена");
        btnPlay.setEnabled(true);
    }

    private void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(audioFile.getAbsolutePath());
            player.prepare();
            player.start();
            tvStatus.setText("Воспроизведение...");
            player.setOnCompletionListener(mp -> {
                tvStatus.setText("Воспроизведение завершено");
            });
        } catch (IOException e) {
            tvStatus.setText("Ошибка воспроизведения");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            stopRecording();
        }
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            if (permissionToRecordAccepted) {
                setupButtons();
                tvStatus.setText("Микрофон разрешён");
            } else {
                tvStatus.setText("Разрешите доступ к микрофону");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}