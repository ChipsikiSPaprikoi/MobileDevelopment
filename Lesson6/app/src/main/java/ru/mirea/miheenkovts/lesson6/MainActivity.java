package ru.mirea.miheenkovts.lesson6;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText etGroup, etListNumber, etFavorite;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etGroup = findViewById(R.id.etGroup);
        etListNumber = findViewById(R.id.etListNumber);
        etFavorite = findViewById(R.id.etFavorite);
        Button btnSave = findViewById(R.id.btnSave);

        sharedPrefs = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);

        etGroup.setText(sharedPrefs.getString("group", ""));
        etListNumber.setText(sharedPrefs.getString("list_number", ""));
        etFavorite.setText(sharedPrefs.getString("favorite", ""));

        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString("group", etGroup.getText().toString());
            editor.putString("list_number", etListNumber.getText().toString());
            editor.putString("favorite", etFavorite.getText().toString());
            editor.apply();
        });
    }
}
