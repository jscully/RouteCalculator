package routes;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;

import models.MarkerPoint;

/**
 * Created by joseph on 16/03/14.
 */
public abstract class Route {
    //LinkedList of Point objects. Point has a x and y coordinate (int)
    protected LinkedList<MarkerPoint> points = new LinkedList<MarkerPoint>();
    protected double distance;

    public Route(){
        distance = 0;
    }

    public abstract double calculateTotalDistance();

    //Return the points linkedList
    public LinkedList<MarkerPoint> getPoints(){
        return points;
    }

    public double getDistance(){
        //returns the current route distance
        return distance;
    }

    public void add(LatLng latLng, Marker marker){
        points.add(new MarkerPoint(latLng, marker));
    }


    //TODO - Create method to return boolean ifFirst()
    public MarkerPoint getLastElement(){
         return points.getLast();
    }

    public boolean isFirst(LatLng l){
        if(points.indexOf(l) == 0){
            return true;
        }
        else{
            return false;
        }
    }

}
