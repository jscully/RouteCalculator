package com.route.calculator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import models.MarkerPoint;
import routes.PointToPointRoute;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointFragment extends Fragment {
    static final String TAG = "TAG";
    public static GoogleMap map;
    private PointToPointRoute route;
    private double distance;
    private TextView distanceView;
    private PolylineOptions polylineOptions;
    public static View view;
    private Polyline polyline =  null;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        getActivity().getActionBar().setTitle("Point Route");
        setMarkerListeners(map);

        route = new PointToPointRoute();
        polylineOptions = new PolylineOptions();
        polylineOptions.width(5);

        //onclick listener for the map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarkerPoint(latLng);
                drawRoute(latLng);
            }
        });
        // Restoring the markers on configuration changes
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("markers")){
                HashMap<String, MarkerPoint> pointList = (HashMap<String, MarkerPoint>) savedInstanceState.getSerializable("markers");

                for(Map.Entry<String, MarkerPoint> entry : pointList.entrySet()){
                    MarkerPoint mk = entry.getValue();

                    LatLng latLng = new LatLng(mk.getLat(), mk.getLng());

                    addMarkerPoint(latLng);

                    drawRoute(latLng);
                }
            }
        }

        distanceView = (TextView) getActivity().findViewById(R.id.distance_view);
        setHasOptionsMenu(true);
    }

    private void addMarkerPoint(LatLng latLng){
        Marker marker = map.addMarker(new MarkerOptions().position(latLng));
        marker.setDraggable(true);
        MarkerPoint markerPoint = new MarkerPoint(latLng, marker);
        route.add(marker.getId(), markerPoint);
    }

    /**
     * Method to redraw the entire route using Polylines
     */
    public void drawRoute(LatLng l) {

        polylineOptions.add(l);
        polyline = map.addPolyline(polylineOptions);

        polyline.setColor(Color.GREEN);
        polyline.setGeodesic(true);
        polyline.setVisible(true);

        //get the distance, and set the distance view to show distance
        distance = route.calculateTotalDistance();
        distance = round(distance / 1000, 2);
        distanceView.setText(Double.toString(distance) + "km");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.point_to_point, container, false);
        } catch (InflateException e) {

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setRetainInstance(true);
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("markers", (Serializable) route.getPoints());

        super.onSaveInstanceState(outState);
    }

    public void setMarkerListeners(GoogleMap map) {
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                if (route.getPoints().containsKey(marker.getId())) {
                    Toast.makeText(getActivity().getBaseContext(), "We have a match", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_action_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_search:
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View promptView = layoutInflater.inflate(R.layout.search_dialog, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setView(promptView);
                final EditText input = (EditText) promptView.findViewById(R.id.userInput);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new LocationSearchAsyncTask(map, getActivity()).execute(input.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
                return true;
            case R.id.action_undo:
                return true;
            case R.id.action_marker:

                return true;
            case R.id.action_clear:
                clearMap();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Remove polylines + markers, reset points
     */
    private void clearMap(){
        polylineOptions = new PolylineOptions();
        route = new PointToPointRoute();
        map.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Method to round double to two decimal places
     *
     * @param value
     * @param places
     * @return
     */
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}