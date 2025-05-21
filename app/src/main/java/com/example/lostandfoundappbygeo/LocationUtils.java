package com.example.lostandfoundappbygeo;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationUtils {
    private static final String TAG = "LocationUtils";

    private static final Pattern COORDINATES_PATTERN = Pattern.compile("(-?\\d+\\.\\d+),\\s*(-?\\d+\\.\\d+)");


    public static LatLng getLocationFromString(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return null;
        }

        Matcher matcher = COORDINATES_PATTERN.matcher(locationString);
        if (matcher.find()) {
            try {
                double latitude = Double.parseDouble(matcher.group(1));
                double longitude = Double.parseDouble(matcher.group(2));
                return new LatLng(latitude, longitude);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Could not parse coordinates from string: " + locationString, e);
            }
        }

        return null;
    }
}