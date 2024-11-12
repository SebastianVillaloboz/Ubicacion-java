package com.example.ubicacion;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private TextView latitudTextView;
    private TextView longitudTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                actualizarUbicacion(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        };

        solicitarPermisos();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latitudTextView = findViewById(R.id.latitudTextView);
        longitudTextView = findViewById(R.id.longitudTextView);

        Button buscarButton = findViewById(R.id.buscarButton);
        buscarButton.setOnClickListener(v -> buscarUbicacion());
    }

    private void solicitarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    private void actualizarUbicacion(Location location) {
        double latitud = location.getLatitude();
        double longitud = location.getLongitude();

        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Mi ubicación"));

        latitudTextView.setText("Latitud: " + latitud);
        longitudTextView.setText("Longitud: " + longitud);
    }

    private void buscarUbicacion() {
        EditText latitudEditText = findViewById(R.id.latitudEditText);
        EditText longitudEditText = findViewById(R.id.longitudEditText);

        try {
            double latitud = Double.parseDouble(latitudEditText.getText().toString());
            double longitud = Double.parseDouble(longitudEditText.getText().toString());

            if (latitud >= -90 && latitud <= 90 && longitud >= -180 && longitud <= 180) {
                LatLng ubicacion = new LatLng(latitud, longitud);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
                mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación seleccionada"));

                latitudTextView.setText("Latitud: " + latitud);
                longitudTextView.setText("Longitud: " + longitud);
            } else {
                Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error al buscar", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, locationListener);
        }
    }
}