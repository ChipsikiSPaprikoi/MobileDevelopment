package ru.mirea.miheenkovts.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.concurrent.TimeUnit;

public class UploadWorker extends Worker {
    static final String TAG = "UploadWorker";

    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork: START - Загрузка данных...");

        try {
            // Имитация долгой работы (10 сек)
            TimeUnit.SECONDS.sleep(10);
            Log.d(TAG, "doWork: SUCCESS - Загрузка завершена!");
            return Result.success();
        } catch (InterruptedException e) {
            Log.e(TAG, "doWork: ERROR - Прервано", e);
            return Result.retry();  // Повторить попытку
        } catch (Exception e) {
            Log.e(TAG, "doWork: FAILURE", e);
            return Result.failure();
        }
    }
}
