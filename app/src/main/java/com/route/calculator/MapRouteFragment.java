package com.route.calculator;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
public class MapRouteFragment extends Fragment {
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
                    route.setMarkerVisibility();
                } else {
                    Marker marker = map.addMarker(newMarker);
                    route.getLastElement().getMarker().setVisible(toggle);
                    route.add(latLng, marker);
                    route.setMarkerVisibility();
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
    public void drawRoute(){
        //Remove all current polylines
        PolylineOptions localOptions = new PolylineOptions();
        for(Polyline line : polylines){
            line.remove();
        }
        //redraw all polylines from points LinkedList
        for(MarkerPoint markerRoute : route.getPoints()){
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
            view = inflater.inflate(R.layout.map_fragment, container, false);
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
    }

    public void setMarkerListeners(GoogleMap map){
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
                //TODO - What to do here. When the marker has moved, update the points LinkedList with the new long + lat
                for(MarkerPoint currentMarker : route.getPoints()){
                    if(currentMarker.getMarker().equals(toBeUpdated)){
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

        getActivity().getActionBar().setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_search:
                Toast.makeText(getActivity(), "Sent from fragment", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_undo:
                undo();
                return true;
            case R.id.action_marker:
                if(route.getPoints().size() > 2){
                   toggle = !toggle;
                   route.toggleMarkers(toggle);
                }
                return true;
            case R.id.action_clear:
                if(route.getPoints().size() >= 1){
                    for(MarkerPoint m : route.getPoints()){
                        m.getMarker().remove();
                    }
                    route.getPoints().clear();
                    for(Polyline p : polylines){
                        p.remove();
                    }
                    polylines.clear();
                    distanceView.setText("0.0km");
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void undo(){
        //if we have a route and some points perform undo
        if(!route.getPoints().isEmpty()){

                //Now I want to pop the last point from routes
                MarkerPoint mk = route.getLastElement();
                //remove the last marker
                mk.getMarker().remove();
                route.getPoints().remove(mk);
                if(route.getPoints().size() > 1){
                    Log.d("TAG", "setting marker visibility");
                    route.setMarkerVisibility();
                }
                drawRoute();
        }
        else{
            Toast.makeText(getActivity(), "No markers added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Toast.makeText(getActivity(), "onPause()", Toast.LENGTH_SHORT).show();
        //I want to remove everything from the map.
        for(Polyline p : polylines){
            p.remove();
        }
        polylines.clear();
        for(MarkerPoint m : route.getPoints()){
            m.getMarker().remove();
        }
        route.getPoints().clear();
        distanceView.setText("0.0");
    }

    /**
     * Method to round double to two decimal places
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
