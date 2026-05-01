package ru.mirea.miheenkovts.lesson5;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.mirea.miheenkovts.lesson5.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        ListView listSensor = binding.sensorListView;

        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<>();

        for (int i = 0; i < sensors.size(); i++) {
            HashMap<String, Object> sensorMap = new HashMap<>();
            sensorMap.put("Name", sensors.get(i).getName());
            sensorMap.put("Value", String.valueOf(sensors.get(i).getMaximumRange()));
            arrayList.add(sensorMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(
                this,
                arrayList,
                android.R.layout.simple_list_item_2,
                new String[]{"Name", "Value"},
                new int[]{android.R.id.text1, android.R.id.text2}
        );

        listSensor.setAdapter(adapter);
    }
}
