package ru.mirea.miheenkovts.notebook;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.database.Cursor;
import android.provider.MediaStore;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Notebook";
    private EditText etFilename, etQuote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etFilename = findViewById(R.id.etFilename);
        etQuote = findViewById(R.id.etQuote);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnLoad = findViewById(R.id.btnLoad);

        etFilename.setText("quote1.txt");
        etQuote.setText("\"Быть или не быть?\" - У. Шекспир");

        btnSave.setOnClickListener(v -> saveFile());
        btnLoad.setOnClickListener(v -> loadFile());
    }

    private void saveFile() {
        String filename = etFilename.getText().toString().trim();
        String content = etQuote.getText().toString();

        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите название файла", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/");

        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);

        if (uri != null) {
            try (OutputStream out = resolver.openOutputStream(uri)) {
                out.write(content.getBytes(StandardCharsets.UTF_8));
                out.flush();
                Toast.makeText(this, "Сохранено: " + filename, Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Сохранено: " + uri);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Ошибка сохранения", e);
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Не удалось создать файл", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFile() {
        String filename = etFilename.getText().toString().trim();
        if (filename.isEmpty()) {
            Toast.makeText(this, "Введите название файла", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentResolver resolver = getContentResolver();
        String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DISPLAY_NAME};
        String selection = MediaStore.MediaColumns.DISPLAY_NAME + " = ?";
        String[] selectionArgs = {filename};

        try (Cursor cursor = resolver.query(
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                selectionArgs,
                null)) {

            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                Uri uri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);

                try (java.io.InputStream inputStream = resolver.openInputStream(uri);
                     BufferedReader reader = new BufferedReader(
                             new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    etQuote.setText(content.toString().trim());
                    Toast.makeText(this, "Загружено: " + filename, Toast.LENGTH_LONG).show();
                    Log.d(LOG_TAG, "Загружено: " + content);
                }
            } else {
                Toast.makeText(this, "Файл не найден: " + filename, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Ошибка загрузки", e);
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
