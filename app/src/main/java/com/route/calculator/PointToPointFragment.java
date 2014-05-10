package com.route.calculator;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import models.MarkerPoint;
import routes.PointToPointRoute;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointFragment extends Fragment {
    static final String TAG = "TAG";
    private GoogleMap map;
    private PointToPointRoute route;
    private double distance;
    private TextView distanceView;
    private PolylineOptions options;
    private List<Polyline> polylines = new ArrayList<Polyline>();
    public static View view;
    //boolean toggle : toggles the markers visible state
    private boolean toggle = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //Get a reference to the map once it is loaded
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        getActivity().getActionBar().setTitle("Point Route");
        //set up the marker listeners
        setMarkerListeners(map);

        //onclick listener for the map
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions newMarker = new MarkerOptions().position(latLng);
                if (route.getPoints().isEmpty()) { //if this is the first click add the point with a marker
                    Marker marker = map.addMarker(newMarker);
                    route.add(latLng, marker);
                    route.setMarkerVisibility(toggle);
                } else {
                    Marker marker = map.addMarker(newMarker);
                    route.getLast().getMarker().setVisible(toggle);
                    route.add(latLng, marker);
                    route.setMarkerVisibility(toggle);
                }
                drawRoute();
            }
        });
        distanceView = (TextView) getActivity().findViewById(R.id.distance_view);
        setHasOptionsMenu(true);
    }

    /**
     * Method to redraw the entire route using Polylines
     */
    public void drawRoute() {
        //Remove all current polylines
        PolylineOptions localOptions = new PolylineOptions();
        for (Polyline line : polylines) {
            line.remove();
            line = null;
        }
        polylines.clear();

        //redraw all polylines from points LinkedList
        for (MarkerPoint markerRoute : route.getPoints()) {
            localOptions.add(new LatLng(markerRoute.getLat(), markerRoute.getLng()));
            Polyline line = map.addPolyline(localOptions);
            polylines.add(line);
            line.setColor(Color.GREEN);
            line.setGeodesic(true);
            line.setVisible(true);
        }
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
        route = new PointToPointRoute();
        options = new PolylineOptions();
        options.width(5);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);     //  Fixed Portrait orientation
    }

    public void setMarkerListeners(GoogleMap map) {
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            Marker toBeUpdated = null;

            @Override
            public void onMarkerDragStart(Marker marker) {
                toBeUpdated = marker;
            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                for (MarkerPoint currentMarker : route.getPoints()) {
                    if (currentMarker.getMarker().equals(toBeUpdated)) {
                        //Update the point in the LinkedList MarkerPoint that has changed
                        currentMarker.setLat(marker.getPosition().latitude);
                        currentMarker.setLng(marker.getPosition().longitude);
                        currentMarker.setMarker(marker);
                        drawRoute();
                        return;
                    }
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
                undo();
                return true;
            case R.id.action_marker:
                if (route.getPoints().size() > 2) {
                    toggle = !toggle;
                    route.setMarkerVisibility(toggle);
                }
                return true;
            case R.id.action_clear:
                if (route.getPoints().size() >= 1) {
                    //I want to remove everything from the map.
                    clearRoute();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void undo() {
        //if we have a route and some points perform undo
        if (!route.getPoints().isEmpty()) {
            MarkerPoint mk = route.getLast();
            mk.getMarker().remove();
            route.getPoints().remove(mk);
            mk = null;
            if (route.getPoints().size() > 1) {
                route.setMarkerVisibility(toggle);
            }
            drawRoute();
        } else {
            Toast.makeText(getActivity(), "No markers added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        clearRoute();
    }

    private void clearRoute() {
        for (Polyline p : polylines) {
            p.remove();
            p = null;
        }
        polylines.clear();
        for (MarkerPoint m : route.getPoints()) {
            m.removeInstance();
            m = null;
        }
        map.clear();
        route.getPoints().clear();
        distanceView.setText("0.0");
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