package ru.mirea.miheenkovts.favoritebook;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ShareActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TextView devBookView = findViewById(R.id.textViewDevBook);
            String devBook = extras.getString(MainActivity.KEY);
            devBookView.setText("Любимая книга разработчика: " + devBook);
        }

        // Отправка данных по кнопке
        Button btnSend = findViewById(R.id.btnSendBook);
        EditText editText = findViewById(R.id.editTextUserBook);
        btnSend.setOnClickListener(v -> {
            Intent data = new Intent();
            data.putExtra(MainActivity.USER_MESSAGE, editText.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        });
    }
}
