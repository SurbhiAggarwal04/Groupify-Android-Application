package com.example.arpit.groupify;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by arpit on 4/16/2016.
 */
public class GetCoordinates {

    double latitude;
    double longitude;
    Location location;
    Context context;


    public List convertAddress(String address, Context context) {
        this.context = context;
        List dest_add = new ArrayList();
        if (address != null && !address.isEmpty()) {
            try {
                Geocoder coder = new Geocoder(context);

                List<Address> addressList = coder.getFromLocationName(address, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();

                    dest_add.add(lat);
                    dest_add.add(lng);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
        return dest_add;
    } // end convertAddress
    public double getLatitude(){
        if(location != null)
        {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude(){
        if(location != null)
        {
            longitude = location.getLongitude();
        }
        return longitude;
    }

}
