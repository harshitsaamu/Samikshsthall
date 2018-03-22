package com.mirakiphi.moztrip.screens;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.mirakiphi.moztrip.Model;
import com.mirakiphi.moztrip.PlaceActivity;
import com.mirakiphi.moztrip.R;
import com.mirakiphi.moztrip.adapters.MainPagerAdapter;
import com.mirakiphi.moztrip.utils.articles;
import com.mirakiphi.moztrip.utils.fare_calculator;
import com.mirakiphi.moztrip.wallet.Wallet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    GoogleApiClient mGoogleApiClient;
    private LinearLayout linearLayout1,
            linearLayout2,
            linearLayout3,
            linearLayout4,
            linearLayout5,
            linearLayout6,
            linearLayout7,
            linearLayout8,
            linearLayout9;


    public static final int REQUEST_READ_PERMISSION = 223;
    LocationManager locationManager;
    double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.mirakiphi.moztrip.R.layout.activity_main);

        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        linearLayout3 = (LinearLayout) findViewById(R.id.linearLayout3);
        linearLayout4 = (LinearLayout) findViewById(R.id.linearLayout4);
        linearLayout5 = (LinearLayout) findViewById(R.id.linearLayout5);
        linearLayout6 = (LinearLayout) findViewById(R.id.linearLayout6);
        linearLayout7 = (LinearLayout) findViewById(R.id.linearLayout7);
        linearLayout8 = (LinearLayout) findViewById(R.id.linearLayout8);
        linearLayout9 = (LinearLayout) findViewById(R.id.linearLayout18);

        linearLayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentnews = new Intent(MainActivity.this, articles.class);
                startActivity(intentnews);
            }
        });

        linearLayout8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentfare = new Intent(MainActivity.this, fare_calculator.class);
                startActivity(intentfare);
            }
        });

        linearLayout9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentfare = new Intent(MainActivity.this, Wallet.class);
                startActivity(intentfare);
            }
        });

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        requestPermission();
    }

    public void findPlace(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getLatLng());
                Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                intent.putExtra("Name", place.getName());
                intent.putExtra("Phone_Number", place.getPhoneNumber());
                intent.putExtra("Latitude", String.valueOf(place.getLatLng().latitude));
                intent.putExtra("Longitude", String.valueOf(place.getLatLng().longitude));
                intent.putExtra("Rating", place.getRating());
                intent.putExtra("Address", place.getAddress());
                intent.putExtra("Website", place.getWebsiteUri());
                intent.putExtra("PriceLevel", place.getPriceLevel());
                intent.putExtra("ID", place.getId());
                startActivity(intent);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
// TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
// The user canceled the operation.
            }
        }
    }

    //Current Location


    private void requestPermission() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_READ_PERMISSION);
            } else {
                lock_on();
            }
        } else {
            lock_on();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    lock_on();

                } else {
                    Toast.makeText(getApplicationContext(), "Cannot get location", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private List<Model> touristPlacesList = new ArrayList<>();

    void lock_on() {
        final String provider;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider,1000, 1,MainActivity.this);

        if(location==null){
            Toast.makeText(this, "Unable to find the current location.", Toast.LENGTH_SHORT).show();
        }
        else{
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                String cityName = addresses.get(0).getAddressLine(2);
                if(cityName!=null) {
                    cityName = cityName.replace(",", "+");
                    cityName = cityName.replace(" ", "+");
                }
                final ViewPager viewPager = (ViewPager) findViewById(com.mirakiphi.moztrip.R.id.vp_main);
                viewPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager(), cityName));
                viewPager.setOffscreenPageLimit(2);

                linearLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putExtra("latitude", String.valueOf(latitude));
                        intent.putExtra("longitude", String.valueOf(longitude));
                        intent.putExtra("tag", "airport");
                        startActivity(intent);
                    }
                });
                linearLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putExtra("latitude", String.valueOf(latitude));
                        intent.putExtra("longitude", String.valueOf(longitude));
                        intent.putExtra("tag", "train_station");
                        startActivity(intent);
                    }
                });
                linearLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putExtra("latitude", String.valueOf(latitude));
                        intent.putExtra("longitude", String.valueOf(longitude));
                        intent.putExtra("tag", "hospital");
                        startActivity(intent);
                    }
                });
                linearLayout4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putExtra("latitude", String.valueOf(latitude));
                        intent.putExtra("longitude", String.valueOf(longitude));
                        intent.putExtra("tag", "atm");
                        startActivity(intent);
                    }
                });
                linearLayout5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putExtra("latitude", String.valueOf(latitude));
                        intent.putExtra("longitude", String.valueOf(longitude));
                        intent.putExtra("tag", "restaurant");
                        startActivity(intent);
                    }
                });
                linearLayout6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putExtra("latitude", String.valueOf(latitude));
                        intent.putExtra("longitude", String.valueOf(longitude));
                        intent.putExtra("tag", "travel_agency");
                        startActivity(intent);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

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

}
