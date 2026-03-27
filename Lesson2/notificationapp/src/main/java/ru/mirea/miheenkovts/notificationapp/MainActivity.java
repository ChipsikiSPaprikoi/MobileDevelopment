package ru.mirea.miheenkovts.notificationapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NotificationApp";
    private static final String CHANNEL_ID = "com.mirea.asd.notification.ANDROID";
    private static final int PERMISSION_CODE = 200;

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Log.d(TAG, "Разрешения получены");
                        Toast.makeText(this, "Разрешения получены", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Нет разрешений!");
                        Toast.makeText(this, "Разрешения не получены", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        checkNotificationPermission();

        Log.i(TAG, "onCreate()");
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Нет разрешений!");
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            } else {
                Log.d(TAG, "Разрешения получены");
            }
        }
    }

    public void onClickNewMessageNotification(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Нет разрешения на уведомления!", Toast.LENGTH_SHORT).show();
            return;
        }

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("MIREA")
                .setContentText("Поздравляем!")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("MIREA - Михеенков Тимофей Сергеевич. Уведомление успешно создано!"));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());

        Toast.makeText(this, "Уведомление отправлено!", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelCompat channel = new NotificationChannelCompat.Builder(CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_DEFAULT)
                    .setName("MIREA Notification")
                    .setDescription("Уведомления студента")
                    .build();

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
