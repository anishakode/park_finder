package com.example.parkfinder;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.parkfinder.adapter.CustomInfoWindow;
import com.example.parkfinder.data.Repository;
import com.example.parkfinder.databinding.ActivityMapsBinding;
import com.example.parkfinder.model.Park;
import com.example.parkfinder.model.ParkViewModel;
import com.example.parkfinder.util.Util;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private ParkViewModel parkViewModel;
    private List<Park> parkList;
    private CardView cardView;
    private EditText stateCodeET;
    private ImageButton searchButton;
    private String code = "";
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        parkViewModel = new ViewModelProvider(this)
                .get(ParkViewModel.class);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cardView = findViewById(R.id.cardView);
        stateCodeET = findViewById(R.id.floating_state_value_ET);
        searchButton = findViewById(R.id.floating_search_button);

        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if(id == R.id.map_nav_button){
                // show the map view
                if(cardView.getVisibility() == View.INVISIBLE || cardView.getVisibility() == View.GONE){
                    cardView.setVisibility(View.VISIBLE);
                }
                parkList.clear();
                mMap.clear();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.map,mapFragment)
                        .commit();
                mapFragment.getMapAsync(this);
                return true;
            }else if(id == R.id.park_nav_button){
                selectedFragment = ParksFragment.newInstance();
                cardView.setVisibility(View.GONE);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map,selectedFragment)
                    .commit();
            return true;
        });

        searchButton.setOnClickListener(view -> {
            parkList.clear();
            Util.hideKeyboard(view);
            String stateCode = stateCodeET.getText().toString().trim();
            if(!TextUtils.isEmpty(stateCode)){
                code = stateCode;
                parkViewModel.selectCode(code);
                onMapReady(mMap);
                stateCodeET.setText("");
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        mMap.setOnInfoWindowClickListener(this);
        parkList = new ArrayList<>();
        parkList.clear();
        populateMap();
    }

    private void populateMap() {
        mMap.clear();
        Repository.getParks(parks -> {
            parkList = parks;
            for (Park park: parks){
                LatLng latlng = new LatLng(Double.parseDouble(park.getLatitude()), Double.parseDouble(park.getLongitude()));

                MarkerOptions markerOptions =
                        new MarkerOptions()
                        .position(latlng)
                        .title(park.getName())
                        .snippet(park.getStates())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                Marker marker = mMap.addMarker(markerOptions);
                marker.setTag(park);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 5));
                Log.d("park", "onMapReady: " + park.getFullName());
            }
            parkViewModel.setSelectedParks(parkList);
            Log.d("SIZE", "onMapReady: " + parkList.size());
        }, code);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        cardView.setVisibility(View.GONE);
        // go to details fragment
        parkViewModel.setSelectedPark((Park) marker.getTag());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.map, DetailsFragment.newInstance())
                .commit();
    }
}