package com.example.instantinvites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class NearbyPlacesOfInterest extends FragmentActivity implements OnMapReadyCallback, LocationListener,
GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    int TAG_CODE_PERMISSION_LOCATION;

    private GoogleMap mMap;
    LocationRequest request;
    GoogleApiClient client;
    LatLng latLngCurrent;

    private LocationManager locationManager;
    private Criteria criteria;
    private String bestProvider;

    double myLat;
    double myLong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places_of_interest);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION },
                    TAG_CODE_PERMISSION_LOCATION);
        }

        locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        Location location = locationManager.getLastKnownLocation(bestProvider);

        myLat = location.getLatitude();
        myLong = location.getLongitude();

        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .build();

        client.connect();
        //Add a marker for the current location and move the camera
        latLngCurrent = new LatLng(myLat, myLong);
        mMap.addMarker(new MarkerOptions().position(latLngCurrent).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15));
    }

    public void onLocationChanged(Location location) {
        if (location == null) {
            Toast.makeText(getApplicationContext(), "Location not found.", Toast.LENGTH_LONG).show();
        } else {
            LatLng latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLngCurrent, 15);
            mMap.animateCamera(update);
            MarkerOptions options = new MarkerOptions();
            options.position(latLngCurrent);
            options.title("Current Location");
            mMap.addMarker(options);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != getPackageManager().PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client,request,(com.google.android.gms.location.LocationListener) this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void findNearbyTouristAttractions(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"tourist_attraction");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyArtGalleries(v);
    }

    public void findNearbyArtGalleries(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"art_gallery");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyAquariums(v);
    }

    public void findNearbyAquariums(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"aquarium");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyMuseums(v);
    }

    public void findNearbyMuseums(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"museum");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyZoos(v);
    }

    public void findNearbyZoos(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"zoo");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyBowlingAlleys(v);
    }

    public void findNearbyBowlingAlleys(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"bowling_alley");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyAmusementParks(v);
    }

    public void findNearbyAmusementParks(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"amusement_park");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyCasinos(v);
    }

    public void findNearbyCasinos(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"casino");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyRestaurants(v);
    }

    public void findNearbyRestaurants(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&keyword="+"restaurant");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbySpas(v);
    }

    public void findNearbySpas(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"spa");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyStadiums(v);
    }

    public void findNearbyStadiums(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"stadium");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyCityHalls(v);
    }

    public void findNearbyCityHalls(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"city_hall");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
        findNearbyNightClubs(v);
    }

    public void findNearbyNightClubs(View v){
        StringBuilder stringBuilder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+latLngCurrent.latitude + "," + latLngCurrent.longitude);
        //Display nearby places within 1500m
        stringBuilder.append("&radius="+1500);
        stringBuilder.append("&type="+"night_club");
        stringBuilder.append("&key="+getResources().getString(R.string.google_places_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        getNearbyPlaces.execute(dataTransfer);
    }
}
