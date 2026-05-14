package ru.mirea.miheenkovts.yandexdriver;

import android.app.Application;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;

public class App extends Application {
    private final String MAPKIT_API_KEY = "0f5bd71e-444e-40ec-997c-35d1d361dfd2";

    @Override
    public void onCreate() {
        super.onCreate();
        MapKitFactory.setApiKey(MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
    }
}
