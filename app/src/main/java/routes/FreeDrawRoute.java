package routes;

import android.location.Location;
import android.util.Log;

import models.MarkerPoint;

/**
 * Created by joseph on 02/05/14.
 */
public class FreeDrawRoute extends Route {

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
        Log.d("TAG", "Returning the distance : " + distance);
        return distance;
    }

    public void toggleMarkers(boolean visible){
        for(MarkerPoint markerRoute : points){
            markerRoute.getMarker().setVisible(visible);
        }
        //make sure first and last markers are visible
        setMarkerVisibility();
    }

    //Method to set the first and last element in the LinkedList to display a marker
    public void setMarkerVisibility(){
        points.get(0).getMarker().setVisible(true);
        points.get(points.size() - 1).getMarker().setVisible(true);
    }
}
