package ru.mirea.miheenkovts.employeedb;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);

        AppDatabase db = App.getInstance().getDatabase();
        SuperHeroDao dao = db.superHeroDao();

        SuperHero[] heroes = {
                new SuperHero("Супермен", "Полёт, суперсила", 100),
                new SuperHero("Бэтмен", "Интеллект, гаджеты", 85),
                new SuperHero("Человек-паук", "Паутина, акробатика", 90),
                new SuperHero("Железный человек", "Броня, ИИ", 95)
        };

        for (SuperHero hero : heroes) {
            dao.insert(hero);
        }

        List<SuperHero> allHeroes = dao.getAll();
        StringBuilder sb = new StringBuilder("Супергерои в базе (" + allHeroes.size() + "):\n\n");
        for (SuperHero h : allHeroes) {
            sb.append(h.toString()).append("\n");
        }

        tvResult.setText(sb.toString());
    }
}
