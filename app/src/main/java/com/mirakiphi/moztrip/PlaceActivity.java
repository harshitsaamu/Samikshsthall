package com.mirakiphi.moztrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mirakiphi.moztrip.utils.Contract.HOTELS_2;
import static com.mirakiphi.moztrip.utils.Contract.NEARBY_PLACES_1;
import static com.mirakiphi.moztrip.utils.Contract.PLACE_IMAGE;
import static com.mirakiphi.moztrip.utils.Contract.POINT_OF_INTEREST;
import static com.mirakiphi.moztrip.utils.Contract.RESTAURANTS_1;
import static com.mirakiphi.moztrip.utils.Contract.RESTAURANTS_2;
import static com.mirakiphi.moztrip.utils.Contract.WEB_API_KEY;

public class PlaceActivity extends AppCompatActivity {
    String placeId;
    GoogleApiClient mGoogleApiClient;
    ImageView placePhoto;

    //Places to visit
    private RecyclerView recyclerViewTouristPlaces;
    private List<Model> touristPlacesList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManagerTouristPlaces;

    //Restaurants
    private RecyclerView recyclerViewRestaurants;
    private List<Model> listRestaurants = new ArrayList<>();
    private RecyclerView.Adapter adapterRestaurants;
    private RecyclerView.LayoutManager layoutManagerRestaurants;

    //Hotels
    private RecyclerView recyclerViewHotels;
    private List<Model> listHotels = new ArrayList<>();
    private RecyclerView.Adapter adapterHotels;
    private RecyclerView.LayoutManager layoutManagerHotels;

    private TextView textViewPlaceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setTitle(getIntent().getStringExtra("Name"));

        Intent intent = getIntent();
        placeId = intent.getStringExtra("ID");
        placePhoto = (ImageView) findViewById(R.id.imageView);
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
        // placePhotosTask(placeId);
        placePhotosAsync(placeId);


        textViewPlaceName = (TextView) findViewById(R.id.textViewPlaceName);
        textViewPlaceName.setText(intent.getStringExtra("Name"));


        /**
         *Tourist Places *************************************************************************
         */
        layoutManagerTouristPlaces = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTouristPlaces = (RecyclerView) findViewById(R.id.recyclerViewTouristPlaces);
        recyclerViewTouristPlaces.setLayoutManager(layoutManagerTouristPlaces);
        recyclerViewTouristPlaces.setItemAnimator(new DefaultItemAnimator());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NEARBY_PLACES_1 + intent.getStringExtra("Name") + POINT_OF_INTEREST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Volley", "onResponse(Tourist Places): " + response);
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                Model model = new Model();
                                model.setTpName(finalObject.getString("name"));
                                model.setTpPlaceID(finalObject.getString("place_id"));
                                try {
                                    JSONArray photoArray = finalObject.getJSONArray("photos");
                                    JSONObject photoObject = photoArray.getJSONObject(0);
                                    model.setTpReference(PLACE_IMAGE + photoObject.getString("photo_reference") + "&key=" + WEB_API_KEY);
                                } catch (JSONException e) {
                                    Log.i("PhotoError", "onResponse: ");
                                }
                                touristPlacesList.add(model);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter = new TouristPlacesAdapter(getApplicationContext(), touristPlacesList);
                        recyclerViewTouristPlaces.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


        /**
         *Restaurants *************************************************************************
         */

        layoutManagerRestaurants = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRestaurants = (RecyclerView) findViewById(R.id.recyclerViewRestaurants);
        recyclerViewRestaurants.setLayoutManager(layoutManagerRestaurants);
        recyclerViewRestaurants.setItemAnimator(new DefaultItemAnimator());
        StringRequest stringRequestRestaurant = new StringRequest(Request.Method.GET, RESTAURANTS_1 + getIntent().getStringExtra("Latitude") + "," + getIntent().getStringExtra("Longitude") + RESTAURANTS_2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Volley", "onResponse(Restaurants): " + response);
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                Model model = new Model();
                                model.setResName(finalObject.getString("name"));
                                model.setResPlaceID(finalObject.getString("place_id"));
//                                model.setResPrice(finalObject.getString("price_level"));
                                try {
                                    JSONArray photoArray = finalObject.getJSONArray("photos");
                                    JSONObject photoObject = photoArray.getJSONObject(0);
                                    model.setResPhoto(PLACE_IMAGE + photoObject.getString("photo_reference") + "&key=" + WEB_API_KEY);
                                } catch (JSONException e) {

                                    Toast.makeText(PlaceActivity.this, "No Image", Toast.LENGTH_SHORT).show();
                                }
                                listRestaurants.add(model);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapterRestaurants = new RestaurantsAdapter(getApplicationContext(), listRestaurants);
                        recyclerViewRestaurants.setAdapter(adapterRestaurants);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequestRestaurant);


/**
 *Hotels *************************************************************************
 */

        layoutManagerHotels =new

    LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);

        recyclerViewHotels =(RecyclerView)

    findViewById(R.id.recyclerViewHotels);
        recyclerViewHotels.setLayoutManager(layoutManagerHotels);
        recyclerViewHotels.setItemAnimator(new

    DefaultItemAnimator());
    StringRequest stringRequestHotels = new StringRequest(Request.Method.GET, RESTAURANTS_1 + getIntent().getStringExtra("Latitude") + "," + getIntent().getStringExtra("Longitude") + HOTELS_2,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                        Log.i("Volley", "onResponse(Hotels): " + response);
                    try {
                        JSONObject parentObject = new JSONObject(response);
                        JSONArray parentArray = parentObject.getJSONArray("results");
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject finalObject = parentArray.getJSONObject(i);
                            Model model = new Model();
                            model.setResName(finalObject.getString("name"));
                            model.setResPlaceID(finalObject.getString("place_id"));
//                                model.setResPrice(finalObject.getString("price_level"));
                            try {
                                JSONArray photoArray = finalObject.getJSONArray("photos");
                                JSONObject photoObject = photoArray.getJSONObject(0);
                                model.setResPhoto(PLACE_IMAGE + photoObject.getString("photo_reference") + "&key=" + WEB_API_KEY);
                            } catch (JSONException e) {

                                Toast.makeText(PlaceActivity.this, "No Image", Toast.LENGTH_SHORT).show();
                            }
                            listHotels.add(model);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    adapterHotels = new RestaurantsAdapter(getApplicationContext(), listHotels);
                    recyclerViewHotels.setAdapter(adapterHotels);
                }
            }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }
    });
    // Add the request to the RequestQueue.
        VolleySingleton.getInstance(

    getApplicationContext()).

    addToRequestQueue(stringRequestHotels);

}


    //Place Image
    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }
            placePhoto.setImageBitmap(placePhotoResult.getBitmap());
        }
    };

    /**
     * Load a bitmap from the photos API asynchronously
     * by using buffers and result callbacks.
     */
    private void placePhotosAsync(String placeId) {
        //   final String placeId = "ChIJrTLr-GyuEmsRBfy61i59si0"; // Australian Cruise Group
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }

                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            // Display the first bitmap in an ImageView in the size of the view
                            Log.i("number",String.valueOf(photoMetadataBuffer.getCount()));
                            photoMetadataBuffer.get(0)
                                    .getScaledPhoto(mGoogleApiClient, placePhoto.getWidth(),
                                            placePhoto.getHeight())
                                    .setResultCallback(mDisplayPhotoResultCallback);
                        }
                        photoMetadataBuffer.release();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
