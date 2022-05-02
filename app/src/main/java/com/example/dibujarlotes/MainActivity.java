package com.example.dibujarlotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.dibujarlotes.databinding.ActivityMainBinding;
import com.example.dibujarlotes.viewmodel.TipoAreaViewModel;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay2;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private MyLocationNewOverlay mLocationOverlay;
    private CompassOverlay mCompassOverlay;
    private RotationGestureOverlay mRotationGestureOverlay;
    private MinimapOverlay mMinimapOverlay;
    private DisplayMetrics dm;
    private ScaleBarOverlay mScaleBarOverlay;
    private LocationManager locationManager;
    private boolean MedicionIniciada = false;
    private boolean MedicionDetenida = true;
    private ArrayList<GeoPoint> PuntosLote = new ArrayList<GeoPoint>();
    Timer timer = new Timer();

    private TipoAreaViewModel mTipoAreaViewModel;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        com.example.dibujarlotes.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        mTipoAreaViewModel = new ViewModelProvider(this).get(TipoAreaViewModel.class);

        mTipoAreaViewModel.getObservable().observe(this, words -> {
            // Update the cached copy of the words in the adapter.
            //adapter.submitList(words);
        });

        map = (MapView) findViewById(R.id.map);
        IMapController mapController = map.getController();
        mapController.setZoom(14D);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        map.setHasTransientState(true);

        requestPermissionsIfNecessary(new String[]{
                // if you need to show the current location, uncomment the line below
                // Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });

        this.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        GeoPoint startPoint = new GeoPoint(locationGPS.getLatitude(), locationGPS.getLongitude());
        mapController.setCenter(startPoint);

        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getApplicationContext()), map);
        this.mLocationOverlay.enableMyLocation();
        this.map.getOverlays().add(this.mLocationOverlay);

        this.dm = getApplicationContext().getResources().getDisplayMetrics();
        this.mScaleBarOverlay = new ScaleBarOverlay(map);
        this.mScaleBarOverlay.setCentred(true);
//play around with these values to get the location on screen in the right place for your application
        this.mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);
        this.map.getOverlays().add(this.mScaleBarOverlay);
    }


    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            runOnUiThread(new Runnable() {

                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    if (MainActivity.this.MedicionIniciada && !MainActivity.this.MedicionDetenida) {
                        Location locationGPS = MainActivity.this.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        GeoPoint mGeoPoint = new GeoPoint(locationGPS.getLatitude(), locationGPS.getLongitude());


                        if (PuntosLote.size() > 1) {
                            ArrayList<GeoPoint> linea = new ArrayList<GeoPoint>();
                            linea.add(PuntosLote.get(PuntosLote.size() - 1));
                            linea.add(mGeoPoint);
                            Polyline line = new Polyline();   //see note below!
                            line.setPoints(linea);
                            map.getOverlayManager().add(line);
                        }
                        PuntosLote.add(mGeoPoint);

                    }
                }
            });
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>(Arrays.asList(permissions).subList(0, grantResults.length));
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    public void IniciarMedicion(View view) {
        if (!this.MedicionIniciada) {
            PuntosLote.clear();
            this.MedicionIniciada = true;
            timer.schedule(task, 0, 5000);
        }
        this.MedicionDetenida = !this.MedicionDetenida;
    }

    public void FinalizarMedicion(View view) {
        this.MedicionIniciada = false;
        this.MedicionDetenida = true;
        timer.cancel();
    }
}