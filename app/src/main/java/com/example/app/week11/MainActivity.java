package com.example.app.week11;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationChangeListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private List<Place> places;
    private List<Marker> markers;
    private double currentLat, currentLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        places = new ArrayList<>();
        places.add(new Place("KMITL U",               new LatLng(13.7267, 100.7801)));
        places.add(new Place("Rangsit U",           new LatLng(13.9649, 100.5865)));
        places.add(new Place("Mahanakorn U",        new LatLng(13.8441, 100.8563)));
        places.add(new Place("The Mall Bangkapi",   new LatLng(13.7648, 100.6452)));
        places.add(new Place("Suvarnabhumi",        new LatLng(13.6925, 100.7500)));
        places.add(new Place("CU",                  new LatLng(13.7385026, 100.5310996)));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        //mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        enableMyLocation();

        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMarkerClickListener(this);
        for (Place p: places) { //สามารถกำหนดไอคอนได้
            mMap.addMarker(new MarkerOptions().position(p.getLatLng()).title(p.getName()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places.get(0).getLatLng(), 10)); //focuse ไปทำแหจ่งตามที่ plot ต้องกลางของแผนที่
    }

    //check for permission to use current location
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Location myLocation = new Location("My location");
        myLocation.setLatitude(currentLat);
        myLocation.setLongitude(currentLong);
        Location destination = new Location(marker.getTitle());
        destination.setLatitude(marker.getPosition().latitude);
        destination.setLongitude(marker.getPosition().longitude);
        float distance = myLocation.distanceTo(destination) / 1000;

        Toast.makeText(this, "Distance to " + marker.getTitle() + ": " + distance + " km.", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onMyLocationChange(Location location) { //ตำแหน่งล่าสุด เก็บ ลอง ละ ปัจจุบันได้
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
        ///  เพิ่มม ถ้า 5kigo ให้แสดง เทียบตำแหน่งเราว่า มากกว่าหรือน้อยกว่า 5
        for(Place p: places) {
            Location dest = new Location(p.getName());
            dest.setLatitude(p.getLatLng().latitude);
            dest.setLongitude(p.getLatLng().longitude);

            float distance = location.distanceTo(dest) / 1000; ///ทำให้เป็น kilo
            // เราอยากให้มันคือ อยุภายใน 5 kigo ให้แสดง
            if(distance > 5) {

            }
        }
    }
}
