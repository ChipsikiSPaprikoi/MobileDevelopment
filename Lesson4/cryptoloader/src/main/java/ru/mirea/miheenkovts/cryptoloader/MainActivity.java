package ru.mirea.miheenkovts.cryptoloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

import ru.mirea.miheenkovts.cryptoloader.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_ID = 1234;

    private ActivityMainBinding binding;

    public static final String ARG_CRYPT_TEXT = "cryptText";
    public static final String ARG_KEY = "key";
    public static final String ARG_PLAIN_TEXT = "plainText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String plaintext = binding.editTextInput.getText().toString();
                if (plaintext.isEmpty()) {
                    return;
                }

                try {
                    SecretKey key = generateKey();

                    byte[] encrypted = encryptMsg(plaintext, key);

                    Bundle bundle = new Bundle();
                    bundle.putByteArray(ARG_CRYPT_TEXT, encrypted);
                    bundle.putByteArray(ARG_KEY, key.getEncoded());
                    bundle.putString(ARG_PLAIN_TEXT, plaintext); // для отладки

                    LoaderManager.getInstance(MainActivity.this).initLoader(LOADER_ID, bundle, MainActivity.this);

                } catch (Exception e) {
                    Log.e(TAG, "Encryption error", e);
                }
            }
        });
    }

    @SuppressLint("TrulyRandom")
    public static SecretKey generateKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());

            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256, sr);

            return new SecretKeySpec(kg.generateKey().getEncoded(), "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptMsg(String message, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptMsg(byte[] cipherText, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            return new String(cipher.doFinal(cipherText));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            Log.d(TAG, "onCreateLoader: " + id);
            return new MyLoader(this, args);
        }
        throw new IllegalArgumentException("Invalid loader id");
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String decrypted) {
        if (loader.getId() == LOADER_ID) {
            Log.d(TAG, "onLoadFinished: " + decrypted);
            android.widget.Toast.makeText(this, "Расшифровано: " + decrypted, android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        Log.d(TAG, "onLoaderReset");
    }
}
