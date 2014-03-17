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

import java.util.Locale;

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
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String title = latLng.toString();
                MarkerOptions newMarker = new MarkerOptions().position(latLng)
                        .title(title);
                map.addMarker(newMarker);
                route.add(latLng);
                //TODO - draw the polyline between points
                options.add(latLng);
                Polyline line = map.addPolyline(options);
                line.setVisible(true);
            }
        });

        //Get a reference to the calculate button and attach a listener
        calculate = (Button) getView().findViewById(R.id.calculate_button);
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO - calculations for the markeres displayed on the map.
                //if there are more than one set of points on the map calculate.
                if(route != null){
                    distance = route.calculateTotalDistance();
                    Toast.makeText(getActivity(), "Distance: " + distance, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            Log.d("TAG", "Destroying the map view");
        }

        super.onDestroyView();
    }
}
