package pe.smarttech.covid19;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.here.sdk.core.Anchor2D;
import com.here.sdk.core.GeoCircle;
import com.here.sdk.core.GeoCoordinates;
import com.here.sdk.mapviewlite.MapCircle;
import com.here.sdk.mapviewlite.MapCircleStyle;
import com.here.sdk.mapviewlite.MapImage;
import com.here.sdk.mapviewlite.MapImageFactory;
import com.here.sdk.mapviewlite.MapMarker;
import com.here.sdk.mapviewlite.MapMarkerImageStyle;
import com.here.sdk.mapviewlite.MapScene;
import com.here.sdk.mapviewlite.MapStyle;
import com.here.sdk.mapviewlite.MapViewLite;
import com.here.sdk.mapviewlite.PixelFormat;

import org.json.JSONException;
import org.json.JSONObject;

public class HereActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private MapViewLite mapView;
    private LocationListener milocListener;
    LocationManager milocManager;
    double lat;
    double lng;
    MapMarker yo;
    MapImage mapImageYo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_here);
        // Get a MapViewLite instance from the layout.
        mapView = findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        loadMapScene();
    }
    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {
                    GeoCoordinates marker1 = new GeoCoordinates(-16.390216,-71.5207997);
                    GeoCoordinates marker2 = new GeoCoordinates(-16.435869,-71.5374447);
                    GeoCoordinates marker3 = new GeoCoordinates(-16.444472,-71.5198577);
                    GeoCoordinates marker4 = new GeoCoordinates(-16.455372,-71.5316107);
                    GeoCoordinates marker5 = new GeoCoordinates(-16.388279,-71.5768887);
                    mapView.getCamera().setTarget(new GeoCoordinates(-16.39889,-71.5390867));
                    mapView.getCamera().setZoomLevel(14);

                    MapImage mapImage = MapImageFactory.fromResource(getBaseContext().getResources(), R.drawable.markericon4);
                    MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
                    mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5f, 1));
                    MapScene mapScene = mapView.getMapScene();

                    MapMarker mapMarker = new MapMarker(marker1);
                    mapMarker.addImage(mapImage, mapMarkerImageStyle);
                    MapCircle mapCircle = createMapCircle(marker1);

                    mapScene.addMapCircle(mapCircle);

                    MapMarker mapMarker2 = new MapMarker(marker2);
                    mapMarker2.addImage(mapImage, mapMarkerImageStyle);
                    MapCircle mapCircle2 = createMapCircle(marker2);
                    mapScene.addMapCircle(mapCircle2);

                    MapMarker mapMarker3 = new MapMarker(marker3);
                    mapMarker3.addImage(mapImage, mapMarkerImageStyle);
                    MapCircle mapCircle3 = createMapCircle(marker3);
                    mapScene.addMapCircle(mapCircle3);

                    MapMarker mapMarker4 = new MapMarker(marker4);
                    mapMarker4.addImage(mapImage, mapMarkerImageStyle);
                    MapCircle mapCircle4 = createMapCircle(marker4);
                    mapScene.addMapCircle(mapCircle4);

                    MapMarker mapMarker5 = new MapMarker(marker5);
                    mapMarker5.addImage(mapImage, mapMarkerImageStyle);
                    MapCircle mapCircle5 = createMapCircle(marker5);
                    mapScene.addMapCircle(mapCircle5);

                    mapView.getMapScene().addMapMarker(mapMarker);
                    mapView.getMapScene().addMapMarker(mapMarker2);
                    mapView.getMapScene().addMapMarker(mapMarker3);
                    mapView.getMapScene().addMapMarker(mapMarker4);
                    mapView.getMapScene().addMapMarker(mapMarker5);


                    //YO
                    mapImageYo = MapImageFactory.fromResource(getBaseContext().getResources(), R.drawable.gpsyo);




                    milocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    milocListener = new MiLocationListener();
                    //Permisos de geolocalización
                    if (ContextCompat.checkSelfPermission(getBaseContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        // Should we show an explanation?
                        Log.d("GPS", "ERROR ");
                    } else {
                        Log.d("GPS", "ok ");
                        milocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, milocListener);
                    }

                } else {
                    Log.d("HERE", "onLoadScene failed: " + errorCode.toString());
                }
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
    private MapCircle createMapCircle(GeoCoordinates marker) {
        float radiusInMeters = 300;
        GeoCircle geoCircle = new GeoCircle(marker, radiusInMeters);
        MapCircleStyle mapCircleStyle = new MapCircleStyle();
        mapCircleStyle.setFillColor(0x094d8cA0, PixelFormat.RGBA_8888);
        MapCircle mapCircle = new MapCircle(geoCircle, mapCircleStyle);

        return mapCircle;
    }
    public class MiLocationListener implements LocationListener {
        public void onLocationChanged(final android.location.Location loc) {
            loc.getLatitude();
            loc.getLongitude();
            if(yo==null){
                Log.i("GPS LOCATIONMANAGER", "Creando Marker");
                GeoCoordinates yogeo = new GeoCoordinates(loc.getLatitude(),loc.getLongitude());
                yo = new MapMarker(yogeo);
                yo.addImage(mapImageYo, new MapMarkerImageStyle());
                mapView.getMapScene().addMapMarker(yo);
            }
            else{
                Log.i("GPS LOCATIONMANAGER", "Marker ya existe");
                GeoCoordinates yogeo = new GeoCoordinates(loc.getLatitude(),loc.getLongitude());
                yo.setCoordinates(yogeo);
                Log.i("GPS LOCATIONMANAGER", "GPS , " + yo.getCoordinates().latitude + "," + yo.getCoordinates().longitude + "");
            }



        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Desactivado", Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Activo", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
