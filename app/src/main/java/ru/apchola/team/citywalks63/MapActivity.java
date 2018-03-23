package ru.apchola.team.citywalks63;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.AudioAttributesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap mMap;
    GeoApiContext geoApiContext = new GeoApiContext.Builder()
            .apiKey("AIzaSyDwjD8XlgKXQ_9sMnkKrxMzVnt2M4uDtdw")
            .build();
    LocationManager locationManager;
    Polyline route;

    ImageButton walkButton;

    boolean markerFocused = false;
    com.google.maps.model.LatLng userLocation = null;
    com.google.maps.model.LatLng destination = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);

        walkButton = findViewById(R.id.walk_button);

        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<com.google.maps.model.LatLng> places = new ArrayList<com.google.maps.model.LatLng>();
                places.add(userLocation);
                places.add(destination);
                drawWalkPath(places);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        BitmapDrawable bitmapDraw=(BitmapDrawable) getResources().getDrawable(R.drawable.column_icon, null);
        Bitmap b=bitmapDraw.getBitmap();
        Bitmap columnMarker = Bitmap.createScaledBitmap(b, 150, 150, false);

        LatLngBounds samara = new LatLngBounds(new LatLng(53.1, 50.15), new LatLng(53.30, 50.28));
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(samara, 0));
        mMap.setMaxZoomPreference(20);
        mMap.setMinZoomPreference(11);

        mMap.getUiSettings().setMapToolbarEnabled(false);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }

        //******** Достопримечательности

        final Marker stellaRookMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(53.21593, 50.13217))
                .title(getString(R.string.stella_rook))
                .icon(BitmapDescriptorFactory.fromBitmap(columnMarker)));

        Marker monumentOfGloryMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(53.2038, 50.10993))
                .title(getString(R.string.monument_of_glory))
                .icon(BitmapDescriptorFactory.fromBitmap(columnMarker)));

        Marker kuibyshevSquareMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(53.19541, 50.10209))
                .title(getString(R.string.kuibyshev_square))
                .icon(BitmapDescriptorFactory.fromBitmap(columnMarker)));

        Marker zhigulevskyBreweryMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(53.2008, 50.09875))
                .title(getString(R.string.zhigulevsky_brewery))
                .icon(BitmapDescriptorFactory.fromBitmap(columnMarker)));

        Marker yl2Marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(53.25105, 50.22265))
                .title(getString(R.string.yl_2))
                .icon(BitmapDescriptorFactory.fromBitmap(columnMarker)));

        Marker spaceSamaraMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(53.21268, 50.14527))
                .title(getString(R.string.space_samara))
                .icon(BitmapDescriptorFactory.fromBitmap(columnMarker)));

        //********

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                destination = new com.google.maps.model.LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                markerFocused = true;
                walkButton.setVisibility(View.VISIBLE);
                return false;
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(markerFocused)
                {
                    markerFocused = false;
                    walkButton.setVisibility(View.GONE);
                }
            }
        });
    }

    public void drawWalkPath(List<com.google.maps.model.LatLng> places)
    {
        if(route != null)
            route.remove();

        com.google.maps.model.LatLng[] waypoints = new com.google.maps.model.LatLng[places.size()-2];

        for(int i = 1; i < places.size() - 1; i++)
        {
            waypoints[i-1] = places.get(i);
        }

        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(geoApiContext)
                    .mode(TravelMode.WALKING)
                    .origin(places.get(0))
                    .destination(places.get(places.size() - 1))
                    .waypoints(waypoints).await();

        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<com.google.maps.model.LatLng> path = result.routes[0].overviewPolyline.decodePath();

        PolylineOptions line = new PolylineOptions();

        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();

        for (int i = 0; i < path.size(); i++) {
            line.add(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
            latLngBuilder.include(new com.google.android.gms.maps.model.LatLng(path.get(i).lat, path.get(i).lng));
        }

        line.width(16f).color(R.color.lightSkyBlue);

        route = mMap.addPolyline(line);

        LatLngBounds latLngBounds = latLngBuilder.build();
        CameraUpdate track = CameraUpdateFactory.newLatLngBounds(latLngBounds, 100, 100, 25);//width это размер нашего экрана
        mMap.moveCamera(track);
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = new com.google.maps.model.LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
