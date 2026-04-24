package ru.mirea.miheenkovts.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import ru.mirea.miheenkovts.looper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MyLooper myLooper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Handler mainHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                String result = msg.getData().getString("result", "Нет результата");
                Log.d(MainActivity.class.getSimpleName(), "Task execute. Result: " + result);
            }
        };

        myLooper = new MyLooper(mainHandler);
        myLooper.start();

        binding.buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ageStr = binding.editTextAge.getText().toString();
                String job = binding.editTextJob.getText().toString();

                if (TextUtils.isEmpty(ageStr) || TextUtils.isEmpty(job)) {
                    Log.d("MainActivity", "Поля не заполнены");
                    return;
                }

                Message msg = Message.obtain();
                Bundle bundle = new Bundle();
                bundle.putString("AGE", ageStr);
                bundle.putString("JOB", job);
                msg.setData(bundle);

                if (myLooper.mHandler != null) {
                    myLooper.mHandler.sendMessage(msg);
                } else {
                    Log.d("MainActivity", "Looper/Handler ещё не инициализирован");
                }
            }
        });
    }
}
