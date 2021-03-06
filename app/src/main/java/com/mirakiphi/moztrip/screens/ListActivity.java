package com.mirakiphi.moztrip.screens;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mirakiphi.moztrip.ListAdapter;
import com.mirakiphi.moztrip.Model;
import com.mirakiphi.moztrip.R;
import com.mirakiphi.moztrip.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mirakiphi.moztrip.utils.Contract.DETAILS_2;
import static com.mirakiphi.moztrip.utils.Contract.NEARBY_SEARCH1;
import static com.mirakiphi.moztrip.utils.Contract.PLACE_IMAGE;
import static com.mirakiphi.moztrip.utils.Contract.WEB_API_KEY;

public class ListActivity extends AppCompatActivity {
    //Places to visit
    private RecyclerView recyclerViewTouristPlaces;
    private List<Model> touristPlacesList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManagerTouristPlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        /**
         *Tourist Places *************************************************************************
         */
        //
        layoutManagerTouristPlaces = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewTouristPlaces = (RecyclerView) findViewById(R.id.recyclerViewTouristPlaces);
        recyclerViewTouristPlaces.setLayoutManager(layoutManagerTouristPlaces);
        recyclerViewTouristPlaces.setItemAnimator(new DefaultItemAnimator());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, NEARBY_SEARCH1 + getIntent().getStringExtra("latitude") +","+ getIntent().getStringExtra("longitude") + "&type=" + getIntent().getStringExtra("tag") + DETAILS_2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("URL", "onCreate: " + NEARBY_SEARCH1 + getIntent().getStringExtra("latitude") +","+ getIntent().getStringExtra("longitude") + "&radius=99999&type=" + getIntent().getStringExtra("tag") + DETAILS_2);

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
                        adapter = new ListAdapter(getApplicationContext(), touristPlacesList);
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

}
