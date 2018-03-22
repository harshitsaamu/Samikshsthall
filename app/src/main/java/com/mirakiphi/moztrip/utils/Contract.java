package com.mirakiphi.moztrip.utils;

/**
 * Created by anuragmaravi on 07/04/17.
 */

public class Contract {
    public Contract(){}
    public final static String API_KEY = "AIzaSyD67eUvLlJWq_CMS2KcCAR8vwMY_xuczK8";
    public final static String WEB_API_KEY = "AIzaSyAPvPv8IPRaS2wzwfnjWR5slDTCpHclsRc";

    //Places Images
    public final static String PLACE_IMAGE = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";

    //Nearby Places
    public final static String NEARBY_PLACES_1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    public final static String POINT_OF_INTEREST = "+point+of+interest&language=en&key=" + WEB_API_KEY;

    public final static String NEARBY_SEARCH1="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=";
    public final static String NEARBY_SEARCH2= "&rankby=distance&radius=9999";



    public final static String RESTAURANTS_1 = "https://maps.googleapis.com/maps/api/place/textsearch/json?location=";
    public final static String RESTAURANTS_2= "&radius=500&type=restaurant&key=" + WEB_API_KEY;

    public final static String HOTELS_2= "&radius=500&type=lodging&key=" + WEB_API_KEY;


    public final static String DETAILS= "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    public final static String DETAILS_2= "&rankby=distance&key=" + WEB_API_KEY;

    public final static String POINT_OF_INTEREST_NEAR = "+monuments+in&radius=9999999&rankby=prominence&language=en&key=" + WEB_API_KEY;




}
