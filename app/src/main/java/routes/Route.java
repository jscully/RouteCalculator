package routes;

import android.graphics.Point;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

/**
 * Created by joseph on 16/03/14.
 */
public abstract class Route {
    //LinkedList of Point objects. Point has a x and y coordinate (int)
    protected LinkedList<LatLng> points = new LinkedList<LatLng>();
    protected double distance;

    public Route(){
        distance = 0;
    }

    //Return the points linkedList
    public LinkedList<LatLng> getPoints(){
        return points;
    }

    public double getDistance(){
        //returns the current route distance
        return distance;
    }

    public void add(LatLng p){
        points.add(p);
    }

    public abstract double calculateTotalDistance();

    //TODO - Create method to return boolean ifFirst()
    public LatLng getLastElement(){
         return points.getLast();
    }

    public boolean isFirst(LatLng l){
        if(points.indexOf(l) == 0){
            Log.d("TAG", "Returning true");
            return true;
        }
        else{
            Log.d("TAG", "Returning false");
            return false;
        }
    }
}
