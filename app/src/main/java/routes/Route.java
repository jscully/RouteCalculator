package routes;

import android.graphics.Point;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.LinkedList;

import models.MarkerRoute;

/**
 * Created by joseph on 16/03/14.
 */
public abstract class Route {
    //LinkedList of Point objects. Point has a x and y coordinate (int)
    protected LinkedList<MarkerRoute> points = new LinkedList<MarkerRoute>();
    protected double distance;

    public Route(){
        distance = 0;
    }

    public abstract double calculateTotalDistance();

    //Return the points linkedList
    public LinkedList<MarkerRoute> getPoints(){
        return points;
    }

    public double getDistance(){
        //returns the current route distance
        return distance;
    }

    public void add(LatLng latLng, Marker marker){
        points.add(new MarkerRoute(latLng, marker));
    }


    //TODO - Create method to return boolean ifFirst()
    public MarkerRoute getLastElement(){
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
