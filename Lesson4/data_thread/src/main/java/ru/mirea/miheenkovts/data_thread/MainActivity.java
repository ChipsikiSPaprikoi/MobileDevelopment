package ru.mirea.miheenkovts.data_thread;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.TimeUnit;

import ru.mirea.miheenkovts.data_thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private void appendLine(String text) {
        String old = binding.tvInfo.getText().toString();
        binding.tvInfo.setText(old + "\n" + text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        appendLine("Старт onCreate, поток: " + Thread.currentThread().getName());

        final Runnable runn1 = new Runnable() {
            @Override
            public void run() {
                appendLine("runn1 (runOnUiThread)");
            }
        };

        final Runnable runn2 = new Runnable() {
            @Override
            public void run() {
                appendLine("runn2 (tvInfo.post)");
            }
        };

        final Runnable runn3 = new Runnable() {
            @Override
            public void run() {
                appendLine("runn3 (tvInfo.postDelayed)");
            }
        };

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    appendFromBg("Фоновый поток стартовал: " + Thread.currentThread().getName());

                    TimeUnit.SECONDS.sleep(2);
                    appendFromBg("Вызываем runOnUiThread(runn1)");
                    runOnUiThread(runn1);

                    TimeUnit.SECONDS.sleep(1);
                    appendFromBg("Вызываем tvInfo.post(runn2)");
                    binding.tvInfo.post(runn2);

                    appendFromBg("Вызываем tvInfo.postDelayed(runn3, 2000)");
                    binding.tvInfo.postDelayed(runn3, 2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void appendFromBg(String text) {
        Log.d("DataThread", text);
        runOnUiThread(() -> appendLine("[BG] " + text));
    }
}
