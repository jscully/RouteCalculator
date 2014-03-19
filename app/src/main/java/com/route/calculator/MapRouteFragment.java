package com.route.calculator;


import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import models.MarkerRoute;
import routes.PointToPointRoute;

/**
 * Created by joseph on 16/03/14.
 */
public class MapRouteFragment extends Fragment {
    private GoogleMap map;
    private Button calculate;
    private PointToPointRoute route;
    private double distance;
    private PolylineOptions options;
    private List<Polyline> polylines = new ArrayList<Polyline>();

    public MapRouteFragment(){

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        route = new PointToPointRoute();
        options = new PolylineOptions();
        options.width(5);
        options.color(Color.RED);

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
                    route.getLastElement().getMarker().setVisible(false);
                    route.add(latLng, marker);
                    route.setMarkerVisibility();
                }
                drawRoute();
            }
        });

        //Get a reference to the calculate button and attach a listener
        calculate = (Button) getView().findViewById(R.id.calculate_button);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if there are more than one set of points on the map calculate.
                if(route != null){
                    distance = route.calculateTotalDistance();
                    Toast.makeText(getActivity(), "Distance: " + distance, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        for(MarkerRoute markerRoute : route.getPoints()){
            localOptions.add(new LatLng(markerRoute.getLat(), markerRoute.getLng()));
            Polyline line = map.addPolyline(localOptions);
            polylines.add(line);
            line.setGeodesic(true);
            line.setVisible(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onDestroyView() {

        FragmentManager fm = getFragmentManager();

        Fragment xmlFragment = fm.findFragmentById(R.id.map);
        if (xmlFragment != null) {
            fm.beginTransaction().remove(xmlFragment).commit();
        }

        super.onDestroyView();
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
                for(MarkerRoute currentMarker : route.getPoints()){
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
}
