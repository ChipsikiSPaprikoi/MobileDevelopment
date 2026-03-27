package ru.mirea.miheenkovts.toastapp;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ToastApp";
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        Log.i(TAG, "onCreate()");
    }

    public void onClickCountChars(View view) {
        String text = editText.getText().toString();
        int charCount = text.length();

        String message = String.format("СТУДЕНТ № 16, ГРУППА БСБО-08-23, Количество символов - %d", charCount);

        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);

        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}
