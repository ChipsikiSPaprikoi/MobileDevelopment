package ru.mirea.miheenkovts.workmanager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import ru.mirea.miheenkovts.workmanager.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private WorkManager workManager;
    private OneTimeWorkRequest currentWorkRequest;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        workManager = WorkManager.getInstance(this);
        binding.startButton.setOnClickListener(v -> startUploadWorker());
    }

    private void startUploadWorker() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .setRequiresCharging(true)
                .build();

        currentWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setConstraints(constraints)
                .build();

        workManager.enqueue(currentWorkRequest);
        observeCurrentWork();

        binding.statusText.setText("Worker запланирован (WiFi + зарядка)");
        Toast.makeText(this, "Worker запущен!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Worker запланирован с constraints");
    }

    private void observeCurrentWork() {
        if (currentWorkRequest != null) {
            workManager.getWorkInfoByIdLiveData(currentWorkRequest.getId())
                    .observe(this, workInfo -> {
                        if (workInfo != null) {
                            WorkInfo.State state = workInfo.getState();
                            binding.statusText.setText("Статус: " + state.name());
                            Log.d(TAG, "WorkManager: " + state.name());

                            switch (state) {
                                case ENQUEUED:
                                    Log.d(TAG, "Ждёт WiFi + зарядку");
                                    break;
                                case RUNNING:
                                    Log.d(TAG, "Выполняется (10 сек)");
                                    break;
                                case SUCCEEDED:
                                    Log.d(TAG, "Успех!");
                                    binding.statusText.setText("Загрузка завершена!");
                                    break;
                                case FAILED:
                                    Log.d(TAG, "Ошибка!");
                                    binding.statusText.setText("Ошибка загрузки");
                                    break;
                                case CANCELLED:
                                    Log.d(TAG, "Отменено");
                                    break;
                            }
                        }
                    });
        }
    }
}
