package com.example.arvind.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;
    TextToSpeech textToSpeech;
    String message="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else{

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
            Location lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation!=null)
            updateLocationInfo(lastKnownLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            startlistening();
        }

    }
    public void startlistening(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }
    void updateLocationInfo(Location location) {
        message="";
        TextView latitudeTextview=findViewById(R.id.latitudeTextview);
        TextView longitudeTextview=findViewById(R.id.longitudeTextview);
        TextView accuracyTextview=findViewById(R.id.accuracyTextview);
        TextView altitudeTextview=findViewById(R.id.altitudeTextview);
        TextView addressTextview=findViewById(R.id.addressTextView);

        latitudeTextview.setText("Latitude : "+ Double.toString(location.getLatitude()));
        longitudeTextview.setText("Longitude : "+Double.toString(location.getLongitude()));
        accuracyTextview.setText("Accuracy : "+Double.toString(location.getAccuracy()));
        altitudeTextview.setText("Altitude : "+Double.toString(location.getAltitude()));
        message+="Welcome to Hiker's watch. Your position Details ..Latitude :"+Double.toString(location.getLatitude())
                 +". Longitude : "+Double.toString(location.getLongitude())+". Accuracy : "+Double.toString(location.getAccuracy())
                +". Altitude : "+Double.toString(location.getAltitude());

        String address="Could not find address :(";
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (listAddress != null && listAddress.size() > 0) {
                address = "Address  : \n";
                message+=". Address is:";
                if (listAddress.get(0).getThoroughfare() != null) {
                    //getThroughfare gives us the street address
                    address += listAddress.get(0).getThoroughfare() + "\n";
                    message +=". "+ listAddress.get(0).getThoroughfare();
                }
                if (listAddress.get(0).getLocality() != null) {
                    address += listAddress.get(0).getLocality() + " \n";
                    message +=". "+ listAddress.get(0).getLocality();
                }
                if (listAddress.get(0).getPostalCode() != null) {
                    address += listAddress.get(0).getPostalCode() + " \n";
                    message +=". "+ listAddress.get(0).getPostalCode();
                }
                if (listAddress.get(0).getAdminArea() != null) {
                    address += listAddress.get(0).getAdminArea();
                    message +=". "+ listAddress.get(0).getAdminArea();
                }
            }
            addressTextview.setText(address);
        }catch (Exception e){
            e.printStackTrace();
        }
        textToSpeech=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH,null,"1");
                textToSpeech.setPitch(1);
                textToSpeech.setLanguage(Locale.getDefault());
            }
        });


    }
}
