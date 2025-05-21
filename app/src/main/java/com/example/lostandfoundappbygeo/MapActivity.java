package com.example.lostandfoundappbygeo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DBHelper dbHelper;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        dbHelper = new DBHelper(this);
        btnBack = findViewById(R.id.btnBack);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng defaultPosition = new LatLng(-37.8476, 145.1140); // Melbourne, Australia
        float defaultZoom = 10.0f;

        List<Item> items = dbHelper.getAllItems();

        if (items.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, defaultZoom));
            Toast.makeText(this, "No items to display on the map", Toast.LENGTH_SHORT).show();
            return;
        }


        for (Item item : items) {
            if (item.getLatitude() != 0.0 && item.getLongitude() != 0.0) {
                LatLng position = new LatLng(item.getLatitude(), item.getLongitude());

                float markerColor = item.getType().equalsIgnoreCase("Lost") ?
                        BitmapDescriptorFactory.HUE_RED : BitmapDescriptorFactory.HUE_BLUE;


                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(item.getType() + ": " + item.getName())
                        .snippet(item.getDescription())
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));

                if (marker != null) {
                    marker.setTag(item.getId());
                }
            }
        }

        boolean cameraSet = false;
        for (Item item : items) {
            LatLng position = LocationUtils.getLocationFromString(item.getLocation());
            if (position != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, defaultZoom));
                cameraSet = true;
                break;
            }
        }

        if (!cameraSet) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultPosition, defaultZoom));
        }


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                Object tag = marker.getTag();
                if (tag != null) {
                    int itemId = (int) tag;
                    Intent intent = new Intent(MapActivity.this, ItemDetailActivity.class);
                    intent.putExtra("item_id", itemId);
                    startActivity(intent);
                }
            }
        });
    }
}