package models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by joseph on 19/03/14.
 */
public class MarkerPoint {
    private double lat;
    private double lng;
    private Marker marker;

    public MarkerPoint(LatLng l, Marker marker){
        this.lat = l.latitude;
        this.lng = l.longitude;
        this.marker = marker;
        this.marker.setVisible(false);
        this.marker.setDraggable(true);
    }

    public MarkerPoint(double lat, double lng, Marker marker){
        this.lat = lat;
        this.lng = lng;
        this.marker = marker;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
