package com.route.calculator;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by joseph on 28/04/14.
 */
public class LocationSearchAsyncTask extends AsyncTask<String, Integer, Address> {
    //Reference held to the map so the map can be updated
    private GoogleMap map = null;
    private Context context = null;

    public LocationSearchAsyncTask(GoogleMap m, Context ctx) {
        super();
        if (m != null) {
            map = m;
            context = ctx;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Address doInBackground(String... location) {
        if (isNetworkAvailable()) {
            Geocoder g = new Geocoder(context, Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = g.getFromLocationName(location[0], 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //if an address is available return it, making it available to postExecute
            if (addresses != null) {
                return addresses.get(0);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Address address) {
        if (address != null) {
            moveMap(address);
        } else {
            Toast.makeText(context, "Sorry no place could be found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    private void moveMap(Address address) {
        CameraPosition newPlace =
                new CameraPosition.Builder().target(
                        new LatLng(address.getLatitude(), address.getLongitude())
                ).zoom(15.5f)
                        .bearing(8)
                        .tilt(25)
                        .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(newPlace));
    }

    public boolean isNetworkAvailable() {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
