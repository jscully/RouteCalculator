package routes;

import android.location.Location;
import android.util.Log;
import models.MarkerRoute;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointRoute extends Route {

    public PointToPointRoute(){

    }

    @Override
    public double calculateTotalDistance() {
        float currentDistance = 0;
        for(MarkerRoute point : points){
            if(!points.getLast().equals(point)){ // loop through the list until we are at the last point.
                MarkerRoute p = points.get(points.indexOf(point));
                MarkerRoute p1 = points.get(points.indexOf(point) + 1);

                float[] results = new float[1];
                Location.distanceBetween(p.getLat(), p.getLng(), p1.getLat(), p1.getLng(), results);
                currentDistance = currentDistance + results[0];
                distance = Math.round(currentDistance);
            }
        }
        return distance;
    }

    public void toggleMarkers(boolean visible){
        for(MarkerRoute markerRoute : points){
            markerRoute.getMarker().setVisible(visible);
        }
    }

    //Method to set the first and last element in the LinkedList to display a marker
    public void setMarkerVisibility(){
        points.getFirst().getMarker().setVisible(true);
        points.getLast().getMarker().setVisible(true);
    }
}
