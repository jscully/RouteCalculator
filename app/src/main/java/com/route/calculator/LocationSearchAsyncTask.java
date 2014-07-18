package com.route.calculator;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
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
    private ProgressDialog progDialog;
    private Context context = null;
    private GoogleMap map = null;

    public LocationSearchAsyncTask(GoogleMap map , Context ctx) {
        super();
        this.map = map;
        context = ctx;
        progDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog.setMessage("Searching...");
        progDialog.show();
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
            if (addresses.size() > 0) {
                return addresses.get(0);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Address address) {
        if(progDialog.isShowing()){
            progDialog.dismiss();
        }
        moveMap(address);
        map = null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * Check internet connectivity
     * @return
     */
    public boolean isNetworkAvailable() {
        return ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }

    /**
     * Method to to move map to new address.
     * @param address
     */
    private void moveMap(Address address) {
        if(address != null){
            CameraPosition newPlace =
                    new CameraPosition.Builder().target(
                            new LatLng(address.getLatitude(), address.getLongitude())
                    ).zoom(15.5f)
                            .bearing(8)
                            .tilt(0)
                            .build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(newPlace));
        }
        else{
            Toast.makeText(context, "Cannot find location."  , Toast.LENGTH_LONG).show();
        }
    }
}
