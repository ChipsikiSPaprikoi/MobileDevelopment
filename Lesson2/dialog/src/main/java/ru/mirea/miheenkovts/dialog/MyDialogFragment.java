package ru.mirea.miheenkovts.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle("Здравствуй МИРЭА!")
                .setMessage("Успех близок?")
                .setIcon(R.mipmap.ic_launcher_round)
                .setPositiveButton("Иду дальше", (dialog, id) -> {
                    ((MainActivity) requireActivity()).onOkClicked();
                    dialog.cancel();
                })
                .setNeutralButton("На паузе", (dialog, id) -> {
                    ((MainActivity) requireActivity()).onNeutralClicked();
                    dialog.cancel();
                })
                .setNegativeButton("Нет", (dialog, id) -> {
                    ((MainActivity) requireActivity()).onCancelClicked();
                    dialog.cancel();
                });

        return builder.create();
    }
}
