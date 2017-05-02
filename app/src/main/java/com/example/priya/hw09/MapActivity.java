package com.example.priya.hw09;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.start;
import static com.example.priya.hw09.R.id.map;
/**
 * Created by priyank Verma
 */
public class MapActivity extends Activity implements OnMapReadyCallback, RoutingListener{


    LocationManager mLocationMngr;
    LocationListener mLocListener;
    private GoogleMap mMap;
    ArrayList<Location> locs;

    private List<Polyline> polylines = new ArrayList<>();
    protected LatLng start;
    protected LatLng end;
    ArrayList<LatLng> WPs = new ArrayList<>();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);
        mLocationMngr = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locs = new ArrayList<>();

        if(getIntent().getExtras()!=null){
            locs = (ArrayList<Location>) getIntent().getSerializableExtra("trips");
        }

        createRoute();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    public void createRoute() {//latlng arraylist

        progressDialog = ProgressDialog.show(this, "Please wait",
                "Loading route", true);


        start = new LatLng(locs.get(0).getLat(), locs.get(0).getLng());

        for(int x=1; x< locs.size()-1; x++){
            WPs.add(new LatLng(round(locs.get(x).getLat(),3), round(locs.get(x).getLng(),3)));
        }

        Log.d("start point", start.latitude + ""+ start.longitude + "");

         end = new LatLng(locs.get(locs.size()-1).getLat(), locs.get(locs.size()-1).getLng());

        if(WPs.size()==0) {

            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start, end, start)
                    .build();
            routing.execute();
        }
        else if (WPs.size()==1) {

            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start, WPs.get(0), end, start)
                    .build();
            routing.execute();

            Log.d("latlng", WPs.get(0).latitude + " " + WPs.get(0).longitude);
        }
        else if (WPs.size()==2) {

            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start,WPs.get(0), WPs.get(1), end, start)
                    .build();
            routing.execute();
        }
        else if (WPs.size()==3) {
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(start, WPs.get(0), WPs.get(1), WPs.get(2), end, start)
                    .build();
            routing.execute();
        }

        Log.d("trips", locs.toString());

    }


    @Override
    public void onRoutingFailure(RouteException e) {
        progressDialog.dismiss();
        Log.d("routing failure", e.toString());
        Toast.makeText(MapActivity.this, "Invalid Route", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {


        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(4);


        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.

            //In case of more than 5 alternative routes

        Log.d("check1", "");
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.BLUE);
            polyOptions.width(10);
            polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);




        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(start);
        mMap.addMarker(options);

        for(int x=0; x<WPs.size(); x++){
            options = new MarkerOptions();
            options.position(WPs.get(x));
            mMap.addMarker(options).setAlpha(5);
        }

        // End marker
        options = new MarkerOptions();
        options.position(end);
        mMap.addMarker(options).setAlpha(5);

        progressDialog.dismiss();
    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
