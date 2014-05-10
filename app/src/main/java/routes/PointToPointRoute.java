package routes;

import android.location.Location;
import android.util.Log;

import models.MarkerPoint;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointRoute extends Route {

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
        Log.d("TAG", "Returning the distance : " + distance);
        return distance;
    }

    //Method to set the first and last element in the LinkedList to display a marker
    public void setMarkerVisibility(boolean markerVisible){
        for (MarkerPoint m : points){
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
    }
}
