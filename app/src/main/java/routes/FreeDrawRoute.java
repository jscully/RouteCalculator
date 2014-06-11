package routes;

import android.location.Location;
import android.util.Log;

import models.MarkerPoint;

/**
 * Created by joseph on 02/05/14.
 */
public class FreeDrawRoute extends Route {
    private MarkerPoint p = null;
    private MarkerPoint p1 = null;

    @Override
    public double calculateTotalDistance() {
        float currentDistance = 0;
        distance = 0;
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
