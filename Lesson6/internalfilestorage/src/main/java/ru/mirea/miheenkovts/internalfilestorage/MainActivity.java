package ru.mirea.miheenkovts.internalfilestorage;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String FILENAME = "mirea.txt";
    private EditText etText;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etText = findViewById(R.id.etText);
        tvResult = findViewById(R.id.tvResult);
        Button btnSaveRead = findViewById(R.id.btnSaveRead);

        etText.setText("9 мая 1945 - День Великой Победы в ВОВ");

        btnSaveRead.setOnClickListener(v -> {
            new Thread(() -> {
                writeToFile(etText.getText().toString());
                String readText = getTextFromFile();
                runOnUiThread(() -> {
                    tvResult.setText(readText);
                    Toast.makeText(MainActivity.this, "Файл записан и прочитан", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
    }

    private void writeToFile(String data) {
        FileOutputStream outputStream = null;
        try {
            outputStream = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, e.toString());
            }
        }
    }

    private String getTextFromFile() {
        FileInputStream fin = null;
        try {
            fin = openFileInput(FILENAME);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            Log.d(LOG_TAG, text);
            return text;
        } catch (IOException ex) {
            Log.d(LOG_TAG, ex.toString());
            return "Ошибка чтения";
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException ex) {
                Log.d(LOG_TAG, ex.toString());
            }
        }
    }
}
