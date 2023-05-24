package com.spot.alert;

import com.google.android.gms.maps.GoogleMap;
import com.spot.alert.dataobjects.User;

public class SpotAlertAppContext {
    public static User ACTIVE_USER;
    public static GoogleMap googleMap;
    public static final String SPOT_ALERT_ADMIN_EMAIL="admin@spotalert.com";
    public static final String CENTER_POINT_STRING ="מוקד אבטחה";
    public static  final User SPOT_ALERT_ADMIN_USER = new User(0,SPOT_ALERT_ADMIN_EMAIL,"spotalert","amdin","0509999999");

}
