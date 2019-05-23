package com.softsolstudi.jugnoo.mapzonealert;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Circle mCircle;
    public static final int LOCATION_REQUEST = 1000;
    public static final int GPS_REQUEST = 1001;
    private LocationCallback locationCallback;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private double MedinaLatitude = 24.467533, MedinaLongitude = 39.611120;
    private double qabaLatitude = 21.422487, qabaLongitude = 39.826206;
    private LocationRequest locationRequest;
    FusedLocationProviderClient mFusedLocationClient;
    HashMap<String, Marker> markerHashMap = new HashMap<>();
    private boolean isQaba = false;
    private boolean isMedina = false;
    Marker mQabalocation;
    int distanceQaba;
    int distanceMedina;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(2 * 1000); // 10 seconds
        locationRequest.setFastestInterval(1000); // 5 seconds


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        Log.d("Chaeck the location","Maps activiy"+wayLatitude+" next "+wayLongitude);
                        AddMarker(wayLongitude,wayLatitude);
                        distanceQaba= (int) getKmFromLatLong(wayLatitude,wayLongitude,qabaLatitude,qabaLongitude);
                        distanceMedina= (int) getKmFromLatLong(wayLatitude,wayLongitude,MedinaLatitude,MedinaLongitude);
                        Log.d("Maps activity","Distance from qaba  "+distanceQaba);
                        Log.d("Maps activity","Distance from Medina  "+distanceMedina);

                        if (distanceQaba<3631){
                            if (!isQaba){
                            Log.d("alarm", "run ho rha");
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.MINUTE, 0);
                            // calendar.add(Calendar.SECOND,0);
                            Intent intent = new Intent(MapsActivity.this, AlarmReceiver.class);
                            intent.putExtra("title","Well Come to Qaba Sharif");
                            PendingIntent broadcast = PendingIntent.getBroadcast(MapsActivity.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
                            isQaba=true;
                        }
                        }
                        if (distanceMedina<3509){
                            if (!isMedina){
                                Log.d("alarm", "run ho rha");
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                Calendar calendar = Calendar.getInstance();
                                calendar.add(Calendar.MINUTE, 0);
                                // calendar.add(Calendar.SECOND,0);
                                Intent intent = new Intent(MapsActivity.this, AlarmReceiver.class);
                                intent.putExtra("title","Well Come to Medina Sharif");
                                PendingIntent broadcast = PendingIntent.getBroadcast(MapsActivity.this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
                                isMedina=true;
                            }
                        }
                        /*if (mFusedLocationClient != null) {
                            Log.d("Chaeck the location","Maps activiy");
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }*/
                    }
                }
            }
        };
        getLocation();
    }

    private void AddQabaMarker(double qabaLatitude, double qabaLongitude) {
        LatLng latLng = new LatLng(qabaLatitude, qabaLongitude);
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(qabaLatitude, qabaLongitude))
                .radius(1000)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Mecca")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_mecca));
        mQabalocation = mMap.addMarker(markerOptions);
        //markerHashMap.put("bus", marker);
    }

    private void AddMadinaMarker(double medinaLatitude, double medinaLongitude) {
        LatLng latLng = new LatLng(medinaLatitude, medinaLongitude);
        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(medinaLatitude, medinaLongitude))
                .radius(1000)
                .strokeColor(Color.RED)
                .fillColor(Color.BLUE));

        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Medina")
                .icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_medina));
        mQabalocation = mMap.addMarker(markerOptions);
        //markerHashMap.put("bus", marker);
    }

    private void AddMarker(double wayLongitude, double wayLatitude) {
        Marker marker = markerHashMap.get("bus");
        if (marker != null) {
            marker.remove();
            markerHashMap.remove("buss");
        }

        //Place current location marker
        LatLng latLng = new LatLng(wayLatitude, wayLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        marker = mMap.addMarker(markerOptions);
        markerHashMap.put("bus", marker);

        //move map camera
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
       // mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
    }

    public static float getKmFromLatLong(double lat1, double lng1, double lat2, double lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        float distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters/1000;
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
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                getLocation();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            getLocation();
            mMap.setMyLocationEnabled(true);
        }

        AddQabaMarker(qabaLatitude,qabaLongitude);
        AddMadinaMarker(MedinaLatitude,MedinaLongitude);
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST);

        } else {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        }
    }


/*    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        Marker marker = markerHashMap.get("bus");
        if (marker != null) {
            marker.remove();
            markerHashMap.remove("buss");
        }else {
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        marker = mMap.addMarker(markerOptions);
        markerHashMap.put("bus", marker);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        Log.d("MapsActivity", "LatLng changes" + mLastLocation.getLatitude() + "next" + mLastLocation.getLongitude());
        Toast.makeText(this, "" + latLng, Toast.LENGTH_SHORT).show();
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            // mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

    }*/

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 40, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        getLocation();
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}