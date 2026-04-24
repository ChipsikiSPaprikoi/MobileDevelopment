package ru.mirea.miheenkovts.thread;

import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.miheenkovts.thread.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Thread mainThread = Thread.currentThread();
        binding.textInfo.setText("Имя текущего потока: " + mainThread.getName());

        mainThread.setName("МОЙ НОМЕР ГРУППЫ: БСБО-08-23, НОМЕР ПО СПИСКУ: 16, МОЙ ЛЮБИМЫЙ ФИЛЬМ: Человек-паук");

        binding.textInfo.append("\nНовое имя потока: " + mainThread.getName());
        binding.textInfo.append("\nГруппа потока: " + mainThread.getThreadGroup());

        Log.d("MainActivity", "Stack: " + java.util.Arrays.toString(mainThread.getStackTrace()));

        binding.buttonMirea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NonNull View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int numberThread = counter++;

                        Log.d("ThreadProject", String.format(
                                "Запущен поток № %d студентом группы БСБО-08-23, номер по списку 16",
                                numberThread));

                        String lessonsStr = binding.editLessons.getText().toString();
                        String daysStr = binding.editDays.getText().toString();

                        if (TextUtils.isEmpty(lessonsStr) || TextUtils.isEmpty(daysStr)) {
                            Log.d("ThreadProject", "Пустой ввод");
                            return;
                        }

                        int totalLessons = Integer.parseInt(lessonsStr);
                        int days = Integer.parseInt(daysStr);

                        float avgPerDay = days > 0 ? (float) totalLessons / days : 0;

                        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

                        long endTime = System.currentTimeMillis() + 20 * 1000;
                        while (System.currentTimeMillis() < endTime) {
                            synchronized (this) {
                                try {
                                    wait(endTime - System.currentTimeMillis());
                                } catch (InterruptedException e) {
                                    Log.w("ThreadProject", "Поток № " + numberThread + " прерван.");
                                    return;
                                }
                            }
                        }

                        Log.d("ThreadProject", "Выполнен поток № " + numberThread + ". Среднее: " + avgPerDay);

                        runOnUiThread(() -> {
                            binding.textViewMirea.setText("Среднее количество пар в день: " + String.format("%.2f", avgPerDay));
                        });
                    }
                }).start();
            }
        });
    }
}
