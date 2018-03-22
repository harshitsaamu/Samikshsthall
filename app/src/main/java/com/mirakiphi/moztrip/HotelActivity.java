package com.mirakiphi.moztrip;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.mirakiphi.moztrip.utils.Contract.DETAILS;
import static com.mirakiphi.moztrip.utils.Contract.DETAILS_2;
import static com.mirakiphi.moztrip.utils.Contract.PLACE_IMAGE;
import static com.mirakiphi.moztrip.utils.Contract.WEB_API_KEY;

public class HotelActivity extends AppCompatActivity {
    private ImageView imageViewBackpic, imageViewCall, imageViewNavigation;
    private TextView textViewName, textViewAdd, textRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);

        imageViewBackpic = (ImageView) findViewById(R.id.imageViewBackpic);
        imageViewCall = (ImageView) findViewById(R.id.imageViewCall);
        imageViewNavigation = (ImageView) findViewById(R.id.imageViewNavigation);
        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewAdd = (TextView) findViewById(R.id.textViewAdd);
        textRating = (TextView) findViewById(R.id.textRating);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, DETAILS + getIntent().getStringExtra("place_id") + DETAILS_2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Volley", "onResponse(Details): " + response);
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            final JSONObject childObject = parentObject.getJSONObject("result");
                            textViewName.setText(childObject.getString("name"));
                            textViewAdd.setText(childObject.getString("formatted_address"));
                            textRating.setText(childObject.getString("rating"));
                            imageViewCall.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = null;
                                    try {
                                        intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + childObject.getString("formatted_phone_number")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    startActivity(intent);
                                }
                            });

                           imageViewNavigation.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   try {
                                       JSONObject jobj = childObject.getJSONObject("geometry");
                                       JSONObject jobj2 = jobj.getJSONObject("location");

                                       Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                               Uri.parse("http://maps.google.com/maps?saddr=26.8801966,75.8110854&daddr=" + jobj2.getString("lat") + ","+ jobj2.getString("lng")));
                                       startActivity(intent);
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }

                               }
                           });
                                try {
                                    JSONArray photoArray = childObject.getJSONArray("photos");
                                    JSONObject photoObject = photoArray.getJSONObject(0);
                                    Glide.with(getApplicationContext()).load(PLACE_IMAGE + photoObject.getString("photo_reference") + "&key=" + WEB_API_KEY).into(imageViewBackpic);

                                } catch (JSONException e) {
                                    Log.i("PhotoError", "onResponse: ");
                                }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
