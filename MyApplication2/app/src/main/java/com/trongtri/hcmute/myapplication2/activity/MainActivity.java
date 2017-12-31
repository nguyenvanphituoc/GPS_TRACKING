package com.trongtri.hcmute.myapplication2.activity;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
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
import com.trongtri.hcmute.myapplication2.adapter.MenuAdapter;
import com.trongtri.hcmute.myapplication2.models.DownPolyline;
import com.trongtri.hcmute.myapplication2.models.Location;
import com.trongtri.hcmute.myapplication2.R;
import com.trongtri.hcmute.myapplication2.models.ParseJsonPolyline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener {

    private GoogleMap googleMap;
    MapFragment mapFragment;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    String deviceName = "gps_1";
    Double la = 0.0, lo =  0.0;
    private long fromDate = 0;
    private long toDate = 0;
    ArrayList<Location> arrLocation = new ArrayList<>();

    ParseJsonPolyline parsetJsonPolyLine;
    DownPolyline downloadPolyLine;

    Toolbar toolbar;
    NavigationView navigationView;
    ListView lvMenu;
    DrawerLayout drawerLayout;
    private HashMap<String, Marker> mMarkers = new HashMap<>();

    int[] arrImage = {R.drawable.history, R.drawable.user, R.drawable.logout};
    String[] arrTitle = {"Lịch sử đường đi", "Quản lý tài khoản", "Đăng xuất"};
    MenuAdapter menuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d("hhh", user.getEmail() + user.getDisplayName());
        }

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        anhXa();
        actionBar();
        //https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=YOUR_API_KEY
    }

    private void actionBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_sort_by_size);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void anhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        lvMenu = (ListView) findViewById(R.id.lvMenu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        menuAdapter = new MenuAdapter(arrTitle, MainActivity.this, arrImage);
        lvMenu.setAdapter(menuAdapter);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMaxZoomPreference(18);

        subscribeToUpdates();

       /* LatLng latLng = new LatLng(10.851351, 106.772044);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        googleMap.moveCamera(cameraUpdate);*/
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        // this.onMapReady(googleMap);

        /*Iterable<DataSnapshot> nodeChild = dataSnapshot.getChildren();
        for (DataSnapshot dataSnapshotChild : nodeChild){
            Location location = dataSnapshotChild.getValue(Location.class);
            Log.d("kt", String.valueOf(location.getFlat()));
        }*/
    }

    private void subscribeToUpdates() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users")
                .child(uid).child(deviceName);

        //databaseReference.addValueEventListener(this);


        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Location location = dataSnapshot.getValue(Location.class);

                //setMarker(dataSnapshot);

                setPolyline(location);
                Log.d("zzzzzz", "co vao 1");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                Location location = dataSnapshot.getValue(Location.class);

                //setMarker(dataSnapshot);
                setPolyline(location);
                Log.d("wwwww", dataSnapshot.toString());

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                //Log.d(TAG, "Failed to read value.", error.toException());
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

    private void setMarker2(DataSnapshot dataSnapshot) {


        googleMap.clear();
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("flat").toString());
        double lng = Double.parseDouble(value.get("flon").toString());
        LatLng location = new LatLng(lat, lng);





        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 16);
        googleMap.moveCamera(cameraUpdate);
    }

    private void setMarker(DataSnapshot dataSnapshot) {


        googleMap.clear();
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("flat").toString());
        double lng = Double.parseDouble(value.get("flon").toString());
        LatLng location = new LatLng(lat, lng);


       /* if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, googleMap.addMarker(new MarkerOptions().title(key).position(location)));
            Log.d("kt", dataSnapshot.getKey());
        } else {
            mMarkers.get(key).setPosition(location);
            Log.d("kt", "tr");
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
            Log.d("t", marker.getPosition().toString());
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));*/



        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 18);
        googleMap.moveCamera(cameraUpdate);
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
