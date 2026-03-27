package ru.mirea.miheenkovts.controltask;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ControlTask";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate()");
    }

    public void onClickTimePicker(View view) {
        new MyTimeDialogFragment().show(getSupportFragmentManager(), "timePicker");
    }

    public void onClickDatePicker(View view) {
        new MyDateDialogFragment().show(getSupportFragmentManager(), "datePicker");
    }

    public void onClickProgress(View view) {
        new MyProgressDialogFragment().show(getSupportFragmentManager(), "progress");
    }

    public void onClickSnackbar(View view) {
        Snackbar.make(view, "Самостоятельная работа выполнена. Все диалоги работают!",
                        Snackbar.LENGTH_LONG)
                .setAction("OK", v -> Toast.makeText(this, "Snackbar работает!", Toast.LENGTH_SHORT).show())
                .show();
    }

    // Обработчики для диалогов (вызываются из DialogFragment)
    public void onTimeSelected(int hour, int minute) {
        Toast.makeText(this, String.format("Выбрано время: %02d:%02d", hour, minute),
                Toast.LENGTH_LONG).show();
    }

    public void onDateSelected(int year, int month, int day) {
        Toast.makeText(this, String.format("Выбрана дата: %d-%02d-%02d", year, month + 1, day),
                Toast.LENGTH_LONG).show();
    }
}
