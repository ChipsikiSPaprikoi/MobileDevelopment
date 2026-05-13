package ru.mirea.miheenkovts.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEdit, passwordEdit;
    private Button registerBtn, loginBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEdit = findViewById(R.id.login_email);
        passwordEdit = findViewById(R.id.login_password);
        registerBtn = findViewById(R.id.login_register_button);
        loginBtn = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.login_progress);

// Если пользователь уже залогинен — сразу в MainActivity
        if (mAuth.getCurrentUser() != null) {
            startMainAndFinish();
            return;
        }

        registerBtn.setOnClickListener(v -> registerUser());
        loginBtn.setOnClickListener(v -> loginUser());
    }

    private void registerUser() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Введите email");
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordEdit.setError("Пароль минимум 6 символов");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                        startMainAndFinish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Ошибка регистрации: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginUser() {
        String email = emailEdit.getText().toString().trim();
        String password = passwordEdit.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailEdit.setError("Введите email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Введите пароль");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        startMainAndFinish();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Ошибка входа: " + (task.getException() != null ? task.getException().getMessage() : ""),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void startMainAndFinish() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
