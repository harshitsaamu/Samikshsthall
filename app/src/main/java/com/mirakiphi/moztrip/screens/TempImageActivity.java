package com.mirakiphi.moztrip.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
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
import com.mirakiphi.moztrip.Model;
import com.mirakiphi.moztrip.R;
import com.mirakiphi.moztrip.TouristPlacesAdapter;
import com.mirakiphi.moztrip.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mirakiphi.moztrip.utils.Contract.NEARBY_PLACES_1;
import static com.mirakiphi.moztrip.utils.Contract.POINT_OF_INTEREST;
import static com.mirakiphi.moztrip.utils.Contract.PLACE_IMAGE;
import static com.mirakiphi.moztrip.utils.Contract.WEB_API_KEY;


public class TempImageActivity extends AppCompatActivity {
    String placeId;
    GoogleApiClient mGoogleApiClient;
    ImageView placePhoto;

    //Places to visit
    private RecyclerView recyclerViewTouristPlaces;
    private List<Model> touristPlacesList= new ArrayList<>();
    private  RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManagerTouristPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_image);
        Intent intent=getIntent();
        placeId=intent.getStringExtra("ID");
        placePhoto=(ImageView)findViewById(R.id.imageView);
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



        //Tourist Places
        layoutManagerTouristPlaces = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTouristPlaces = (RecyclerView) findViewById(R.id.recyclerViewTouristPlaces);
        recyclerViewTouristPlaces.setLayoutManager(layoutManagerTouristPlaces);
        recyclerViewTouristPlaces.setItemAnimator(new DefaultItemAnimator());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NEARBY_PLACES_1 + intent.getStringExtra("Name") + POINT_OF_INTEREST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.i("Volley", "onResponse(Tourist Places): " + response);
                        try {
                            JSONObject parentObject= new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for(int i=0;i<parentArray.length();i++){
                                JSONObject finalObject = parentArray.getJSONObject(i);
                                Model model = new Model();
                                model.setTpName(finalObject.getString("name"));
                                model.setTpPlaceID(finalObject.getString("place_id"));
                                JSONArray photoArray = finalObject.getJSONArray("photos");
                                JSONObject photoObject = photoArray.getJSONObject(0);
                                model.setTpReference(PLACE_IMAGE + photoObject.getString("photo_reference") + "&key=" + WEB_API_KEY);
                                touristPlacesList.add(model);
                                Log.i("Tourist Places", "onResponse: " + touristPlacesList);

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

    }
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
}
