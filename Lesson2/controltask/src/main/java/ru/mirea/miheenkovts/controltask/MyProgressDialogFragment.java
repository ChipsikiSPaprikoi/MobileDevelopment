package ru.mirea.miheenkovts.controltask;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(64, 64, 64, 64);
        layout.setGravity(android.view.Gravity.CENTER);

        ProgressBar progressBar = new ProgressBar(getActivity());
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, 0, 32);

        TextView textView = new TextView(getActivity());
        textView.setText("Загрузка данных МИРЭА...");
        textView.setTextSize(18);
        textView.setTextColor(0xFF000000);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setPadding(32, 0, 32, 0);

        layout.addView(progressBar);
        layout.addView(textView);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setView(layout)
                .setCancelable(false);

        final AlertDialog dialog = builder.create();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 3000);

        return dialog;
    }
}
