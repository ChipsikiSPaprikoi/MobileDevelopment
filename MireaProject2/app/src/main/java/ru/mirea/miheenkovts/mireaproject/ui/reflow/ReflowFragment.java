package ru.mirea.miheenkovts.mireaproject.ui.reflow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import ru.mirea.miheenkovts.mireaproject.databinding.FragmentReflowBinding;

import java.util.concurrent.TimeUnit;

public class ReflowFragment extends Fragment {

    private FragmentReflowBinding binding;
    private boolean isWorkRunning = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ReflowViewModel reflowViewModel = new ViewModelProvider(this).get(ReflowViewModel.class);

        binding = FragmentReflowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textReflow;
        reflowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding.btnStartWorker.setOnClickListener(v -> {
            isWorkRunning = true;
            reflowViewModel.setText("Worker запущен...");

            handler.postDelayed(() -> {
                OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ReflowWorker.class)
                        .setInitialDelay(5, TimeUnit.SECONDS)
                        .build();

                WorkManager.getInstance(requireContext())
                        .enqueueUniqueWork("reflow_work", ExistingWorkPolicy.REPLACE, workRequest);
            }, 2000);
        });

        binding.btnCancelWorker.setOnClickListener(v -> {
            WorkManager.getInstance(requireContext()).cancelUniqueWork("reflow_work");
            if (!isWorkRunning) {
                reflowViewModel.setText("Нет активного Worker для отмены");
            } else {
                reflowViewModel.setText("Отмена отправлена...");
                isWorkRunning = false;
            }
        });

        WorkManager.getInstance(requireContext())
                .getWorkInfosForUniqueWorkLiveData("reflow_work")
                .observe(getViewLifecycleOwner(), workInfos -> {
                    if (workInfos == null || workInfos.isEmpty()) {
                        isWorkRunning = false;
                        return;
                    }
                    isWorkRunning = true;

                    WorkInfo workInfo = workInfos.get(0);
                    WorkInfo.State state = workInfo.getState();

                    switch (state) {
                        case ENQUEUED:
                            reflowViewModel.setText("Worker в очереди (ждёт 5 сек)...");
                            break;
                        case RUNNING:
                            int progress = workInfo.getProgress().getInt(ReflowWorker.KEY_PROGRESS, 0);
                            reflowViewModel.setText("Выполняется: " + progress + "%");
                            break;
                        case SUCCEEDED:
                            String result = workInfo.getOutputData().getString(ReflowWorker.KEY_RESULT);
                            reflowViewModel.setText("Успех: " + (result != null ? result : "Завершено"));
                            break;
                        case FAILED:
                            reflowViewModel.setText("Worker провалился");
                            break;
                        case CANCELLED:
                            reflowViewModel.setText("Worker отменён пользователем!");
                            isWorkRunning = false;
                            break;
                    }
                });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacksAndMessages(null);
    }
}
