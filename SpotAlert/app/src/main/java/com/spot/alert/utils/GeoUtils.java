package com.spot.alert.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class GeoUtils {
    private static DecimalFormat decimalFormat = new DecimalFormat("#.#####");

    public static double getDistanceFromLatLonInKm(LatLng from, LatLng to) {
        return getDistanceFromLatLonInKm(from.latitude, from.longitude, to.latitude, to.longitude);
    }

    public static double getDistanceFromLatLonInKm(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2 - lat1);  // deg2rad below
        double dLon = deg2rad(lon2 - lon1);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d;
    }

    private static double deg2rad(double deg) {
        return deg * (Math.PI / 180);
    }

    public static void alertDialogEnableLocation(Context context) {// here we are doing the alertDialog for the location permission

        new AlertDialog.Builder(context)
                .setMessage("המיקום של המכשיר כבוי, האם אתה מעונין להדליק?")//message
                .setCancelable(true)
                .setPositiveButton(
                        "כן",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(
                        " לא",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })

                .create()
                .show();
    }

    public static String getFormattedPoint(double point) {
        return decimalFormat.format(point);
    }

    public static String getFormattedLatLng(LatLng point) {
       return "(" + GeoUtils.getFormattedPoint(point.latitude) + "," + GeoUtils.getFormattedPoint(point.longitude) + ")";
    }
}
