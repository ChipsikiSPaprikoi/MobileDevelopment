package ru.mirea.miheenkovts.mireaproject;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;

public class EstablishmentsFragment extends Fragment {

    private MapView mapView = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));

        View view = inflater.inflate(R.layout.fragment_establishments, container, false);
        mapView = view.findViewById(R.id.mapView);

        mapView.setZoomRounding(true);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(14.0);

        GeoPoint startPoint = new GeoPoint(55.669986, 37.480409);
        mapController.setCenter(startPoint);

        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);


        addMarker(new GeoPoint(55.669986, 37.480409),
                "РТУ МИРЭА (В-78)",
                "Главный кампус. пр-т Вернадского, 78");

        addMarker(new GeoPoint(55.664160, 37.481210),
                "Вкусно — и точка",
                "Фастфуд ресторан. ул. Покрышкина, 2");

        addMarker(new GeoPoint(55.670644, 37.477047),
                "Дикси",
                "Продуктовый магазин. ул. Академика Анохина, 2к1Б");

        addMarker(new GeoPoint(55.794229, 37.700772),
                "РТУ МИРЭА (Стромынка)",
                "Кампус на ул. Стромынка, 20");

        addMarker(new GeoPoint(55.794707, 37.698164),
                "Пятёрочка",
                "Продуктовый магазин. ул. Стромынка, 19А, корп. 1");

        addMarker(new GeoPoint(55.788758, 37.681358),
                "Вкусно — и точка",
                "Фастфуд у метро Сокольники. ул. Русаковская, 26");

        return view;
    }

    private void addMarker(GeoPoint point, String title, String description) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ResourcesCompat.getDrawable(getResources(), org.osmdroid.library.R.drawable.osm_ic_follow_me_on, null));
        marker.setTitle(title);
        marker.setSnippet(description);

        marker.setOnMarkerClickListener((m, mv) -> {
            Toast.makeText(requireContext(), title + "\n" + description, Toast.LENGTH_SHORT).show();
            m.showInfoWindow();
            return true;
        });

        mapView.getOverlays().add(marker);
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        if (mapView != null) mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()));
        if (mapView != null) mapView.onPause();
    }
}
