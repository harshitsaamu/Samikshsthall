package com.mirakiphi.moztrip.utils;

        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.annotation.NonNull;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.CardView;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;;
        import com.android.volley.toolbox.StringRequest;
        import com.android.volley.toolbox.Volley;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
        import com.google.android.gms.common.GooglePlayServicesRepairableException;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.common.api.Status;
        import com.google.android.gms.location.places.Place;
        import com.google.android.gms.location.places.Places;
        import com.google.android.gms.location.places.ui.PlaceAutocomplete;
        import com.mirakiphi.moztrip.R;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;


public class fare_calculator extends AppCompatActivity {
    RequestQueue requestQueue;
    private ProgressDialog pdLoading;
    CardView calculate_fare_card;
    GoogleApiClient mGoogleApiClient;
    String origin="",destination="";
    TextView fare_view,search_origin,search_dest;
    private String URL="https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=";//Jaipur&destinations=Chennai&key=AIzaSyCjjy4aVs4nrphMBGsy4tpnpY8zsffoHPY";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_calculator);
        fare_view=(TextView)findViewById(R.id.rate_view);
        calculate_fare_card=(CardView)findViewById(R.id.calculate_fare);
        search_origin=(TextView)findViewById(R.id.serach_originn);
        search_dest=(TextView)findViewById(R.id.search_dest);
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
        calculate_fare_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(origin==""||destination=="")
                {
                    Toast.makeText(fare_calculator.this, "Enter the source and destination", Toast.LENGTH_SHORT).show();
                }
                else
                getDistance();
            }
        });

    }
    public void findOrigin(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, 1);
        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }
    public void findDest(View view) {
        try {
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, 2);
        } catch (GooglePlayServicesRepairableException e) {
        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber()+place.getLatLng()+place.getId());
                origin=place.getName().toString();
                search_origin.setText(origin);
                origin=origin.replace(" ","+");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Tag", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
            }
        }
        else if(requestCode==2){
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber()+place.getLatLng()+place.getId());
                destination=place.getName().toString();
                search_dest.setText(destination);
                destination=destination.replace(" ","+");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Tag", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }
    public void getDistance()

    {
      URL="https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=";
        URL=URL.concat(origin).concat("&destinations=").concat(destination).concat("&key=AIzaSyCjjy4aVs4nrphMBGsy4tpnpY8zsffoHPY");
        requestQueue = Volley.newRequestQueue(this);
        final StringRequest arrayRequest =new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try { String result=response.toString();
                    JSONObject parentObject = new JSONObject(result);
                    JSONArray childArray=parentObject.getJSONArray("rows");
                    JSONObject childObject=childArray.getJSONObject(0);
                    JSONArray grandchildArray=childObject.getJSONArray("elements");
                    JSONObject grandObject=grandchildArray.getJSONObject(0);
                    JSONObject distance=grandObject.getJSONObject("distance");
                    String mainDistance=distance.getString("text");
                   String realDistance=mainDistance.replace(" mi","");
                    float realDistanc=0;
                    try{
                         realDistanc= Float.parseFloat(realDistance.replace(",",""));
                    }catch (Exception e)
                    {
                        Toast.makeText(fare_calculator.this, ""+e, Toast.LENGTH_SHORT).show();
                    }
                    fare_view.setText("₹"+String.valueOf((int)(realDistanc*1.609344*12.5)));
//                    pdLoading.dismiss();
                }
                catch (JSONException e) {
                    pdLoading.dismiss();
                    AlertDialog.Builder dialogbox = new AlertDialog.Builder(fare_calculator.this);
                    dialogbox.setMessage("Can't fetch the data click to retry...");
                    dialogbox.setCancelable(false);
                    dialogbox.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getDistance();
                        }
                    });
                    dialogbox.show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AlertDialog.Builder dialogbox = new AlertDialog.Builder(fare_calculator.this);
                        dialogbox.setMessage("Can't fetch the data click to retry...");
                        dialogbox.setCancelable(false);
                        dialogbox.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getDistance();
                            }
                        });
                        dialogbox.show();
                    }
                }
        );

        requestQueue.add(arrayRequest);
    }
}
