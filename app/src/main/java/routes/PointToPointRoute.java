package routes;

import android.location.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import models.MarkerPoint;

/**
 * Created by joseph on 16/03/14.
 */
public class PointToPointRoute implements Route{
    static final String TAG = "TAG";
    private Map<String, MarkerPoint> points = null;
    private double distance = 0;

    public PointToPointRoute(){
        points = new LinkedHashMap<String, MarkerPoint>();
    }


    public void add(String ID, MarkerPoint point){
        points.put(ID, point);
    }

    public Map<String, MarkerPoint> getPoints(){
        return points;
    }

    public void setPoints(HashMap<String, MarkerPoint> points){
        this.points = points;
    }

    @Override
    public double calculateTotalDistance() {
        float currentDistance = 0;
        distance = 0;
        MarkerPoint p = null;
        MarkerPoint p1 = null;

        ArrayList<MarkerPoint> values = new ArrayList<MarkerPoint>(points.values());

        for(MarkerPoint point : values){
            if(!values.get(values.size() - 1).equals(point)){ // loop through the list until we are at the last point.
                p = values.get(values.indexOf(point));
                p1 = values.get(values.indexOf(point) + 1);

                float[] results = new float[1];
                Location.distanceBetween(p.getLat(), p.getLng(), p1.getLat(), p1.getLng(), results);
                currentDistance = currentDistance + results[0];
                distance = Math.round(currentDistance);
            }
        }
        return distance;
    }


}
