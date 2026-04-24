package ru.mirea.miheenkovts.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {

    public Handler mHandler;
    private final Handler mainHandler;

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d("MyLooper", "run");

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String ageStr = bundle.getString("AGE", "0");
                String job = bundle.getString("JOB", "Unknown");

                int age = Integer.parseInt(ageStr);
                long delay = age * 1000L;

                Log.d("MyLooper get message: ", "AGE = " + age + ", JOB = " + job);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Log.d("MyLooper", "Sleep interrupted.");
                    return;
                }

                Message reply = new Message();
                Bundle replyBundle = new Bundle();
                replyBundle.putString("result", String.format("Возраст: %d, работа: %s, задержка = %d сек.", age, job, age));
                reply.setData(replyBundle);

                mainHandler.sendMessage(reply);
            }
        };

        Looper.loop();
    }
}
