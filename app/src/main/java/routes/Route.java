package routes;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import models.MarkerPoint;

/**
 * Created by joseph on 16/03/14.
 */
public abstract class Route {
    //LinkedList of Point objects. Point has a x and y coordinate (int)
    protected ArrayList<MarkerPoint> points = new ArrayList<MarkerPoint>();
    protected double distance;

    public Route() {
        distance = 0;
    }

    public abstract double calculateTotalDistance();

    public abstract void setMarkerVisibility(boolean toggle);

    //public abstract void toggleMarkers(boolean toggle);

    //Return the points linkedList
    public ArrayList<MarkerPoint> getPoints() {
        return points;
    }

    public void add(LatLng latLng, Marker marker) {
        points.add(new MarkerPoint(latLng, marker));
    }
    public void add(LatLng latLng){
        points.add(new MarkerPoint(latLng));
    }

    public MarkerPoint getLast(){
        return points.get(points.size() -1);
    }
    public MarkerPoint getFirst(){
        return points.get(0);
    }
    public boolean isFirst(MarkerPoint mk){
        if(points.get(0).equals(mk)){
            return true;
        }
        else{
            return false;
        }
    }
    public boolean isLast(MarkerPoint mk){
        if(points.get(points.size() - 1).equals(mk)){
            return true;
        }
        else{
            return false;
        }
    }
}
