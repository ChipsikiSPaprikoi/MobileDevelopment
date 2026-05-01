package ru.mirea.miheenkovts.mireaproject.ui.slideshow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ru.mirea.miheenkovts.mireaproject.databinding.FragmentSlideshowBinding;

public class SlideshowFragment extends Fragment implements SensorEventListener {

    private FragmentSlideshowBinding binding;
    private SlideshowViewModel slideshowViewModel;

    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private Sensor sensorMagnetometer;
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float azimuth = 0f;
    private ImageView ivCompass;

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.textSlideshow;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        ivCompass = binding.ivCompass;
        binding.tvDirection.setText("Направление: ---");
        binding.tvAzimuth.setText("Азимут: 0°");

        binding.btnRequestPermissions.setOnClickListener(v -> requestPermissions());

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        return root;
    }

    private void requestPermissions() {
        if (hasPermissions()) {
            slideshowViewModel.setText("Разрешения уже есть. Датчики активны.");
            startSensors();
            return;
        }

        ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, LOCATION_PERMISSION_REQUEST);
    }

    private boolean hasPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void startSensors() {
        slideshowViewModel.setText("Компас активен. Поворачивайте устройство!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (hasPermissions()) {
                startSensors();
                Toast.makeText(requireContext(), "Разрешения получены", Toast.LENGTH_SHORT).show();
            } else {
                slideshowViewModel.setText("Разрешения нужны для компаса");
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
        }

        float[] R = new float[9];
        float[] I = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
        if (success) {
            float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimuth = (float) Math.toDegrees(orientation[0]);
            if (azimuth < 0) azimuth += 360;

            updateCompass();
        }
    }

    private void updateCompass() {
        binding.tvAzimuth.setText(String.format("Азимут: %.0f°", azimuth));

        String direction = getDirection(azimuth);
        binding.tvDirection.setText("Направление: " + direction);

        // Анимация стрелки
        RotateAnimation animation = new RotateAnimation(
                0, -azimuth,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(250);
        animation.setRepeatCount(0);
        animation.setFillAfter(true);
        ivCompass.startAnimation(animation);
    }

    private String getDirection(float azimuth) {
        if (azimuth >= 337.5 || azimuth < 22.5) return "Север";
        if (azimuth < 67.5) return "Северо-Восток";
        if (azimuth < 112.5) return "Восток";
        if (azimuth < 157.5) return "Юго-Восток";
        if (azimuth < 202.5) return "Юг";
        if (azimuth < 247.5) return "Юго-Запад";
        if (azimuth < 292.5) return "Запад";
        return "Северо-Запад";
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onResume() {
        super.onResume();
        if (hasPermissions()) {
            sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sensorMagnetometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sensorManager.unregisterListener(this);
        binding = null;
    }
}
