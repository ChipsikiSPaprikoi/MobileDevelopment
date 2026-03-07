package ru.mirea.miheenkovts.buttonclicker;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView textViewStudent;
    private CheckBox checkBox;

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

        textViewStudent = findViewById(R.id.tvOut);
        checkBox = findViewById(R.id.checkBox);
    }

    public void onButtonClick(View view) {
        if (view.getId() == R.id.WhoAmI) {
            textViewStudent.setText("Мой номер по списку № 16");
            checkBox.setChecked(true);
        } else if (view.getId() == R.id.ItIsNotMe) {
            textViewStudent.setText("Это не я сделал");
            checkBox.setChecked(false);
        }
    }
}
