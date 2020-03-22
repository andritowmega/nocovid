package pe.smarttech.covid19;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HereActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private MapViewLite mapView;
    private LocationListener milocListener;
    LocationManager milocManager;
    double lat;
    double lng;
    MapMarker yo;
    MapImage mapImageYo;
    String response = "";
    String token = "";
    String URLbase = "http://nocovid.org.pe";
    TextView titulo;
    String muertes = "";
    String infectados = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_here);
        // Get a MapViewLite instance from the layout.
        mapView = findViewById(R.id.map_view);
        titulo = findViewById(R.id.titulo);
        mapView.onCreate(savedInstanceState);
        loadMapScene();
    }
    private void loadMapScene() {
        // Load a scene from the SDK to render the map with a map style.
        mapView.getMapScene().loadScene(MapStyle.NORMAL_DAY, new MapScene.LoadSceneCallback() {
            @Override
            public void onLoadScene(@Nullable MapScene.ErrorCode errorCode) {
                if (errorCode == null) {

                    mapView.getCamera().setTarget(new GeoCoordinates(-16.39889,-71.5390867));
                    mapView.getCamera().setZoomLevel(14);

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
                    new CheckInfo().execute();

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
    private class CheckInfo extends AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            if(token != null || !token.equals("")){
                JSONObject data = null;
                String urlParameters  = "ciudad=Arequipa";
                Log.i("Parametros", urlParameters);
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;
                URL url;
                HttpURLConnection urlConnection = null;
                try{
                    url = new URL(URLbase+"/api/incidencias.php");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput( true );
                    urlConnection.setInstanceFollowRedirects( false );
                    urlConnection.setRequestMethod( "POST" );
                    urlConnection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty( "charset", "utf-8");
                    //urlConnection.setRequestProperty( "x-access-token", data.getString("token"));
                    urlConnection.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
                    urlConnection.setUseCaches( false );
                    DataOutputStream wr = new DataOutputStream( urlConnection.getOutputStream());
                    wr.write( postData );
                    int responseCode = urlConnection.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK){
                        publishProgress(100);
                        readStream(urlConnection.getInputStream());
                        return true;
                    }
                    else{
                        Log.d("ERROR ","nO HAY RESPUESTA "+responseCode);
                        return false;
                    }
                } catch (MalformedURLException e){
                    Log.d("ERROR ",e.toString());
                    return false;
                } catch (IOException e){
                    Log.d("ERROR ",e.toString());
                    return false;
                }
            }
            else return true;

        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            int progreso = values[0].intValue();
            if(progreso==100){ }
        }
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected void onPostExecute(Boolean result) {

            if(result){
                Log.d("HILO ","OK");
                if(!muertes.equals("") && !muertes.equals("")){
                    if(muertes.equals("vacio") || muertes.equals("error")){
                        titulo.setText("NOCOVID.ORG.PE - No hay datos para esta ciudad");
                    }
                    else{
                        titulo.setText("NOCOVID.ORG.PE - Muertes: "+muertes+" | Infectados: "+infectados);
                    }
                }
                else{
                    titulo.setText("NOCOVID.ORG.PE - No hay datos para esta ciudad");
                }
            }
            else{
                Log.d("HILO ","ERROR");
            }
        }
        @Override
        protected void onCancelled() {

        }
    }
    private void readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        int flag = 0;
        try {

            MapImage mapImage = MapImageFactory.fromResource(getBaseContext().getResources(), R.drawable.markericon4);
            MapMarkerImageStyle mapMarkerImageStyle = new MapMarkerImageStyle();
            mapMarkerImageStyle.setAnchorPoint(new Anchor2D(0.5f, 1));
            MapScene mapScene = mapView.getMapScene();

            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if(flag==0){
                    Log.d("RESPONSE","muertos: " + line);
                    if(line.equals("vacio") || line.equals("error")){
                        break;
                    }
                    else {
                        muertes = line;
                    }
                    flag++;
                }
                else if(flag==1){
                    infectados = line;
                    Log.d("RESPONSE","infectados: " + line);
                    flag++;
                }
                else if(flag>1){
                    Log.d("RESPONSE","gps: " + line);
                    String[] data = line.split(",");
                    GeoCoordinates marker = new GeoCoordinates(Double.parseDouble(data[0]),Double.parseDouble(data[1]));
                    MapMarker mapMarker = new MapMarker(marker);
                    mapMarker.addImage(mapImage, mapMarkerImageStyle);
                    MapCircle mapCircle = createMapCircle(marker);
                    mapScene.addMapCircle(mapCircle);
                    mapView.getMapScene().addMapMarker(mapMarker);
                    Log.d("RESPONSE"," Marker Agregado");
                    flag++;
                }
                //response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //return response.toString();
    }
}
