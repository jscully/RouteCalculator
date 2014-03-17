package routes;

import android.graphics.Point;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointRoute extends Route {

    public PointToPointRoute(){

    }

    @Override
    public double calculateTotalDistance() {
        float currentDistance = 0;
        for(LatLng point : points){
            if(!points.getLast().equals(point)){ // loop through the list until we are at the last point.
                LatLng p = points.get(points.indexOf(point));
                LatLng p1 = points.get(points.indexOf(point) + 1);

                float[] results = new float[1];
                Location.distanceBetween(p.latitude, p.longitude, p1.latitude, p1.longitude, results);
                // TODO - calculate the distance. tidy this up. DistanceBetween would be nicer
                currentDistance = currentDistance + results[0];
                distance = Math.round(currentDistance);
            }
        }
        return distance;
    }

}
