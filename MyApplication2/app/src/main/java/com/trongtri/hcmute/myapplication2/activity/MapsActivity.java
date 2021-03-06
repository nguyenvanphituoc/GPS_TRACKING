/* package com.trongtri.hcmute.myapplication2.activity;

import android.content.Intent;
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

    private static final String TAG = MainActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    //get current user
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String uid = user.getUid();
    private long fromDate = 0;
    private long toDate = 0;
    String deviceName = "gps_1";
    MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        //The second parameter below is the default string returned if the value is not there.
        if (getIntent().getExtras() != null) {
            fromDate = intent.getExtras().getLong("fromDate") / 1000;
            toDate = intent.getExtras().getLong("toDate") / 1000;
            deviceName = intent.getExtras().getString("deviceName");
        }

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        subscribeToUpdates();
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        mMap.setMaxZoomPreference(16);
//        subscribeToUpdates();
    }

    private void subscribeToUpdates() {
        // Functionality coming next step

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path) + uid + "/" + deviceName);
        Log.d("reffffffff", "" + ref);
        if (fromDate > 0 && toDate > 0) {
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Location location = dataSnapshot.getValue(Location.class);
                    Calendar cal = Calendar.getInstance();
                    Log.d(TAG, "onChildAdded: setmakter");
                    setMarker(dataSnapshot);
//                    if (location.getTime() >= fromDate && location.getTime() <= toDate) {
//                        setMarker(dataSnapshot);
//                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Location location = dataSnapshot.getValue(Location.class);
                    Calendar cal = Calendar.getInstance();
                    Log.d(TAG, "onChildAdded: setmakter");
                    setMarker(dataSnapshot);
//                    if (location.getTime() >= fromDate && location.getTime() <= toDate) {
//                        setMarker(dataSnapshot);
//                    }

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "Failed to read value.", error.toException());
                }
            });
        } else {
            ref.limitToLast(1).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Location location = dataSnapshot.getValue(Location.class);
//                Calendar cal = Calendar.getInstance();
//                if(location.time > (cal.getTimeInMillis() - 6000)/1000) {
//                    setMarker(dataSnapshot);
//                }
                    setMarker(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    setMarker(dataSnapshot);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Log.d(TAG, "Failed to read value.", error.toException());
                }
            });
        }

    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // Functionality coming next step
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        if(fromDate == 0 && toDate == 0){
            mMap.clear();
        }
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("flat").toString());
        double lng = Double.parseDouble(value.get("flon").toString());
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(builder.build().getCenter(), 17));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
} */


//package com.trongtri.hcmute.myapplication2.activity;
//
//        import android.content.Intent;
//        import android.support.design.widget.NavigationView;
//        import android.support.v4.view.GravityCompat;
//        import android.support.v4.widget.DrawerLayout;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.support.v7.widget.Toolbar;
//        import android.util.Log;
//        import android.view.View;
//        import android.widget.ListView;
//
//        import com.google.android.gms.maps.CameraUpdate;
//        import com.google.android.gms.maps.CameraUpdateFactory;
//        import com.google.android.gms.maps.GoogleMap;
//        import com.google.android.gms.maps.MapFragment;
//        import com.google.android.gms.maps.OnMapReadyCallback;
//        import com.google.android.gms.maps.model.LatLng;
//        import com.google.android.gms.maps.model.LatLngBounds;
//        import com.google.android.gms.maps.model.Marker;
//        import com.google.android.gms.maps.model.MarkerOptions;
//        import com.google.android.gms.maps.model.Polyline;
//        import com.google.android.gms.maps.model.PolylineOptions;
//        import com.google.firebase.auth.FirebaseAuth;
//        import com.google.firebase.auth.FirebaseUser;
//        import com.google.firebase.database.ChildEventListener;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.ValueEventListener;
//        import com.trongtri.hcmute.myapplication2.adapter.MenuAdapter;
//        import com.trongtri.hcmute.myapplication2.models.DownPolyline;
//        import com.trongtri.hcmute.myapplication2.models.Location;
//        import com.trongtri.hcmute.myapplication2.R;
//        import com.trongtri.hcmute.myapplication2.models.ParseJsonPolyline;
//
//        import java.util.ArrayList;
//        import java.util.Calendar;
//        import java.util.HashMap;
//        import java.util.List;
//        import java.util.concurrent.ExecutionException;
//
//public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, ValueEventListener {
//
//    private static final String TAG = MainActivity.class.getSimpleName();
//    private HashMap<String, Marker> mMarkers = new HashMap<>();
//    private GoogleMap mMap;
//    //get current user
//    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    String uid = user.getUid();
//    private long fromDate = 0;
//    private long toDate = 0;
//    String deviceName = "gps_1";
//    MapFragment mapFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Intent intent = getIntent();
//        //The second parameter below is the default string returned if the value is not there.
//        if (getIntent().getExtras() != null) {
//            fromDate = intent.getExtras().getLong("fromDate") / 1000;
//            toDate = intent.getExtras().getLong("toDate") / 1000;
//            deviceName = intent.getExtras().getString("deviceName");
//        }
//
////        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
////                .findFragmentById(R.id.map);
////        mapFragment.getMapAsync(this);
//        subscribeToUpdates();
//        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        // Authenticate with Firebase when the Google map is loaded
//        mMap = googleMap;
//        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
////        mMap.setMaxZoomPreference(16);
////        subscribeToUpdates();
//    }
//
//    private void subscribeToUpdates() {
//        // Functionality coming next step
//
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path) + uid + "/" + deviceName);
//        Log.d("reffffffff", "" + ref);
//        if (fromDate > 0 && toDate > 0) {
//            ref.addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                    Location location = dataSnapshot.getValue(Location.class);
//                    Calendar cal = Calendar.getInstance();
//                    Log.d(TAG, "onChildAdded: setmakter");
//                    setMarker(dataSnapshot);
////                    if (location.getTime() >= fromDate && location.getTime() <= toDate) {
////                        setMarker(dataSnapshot);
////                    }
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                    Location location = dataSnapshot.getValue(Location.class);
//                    Calendar cal = Calendar.getInstance();
//                    Log.d(TAG, "onChildAdded: setmakter");
//                    setMarker(dataSnapshot);
////                    if (location.getTime() >= fromDate && location.getTime() <= toDate) {
////                        setMarker(dataSnapshot);
////                    }
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    Log.d(TAG, "Failed to read value.", error.toException());
//                }
//            });
//        } else {
//            ref.limitToLast(1).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
////                Location location = dataSnapshot.getValue(Location.class);
////                Calendar cal = Calendar.getInstance();
////                if(location.time > (cal.getTimeInMillis() - 6000)/1000) {
////                    setMarker(dataSnapshot);
////                }
//                    setMarker(dataSnapshot);
//                }
//
//                @Override
//                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                    setMarker(dataSnapshot);
//
//                }
//
//                @Override
//                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                }
//
//                @Override
//                public void onChildRemoved(DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onCancelled(DatabaseError error) {
//                    Log.d(TAG, "Failed to read value.", error.toException());
//                }
//            });
//        }
//
//    }
//
//    private void setMarker(DataSnapshot dataSnapshot) {
//        // Functionality coming next step
//        // When a location update is received, put or update
//        // its value in mMarkers, which contains all the markers
//        // for locations received, so that we can build the
//        // boundaries required to show them all on the map at once
//        if(fromDate == 0 && toDate == 0){
//            mMap.clear();
//        }
//        String key = dataSnapshot.getKey();
//        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
//        double lat = Double.parseDouble(value.get("flat").toString());
//        double lng = Double.parseDouble(value.get("flon").toString());
//        LatLng location = new LatLng(lat, lng);
//        if (!mMarkers.containsKey(key)) {
//            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(key).position(location)));
//        } else {
//            mMarkers.get(key).setPosition(location);
//        }
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        for (Marker marker : mMarkers.values()) {
//            builder.include(marker.getPosition());
//        }
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(builder.build().getCenter(), 17));
////        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
//    }
//
//    @Override
//    public void onDataChange(DataSnapshot dataSnapshot) {
//
//    }
//
//    @Override
//    public void onCancelled(DatabaseError databaseError) {
//
//    }
//}
