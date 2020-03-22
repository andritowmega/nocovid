package pe.smarttech.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    Button entrar;
    Button registrar;
    EditText dni;
    EditText password;
    String Sdni;
    String Spassword;
    String token="mitoken";
    String URLbase = "http://nocovid.org.pe";
    String response = "";
    TextView titulo;
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        entrar = findViewById(R.id.entrar);
        registrar = findViewById(R.id.registrar);
        dni = findViewById(R.id.dni);
        password = findViewById(R.id.password);
        titulo = findViewById(R.id.titulo);
        checkPermissions();
        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sdni = dni.getText().toString();
                Spassword = password.getText().toString();
                new CheckInfo().execute();
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<String>();
        // check all required dynamic permissions
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (!missingPermissions.isEmpty()) {
            // request all missing permissions
            final String[] permissions = missingPermissions
                    .toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        } else {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        }
    }
    private class CheckInfo extends AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            if(token != null || !token.equals("")){
                JSONObject data = null;
                String urlParameters  = "dni="+Sdni+"&password="+Spassword;
                Log.i("Parametros", urlParameters);
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;
                URL url;
                HttpURLConnection urlConnection = null;
                try{
                    url = new URL(URLbase+"/api/login.php");
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
                        response = readStream(urlConnection.getInputStream());
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
            titulo.setText("Conectando ...");
        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("Response ",response);
            if(result){
                if(response.equals("vacio")){
                    titulo.setText("ERROR, dni o contraseña incorrecto");
                }
                else if(response.equals("error")){
                    titulo.setText("ERROR, servidor en mantenimiento");
                }
                else{
                    titulo.setText("BIENVENIDO");
                    Log.d("TOKEN ",response);
                    String filename = "ctoken.txt";
                    String string= response;
                    FileOutputStream outputStream;
                    try{
                        outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                        outputStream.write(string.getBytes());
                        outputStream.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(),"Bienvenido",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            else{
                titulo.setText("ERROR, no se resivio respuesta");
            }
        }
        @Override
        protected void onCancelled() {

        }
    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
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
        return response.toString();
    }
}
