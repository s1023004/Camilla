package com.example.user.camilla;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class CamillaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private static final int REQUEST_CODE_LOCATION = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camilla);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private LocationManager lms;
    private String bestProvider = LocationManager.GPS_PROVIDER;	//最佳資訊提供者
    private Marker marker;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        }
        mMap.setMyLocationEnabled(true);
        UiSettings mapSettings;
        mapSettings = mMap.getUiSettings();
        mapSettings.setZoomControlsEnabled(true);
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
        Criteria criteria = new Criteria();	//資訊提供者選取標準
        bestProvider = lms.getBestProvider(criteria, true);	//選擇精準度最高的提供者
        Location location = lms.getLastKnownLocation(bestProvider);
        if(location!=null){
            double longitude = location.getLongitude();	//取得經度
            double latitude = location.getLatitude();	//取得緯度
            LatLng address = new LatLng(latitude, longitude);
            marker = mMap.addMarker(new MarkerOptions().position(address).title("Your location").visible(true).anchor(0.5f,0.5f));
            CameraPosition cp = new CameraPosition.Builder().target(address).zoom(17).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cp));
            //locationServiceInitial(longitude,latitude);
        }
        else{
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
        //locationServiceInitial(-120,37.4);
    }
    private void locationServiceInitial(double longitude,double latitude) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request missing location permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        }
        LatLng address = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(address).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
    }
    public void onSearch(View view){
        List<Address> addressList = null;
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();

        if(location != null || location != ""){
            Geocoder geocoder = new Geocoder(this);
            try{
                addressList = geocoder.getFromLocationName(location,1);
            }catch (IOException e){
                e.printStackTrace();
            }
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title(location));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }
}
