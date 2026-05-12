package ru.mirea.miheenkovts.securesharedpreferences;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class MainActivity extends AppCompatActivity {
    private TextView tvPoetName;
    private ImageView ivPoet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvPoetName = findViewById(R.id.tvPoetName);
        ivPoet = findViewById(R.id.ivPoet);

        String favoritePoet = "Александр Сергеевич Пушкин";

        try {
            String mainKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            android.content.SharedPreferences secureSharedPreferences =
                    EncryptedSharedPreferences.create(
                            "secret_shared_prefs",
                            mainKeyAlias,
                            getBaseContext(),
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    );

            secureSharedPreferences.edit().putString("poet_name", favoritePoet).apply();

            String result = secureSharedPreferences.getString("poet_name", "Неизвестно");
            tvPoetName.setText(result);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
