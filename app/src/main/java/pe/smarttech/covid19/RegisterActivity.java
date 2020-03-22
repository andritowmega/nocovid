package pe.smarttech.covid19;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    Button registrar;
    EditText dni;
    EditText password;
    EditText password2;
    EditText nombres;
    EditText apellidos;
    EditText celular;
    String sdni="";
    String spassword="";
    String snombres="";
    String sapellidos="";
    String scelular="";
    String response="";
    String token="mitoken";
    String URLbase = "http://nocovid.org.pe";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        registrar = findViewById(R.id.registrar);
        dni = findViewById(R.id.dni);
        nombres = findViewById(R.id.nombres);
        apellidos = findViewById(R.id.apellidos);
        celular = findViewById(R.id.celular);
        password = findViewById(R.id.password);
        password2 = findViewById(R.id.password2);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getText().toString().equals(password2.getText().toString())){
                    snombres = nombres.getText().toString();
                    sapellidos = apellidos.getText().toString();
                    sdni = dni.getText().toString();
                    scelular = celular.getText().toString();
                    spassword = password.getText().toString();
                    new CheckInfo().execute();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Contraseñas no coinciden",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }
    private class CheckInfo extends AsyncTask<Void, Integer, Boolean> {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected Boolean doInBackground(Void... params) {
            if(token != null || !token.equals("")){
                JSONObject data = null;
                String urlParameters  = "dni="+sdni+"&password="+spassword+"&nombres="+snombres+"&apellidos="+sapellidos+"&celular="+scelular;
                Log.i("Parametros", urlParameters);
                byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
                int    postDataLength = postData.length;
                URL url;
                HttpURLConnection urlConnection = null;
                try{
                    url = new URL(URLbase+"/api/register.php");
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

        }
        @Override
        protected void onPostExecute(Boolean result) {
            Log.d("Response ",response);
            if(result){
                if(response.equals("ok")){
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(getApplicationContext(),"Registro éxitoso",Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(response.equals("vacio")){
                    Toast toast = Toast.makeText(getApplicationContext(),"ERROR, Datos ya registrados",Toast.LENGTH_LONG);
                    toast.show();
                }
                else if(response.equals("error")){
                    Toast toast = Toast.makeText(getApplicationContext(),"En Mantenimiento",Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Error Desconocido",Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(),"NO HAY RESPUESTA, INTENTELO DE NUEVO",Toast.LENGTH_LONG);
                toast.show();
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
