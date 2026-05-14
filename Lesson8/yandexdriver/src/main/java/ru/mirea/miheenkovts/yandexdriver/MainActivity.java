package ru.mirea.miheenkovts.yandexdriver;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.directions.DirectionsFactory;
import com.yandex.mapkit.directions.driving.DrivingOptions;
import com.yandex.mapkit.directions.driving.DrivingRoute;
import com.yandex.mapkit.directions.driving.DrivingRouter;
import com.yandex.mapkit.directions.driving.DrivingSession;
import com.yandex.mapkit.directions.driving.VehicleOptions;
import com.yandex.mapkit.directions.driving.DrivingRouterType;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.map.PolylineMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.RequestPoint;
import com.yandex.mapkit.RequestPointType;
import com.yandex.runtime.Error;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.network.NetworkError;
import com.yandex.runtime.network.RemoteError;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DrivingSession.DrivingRouteListener {

    private static final int REQ_LOCATION_PERMS = 2001;

    private MapView mapView;
    private MapObjectCollection mapObjects;
    private DrivingRouter drivingRouter;
    private DrivingSession drivingSession;

    private final Point TARGET_POINT = new Point(55.669939, 37.480439);
    private final Point STROMYNKA = new Point(55.794216, 37.699683);
    private final Point VERNADSKOGO = new Point(55.669939, 37.480439);
    private final int[] colors = {0xFFFF0000, 0xFF00FF00, 0xFF00FFFF, 0xFF0000FF};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapview);
        mapView.getMap().setRotateGesturesEnabled(false);

        mapView.getMap().move(
                new CameraPosition(TARGET_POINT, 11.0f, 0.0f, 0.0f),
                new Animation(Animation.Type.SMOOTH, 0),
                null
        );

        mapObjects = mapView.getMap().getMapObjects().addCollection();
        drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED);

        checkPermissions();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQ_LOCATION_PERMS);
        } else {
            startRouting();
        }
    }

    private void startRouting() {
        Point userPoint = getUserLocation();

        if (userPoint == null || Math.abs(userPoint.getLatitude()) < 0.1) {
            userPoint = STROMYNKA;
            Toast.makeText(this, "Используется точка старта: Стромынка 20", Toast.LENGTH_SHORT).show();
        }

        String debugMsg = String.format("Маршрут: %.2f -> %.2f", userPoint.getLatitude(), VERNADSKOGO.getLatitude());
        Toast.makeText(this, debugMsg, Toast.LENGTH_SHORT).show();

        mapObjects.clear();
        addMarker(VERNADSKOGO);
        submitRequest(userPoint, VERNADSKOGO);
    }

    private Point getUserLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (loc != null) return new Point(loc.getLatitude(), loc.getLongitude());
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    private void addMarker(Point point) {
        PlacemarkMapObject marker = mapObjects.addPlacemark(point,
                ImageProvider.fromResource(this, com.yandex.maps.mobile.R.drawable.search_layer_pin_icon_default));


        marker.addTapListener((mapObject, p) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Любимое заведение")
                    .setMessage("Адрес: пр-т Вернадского, 78\nУниверситет МИРЭА")
                    .setPositiveButton("ОК", null)
                    .show();
            return true;
        });
    }

    private void submitRequest(Point start, Point end) {
        if (drivingSession != null) drivingSession.cancel();

        DrivingOptions drivingOptions = new DrivingOptions();
        drivingOptions.setRoutesCount(1);

        VehicleOptions vehicleOptions = new VehicleOptions();

        List<RequestPoint> requestPoints = new ArrayList<>();
        requestPoints.add(new RequestPoint(start, RequestPointType.WAYPOINT, null, null));
        requestPoints.add(new RequestPoint(end, RequestPointType.WAYPOINT, null, null));

        drivingSession = drivingRouter.requestRoutes(requestPoints, drivingOptions, vehicleOptions, this);
    }

    @Override
    public void onDrivingRoutes(@NonNull List<DrivingRoute> routes) {
        if (!routes.isEmpty()) {
            PolylineMapObject polyline = mapObjects.addPolyline(routes.get(0).getGeometry());
            polyline.setStrokeColor(0xFF0000FF);
            polyline.setStrokeWidth(5f);

            mapView.getMap().move(
                    new CameraPosition(new Point(55.73, 37.59), 10.0f, 0.0f, 0.0f),
                    new Animation(Animation.Type.SMOOTH, 1),
                    null
            );
        } else {
            Toast.makeText(this, "Сервер вернул пустой список путей", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDrivingRoutesError(@NonNull Error error) {
        String message = "Ошибка навигации";
        if (error instanceof RemoteError) message = "Ошибка на сервере";
        if (error instanceof NetworkError) message = "Проблема с сетью";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_LOCATION_PERMS && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRouting();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        if (drivingSession != null) drivingSession.cancel();
        super.onStop();
    }
}
