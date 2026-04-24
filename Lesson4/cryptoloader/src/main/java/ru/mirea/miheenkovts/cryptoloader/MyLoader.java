package ru.mirea.miheenkovts.cryptoloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public class MyLoader extends AsyncTaskLoader<String> {

    private Bundle args;

    public static final String ARG_CRYPT_TEXT = "cryptText";
    public static final String ARG_KEY = "key";
    public static final String ARG_PLAIN_TEXT = "plainText";

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.args = args;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        if (args == null) {
            Log.e("MyLoader", "args == null");
            return "ERROR: args == null";
        }

        byte[] cryptText = args.getByteArray(ARG_CRYPT_TEXT);
        byte[] keyBytes = args.getByteArray(ARG_KEY);

        if (cryptText == null || keyBytes == null) {
            Log.e("MyLoader", "cryptText или key == null");
            return "ERROR: cryptText или key == null";
        }

        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");

        try {
            String decrypted = MainActivity.decryptMsg(cryptText, key);

            SystemClock.sleep(5000);

            return "Дешифровано: " + decrypted;

        } catch (Exception e) {
            Log.e("MyLoader", "Decryption error", e);
            return "ERROR: " + e.getMessage();
        }
    }
}
