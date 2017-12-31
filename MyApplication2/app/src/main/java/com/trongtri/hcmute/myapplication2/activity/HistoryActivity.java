package com.trongtri.hcmute.myapplication2.activity;

import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.trongtri.hcmute.myapplication2.R;
import com.trongtri.hcmute.myapplication2.models.DownPolyline;
import com.trongtri.hcmute.myapplication2.models.Location;
import com.trongtri.hcmute.myapplication2.models.ParseJsonPolyline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class HistoryActivity extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap googleMap;
    MapFragment mapFragment;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    ParseJsonPolyline parsetJsonPolyLine;
    DownPolyline downloadPolyLine;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String deviceName = "gps_1";
    ArrayList<Location> arrLocation = new ArrayList<>();
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBar();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    private void actionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

        googleMap.setMaxZoomPreference(18);
       // veDuong();
        subscribeToUpdates();

    }


    private void subscribeToUpdates() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users")
                .child(uid).child(deviceName);




        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Location location = dataSnapshot.getValue(Location.class);
                setPolyline(location);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Location location = dataSnapshot.getValue(Location.class);
                setPolyline(location);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    void setPolyline(Location location){


        arrLocation.add(location);

        Location tempLocation = new Location();
        int length = arrLocation.size();
        if(arrLocation.size() == 1){
            tempLocation = location;
            Log.d("v", "ok");
            Log.d("m", String.valueOf(tempLocation.getFlon()));
        }
        else {
            tempLocation = arrLocation.get(length - 2);
            Log.d("ss", String.valueOf(tempLocation.getFlon()));
        }

        LatLng latLng = new LatLng(location.getFlat(), location.getFlon());


        parsetJsonPolyLine = new ParseJsonPolyline();
        downloadPolyLine = new DownPolyline();
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + tempLocation.getFlat()
                + "," + tempLocation.getFlon() +
                "&destination=" + location.getFlat() + "," + location.getFlon() +
                "&key=AIzaSyCpCKPdwlP5yFULA47P9i3WILdi-_PIAAk" ;
        Log.d("eee", url);
        downloadPolyLine.execute(url);

        try {
            String dataJson = downloadPolyLine.get();
            List<LatLng> latLngList = parsetJsonPolyLine.layDanhSachToaDo(dataJson);

            PolylineOptions polylineOptions = new PolylineOptions();
            for(LatLng latLng1 : latLngList)
            {
                polylineOptions.add(latLng1);

            }

            polylineOptions.color(R.color.colorAccent);
            Polyline polyline = googleMap.addPolyline(polylineOptions);

            LatLng latLng1 = new LatLng(tempLocation.getFlat(), tempLocation.getFlon());
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(latLng1);

            LatLng latLng2 = new LatLng(location.getFlat(), location.getFlon());
            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.position(latLng2);

            googleMap.addMarker(markerOptions1);
            googleMap.addMarker(markerOptions2);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 18);
            googleMap.moveCamera(cameraUpdate);

            Log.d("ty", dataJson.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }




    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {


        ArrayList<Location> arrLocation = new ArrayList<>();

        for(DataSnapshot data : dataSnapshot.getChildren()){
            Location location = data.getValue(Location.class);
            arrLocation.add(location);


        }

        for(int i = 0; i < arrLocation.size(); i++){
            Location l1 = new Location();
            Location l2 = new Location();
            if(i == arrLocation.size() - 1){

                break;
            }
            l1 = arrLocation.get(i);
            l2 = arrLocation.get(i + 1);

            setPolyLine(l1, l2);
        }
    }

    public void setPolyLine(Location location1, Location location2){
        parsetJsonPolyLine = new ParseJsonPolyline();
        downloadPolyLine = new DownPolyline();
        String  url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + location1.getFlat()
                + "," + location1.getFlon() +
                "&destination=" + location2.getFlat() + "," + location2.getFlon() +
                "&key=AIzaSyCpCKPdwlP5yFULA47P9i3WILdi-_PIAAk" ;
        Log.d("eee", url);
        downloadPolyLine.execute(url);

        try {
            String dataJson = downloadPolyLine.get();
            List<LatLng> latLngList = parsetJsonPolyLine.layDanhSachToaDo(dataJson);

            PolylineOptions polylineOptions = new PolylineOptions();
            for(LatLng latLng : latLngList)
            {
                polylineOptions.add(latLng);

            }

            polylineOptions.color(R.color.colorAccent);
            Polyline polyline = googleMap.addPolyline(polylineOptions);

            LatLng latLng1 = new LatLng(location1.getFlat(), location1.getFlon());
            MarkerOptions markerOptions1 = new MarkerOptions();
            markerOptions1.position(latLng1);

            LatLng latLng2 = new LatLng(location2.getFlat(), location2.getFlon());
            MarkerOptions markerOptions2 = new MarkerOptions();
            markerOptions2.position(latLng2);

            googleMap.addMarker(markerOptions1);
            googleMap.addMarker(markerOptions2);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng1, 18);
            googleMap.moveCamera(cameraUpdate);

            Log.d("ty", dataJson.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
