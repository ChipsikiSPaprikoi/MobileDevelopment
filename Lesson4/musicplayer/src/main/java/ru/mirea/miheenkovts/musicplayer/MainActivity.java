package ru.mirea.miheenkovts.musicplayer;
import ru.mirea.miheenkovts.musicplayer.databinding.ActivityMainBinding;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final int DURATION_SEC = 210;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Настройка SeekBar и текстов времени
        binding.seekBar.setMax(DURATION_SEC);
        binding.textCurrentTime.setText(formatTime(0));
        binding.textTotalTime.setText(formatTime(DURATION_SEC));

        binding.buttonPlayPause.setOnClickListener(v -> {
            Log.d("MusicPlayer", "Play/Pause clicked");
        });

        binding.buttonPrev.setOnClickListener(v -> {
            Log.d("MusicPlayer", "Previous clicked");
        });

        binding.buttonNext.setOnClickListener(v -> {
            Log.d("MusicPlayer", "Next clicked");
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    binding.textCurrentTime.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }
}
