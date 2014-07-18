package routes;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.route.calculator.PointToPointFragment;

import models.MarkerPoint;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointRoute extends Route {
    static final String TAG = "TAG";

    public PointToPointRoute(){

    }

    @Override
    public double calculateTotalDistance() {
        float currentDistance = 0;
        distance = 0;
        MarkerPoint p = null;
        MarkerPoint p1 = null;
        for(MarkerPoint point : points){
            if(!points.get(points.size() - 1).equals(point)){ // loop through the list until we are at the last point.
                p = points.get(points.indexOf(point));
                p1 = points.get(points.indexOf(point) + 1);

                float[] results = new float[1];
                Location.distanceBetween(p.getLat(), p.getLng(), p1.getLat(), p1.getLng(), results);
                currentDistance = currentDistance + results[0];
                distance = Math.round(currentDistance);
            }
        }
        return distance;
    }

    //Method to set the first and last element in the LinkedList to display a marker
    public void setMarkerVisibility(boolean markerVisible){
        Log.d(TAG, "Points size: " + points.size());
        //first remove all markers. Then add them to the map again. This is for cases that undo()
        // has been called, the markers do not exist on the map so setVisible does not display the marker on the map.
        for(MarkerPoint mk : points){
            mk.getMarker().remove();
        }
        for (MarkerPoint m : points){
            Marker marker = PointToPointFragment.map.addMarker(new MarkerOptions().position(new LatLng(m.getLat(), m.getLng())));
            m.setMarker(marker);
            if (isFirst(m)) {
                m.getMarker().setVisible(true);
            }
            else if (isLast(m)) {
                m.getMarker().setVisible(true);
            }
            else{
                m.getMarker().setVisible(markerVisible);
            }
        }
        Log.d(TAG, "----------------------------------------------------------------------");
    }
}
