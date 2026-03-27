package ru.mirea.miheenkovts.activitylifecycle;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityLifecycle";

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editText = findViewById(R.id.editText);

        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState()");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState()");
    }
}
// 1. Будет ли вызван метод «onCreate» после нажатия на кнопку «Home» и возврата
//в приложение?
// Нет. При нажатии Home Activity переходит в состояние Stopped (onPause, onStop), но не уничтожается.
// При возврате вызываются onRestart(), onStart(), onResume(), а onCreate() — не вызывается.
// 2. Изменится ли значение поля «EditText» после нажатия на кнопку «Home» и
//возврата в приложение?
// Нет. Если Activity не была уничтожена системой,
// текст в EditText (у которого есть id) сохраняется автоматически и останется прежним при возврате из Home.
// 3. Изменится ли значение поля «EditText» после нажатия на кнопку «Back» и
//возврата в приложение?
// Да, обычно исчезнет. При нажатии Back в MainActivity вызывается onDestroy(), Activity уничтожается,
// и при новом запуске создаётся новая Activity через onCreate().
// Текст из предыдущего EditText в этом случае не сохраняется автоматически,
// если не используется onSaveInstanceState / БД / SharedPreferences.
