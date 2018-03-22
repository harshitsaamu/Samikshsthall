package com.mirakiphi.moztrip.screens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mirakiphi.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.mirakiphi.moztrip.Model;
import com.mirakiphi.moztrip.VolleySingleton;
import com.mirakiphi.moztrip.adapters.HorizontalPagerAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.mirakiphi.moztrip.utils.Contract.NEARBY_PLACES_1;
import static com.mirakiphi.moztrip.utils.Contract.PLACE_IMAGE;
import static com.mirakiphi.moztrip.utils.Contract.POINT_OF_INTEREST_NEAR;
import static com.mirakiphi.moztrip.utils.Contract.WEB_API_KEY;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class HorizontalPagerFragment extends Fragment {

    private List<Model> touristPlacesList = new ArrayList<>();
    String cityName;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        cityName = getArguments().getString("cityName");

        return inflater.inflate(com.mirakiphi.moztrip.R.layout.fragment_horizontal, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NEARBY_PLACES_1 + "Jaipur" + POINT_OF_INTEREST_NEAR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("Volley", "onResponse(Current): " + response);
                        try {
                            JSONObject parentObject = new JSONObject(response);
                            JSONArray parentArray = parentObject.getJSONArray("results");
                            for (int i = 0; i < 5; i++) {
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

                        final HorizontalInfiniteCycleViewPager horizontalInfiniteCycleViewPager =
                                (HorizontalInfiniteCycleViewPager) view.findViewById(com.mirakiphi.moztrip.R.id.hicvp);
                        horizontalInfiniteCycleViewPager.setAdapter(new HorizontalPagerAdapter(getContext(), false, touristPlacesList));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Some Error Occurred. Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);

    }

}
