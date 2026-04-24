package ru.mirea.miheenkovts.mireaproject.ui.reflow;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ReflowWorker extends Worker {
    private static final String TAG = "ReflowWorker";
    public static final String KEY_RESULT = "result_key";
    public static final String KEY_PROGRESS = "progress_key";

    public ReflowWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int totalSteps = 10;
        for (int i = 1; i <= totalSteps; i++) {
            if (isStopped()) {
                Log.d(TAG, "Worker отменён на шаге " + i);
                return Result.failure();
            }

            try {
                Thread.sleep(1000);
                Log.d(TAG, "Шаг " + i + "/" + totalSteps + " выполнен");

                Data progressData = new Data.Builder()
                        .putInt(KEY_PROGRESS, i * 10)
                        .build();
                setProgressAsync(progressData);

            } catch (InterruptedException e) {
                Log.e(TAG, "Прервано", e);
                return Result.failure();
            }
        }

        Data outputData = new Data.Builder()
                .putString(KEY_RESULT, "Все 10 шагов завершены!")
                .build();

        return Result.success(outputData);
    }
}
